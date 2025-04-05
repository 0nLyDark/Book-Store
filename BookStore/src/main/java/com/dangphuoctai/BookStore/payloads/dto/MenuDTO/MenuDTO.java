package com.dangphuoctai.BookStore.payloads.dto.MenuDTO;

import java.time.LocalDateTime;

import com.dangphuoctai.BookStore.enums.MenuPosition;

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
    private MenuPosition position;
    private int sortOrder;
    private Boolean status;

    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
