package com.dangphuoctai.BookStore.entity;

import java.time.LocalDateTime;

import com.dangphuoctai.BookStore.enums.PromotionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "promotions")
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotionId;

    @NotBlank
    private String promotionName;

    @Column(nullable = false, unique = true)
    private String promotionCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private PromotionType promotionType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime endDate;

    @Min(0)
    @Column(nullable = false)
    private Double value;

    @Min(0)
    @Column(nullable = false, columnDefinition = "double DEFAULT 0")
    private Double valueApply;

    @Column(nullable = false)
    private Boolean valueType; // true == % ,false == number

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean status;

    @Column(nullable = false)
    private Long createdBy;
    @Column(nullable = false)
    private Long updatedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void validateSave() {
        if (valueType) {
            if (value > 100) {
                throw new IllegalArgumentException("Value cannot be greater than 100 when valueType is %");
            }
        }
    }

}
