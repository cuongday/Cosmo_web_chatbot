package com.ndc.be.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndc.be.domain.Cart;
import com.ndc.be.domain.CartDetail;
import com.ndc.be.domain.Product;
import com.ndc.be.domain.User;
import com.ndc.be.repository.CartDetailRepository;
import com.ndc.be.repository.CartRepository;
import com.ndc.be.repository.ProductRepository;
// import com.ndc.be.util.SecurityUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Transactional
    public Cart getCurrentUserCart() {
        User currentUser = this.userService.getCurrentUser();
        List<Cart> userCarts = cartRepository.findByUser(currentUser);
        
        if (userCarts.isEmpty()) {
            // Tạo giỏ hàng mới nếu người dùng chưa có
            Cart newCart = Cart.builder()
                    .user(currentUser)
                    .build();
            return cartRepository.save(newCart);
        }
        
        // Lấy giỏ hàng đầu tiên (thường chỉ có một giỏ hàng cho mỗi người dùng)
        return userCarts.get(0);
    }

    @Transactional
    public CartDetail addProductToCart(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm phải lớn hơn 0");
        }
        
        // Lấy giỏ hàng hiện tại
        Cart cart = getCurrentUserCart();
        
        // Lấy thông tin sản phẩm
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));
        
        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        List<CartDetail> cartDetails = cartDetailRepository.findByCart(cart);
        Optional<CartDetail> existingCartDetail = cartDetails.stream()
                .filter(detail -> detail.getProduct().getId().equals(productId))
                .findFirst();
        
        if (existingCartDetail.isPresent()) {
            // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
            CartDetail detail = existingCartDetail.get();
            detail.setQuantity(detail.getQuantity() + quantity);
            return cartDetailRepository.save(detail);
        } else {
            // Nếu sản phẩm chưa có trong giỏ hàng, thêm mới
            CartDetail newDetail = CartDetail.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            return cartDetailRepository.save(newDetail);
        }
    }

    @Transactional
    public CartDetail updateCartItemQuantity(Long cartDetailId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm không thể âm");
        }
        
        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chi tiết giỏ hàng với ID: " + cartDetailId));
        
        if (quantity == 0) {
            // Nếu số lượng = 0, xóa sản phẩm khỏi giỏ hàng
            cartDetailRepository.delete(cartDetail);
            return null;
        } else {
            // Cập nhật số lượng
            cartDetail.setQuantity(quantity);
            return cartDetailRepository.save(cartDetail);
        }
    }

    @Transactional
    public void removeCartItem(Long cartDetailId) {
        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chi tiết giỏ hàng với ID: " + cartDetailId));
        
        cartDetailRepository.delete(cartDetail);
    }

    public List<CartDetail> getCartItems() {
        Cart cart = getCurrentUserCart();
        return cartDetailRepository.findByCart(cart);
    }

    @Transactional
    public void clearCart() {
        Cart cart = getCurrentUserCart();
        List<CartDetail> cartDetails = cartDetailRepository.findByCart(cart);
        cartDetailRepository.deleteAll(cartDetails);
    }
} 