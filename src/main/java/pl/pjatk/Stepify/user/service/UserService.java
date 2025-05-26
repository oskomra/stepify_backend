package pl.pjatk.Stepify.user.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.exception.UnauthorizedAccessException;
import pl.pjatk.Stepify.user.advice.UserAlreadyExistsException;
import pl.pjatk.Stepify.user.dto.LoginRequestDTO;
import pl.pjatk.Stepify.user.mapper.UserMapper;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.dto.UserDTO;
import pl.pjatk.Stepify.user.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JWTService jwtService;

    public void register(UserDTO userDTO) {
        if(userRepository.findUserByEmail(userDTO.getEmail()).isEmpty()) {
            User user = new User(
                    userDTO.getEmail(),
                    userDTO.getName(),
                    userDTO.getLastName(),
                    userDTO.getPhone(),
                    passwordEncoder.encode(userDTO.getPassword()),
                    "ROLE_REGISTERED"
            );
            userRepository.save(user);
        } else {
            throw new UserAlreadyExistsException("User with that email already exists");
        }
    }

    public UserDTO authenticate(LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        Authentication authentication =
                authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword())
        );

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(loginRequestDTO.getEmail());

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(false)
                   .path("/")
                   .sameSite("Lax")
                    .maxAge(24 * 60 * 60)
                 .build();
            response.addHeader("Set-Cookie", cookie.toString());
            //  return jwtService.generateToken(loginRequestDTO.getEmail());

            User user = userRepository.findUserByEmail(loginRequestDTO.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));


            UserDTO userDTO = userMapper.userToUserDTO(user);

            return userDTO;

        } else {
            throw new UnauthorizedAccessException("Invalid email or password");
        }
    }

    public void logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", deleteCookie.toString());
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDTO getUserDTO() {
        return userMapper.userToUserDTO(getCurrentUser());
    }

    public UserDTO updateUser(UserDTO userDTO) {
        User user = userRepository.findUserByEmail(userDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getEmail().equals(userDTO.getEmail())) {
            throw new UnauthorizedAccessException("Invalid email or password");
        } else {
            user.setName(userDTO.getName());
            user.setLastName(userDTO.getLastName());
            user.setPhone(userDTO.getPhone());
            user.setEmail(userDTO.getEmail());
        }

        return userMapper.userToUserDTO(userRepository.save(user));
    }



}
