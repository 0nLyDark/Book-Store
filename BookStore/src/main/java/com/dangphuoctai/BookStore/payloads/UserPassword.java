package com.dangphuoctai.BookStore.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPassword {
    private String currentPassword;

    private String newPassword;

}
