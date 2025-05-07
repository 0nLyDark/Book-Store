package com.dangphuoctai.BookStore.payloads.dto.UserDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPassword {

    @NotBlank
    private String currentPassword;
    @NotBlank
    private String newPassword;

}
