package com.dangphuoctai.BookStore.controller;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.entity.OTP;
import com.dangphuoctai.BookStore.entity.User;
import com.dangphuoctai.BookStore.exceptions.UserNotFoundException;
import com.dangphuoctai.BookStore.payloads.EmailDetails;
import com.dangphuoctai.BookStore.payloads.RequestLogin;
import com.dangphuoctai.BookStore.payloads.UserRegister;
import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO;
import com.dangphuoctai.BookStore.security.JWTUtil;
import com.dangphuoctai.BookStore.service.AuthService;
import com.dangphuoctai.BookStore.service.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

        @Autowired
        private JWTUtil jwtUtil;

        @Autowired
        private AuthService authService;

        @Autowired
        private EmailService emailService;

        @PostMapping("/register")
        public ResponseEntity<Map<String, Object>> registerHandler(@RequestBody UserRegister userRegister)
                        throws UserNotFoundException {

                UserDTO userDTO = authService.registerUser(userRegister);

                String otd = authService.generateOTPEmail(userDTO.getEmail());
                String verifyUrl = "http://localhost:8080/api/auth/verify/"
                                + jwtUtil.generateCodeOTP(userRegister.getEmail(), otd);
                String htmlContent = "<html><body>"
                                + "<h2>Chào " + userRegister.getEmail() + ",</h2>"
                                + "<p>Bấm vào nút bên dưới để kích hoạt tài khoản của bạn:</p>"
                                + "<table role='presentation' border='0' cellpadding='0' cellspacing='0'>"
                                + "  <tr>"
                                + "    <td align='center'>"
                                + "      <a href='" + verifyUrl + "' "
                                + "         style='display: inline-block; background-color: #28a745; color: white; padding: 10px 20px; "
                                + "         text-decoration: none; font-size: 16px; border-radius: 5px;'>"
                                + "        Xác nhận tài khoản"
                                + "      </a>"
                                + "    </td>"
                                + "  </tr>"
                                + "</table>"
                                + "<p>Nếu bạn không đăng ký, hãy bỏ qua email này.</p>"
                                + "</body></html>";
                EmailDetails emailDetails = new EmailDetails();
                emailDetails.setRecipient(userDTO.getEmail());
                emailDetails.setMsgBody(htmlContent);
                emailDetails.setSubject("Book Store Xác thực tài khoản");
                String resultSendEmail = emailService.sendMailWithAttachment(emailDetails);
                System.out.println(resultSendEmail);

                System.out.println(userDTO);

                return new ResponseEntity<Map<String, Object>>(
                                Collections.singletonMap("messages", "Account register succcessful "),
                                HttpStatus.CREATED);
        }

        @PostMapping("auth/login")
        public ResponseEntity<Map<String, Object>> login(@RequestBody RequestLogin requestLogin) {

                UserDTO userDTO = authService.loginUser(requestLogin.getUsername(), requestLogin.getPassword());

                String token = jwtUtil.generateToken(userDTO);

                return new ResponseEntity<Map<String, Object>>(Collections.singletonMap("jwt-token", token),
                                HttpStatus.OK);
        }

        @GetMapping("auth/verify/{code}")
        public ResponseEntity<Map<String, Object>> verifyAccount(@PathVariable String code) {
                OtpDTO otpDTO = jwtUtil.extractCodeOTP(code);
                boolean verify = authService.verityOTPEmail(otpDTO);

                return new ResponseEntity<Map<String, Object>>(Collections.singletonMap("result", verify),
                                HttpStatus.OK);
        }

}
