package com.dangphuoctai.BookStore.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Size(min = 2, message = "Delivery name must contain atleast 3 characters")
    private String deliveryName;

    @Column(nullable = false)
    @Size(min = 10, max = 10, message = "Delivery phone must be exactly 10 characters")
    private String deliveryPhone;

    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "address_id") // Cột chứa khóa ngoại tới Address
    private Address address;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Promotion coupon;

    @ManyToOne
    @JoinColumn(name = "freeship_id")
    private Promotion freeship;

    private double totalAmount;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private String orderStatus;
}
