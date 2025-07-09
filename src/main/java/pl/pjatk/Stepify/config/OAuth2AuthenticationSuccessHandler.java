package pl.pjatk.Stepify.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.pjatk.Stepify.user.dto.AuthResponseDTO;
import pl.pjatk.Stepify.user.dto.UserDTO;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.service.JWTService;
import pl.pjatk.Stepify.user.service.OAuth2UserService;
import pl.pjatk.Stepify.user.mapper.UserMapper;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTService jwtService;
    private final OAuth2UserService oAuth2UserService;
    private final UserMapper userMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");

        // Check if user exists, if not create new user
        User user = oAuth2UserService.findOrCreateGoogleUser(email, name, lastName);

        // Generate JWT token
        String token = jwtService.generateToken(email);

        // Set JWT token in cookie
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // Create response DTO
        UserDTO userDTO = userMapper.userToUserDTO(user);
        AuthResponseDTO authResponse = new AuthResponseDTO(token, userDTO);

        // Redirect to frontend with token
        String redirectUrl = "https://stepify-frontend.vercel.app/oauth2/login/google";
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
} 