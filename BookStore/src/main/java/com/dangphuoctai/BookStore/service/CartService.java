package com.dangphuoctai.BookStore.service;

import java.util.List;

import com.dangphuoctai.BookStore.payloads.dto.CartDTO;
import com.dangphuoctai.BookStore.payloads.response.CartResponse;

public interface CartService {

    CartDTO addProductToCart(Long cartId, Long productId, Integer quantity);

    CartDTO updateCartQuantityProduct(Long cartId, Long productId, Integer quantity);

    CartDTO getCartById(Long cartId);

    CartResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CartDTO deleteProductFromCart(Long cartId, Long productId);

    CartDTO deleteProductFromCartAll(Long cartId, List<Long> productIds);

    String clearCart(Long cartId);
}
