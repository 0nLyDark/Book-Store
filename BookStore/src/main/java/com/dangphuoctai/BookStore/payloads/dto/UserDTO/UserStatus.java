package com.dangphuoctai.BookStore.payloads.dto.UserDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatus {

    @NotNull
    private Long userId;
    @NotNull
    private Boolean status;
}
