package com.dangphuoctai.BookStore.payloads.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.dangphuoctai.BookStore.entity.Role;
import com.dangphuoctai.BookStore.enums.AccountType;

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
    private String avatar;
    private AccountType accountType;
    private Boolean endabled;
    private Boolean verified;
    private LocalDateTime createdAt;

    private Set<Role> roles = new HashSet<>();

    // private List<AddressDTO> address;
    // private CartDTO cart;

}
