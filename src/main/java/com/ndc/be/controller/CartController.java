package com.ndc.be.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ndc.be.domain.CartDetail;
import com.ndc.be.service.CartService;
import com.ndc.be.util.annotation.ApiMessage;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @ApiMessage("Thêm sản phẩm vào giỏ hàng")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> addProductToCart(@RequestBody Map<String, Object> request) {
        Long productId = Long.parseLong(request.get("productId").toString());
        int quantity = Integer.parseInt(request.get("quantity").toString());
        
        try {
            CartDetail cartDetail = cartService.addProductToCart(productId, quantity);
            return ResponseEntity.ok(cartDetail);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @ApiMessage("Cập nhật số lượng sản phẩm trong giỏ hàng")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/update/{cartDetailId}")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable Long cartDetailId,
            @RequestBody Map<String, Object> request) {
        int quantity = Integer.parseInt(request.get("quantity").toString());
        
        try {
            CartDetail cartDetail = cartService.updateCartItemQuantity(cartDetailId, quantity);
            return ResponseEntity.ok(cartDetail);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @ApiMessage("Xóa sản phẩm khỏi giỏ hàng")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/remove/{cartDetailId}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long cartDetailId) {
        try {
            cartService.removeCartItem(cartDetailId);
            return ResponseEntity.ok("Sản phẩm đã được xóa khỏi giỏ hàng");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @ApiMessage("Lấy danh sách sản phẩm trong giỏ hàng")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<CartDetail>> getCartItems() {
        List<CartDetail> cartItems = cartService.getCartItems();
        return ResponseEntity.ok(cartItems);
    }

    @ApiMessage("Xóa tất cả sản phẩm trong giỏ hàng")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok("Giỏ hàng đã được làm trống");
    }
} 