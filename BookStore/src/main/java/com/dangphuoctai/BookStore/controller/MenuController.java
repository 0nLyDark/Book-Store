package com.dangphuoctai.BookStore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.MenuDTO.MenuDTO;
import com.dangphuoctai.BookStore.payloads.dto.MenuDTO.ChildMenuDTO;
import com.dangphuoctai.BookStore.payloads.response.MenuResponse;
import com.dangphuoctai.BookStore.service.MenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/public/menus/{menuId}")
    public ResponseEntity<MenuDTO> getMenuById(@PathVariable Long menuId) {
        MenuDTO menuDTO = menuService.getMenuById(menuId);

        return new ResponseEntity<MenuDTO>(menuDTO, HttpStatus.OK);
    }

    @GetMapping("/public/menus/ids")
    public ResponseEntity<List<MenuDTO>> getCategoryBySlug(@RequestParam(value = "id") List<Long> menuIds) {
        List<MenuDTO> menuDTOs = menuService.getManyMenuById(menuIds);

        return new ResponseEntity<List<MenuDTO>>(menuDTOs, HttpStatus.OK);
    }

    @GetMapping("/public/menus")
    public ResponseEntity<MenuResponse> getAllMenus(
            @RequestParam(name = "type", defaultValue = "parent", required = false) String type,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_MENUS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        MenuResponse menuResponse = menuService.getAllMenus(type,
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "menuId" : sortBy,
                sortOrder);

        return new ResponseEntity<MenuResponse>(menuResponse, HttpStatus.OK);
    }

    @PostMapping("/admin/menus")
    public ResponseEntity<MenuDTO> createMenu(@RequestBody ChildMenuDTO menu) {
        MenuDTO menuDTO = menuService.createMenu(menu);

        return new ResponseEntity<MenuDTO>(menuDTO, HttpStatus.CREATED);
    }

    @PutMapping("/admin/menus")
    public ResponseEntity<MenuDTO> updateMenu(@RequestBody ChildMenuDTO menu) {
        MenuDTO menuDTO = menuService.updateMenu(menu);

        return new ResponseEntity<MenuDTO>(menuDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/menus/{menuId}")
    public ResponseEntity<String> deleteMenu(@PathVariable Long menuId) {
        String result = menuService.deleteMenu(menuId);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
