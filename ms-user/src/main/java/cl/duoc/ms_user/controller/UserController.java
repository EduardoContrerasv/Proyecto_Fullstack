package cl.duoc.ms_user.controller;

import cl.duoc.ms_user.dto.AuthResponseDto;
import cl.duoc.ms_user.dto.LoginRequestDto;
import cl.duoc.ms_user.dto.UserRequestDto;
import cl.duoc.ms_user.dto.UserResponseDto;
import cl.duoc.ms_user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService service;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(service.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(service.login(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/getUserId/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/getEmail/{email}")
    public ResponseEntity<UserResponseDto> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @GetMapping("/getstatus/{status}")
    public ResponseEntity<List<UserResponseDto>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.findByAccountStatus(status));
    }

    @PutMapping("/updateEmail/{id}")
    public ResponseEntity<UserResponseDto> updateEmail(@PathVariable Long id, @Valid @RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(service.updateEmail(id, dto.getEmail()));
    }

    @PutMapping("/updatePassword/{id}")
    public ResponseEntity<UserResponseDto> updatePassword(@PathVariable Long id, @Valid @RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(service.updatePassword(id, dto.getPassword()));
    }

    @PutMapping("/updateStatus/{id}/{status}")
    public ResponseEntity<UserResponseDto> updateStatus(@PathVariable Long id, @PathVariable String status) {
        return ResponseEntity.ok(service.updateAccountStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
