package cl.duoc.ms_user.service.UserServiceImpl;

import cl.duoc.ms_user.dto.AuthResponseDto;
import cl.duoc.ms_user.dto.LoginRequestDto;
import cl.duoc.ms_user.dto.UserRequestDto;
import cl.duoc.ms_user.dto.UserResponseDto;
import cl.duoc.ms_user.model.User;
import cl.duoc.ms_user.repository.UserRepository;
import cl.duoc.ms_user.security.JwtService;
import cl.duoc.ms_user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getRegisterDate(),
                user.getAccountLevel()
        );
    }

    private User toEntity(UserRequestDto dto) {
        return new User(null, dto.getEmail(), dto.getUsername(), dto.getPassword(),
                LocalDateTime.now(), "ACTIVE", 1);
    }

    @Override
    public UserResponseDto register(UserRequestDto dto) {
        log.info("Register User");
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya existe");
        }

        User newUser = this.toEntity(dto);
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        return this.toDto(repository.save(newUser));
    }

    @Override
    public AuthResponseDto login(LoginRequestDto dto) {
        log.info("Login User");
        User user = repository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponseDto(token, user.getId(), user.getUsername());
    }

    @Override
    public List<UserResponseDto> findAll() {
        log.info("Find All Users");
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public UserResponseDto findById(Long id) {
        log.info("Find User by ID {}", id);
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Override
    public List<UserResponseDto> findByAccountStatus(String accountStatus) {
        List<UserResponseDto> users = repository.findByAccountStatus(accountStatus)
                .stream()
                .map(this::toDto)
                .toList();

        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay cuentas con el estado '" + accountStatus + "'");
        }

        return users;
    }

    @Override
    public UserResponseDto findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Override
    public UserResponseDto updateEmail(Long id, String newEmail) {
        log.info("Update User email by ID {}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.setEmail(newEmail);
        return toDto(repository.save(user));
    }

    @Override
    public UserResponseDto updatePassword(Long id, String newPassword) {
        log.info("Update User password by ID {}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.setPassword(passwordEncoder.encode(newPassword));
        return toDto(repository.save(user));
    }

    @Override
    public UserResponseDto updateAccountStatus(Long id, String newStatus) {
        log.info("Update User status by ID {}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.setAccountStatus(newStatus);
        return toDto(repository.save(user));
    }

    @Override
    public void deleteById(Long id) {
        log.info("Delete User by ID {}", id);
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario con ID " + id + " no encontrado");
        }
        repository.deleteById(id);
    }
}
