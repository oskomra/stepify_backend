package pl.pjatk.Stepify.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String type="Bearer";
    private String email;
    private String role;

    public JwtResponseDTO(String token, String email, String role) {
        this.token = token;
        this.email = email;
        this.role = role;
    }
}
