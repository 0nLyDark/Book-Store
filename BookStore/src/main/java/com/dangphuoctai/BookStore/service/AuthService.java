package com.dangphuoctai.BookStore.service;

import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserRegister;

public interface AuthService {

    UserDTO registerUser(UserRegister userRegister);

    UserDTO loginUser(String username, String password);

    UserDTO loginGoogle(UserDTO userDTO);

    Boolean verityOTPEmail(OtpDTO otpDTO);

    String generateOTPEmail(String email);

    String generateRefreshToken(Long userId);

    UserDTO getUserByRefreshToken(String refreshtoken);

}
