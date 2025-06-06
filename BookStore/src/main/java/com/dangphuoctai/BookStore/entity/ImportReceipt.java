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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "import_receipts")
@NoArgsConstructor
@AllArgsConstructor
public class ImportReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long importReceiptId;

    @Column(nullable = false)
    private LocalDateTime importDate;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean status = true;

    @OneToMany(mappedBy = "importReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImportReceiptItem> importReceiptItems = new ArrayList<>();

    @Column(nullable = false)
    private Long createdBy;

    // @Column(nullable = false)
    // private Long updateBy;

    // @Column(nullable = false)
    // LocalDateTime createdAt;
    // @Column(nullable = false)
    // LocalDateTime updatedAt;

}
