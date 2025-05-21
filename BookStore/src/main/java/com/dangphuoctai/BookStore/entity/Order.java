package com.dangphuoctai.BookStore.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Index;
import org.hibernate.annotations.NaturalId;

import com.dangphuoctai.BookStore.enums.OrderStatus;
import com.dangphuoctai.BookStore.enums.OrderType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_code", columnList = "orderCode")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @NaturalId
    @Column(nullable = false, unique = true)
    private String orderCode;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Email
    private String email;

    @Size(min = 2, message = "Delivery name must contain atleast 3 characters")
    private String deliveryName;

    @Size(min = 10, max = 10, message = "Delivery phone must be exactly 10 characters")
    private String deliveryPhone;

    @OneToMany(mappedBy = "order", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    @Column(nullable = false)
    private double subTotal;

    @Column(nullable = false)
    private double priceShip;

    @Column(nullable = false)
    private double totalAmount;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private PromotionSnapshot coupon;

    @ManyToOne
    @JoinColumn(name = "freeship_id")
    private PromotionSnapshot freeship;

    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @NotNull(message = "Order status is required")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @NotNull(message = "Order type is required")
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @PrePersist
    @PreUpdate
    private void validateSave() {
        if (OrderType.ONLINE.equals(orderType)) {
            if (email.isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            if (deliveryName.isEmpty()) {
                throw new IllegalArgumentException("Delivery name cannot be empty");
            }
            if (deliveryPhone.isEmpty()) {
                throw new IllegalArgumentException("Delivery phone cannot be empty");
            }
            if (address == null) {
                throw new IllegalArgumentException("Address cannot be null");
            }
        }
    }

}
