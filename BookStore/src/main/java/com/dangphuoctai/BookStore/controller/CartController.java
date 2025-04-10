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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.payloads.dto.CartDTO;
import com.dangphuoctai.BookStore.payloads.dto.CartRequestDTO;
import com.dangphuoctai.BookStore.payloads.response.CartResponse;
import com.dangphuoctai.BookStore.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/public/carts/{cartId}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable Long cartId) {
        CartDTO cartDTO = cartService.getCartById(cartId);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/carts")
    public ResponseEntity<CartResponse> getAllCarts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CARTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        CartResponse cartResponse = cartService.getAllCarts(
                pageNumber == 0 ? pageNumber : pageNumber - 1,
                pageSize,
                "id".equals(sortBy) ? "cartId" : sortBy,
                sortOrder);

        return new ResponseEntity<CartResponse>(cartResponse, HttpStatus.OK);
    }

    @PostMapping("/public/carts")
    public ResponseEntity<CartDTO> addProductToCart(@RequestBody CartRequestDTO cart) {
        CartDTO cartDTO = cartService.addProductToCart(cart.getCartId(), cart.getProductId(), cart.getQuantity());

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    @PutMapping("/public/carts")
    public ResponseEntity<CartDTO> updateCart(@RequestBody CartRequestDTO cart) {
        CartDTO cartDTO = cartService.updateCartQuantityProduct(cart.getCartId(), cart.getProductId(),
                cart.getQuantity());

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/public/carts/{cartId}/product")
    public ResponseEntity<CartDTO> deleteProductFromCart(@PathVariable Long cartId, @RequestBody Long productId) {
        CartDTO cartDTO = cartService.deleteProductFromCart(cartId, productId);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/public/carts/{cartId}/products")
    public ResponseEntity<CartDTO> deleteProductFromCart(@PathVariable Long cartId,
            @RequestBody List<Long> productIds) {
        CartDTO cartDTO = cartService.deleteProductFromCartAll(cartId, productIds);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/public/carts/{cartId}")
    public ResponseEntity<String> deleteCart(@PathVariable Long cartId) {
        String result = cartService.clearCart(cartId);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
