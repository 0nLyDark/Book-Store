package com.dangphuoctai.BookStore.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestLogin {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
