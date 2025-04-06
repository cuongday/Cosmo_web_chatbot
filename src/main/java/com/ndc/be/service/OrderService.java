package com.ndc.be.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndc.be.domain.Order;
import com.ndc.be.domain.OrderDetail;
import com.ndc.be.domain.Product;
import com.ndc.be.domain.User;
import com.ndc.be.domain.CartDetail;
import com.ndc.be.domain.request.CreateOrderDTO;
// import com.ndc.be.domain.request.OrderItemDTO;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.repository.OrderDetailRepository;
import com.ndc.be.repository.OrderRepository;
import com.ndc.be.repository.ProductRepository;
import com.ndc.be.repository.UserRepository;
import com.ndc.be.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Transactional
    public Order createOrder(CreateOrderDTO createOrderDTO) {
        // Lấy thông tin người dùng hiện tại
        String currentUsername = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại"));
        User currentUser = userRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new RuntimeException("Không tìm thấy người dùng hiện tại");
        }
        
        // Lấy danh sách sản phẩm từ giỏ hàng
        List<CartDetail> cartItems = cartService.getCartItems();
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng của bạn đang trống, không thể tạo đơn hàng");
        }
        
        // Tính tổng giá trị đơn hàng
        long totalPrice = 0;
        for (CartDetail item : cartItems) {
            Product product = item.getProduct();
            // Sử dụng giá bán từ sản phẩm
            long itemPrice = product.getSellPrice() * item.getQuantity();
            
            // Debug log
            System.out.println("Product ID: " + product.getId() + 
                               ", SellPrice: " + product.getSellPrice() + 
                               ", Quantity: " + item.getQuantity() + 
                               ", ItemTotal: " + itemPrice);
            
            totalPrice += itemPrice;
        }
        
        // Debug tổng giá trị
        System.out.println("Final Total Price: " + totalPrice);
        
        // Tạo và lưu đơn hàng
        Order order = Order.builder()
                .paymentMethod(createOrderDTO.getPaymentMethod())
                .totalPrice(totalPrice)
                .user(currentUser)
                .phone(createOrderDTO.getPhone())
                .address(createOrderDTO.getAddress())
                .createdAt(Instant.now())
                .createdBy(currentUsername)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        // Tạo và lưu chi tiết đơn hàng
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartDetail item : cartItems) {
            Product product = item.getProduct();
            
            // Cập nhật số lượng tồn kho
            int currentStock = product.getQuantity();
            if (currentStock < item.getQuantity()) {
                throw new RuntimeException("Số lượng sản phẩm " + product.getName() + " không đủ");
            }
            product.setQuantity(currentStock - item.getQuantity());
            productRepository.save(product);
            
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(savedOrder)
                    .product(product)
                    .price(product.getSellPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(product.getSellPrice() * item.getQuantity())
                    .createdAt(Instant.now())
                    .createdBy(currentUsername)
                    .build();
            
            orderDetails.add(orderDetailRepository.save(orderDetail));
        }
        
        // Xóa giỏ hàng sau khi đặt hàng thành công
        cartService.clearCart();
        
        return savedOrder;
    }
    
    /**
     * Update an existing order
     * 
     * @param order Order to update
     * @return Updated order
     */
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public ResultPaginationDTO getAllOrders(Specification<Order> orderSpec, Pageable pageable) {
        Page<Order> orderPage = this.orderRepository.findAll(orderSpec, pageable);
        
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(orderPage.getNumber() + 1);
        meta.setPageSize(orderPage.getSize());
        meta.setPages(orderPage.getTotalPages());
        meta.setTotal(orderPage.getTotalElements());
        
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(orderPage.getContent());
        
        return resultPaginationDTO;
    }
    
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
    
    public List<OrderDetail> getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        return order.getOrderDetails();
    }
} 