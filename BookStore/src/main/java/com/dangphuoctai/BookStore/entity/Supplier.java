package com.dangphuoctai.BookStore.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "suppliers")
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    @Column(nullable = false)
    private String supplierName;

    @Column(nullable = false, unique = true)
    private String slug;

    @Email
    private String email;

    private String address;

    private String mobieNumber;

    @OneToMany(mappedBy = "supplier", cascade = { CascadeType.ALL })
    private List<Product> products = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    @OneToOne
    @JoinColumn(name = "update_by")
    private User updateBy;
    @Column(nullable = false)
    LocalDateTime createdAt;
    @Column(nullable = false)
    LocalDateTime updatedAt;
}
