package cl.duoc.ms_user.service.UserServiceImpl;

import cl.duoc.ms_user.dto.AuthResponseDto;
import cl.duoc.ms_user.dto.LoginRequestDto;
import cl.duoc.ms_user.dto.UserRequestDto;
import cl.duoc.ms_user.dto.UserResponseDto;
import cl.duoc.ms_user.model.User;
import cl.duoc.ms_user.repository.UserRepository;
import cl.duoc.ms_user.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @InjectMocks private UserServiceImpl service;

    private UserRequestDto reqUser(String email, String username, String password) {
        UserRequestDto dto = new UserRequestDto();
        dto.setEmail(email);
        dto.setUsername(username);
        dto.setPassword(password);
        return dto;
    }

    private LoginRequestDto login(String email, String password) {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    @Test
    void register_emailNuevo_hasheaContrasenaYGuarda() {
        when(repository.findByEmail("ana@duoc.cl")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secreta")).thenReturn("HASH");
        when(repository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponseDto res = service.register(reqUser("ana@duoc.cl", "ana", "secreta"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("HASH");
        assertThat(res.getUsername()).isEqualTo("ana");
    }

    @Test
    void register_emailDuplicado_lanza409() {
        when(repository.findByEmail("ana@duoc.cl")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> service.register(reqUser("ana@duoc.cl", "ana", "secreta")))
                .isInstanceOf(ResponseStatusException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void login_credencialesValidas_devuelveToken() {
        User u = new User();
        u.setId(1L);
        u.setUsername("ana");
        u.setEmail("ana@duoc.cl");
        u.setPassword("HASH");
        when(repository.findByEmail("ana@duoc.cl")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("secreta", "HASH")).thenReturn(true);
        when(jwtService.generateToken(u)).thenReturn("TOKEN");

        AuthResponseDto res = service.login(login("ana@duoc.cl", "secreta"));

        assertThat(res.getToken()).isEqualTo("TOKEN");
        assertThat(res.getUserId()).isEqualTo(1L);
        assertThat(res.getUsername()).isEqualTo("ana");
    }

    @Test
    void login_contrasenaIncorrecta_lanza401YNoEmiteToken() {
        User u = new User();
        u.setPassword("HASH");
        when(repository.findByEmail("ana@duoc.cl")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("mala", "HASH")).thenReturn(false);

        assertThatThrownBy(() -> service.login(login("ana@duoc.cl", "mala")))
                .isInstanceOf(ResponseStatusException.class);
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_emailInexistente_lanza401() {
        when(repository.findByEmail("nadie@duoc.cl")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(login("nadie@duoc.cl", "x")))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void updatePassword_rehasheaLaNueva() {
        User u = new User();
        u.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(u));
        when(passwordEncoder.encode("nueva")).thenReturn("NEWHASH");
        when(repository.save(u)).thenReturn(u);

        service.updatePassword(1L, "nueva");

        assertThat(u.getPassword()).isEqualTo("NEWHASH");
    }
}
