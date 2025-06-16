package com.dangphuoctai.BookStore.service;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.CartDTO;

public interface CartService {

    String addProductToCart(Long userId, Long productId, Integer quantity);

    String updateCartQuantityProduct(Long cartId, Long productId, Integer quantity);

    CartDTO getCartByUserId(Long userId);

    String deleteProductFromCart(Long userId, Long productId);

    String deleteProductFromCartAll(Long userId, List<Long> productIds);

    String clearCart(Long userId);
}
