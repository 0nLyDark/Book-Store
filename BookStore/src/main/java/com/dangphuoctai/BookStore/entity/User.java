package com.dangphuoctai.BookStore.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dangphuoctai.BookStore.enums.AccountType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Size(min = 5, max = 50, message = "Họ tên phải từ 5 đến 50 ký tự")
    private String fullName;

    @Column(unique = true)
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải gồm đúng 10 chữ số")
    private String mobileNumber;

    @Column(unique = true)
    private String username;

    private String password;
    @Email
    @Column(unique = true)
    private String email;

    @ManyToMany(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Order> orders = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean enabled = true;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean verified;

    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'USER'")
    private AccountType accountType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private List<RefreshToken> refreshTokens;

    @PrePersist
    @PreUpdate
    private void validateSave() {
        if (this.accountType == AccountType.USER || this.accountType == null) {
            if (this.email == null) {
                throw new IllegalArgumentException("Email is required for this account type");
            }
            // if (this.mobileNumber == null) {
            // throw new IllegalArgumentException("Mobile Number is required for this
            // account type");
            // }
            if (this.username == null || this.username.isBlank()) {
                throw new IllegalArgumentException("Username is required for this account type");
            }
            if (this.password == null || this.password.isBlank()) {
                throw new IllegalArgumentException("Password is required for this account type");
            }
        } else if (this.accountType == AccountType.GOOGLE) {
            if (this.email == null) {
                throw new IllegalArgumentException("Email is required for this account type");
            }
        }
    }
}
