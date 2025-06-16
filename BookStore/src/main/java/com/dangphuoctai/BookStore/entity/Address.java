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
    @Size(min = 4, message = "Tên thành phố phải có ít nhất 4 ký tự")
    private String city;

    @NotBlank(message = "Quận/Huyện không được để trống")
    @Size(min = 2, message = "Tên quận/huyện phải có ít nhất 2 ký tự")
    private String district;

    @Size(min = 5, message = "Tên phường/xã phải có ít nhất 5 ký tự")
    private String ward;

    @NotBlank(message = "Tên tòa nhà không được để trống")
    @Size(min = 5, message = "Tên tòa nhà phải có ít nhất 5 ký tự")
    private String buildingName;

    @NotBlank(message = "Quốc gia không được để trống")
    @Size(min = 2, message = "Tên quốc gia phải có ít nhất 2 ký tự")
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
