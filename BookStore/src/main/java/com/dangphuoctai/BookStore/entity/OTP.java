package com.dangphuoctai.BookStore.entity;

import java.time.Instant;

import com.dangphuoctai.BookStore.enums.OTPType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "otps")
@NoArgsConstructor
@AllArgsConstructor
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otpId;

    @Email
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải gồm đúng 10 chữ số")
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private OTPType type;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Instant expiryDate;

    @PrePersist
    @PreUpdate
    private void validateSave() {
        if (OTPType.ORDER_VERIFICATION.equals(type)) {
            if (order == null) {
                throw new IllegalArgumentException("Order must be provided for ORDER type OTP");
            }
        } else {
            if (email == null && phoneNumber == null) {
                throw new IllegalArgumentException("Email or Phone Number must be provided");
            }
        }

    }
}
