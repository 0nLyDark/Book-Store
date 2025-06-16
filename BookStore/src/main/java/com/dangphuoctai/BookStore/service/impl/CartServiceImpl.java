package com.dangphuoctai.BookStore.service.impl;

import java.util.ArrayList;
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
    public String addProductToCart(Long userId, Long productId, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long id = jwt.getClaim("userId");
        String key = CART_CACHE_KEY + ":user:" + userId;
        if (id != userId) {
            throw new APIException("Bạn không có quyền thêm sản phẩm vào giỏ hàng này");
        }
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        Integer quantityInCart = cartRedisService.hashGet(key, productId);
        if (quantityInCart != null) {
            quantity += quantityInCart;
        }
        if (product.getQuantity() < quantity) {
            throw new APIException("Số lượng sản phẩm không đủ để thêm vào giỏ hàng");
        }
        // Save cart to redis
        cartRedisService.hashSet(key, productId, quantity);
        cartRedisService.setTimeToLive(key, 30, TimeUnit.DAYS);

        return "Thêm sản phẩm vào giỏ hàng thành công";
    }

    @Override
    public String updateCartQuantityProduct(Long userId, Long productId, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long id = jwt.getClaim("userId");
        String key = CART_CACHE_KEY + ":user:" + userId;
        if (id != userId) {
            throw new APIException("Bạn không có quyền cập nhật sản phẩm trong giỏ hàng này");
        }
        if (quantity <= 0) {
            throw new APIException("Số lượng phải lớn hơn 0");
        }
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", "id", productId));
        if (product.getQuantity() < quantity) {
            throw new APIException("Số lượng sản phẩm không đủ để cập nhật trong giỏ hàng");
        }
        // Update cart in redis
        cartRedisService.hashSet(key, productId, quantity);
        cartRedisService.setTimeToLive(key, 30, TimeUnit.DAYS);

        return "Cập nhật số lượng thành công";
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
            throw new APIException("Bạn không có quyền truy cập giỏ hàng này");
        }

        CartDTO cartDTO = mapToCartDTO(key);

        return cartDTO;
    }

    @Override
    public String deleteProductFromCart(Long userId, Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long id = jwt.getClaim("userId");
        String role = jwt.getClaim("scope");
        boolean isAdmin = role.contains("ADMIN");
        String key = CART_CACHE_KEY + ":user:" + userId;
        if (id != userId && !isAdmin) {
            throw new APIException("Bạn không có quyền xóa sản phẩm khỏi giỏ hàng này");
        }
        cartRedisService.delete(key, productId);
        cartRedisService.setTimeToLive(key, 30, TimeUnit.DAYS);

        return "Xóa sản phẩm theo khỏi giỏ hàng thành công";
    }

    @Override
    public String deleteProductFromCartAll(Long userId, List<Long> productIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long id = jwt.getClaim("userId");
        String role = jwt.getClaim("scope");
        boolean isAdmin = role.contains("ADMIN");
        String key = CART_CACHE_KEY + ":user:" + userId;
        if (id != userId && !isAdmin) {
            throw new APIException("Bạn không có quyền xóa sản phẩm khỏi giỏ hàng này");
        }
        cartRedisService.delete(key, productIds);
        cartRedisService.setTimeToLive(key, 30, TimeUnit.DAYS);

        return "Xóa sản phẩm theo khỏi giỏ hàng thành công";
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
            throw new APIException("Bạn không có quyền xóa giỏ hàng này");
        }
        cartRedisService.delete(key);

        return "Xóa toàn bộ giỏ hàng thành công";
    }

    private CartDTO mapToCartDTO(String key) {
        CartDTO cartDTO = new CartDTO();
        Map<Long, Integer> cartItems = cartRedisService.getField(key);
        List<CartItemDTO> cartItemList = cartItems.entrySet().stream()
                .map((Map.Entry<Long, Integer> entry) -> {
                    Long productIdKey = ((Number) entry.getKey()).longValue();
                    Integer quantityValue = entry.getValue();
                    Product product = productRepo.findById(productIdKey).orElse(null);
                    if (product == null) {
                        cartRedisService.delete(key, productIdKey);
                        return null;
                    }
                    ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                    CartItemDTO cartItem = new CartItemDTO();
                    cartItem.setProduct(productDTO);
                    cartItem.setQuantity(quantityValue);
                    // Optionally, you can set more product info to cartItem if needed
                    return cartItem;
                }).filter(item -> item != null).collect(Collectors.toList());
        Double total = cartItemList.stream().mapToDouble(item -> {
            ProductDTO productDTO = item.getProduct();
            return item.getQuantity() * productDTO.getPrice() * (100 - productDTO.getDiscount()) / 100;
        }).sum();

        cartDTO.setCartItems(cartItemList);
        cartDTO.setTotalPrice(total);

        return cartDTO;
    }
}
