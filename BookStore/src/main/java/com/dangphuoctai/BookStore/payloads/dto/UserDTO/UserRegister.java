package com.dangphuoctai.BookStore.payloads.dto.UserDTO;

import com.dangphuoctai.BookStore.payloads.dto.AddressDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegister extends UserDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    // private AddressDTO address;
}
