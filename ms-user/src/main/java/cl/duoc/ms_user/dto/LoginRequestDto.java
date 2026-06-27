package cl.duoc.ms_user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "Debe ingresar un correo")
    @Email(message = "Debe ingresar un formato de email válido")
    private String email;

    @NotBlank(message = "Debe ingresar una contraseña")
    private String password;
}
