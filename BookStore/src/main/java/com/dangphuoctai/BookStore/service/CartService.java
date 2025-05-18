package com.dangphuoctai.BookStore.service;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.CartDTO;
import com.dangphuoctai.BookStore.payloads.response.CartResponse;

public interface CartService {

    CartDTO addProductToCart(Long userId, Long productId, Integer quantity);

    CartDTO updateCartQuantityProduct(Long cartId, Long productId, Integer quantity);

    CartDTO getCartByUserId(Long userId);

    CartDTO deleteProductFromCart(Long userId, Long productId);

    CartDTO deleteProductFromCartAll(Long userId, List<Long> productIds);

    String clearCart(Long userId);
}
