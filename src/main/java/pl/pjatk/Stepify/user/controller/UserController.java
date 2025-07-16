package pl.pjatk.Stepify.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.Stepify.user.dto.AuthResponseDTO;
import pl.pjatk.Stepify.user.dto.LoginRequestDTO;
import pl.pjatk.Stepify.user.dto.UserDTO;
import pl.pjatk.Stepify.user.service.UserService;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    ResponseEntity<Void> register(@Valid @RequestBody UserDTO userDTO) {
        userService.register(userDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/login")
    ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {

        return ResponseEntity.ok(userService.authenticate(loginRequestDTO, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        userService.logout(response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    ResponseEntity<UserDTO> getUser() {
        return ResponseEntity.ok(userService.getUserDTO());
    }

    @PutMapping("/user")
    ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(userDTO));
    }

    @DeleteMapping("/user/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{id}")
    ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));

    }

    @GetMapping("/users")
    ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}
