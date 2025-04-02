package com.dangphuoctai.BookStore.payloads;

import com.dangphuoctai.BookStore.payloads.dto.AddressDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegister extends UserDTO {

    private String username;
    private String password;

    private AddressDTO address;
}
