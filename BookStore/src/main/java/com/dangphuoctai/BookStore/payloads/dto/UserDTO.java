package com.dangphuoctai.BookStore.payloads.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dangphuoctai.BookStore.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String fullName;
    private String mobileNumber;
    private String email;
    private Boolean endabled;
    private Boolean verified;
    private Set<Role> roles = new HashSet<>();
    // private List<AddressDTO> address;
    // private CartDTO cart;

}
