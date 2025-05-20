package pl.pjatk.Stepify.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.Stepify.user.model.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
}
