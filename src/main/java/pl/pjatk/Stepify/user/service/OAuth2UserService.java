package pl.pjatk.Stepify.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2UserService {
    private final UserRepository userRepository;

    public User findOrCreateGoogleUser(String email, String name, String lastName) {
        return userRepository.findUserByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User(
                            email,
                            name,
                            lastName,
                            "", // Phone number is empty for Google users
                            UUID.randomUUID().toString(), // Random password for Google users
                            "ROLE_USER"
                    );
                    return userRepository.save(newUser);
                });
    }
} 