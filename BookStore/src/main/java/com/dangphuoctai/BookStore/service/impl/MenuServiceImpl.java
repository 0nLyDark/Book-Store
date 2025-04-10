package com.dangphuoctai.BookStore.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.dangphuoctai.BookStore.entity.Menu;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.MenuDTO.MenuDTO;
import com.dangphuoctai.BookStore.payloads.dto.MenuDTO.ChildMenuDTO;
import com.dangphuoctai.BookStore.payloads.dto.MenuDTO.ParentMenuDTO;
import com.dangphuoctai.BookStore.payloads.response.MenuResponse;
import com.dangphuoctai.BookStore.repository.MenuRepo;
import com.dangphuoctai.BookStore.service.MenuService;

import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public MenuDTO getMenuById(Long menuId) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu", "menuId", menuId));

        return modelMapper.map(menu, ChildMenuDTO.class);
    }

    @Override
    public MenuResponse getAllMenus(String type, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Menu> pageCategories;
        List<MenuDTO> menuDTOs;

        if (type.equalsIgnoreCase("parent")) {
            pageCategories = menuRepo.findByParentIsNull(pageDetails);
            menuDTOs = pageCategories.getContent().stream()
                    .map(menu -> modelMapper.map(menu, ParentMenuDTO.class)).collect(Collectors.toList());
        } else {
            pageCategories = menuRepo.findAll(pageDetails);
            menuDTOs = pageCategories.getContent().stream()
                    .map(menu -> modelMapper.map(menu, ChildMenuDTO.class)).collect(Collectors.toList());
        }

        MenuResponse menuResponse = new MenuResponse();
        menuResponse.setContent(menuDTOs);
        menuResponse.setPageNumber(pageCategories.getNumber());
        menuResponse.setPageSize(pageCategories.getSize());
        menuResponse.setTotalElements(pageCategories.getTotalElements());
        menuResponse.setTotalPages(pageCategories.getTotalPages());
        menuResponse.setLastPage(pageCategories.isLast());

        return menuResponse;
    }

    @Override
    public MenuDTO createMenu(ChildMenuDTO menuDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        Menu menu = new Menu();
        menu.setName(menuDTO.getName());
        menu.setLink(menuDTO.getLink());
        menu.setPosition(menuDTO.getPosition());
        menu.setSortOrder(menuDTO.getSortOrder());

        if (menuDTO.getParent() != null) {
            Menu parentMenu = menuRepo.findById(menuDTO.getParent().getMenuId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu", "menuId",
                            menuDTO.getParent().getMenuId()));
            menu.setParent(parentMenu);
        }
        menu.setStatus(false);

        menu.setCreatedBy(userId);
        menu.setUpdatedBy(userId);
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());
        menuRepo.save(menu);

        return modelMapper.map(menu, ChildMenuDTO.class);
    }

    @Override
    public MenuDTO updateMenu(ChildMenuDTO menuDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Menu menu = menuRepo.findById(menuDTO.getMenuId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Menu", "menuId", menuDTO.getMenuId()));
        menu.setName(menuDTO.getName());
        menu.setLink(menuDTO.getLink());
        menu.setPosition(menuDTO.getPosition());
        menu.setSortOrder(menuDTO.getSortOrder());

        if (menuDTO.getParent() != null) {
            if (menuDTO.getMenuId() == menuDTO.getParent().getMenuId()) {
                throw new APIException("Menu cannot be its own parent");
            }
            Menu parentMenu = menuRepo.findById(menuDTO.getParent().getMenuId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu", "menuId",
                            menuDTO.getParent().getMenuId()));
            menu.setParent(parentMenu);
        } else {
            menu.setParent(null);
        }
        menu.setStatus(menuDTO.getStatus());

        menu.setUpdatedBy(userId);
        menu.setUpdatedAt(LocalDateTime.now());
        menuRepo.save(menu);

        return modelMapper.map(menu, ChildMenuDTO.class);
    }

    @Override
    public String deleteMenu(Long menuId) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu", "menuId", menuId));
        menuRepo.delete(menu);

        return "Menu with ID: " + menuId + " deleted successfully";
    }

}
