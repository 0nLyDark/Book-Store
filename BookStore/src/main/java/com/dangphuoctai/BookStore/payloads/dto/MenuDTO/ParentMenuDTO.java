package com.dangphuoctai.BookStore.payloads.dto.MenuDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentMenuDTO extends MenuDTO {

    private List<ParentMenuDTO> childrens;

}
