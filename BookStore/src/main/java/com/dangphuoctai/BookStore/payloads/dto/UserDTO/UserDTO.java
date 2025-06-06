package com.dangphuoctai.BookStore.payloads.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.dangphuoctai.BookStore.entity.Role;
import com.dangphuoctai.BookStore.enums.AccountType;
import com.dangphuoctai.BookStore.payloads.dto.AddressDTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    @Size(min = 5, max = 50, message = "Họ tên phải từ 5 đến 50 ký tự")
    private String fullName;
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải gồm đúng 10 chữ số")
    private String mobileNumber;
    private String email;
    private String username;
    private String avatar;
    private AccountType accountType;
    private Boolean enabled;
    private Boolean verified;
    private LocalDateTime createdAt;

    private Set<Role> roles = new HashSet<>();

    private AddressDTO address;

    // private List<AddressDTO> address;
    // private CartDTO cart;

}
