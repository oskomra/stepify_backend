package pl.pjatk.Stepify.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers(("/login")).permitAll()
                        .requestMatchers(("/logout")).permitAll()
                        .requestMatchers(("/products/**")).permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/colors/**").hasRole("USER")
                        .requestMatchers("/sizes/**").hasRole("USER")
                        .requestMatchers("/users/**").hasRole("USER")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/addresses/**").hasRole("USER")
                        .requestMatchers("/cart").hasRole("USER")
                        .requestMatchers("/order").hasRole("USER")
                        .requestMatchers("/payment").hasRole("USER")
                        .requestMatchers("/user").hasRole("USER")
                        .requestMatchers("/users").hasRole("USER")
                        .anyRequest().authenticated())
                .cors(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            ResponseCookie deleteCookie = ResponseCookie.from("token", "")
                                    .httpOnly(true)
                                    .secure(false)
                                    .path("/")
                                    .sameSite("Lax")
                                    .maxAge(0)
                                    .build();
                            response.addHeader("Set-Cookie", deleteCookie.toString());
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
