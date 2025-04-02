package com.dangphuoctai.BookStore.service;

import com.dangphuoctai.BookStore.payloads.UserRegister;
import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO;

public interface AuthService {

    UserDTO registerUser(UserRegister userRegister);

    UserDTO loginUser(String username, String password);

    Boolean verityOTPEmail(OtpDTO otpDTO);

    String generateOTPEmail(String email);

}
