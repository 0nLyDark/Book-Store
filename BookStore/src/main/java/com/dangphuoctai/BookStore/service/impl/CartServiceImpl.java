package com.dangphuoctai.BookStore.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.dangphuoctai.BookStore.entity.Cart;
import com.dangphuoctai.BookStore.entity.CartItem;
import com.dangphuoctai.BookStore.entity.Product;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.CartDTO;
import com.dangphuoctai.BookStore.payloads.response.CartResponse;
import com.dangphuoctai.BookStore.repository.CartItemRepo;
import com.dangphuoctai.BookStore.repository.CartRepo;
import com.dangphuoctai.BookStore.repository.ProductRepo;
import com.dangphuoctai.BookStore.service.CartService;

import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long cartId, Long productId, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        Cart cart = cartRepo.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
        if (cart.getUserId() != userId) {
            throw new APIException("You are not authorized to add product to this cart");
        }

        boolean check = cart.getCartItems().stream()
                .anyMatch(cartItem -> cartItem.getProduct().getProductId().equals(productId));
        if (check) {
            throw new APIException("Product already exists in the cart");
        }
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setCart(cart);
        cart.getCartItems().add(cartItem);
        cart.setTotalPrice(cart.getTotalPrice() + (product.getPrice() * quantity));

        cartItemRepo.save(cartItem);
        cartRepo.save(cart);

        return modelMapper.map(cart, CartDTO.class);
    }

    @Override
    public CartDTO updateCartQuantityProduct(Long cartId, Long productId, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        if (quantity <= 0) {
            throw new APIException("Quantity must be greater than 0");
        }
        CartItem cartItem = cartItemRepo.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "cartId and productId",
                        cartId + "" + productId));
        Product product = cartItem.getProduct();
        Cart cart = cartItem.getCart();
        if (cart.getUserId() != userId) {
            throw new APIException("You are not authorized to add product to this cart");
        }

        Double newPrice = cart.getTotalPrice() - product.getPrice() * cartItem.getQuantity()
                + product.getPrice() * quantity;
        cartItem.setQuantity(quantity);
        cart.setTotalPrice(newPrice);

        cartItemRepo.save(cartItem);
        cartRepo.save(cart);

        return modelMapper.map(cart, CartDTO.class);
    }

    @Override
    public CartDTO getCartById(Long cartId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String role = jwt.getClaim("scope");
        boolean isAdmin = role.contains("ADMIN");
        Cart cart = cartRepo.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
        if (cart.getUserId() != userId && !isAdmin) {
            throw new APIException("You are not authorized to add product to this cart");
        }

        return modelMapper.map(cart, CartDTO.class);
    }

    @Override
    public CartResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Cart> pageCarts = cartRepo.findAll(pageDetails);
        List<CartDTO> cartDTOs = pageCarts.getContent().stream()
                .map(cart -> modelMapper.map(cart, CartDTO.class)).toList();

        CartResponse cartResponse = new CartResponse();
        cartResponse.setContent(cartDTOs);
        cartResponse.setPageNumber(pageCarts.getNumber());
        cartResponse.setPageSize(pageCarts.getSize());
        cartResponse.setTotalElements(pageCarts.getTotalElements());
        cartResponse.setTotalPages(pageCarts.getTotalPages());
        cartResponse.setLastPage(pageCarts.isLast());

        return cartResponse;
    }

    @Override
    public CartDTO deleteProductFromCart(Long cartId, Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String role = jwt.getClaim("scope");
        boolean isAdmin = role.contains("ADMIN");
        CartItem cartItem = cartItemRepo.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "cartId and productId",
                        cartId + "" + productId));
        Cart cart = cartItem.getCart();
        if (cart.getUserId() != userId && !isAdmin) {
            throw new APIException("You are not authorized to add product to this cart");
        }

        Product product = cartItem.getProduct();
        Double newPrice = cart.getTotalPrice() - product.getPrice() * cartItem.getQuantity();
        cartItem.setCart(null);
        cart.setTotalPrice(newPrice);
        cart.getCartItems().remove(cartItem);

        cartItemRepo.save(cartItem);
        cartRepo.save(cart);

        return modelMapper.map(cart, CartDTO.class);
    }

    @Override
    public CartDTO deleteProductFromCartAll(Long cartId, List<Long> productIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String role = jwt.getClaim("scope");
        boolean isAdmin = role.contains("ADMIN");
        Cart cart = cartRepo.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
        if (cart.getUserId() != userId && !isAdmin) {
            throw new APIException("You are not authorized to add product to this cart");
        }
        List<CartItem> cartItems = cart.getCartItems();
        List<CartItem> cartItemRemove = cartItems.stream()
                .filter(cartItem -> productIds.contains(cartItem.getProduct().getProductId()))
                .map(cartItem -> {
                    cartItem.setCart(null);
                    return cartItem;
                })
                .collect(Collectors.toList());
        cartItems.removeAll(cartItemRemove);
        double newPrice = cartItems.stream()
                .mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .sum();
        cart.setTotalPrice(newPrice);

        cartItemRepo.deleteAll(cartItemRemove);
        cartRepo.save(cart);

        return modelMapper.map(cart, CartDTO.class);
    }

    @Override
    public String clearCart(Long cartId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String role = jwt.getClaim("scope");
        boolean isAdmin = role.contains("ADMIN");
        Cart cart = cartRepo.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
        if (cart.getUserId() != userId && !isAdmin) {
            throw new APIException("You are not authorized to add product to this cart");
        }
        cart.setTotalPrice(0.0);
        for (CartItem item : cart.getCartItems()) {
            item.setCart(null); // tách liên kết
        }
        cart.getCartItems().clear(); // xóa tất cả các sản phẩm trong giỏ hàng

        cartRepo.save(cart);

        return "Clear Cart successfully";

    }
}
