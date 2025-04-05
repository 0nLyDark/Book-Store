package com.dangphuoctai.BookStore.payloads.dto.MenuDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildMenuDTO extends MenuDTO {

    private MenuDTO parent;
}
