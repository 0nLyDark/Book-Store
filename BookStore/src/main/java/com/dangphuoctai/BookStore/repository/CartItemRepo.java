package com.dangphuoctai.BookStore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dangphuoctai.BookStore.entity.CartItem;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

    @Query("SELECT c FROM CartItem c WHERE c.cart.cartId = :cartId AND c.product.productId = :productId")
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cartItemId = :cartItemId")
    void deleteByCartItemId(Long cartItemId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cartItemId IN :cartItemIds")
    void deleteByCartItemIds(List<Long> cartItemIds);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cart.cartId = :cartId")
    void deleteAllByCartId(Long cartId);

}
