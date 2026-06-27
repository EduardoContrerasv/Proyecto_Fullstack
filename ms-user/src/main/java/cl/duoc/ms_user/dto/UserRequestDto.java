package cl.duoc.ms_user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserRequestDto {
    @NotBlank(message = "Debe ingresar un correo")
    @Email(message = "Debe ingresar un formato de email válido")
    String email;

    @NotBlank(message = "Debe ingresar una contraseña")
    String password;

    @NotBlank(message = "Debe ingresar un nombre de usuario")
    private String username;

}
