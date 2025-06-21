package com.dangphuoctai.BookStore.controller;

import java.text.ParseException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.exceptions.UserNotFoundException;
import com.dangphuoctai.BookStore.payloads.EmailDetails;
import com.dangphuoctai.BookStore.payloads.RequestLogin;
import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserRegister;
import com.dangphuoctai.BookStore.security.JWTUtil;
import com.dangphuoctai.BookStore.service.AuthService;
import com.dangphuoctai.BookStore.service.EmailService;
import com.dangphuoctai.BookStore.utils.Email;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.nimbusds.jose.JOSEException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

        @Value("${google.client.id}")
        private String googleClientId;

        @Value("${fontend.urlVerify}")
        private String urlVerify;

        @PostMapping("/register")
        public ResponseEntity<Map<String, Object>> registerHandler(@RequestBody UserRegister userRegister)
                        throws UserNotFoundException {

                UserDTO userDTO = authService.registerUser(userRegister);

                String otp = authService.generateOTPEmail(userDTO.getEmail());
                String verifyUrl = urlVerify + jwtUtil.generateCodeOTP(userRegister.getEmail(), otp);
                String htmlContent = Email.getFormOTPVerifyAccountSendEmail(verifyUrl, userDTO.getFullName());
                EmailDetails emailDetails = new EmailDetails();
                emailDetails.setRecipient(userDTO.getEmail());
                emailDetails.setMsgBody(htmlContent);
                emailDetails.setSubject("Cửa hàng sách BookStore - Xác thực tài khoản");
                String resultSendEmail = emailService.sendMailWithAttachment(emailDetails);
                System.out.println(resultSendEmail);

                System.out.println(userDTO);

                return new ResponseEntity<Map<String, Object>>(
                                Collections.singletonMap("messages", "Account register succcessful "),
                                HttpStatus.CREATED);
        }

        @PostMapping("/auth/google")
        public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
                try {
                        String googleToken = request.get("token");
                        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                                        new NetHttpTransport(), GsonFactory.getDefaultInstance())
                                        .setAudience(Collections.singletonList(googleClientId))
                                        .build();
                        GoogleIdToken idToken = verifier.verify(googleToken);
                        if (idToken != null) {
                                String email = idToken.getPayload().getEmail();
                                String name = (String) idToken.getPayload().get("name");
                                String pictureUrl = (String) idToken.getPayload().get("picture");
                                UserDTO userDTO = new UserDTO();
                                userDTO.setEmail(email);
                                userDTO.setFullName(name);
                                userDTO.setAvatar(pictureUrl);
                                userDTO = authService.loginGoogle(userDTO);
                                String token = jwtUtil.generateToken(userDTO);
                                String refreshToken = authService.generateRefreshToken(userDTO.getUserId());
                                Map<String, Object> tokens = Map.of(
                                                "jwt-token", token);
                                ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                                                .httpOnly(true)
                                                .secure(true)
                                                .path("/")
                                                .maxAge(Duration.ofDays(3))
                                                .sameSite("Strict")
                                                .build();

                                return ResponseEntity.ok()
                                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                                .body(tokens);
                        }
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token - " + e.getMessage());
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }

        @PostMapping("auth/login")
        public ResponseEntity<Map<String, Object>> login(@RequestBody RequestLogin requestLogin) {

                UserDTO userDTO = authService.loginUser(requestLogin.getUsername(), requestLogin.getPassword());

                String token = jwtUtil.generateToken(userDTO);
                String refreshToken = authService.generateRefreshToken(userDTO.getUserId());
                Map<String, Object> tokens = Map.of(
                                "jwt-token", token);
                ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(Duration.ofDays(3))
                                .sameSite("Strict")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(tokens);
        }

        @PostMapping("auth/refresh-token")
        public ResponseEntity<?> refreshToken(HttpServletRequest request) throws JOSEException, ParseException {
                // 1. Lấy refresh token từ cookie

                Cookie[] cookies = request.getCookies();
                if (cookies == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No cookies found");
                }
                String refreshToken = null;
                for (Cookie cookie : cookies) {
                        if ("refreshToken".equals(cookie.getName())) {
                                refreshToken = cookie.getValue();
                                break;
                        }
                }

                // 2. Kiểm tra token
                if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
                }
                // 3. Lấy thông tin user từ token
                UserDTO userDTO = authService.getUserByRefreshToken(refreshToken);
                // 4. Tạo access token mới
                String newAccessToken = jwtUtil.generateToken(userDTO);
                Map<String, Object> token = Map.of(
                                "jwt-token", newAccessToken);
                // 5. Trả về access token mới
                return ResponseEntity.ok(token);
        }

        @GetMapping("auth/verify/{code}")
        public ResponseEntity<Map<String, Object>> verifyAccount(@PathVariable String code) {
                OtpDTO otpDTO = jwtUtil.extractCodeOTP(code);
                boolean verify = authService.verityOTPEmail(otpDTO);

                return new ResponseEntity<Map<String, Object>>(Collections.singletonMap("result", verify),
                                HttpStatus.OK);
        }

}
