package com.dangphuoctai.BookStore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "import_receipt_items")
@NoArgsConstructor
@AllArgsConstructor
public class ImportReceiptItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long importReceiptItemId;

    @ManyToOne
    @JoinColumn(name = "import_receipt_id")
    private ImportReceipt importReceipt;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Min(0)
    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Double price;

    @Min(0)
    @Max(100)
    @Column(nullable = false)
    private Integer discount;

    @Column(nullable = false)
    private Double totalPrice;

}
