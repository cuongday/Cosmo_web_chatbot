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

import com.ndc.be.domain.Customer;
import com.ndc.be.domain.Order;
import com.ndc.be.domain.OrderDetail;
import com.ndc.be.domain.Product;
import com.ndc.be.domain.User;
import com.ndc.be.domain.request.CreateOrderDTO;
import com.ndc.be.domain.request.OrderItemDTO;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.repository.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    @Transactional
    public Order createOrder(CreateOrderDTO createOrderDTO) {
        // Get current user
        String currentUsername = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại"));
        User currentUser = userRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new RuntimeException("Không tìm thấy người dùng hiện tại");
        }
        
        // Get customer if provided
        Customer customer = null;
        if (createOrderDTO.getCustomerId() != null) {
            customer = customerRepository.findById(createOrderDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        }
        
        // Pre-load all products to avoid multiple database calls
        List<Long> productIds = createOrderDTO.getItems().stream()
                .map(OrderItemDTO::getProductId)
                .toList();
                
        List<Product> products = productRepository.findAllById(productIds);
        
        // Create a map for quick access to products
        var productMap = products.stream()
                .collect(java.util.stream.Collectors.toMap(Product::getId, p -> p));
        
        // Calculate total price using prices from frontend (OrderItemDTO)
        long totalPrice = 0;
        for (OrderItemDTO item : createOrderDTO.getItems()) {
            Product product = productMap.get(item.getProductId());
            if (product == null) {
                throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + item.getProductId());
            }
            // Use the sellPrice from OrderItemDTO instead of product
            long itemPrice = item.getSellPrice() * item.getQuantity();
            
            // Debug log for price calculation
            System.out.println("Product ID: " + item.getProductId() + 
                               ", SellPrice: " + item.getSellPrice() + 
                               ", Quantity: " + item.getQuantity() + 
                               ", ItemTotal: " + itemPrice);
            
            totalPrice += itemPrice;
        }
        
        // Debug total price
        System.out.println("Final Total Price: " + totalPrice);
        
        // Create and save order
        Order order = Order.builder()
                .paymentMethod(createOrderDTO.getPaymentMethod())
                .totalPrice(totalPrice)
                .user(currentUser)
                .customer(customer)
                .createdAt(Instant.now())
                .createdBy(currentUsername)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        // Create and save order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderItemDTO item : createOrderDTO.getItems()) {
            Product product = productMap.get(item.getProductId());
            
            // Update product stock
            int currentStock = product.getQuantity();
            if (currentStock < item.getQuantity()) {
                throw new RuntimeException("Số lượng sản phẩm " + product.getName() + " không đủ");
            }
            product.setQuantity(currentStock - item.getQuantity());
            productRepository.save(product);
            
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(savedOrder)
                    .product(product)
                    // Use sellPrice from OrderItemDTO
                    .price(item.getSellPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(item.getSellPrice() * item.getQuantity())
                    .createdAt(Instant.now())
                    .createdBy(currentUsername)
                    .build();
            
            orderDetails.add(orderDetailRepository.save(orderDetail));
        }
        
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