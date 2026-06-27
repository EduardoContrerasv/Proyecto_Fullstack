package cl.duoc.ms_user.service;

import cl.duoc.ms_user.dto.AuthResponseDto;
import cl.duoc.ms_user.dto.LoginRequestDto;
import cl.duoc.ms_user.dto.UserRequestDto;
import cl.duoc.ms_user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto register(UserRequestDto dto);
    AuthResponseDto login(LoginRequestDto dto);
    UserResponseDto findByEmail(String email);
    List<UserResponseDto> findByAccountStatus(String accountStatus);
    UserResponseDto findById(Long id);
    List<UserResponseDto> findAll();
    UserResponseDto updateEmail(Long id, String newEmail);
    UserResponseDto updatePassword(Long id, String newPassword);
    UserResponseDto updateAccountStatus(Long id, String newStatus);
    void deleteById(Long id);
}
