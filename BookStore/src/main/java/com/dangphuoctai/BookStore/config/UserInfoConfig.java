package com.dangphuoctai.BookStore.config;

import java.security.Principal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoConfig implements Principal {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String email;
    private String role;

    @Override
    public String getName() {
        return this.email;
    }
}
