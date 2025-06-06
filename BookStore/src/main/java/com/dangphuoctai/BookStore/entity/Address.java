package com.dangphuoctai.BookStore.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    @NotBlank
    @NotBlank
    @Size(min = 4, message = "City name must contain atleast 4 characters")
    private String city;
    @NotBlank
    @Size(min = 2, message = "District name must contain atleast 2 characters")
    private String district;

    @Size(min = 5, message = "Ward name must contain atleast 5 characters")
    private String ward;
    @NotBlank
    @Size(min = 5, message = "Building name must contain atleast 5 characters")
    private String buildingName;
    @NotBlank
    @Size(min = 2, message = "Country name must contain atleast 2 characters")
    private String country;
    // @NotBlank
    // @Size(min = 6, message = "Pincode must contain atleast 6 characters")
    // private String pincode;

    @OneToMany(mappedBy = "address")
    private List<Order> orders = new ArrayList<>();

    public Address(String country, String district, String city, String ward, String buildingName) {
        this.country = country;
        this.district = district;
        this.city = city;
        this.ward = ward;
        this.buildingName = buildingName;
        // this.pincode = pincode;

    }
}
