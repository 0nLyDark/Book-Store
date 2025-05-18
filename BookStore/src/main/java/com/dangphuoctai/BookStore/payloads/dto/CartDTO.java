package com.dangphuoctai.BookStore.payloads.dto;

import java.util.ArrayList;
import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {


    private UserDTO user;

    private List<CartItemDTO> cartItems = new ArrayList<>();

    private Double totalPrice = 0.0;
}
