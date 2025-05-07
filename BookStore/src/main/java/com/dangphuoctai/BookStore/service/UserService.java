package com.dangphuoctai.BookStore.service;

import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserDTO;
import com.dangphuoctai.BookStore.payloads.response.UserResponse;

public interface UserService {

    UserDTO getUserInfor();

    UserDTO getUserById(Long userId);

    UserDTO getUserByEmail(String email);

    UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    String changePassword(String currentPassword, String newPassword);

    String changeAccountStatus(Long userId, Boolean status);

    String resetPassword(Long userId, String newPassword);
}
