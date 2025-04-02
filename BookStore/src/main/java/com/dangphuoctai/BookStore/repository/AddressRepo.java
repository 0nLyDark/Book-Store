package com.dangphuoctai.BookStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.Address;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {
    Address findByCountryAndDistrictAndCityAndPincodeAndWardAndBuildingName(String country, String district,
            String city, String pincode, String ward, String buildingName);
}
