package com.dangphuoctai.BookStore.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserRole;
import com.dangphuoctai.BookStore.payloads.response.UserResponse;

public interface UserService {

    UserDTO getUserInfor();

    UserDTO getUserById(Long userId);

    UserDTO getUserByEmail(String email);

    UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    UserDTO updateUser(UserDTO userDTO);

    UserDTO updateUserAvatar(Long userId, MultipartFile image) throws IOException;

    String changeRole(UserRole userRole);

    String changePassword(String currentPassword, String newPassword);

    String changeAccountStatus(Long userId, Boolean status);

    String resetPassword(Long userId, String newPassword);
}
