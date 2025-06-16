package com.dangphuoctai.BookStore.payloads.dto.MenuDTO;

import java.time.LocalDateTime;

import com.dangphuoctai.BookStore.enums.MenuPosition;
import com.dangphuoctai.BookStore.enums.MenuType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {
    private Long menuId;
    private String name;
    private String link;

    @NotNull(message = "Kiểu menu (type) không được để trống")
    private MenuType type;
    @NotNull(message = "Vị trí hiển thị (position) không được để trống")
    private MenuPosition position;

    private int sortOrder;
    private Boolean status;
    private Long refId;

    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
