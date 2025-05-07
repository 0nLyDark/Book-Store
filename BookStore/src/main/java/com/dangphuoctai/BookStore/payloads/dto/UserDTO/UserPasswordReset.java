package com.dangphuoctai.BookStore.payloads.dto.UserDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordReset {

    @NotNull
    private Long userId;

    @NotBlank
    private String newPassword;

}
