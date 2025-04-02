package com.dangphuoctai.BookStore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.OTP;

@Repository
public interface OTPRepo extends JpaRepository<OTP, Long> {

    Optional<OTP> findByEmail(String email);

}
