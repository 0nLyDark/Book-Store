package com.dangphuoctai.BookStore.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.CartDTO;
import com.dangphuoctai.BookStore.payloads.dto.CartItemDTO;
import com.dangphuoctai.BookStore.payloads.dto.ProductDTO;
import com.dangphuoctai.BookStore.repository.ProductRepo;
import com.dangphuoctai.BookStore.service.BaseRedisService;
import com.dangphuoctai.BookStore.service.CartService;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseRedisService<String, Long, Integer> cartRedisService;

    private static final String CART_CACHE_KEY = "cart";

    @Override
    public CartDTO addProductToCart(Long userId, Long productId, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long id = jwt.getClaim("userId");
        String key = CART_CACHE_KEY + ":user:" + userId;
        if (id != userId) {
            throw new APIException("You are not authorized to add product to this cart");
        }
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        Integer quantityInCart = cartRedisService.hashGet(key, productId);
        if (quantityInCart != null) {
            quantity += quantityInCart;
        }
        if (product.getQuantity() < quantity) {
            throw new APIException("Product quantity is not sufficient to add to the cart");
        }
        // Save cart to redis
        cartRedisService.hashSet(key, productId, quantity);
        cartRedisService.setTimeToLive(key, 30, TimeUnit.DAYS);
        CartDTO cartDTO = mapToCartDTO(key);

        return cartDTO;
    }

    @Override
    public CartDTO updateCartQuantityProduct(Long userId, Long productId, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long id = jwt.getClaim("userId");
        String key = CART_CACHE_KEY + ":user:" + userId;
        if (id != userId) {
            throw new APIException("You are not authorized to add product to this cart");
        }
        if (quantity <= 0) {
            throw new APIException("Quantity must be greater than 0");
        }
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        if (product.getQuantity() < quantity) {
            throw new APIException("Product quantity is not sufficient to add to the cart");
        }
        // Update cart in redis
        cartRedisService.hashSet(key, productId, quantity);
        cartRedisService.setTimeToLive(key, 30, TimeUnit.DAYS);

        CartDTO cartDTO = mapToCartDTO(key);

        return cartDTO;
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long id = jwt.getClaim("userId");
        String role = jwt.getClaim("scope");
        boolean isAdmin = role.contains("ADMIN");
        String key = CART_CACHE_KEY + ":user:" + userId;
        if (id != userId && !isAdmin) {
            throw new APIException("You are not authorized to add product to this cart");
        }

        CartDTO cartDTO = mapToCartDTO(key);

        return cartDTO;
    }

    @Override
    public CartDTO deleteProductFromCart(Long userId, Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long id = jwt.getClaim("userId");
        String role = jwt.getClaim("scope");
        boolean isAdmin = role.contains("ADMIN");
        String key = CART_CACHE_KEY + ":user:" + userId;
        if (id != userId && !isAdmin) {
            throw new APIException("You are not authorized to add product to this cart");
        }
        cartRedisService.delete(key, productId);
        cartRedisService.setTimeToLive(key, 30, TimeUnit.DAYS);
        CartDTO cartDTO = mapToCartDTO(key);

        return cartDTO;
    }

    @Override
    public CartDTO deleteProductFromCartAll(Long userId, List<Long> productIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long id = jwt.getClaim("userId");
        String role = jwt.getClaim("scope");
        boolean isAdmin = role.contains("ADMIN");
        String key = CART_CACHE_KEY + ":user:" + userId;
        if (id != userId && !isAdmin) {
            throw new APIException("You are not authorized to add product to this cart");
        }
        cartRedisService.delete(key, productIds);
        cartRedisService.setTimeToLive(key, 30, TimeUnit.DAYS);
        CartDTO cartDTO = mapToCartDTO(key);

        return cartDTO;
    }

    @Override
    public String clearCart(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long id = jwt.getClaim("userId");
        String role = jwt.getClaim("scope");
        boolean isAdmin = role.contains("ADMIN");
        String key = CART_CACHE_KEY + ":user:" + userId;
        if (id != userId && !isAdmin) {
            throw new APIException("You are not authorized to add product to this cart");
        }
        cartRedisService.delete(key);

        return "Clear Cart successfully";
    }

    private CartDTO mapToCartDTO(String key) {
        CartDTO cartDTO = new CartDTO();
        Map<Long, Integer> cartItems = cartRedisService.getField(key);
        List<CartItemDTO> cartItemList = cartItems.entrySet().stream()
                .map(entry -> {
                    Long productIdKey = ((Number) entry.getKey()).longValue();
                    Integer quantityValue = entry.getValue();
                    Product productItem = productRepo.findById(productIdKey)
                            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productIdKey));
                    ProductDTO productDTO = modelMapper.map(productItem, ProductDTO.class);
                    CartItemDTO cartItem = new CartItemDTO();
                    cartItem.setProduct(productDTO);
                    cartItem.setQuantity(quantityValue);
                    return cartItem;
                }).collect(Collectors.toList());
        Double totalPrice = cartItemList.stream()
                .mapToDouble(item -> {
                    ProductDTO productItem = item.getProduct();
                    return productItem.getPrice() * item.getQuantity() * (100 - productItem.getDiscount()) / 100;
                }).sum();
        cartDTO.setCartItems(cartItemList);
        cartDTO.setTotalPrice(totalPrice);

        return cartDTO;
    }
}
