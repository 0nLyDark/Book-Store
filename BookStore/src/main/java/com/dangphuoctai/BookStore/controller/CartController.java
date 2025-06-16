package com.dangphuoctai.BookStore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.payloads.dto.CartDTO;
import com.dangphuoctai.BookStore.payloads.dto.CartRequestDTO;
import com.dangphuoctai.BookStore.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/public/carts/user/{userId}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable Long userId) {
        CartDTO cartDTO = cartService.getCartByUserId(userId);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @PostMapping("/public/carts")
    public ResponseEntity<String> addProductToCart(@RequestBody CartRequestDTO cart) {
        String message = cartService.addProductToCart(cart.getUserId(), cart.getProductId(), cart.getQuantity());

        return new ResponseEntity<String>(message, HttpStatus.CREATED);
    }

    @PutMapping("/public/carts")
    public ResponseEntity<String> updateCart(@RequestBody CartRequestDTO cart) {
        String message = cartService.updateCartQuantityProduct(cart.getUserId(), cart.getProductId(),
                cart.getQuantity());

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }

    @DeleteMapping("/public/carts/user/{userId}/product")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long userId, @RequestBody Long productId) {
        String message = cartService.deleteProductFromCart(userId, productId);

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }

    @DeleteMapping("/public/carts/user/{userId}/products")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long userId,
            @RequestBody List<Long> productIds) {
        String message = cartService.deleteProductFromCartAll(userId, productIds);

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }

    @DeleteMapping("/public/carts/user/{userId}")
    public ResponseEntity<String> deleteCart(@PathVariable Long userId) {
        String message = cartService.clearCart(userId);

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }
}
