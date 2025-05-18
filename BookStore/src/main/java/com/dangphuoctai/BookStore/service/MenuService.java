package com.dangphuoctai.BookStore.service;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.MenuDTO.ChildMenuDTO;
import com.dangphuoctai.BookStore.payloads.dto.MenuDTO.MenuDTO;
import com.dangphuoctai.BookStore.payloads.response.MenuResponse;

public interface MenuService {

    MenuDTO getMenuById(Long menuId);

    List<MenuDTO> getManyMenuById(List<Long> menuIds);

    MenuResponse getAllMenus(Boolean status, String type, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder);

    MenuDTO createMenu(ChildMenuDTO menuDTO);

    MenuDTO updateMenu(ChildMenuDTO menuDTO);

    String deleteMenu(Long menuId);
}
