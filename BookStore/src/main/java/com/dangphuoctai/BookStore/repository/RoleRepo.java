package com.dangphuoctai.BookStore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

}
