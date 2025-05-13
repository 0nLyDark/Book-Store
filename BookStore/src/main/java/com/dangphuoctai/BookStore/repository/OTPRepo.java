package com.dangphuoctai.BookStore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.OTP;
import com.dangphuoctai.BookStore.enums.OTPType;

@Repository
public interface OTPRepo extends JpaRepository<OTP, Long> {

    Optional<OTP> findByEmail(String email);

    Optional<OTP> findByEmailAndType(String email, OTPType accountVerification);

    Optional<OTP> findByOrderOrderIdAndType(Long orderId, OTPType orderVerification);

}
