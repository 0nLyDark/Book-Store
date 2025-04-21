package com.dangphuoctai.BookStore.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dangphuoctai.BookStore.enums.PromotionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "promotionSnapshots")
@NoArgsConstructor
@AllArgsConstructor
public class PromotionSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotionId;

    @Column(nullable = false, unique = true)
    private String hash;

    @NotBlank
    private String promotionName;

    @Column(nullable = false)
    private String promotionCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private PromotionType promotionType;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private Double valueApply;

    @Column(nullable = false)
    private Boolean valueType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "coupon")
    private List<Order> orderCoupons = new ArrayList<>();

    @OneToMany(mappedBy = "freeship")
    private List<Order> orderFreeships = new ArrayList<>();

    @Column(nullable = false)
    private Long createdBy;
    @Column(nullable = false)
    private Long updatedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
