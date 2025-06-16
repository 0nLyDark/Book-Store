package com.dangphuoctai.BookStore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Menu;
import com.dangphuoctai.BookStore.enums.MenuPosition;
import com.dangphuoctai.BookStore.enums.MenuType;

@Repository
public interface MenuRepo extends JpaRepository<Menu, Long> {

    Page<Menu> findAllByParentIsNull(Pageable pageDetails);

    Page<Menu> findAllByParentIsNullAndStatus(Boolean status, Pageable pageDetails);

    Page<Menu> findAllByStatus(Boolean status, Pageable pageDetails);

    boolean existsByNameAndTypeAndPosition(String name, MenuType type, MenuPosition position);

    Page<Menu> findAll(Specification<Menu> menuSpecification, Pageable pageDetails);

}
