package com.dangphuoctai.BookStore.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserPassword;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserPasswordReset;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserRole;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserStatus;
import com.dangphuoctai.BookStore.payloads.response.UserResponse;
import com.dangphuoctai.BookStore.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/public/users/infor")
    public ResponseEntity<UserDTO> getUserInfor() {
        UserDTO userDTO = userService.getUserInfor();
        return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/public/users/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long Id = jwt.getClaim("userId");
        String role = jwt.getClaim("scope");
        boolean isAdmin = role.contains("ADMIN");
        if (userId != Id && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to access this resource");
        }
        UserDTO userDTO = userService.getUserById(userId);

        return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/public/users/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String emailUser = jwt.getClaim("email");
        String roleUser = jwt.getClaim("scope");
        if (!roleUser.contains("ADMIN") && !emailUser.equals(email)) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }
        UserDTO userDTO = userService.getUserByEmail(email);

        return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/users")
    public ResponseEntity<UserResponse> getAllUser(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_USERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        UserResponse userResponse = userService.getAllUsers(
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "userId" : sortBy,
                sortOrder);
        return new ResponseEntity<UserResponse>(userResponse, HttpStatus.OK);
    }

    @PutMapping("/public/users")
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Valid UserDTO user) {

        UserDTO userDTO = userService.updateUser(user);

        return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/public/users/{userId}/avatar")
    public ResponseEntity<UserDTO> updateUserAvatar(@PathVariable Long userId,
            @RequestParam("file") MultipartFile image) throws IOException {

        UserDTO userDTO = userService.updateUserAvatar(userId, image);

        return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/admin/users/role")
    public ResponseEntity<String> changeUserRole(@RequestBody @Valid UserRole userRole) {

        String message = userService.changeRole(userRole);

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }

    @PostMapping("/public/users/password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody UserPassword userPassword) {

        String messages = userService.changePassword(userPassword.getCurrentPassword(), userPassword.getNewPassword());

        return new ResponseEntity<String>(messages, HttpStatus.OK);
    }

    @PostMapping("/admin/users/status")
    public ResponseEntity<String> changeAccountStatus(@Valid @RequestBody UserStatus userStatus) {

        String messages = userService.changeAccountStatus(userStatus.getUserId(), userStatus.getStatus());

        return new ResponseEntity<String>(messages, HttpStatus.OK);
    }

    @PostMapping("/admin/users/password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody UserPasswordReset userPasswordReset) {

        String messages = userService.resetPassword(userPasswordReset.getUserId(), userPasswordReset.getNewPassword());

        return new ResponseEntity<String>(messages, HttpStatus.OK);
    }
}
