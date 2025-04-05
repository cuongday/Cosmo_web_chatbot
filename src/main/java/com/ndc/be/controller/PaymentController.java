package com.ndc.be.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.ndc.be.domain.Order;
import com.ndc.be.service.OrderService;
import com.ndc.be.service.VNPayService;
import com.ndc.be.util.annotation.ApiMessage;
import com.ndc.be.util.constant.PaymentMethod;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final VNPayService vnPayService;
    private final OrderService orderService;
    
    @Value("${frontend.payment-result-url:http://localhost:3000/payment-result}")
    private String frontendPaymentResultUrl;

    @GetMapping("/create-payment/{orderId}")
    @ApiMessage("Tạo URL thanh toán cho đơn hàng thành công")
    public ResponseEntity<Map<String, String>> createPayment(@PathVariable Long orderId) {
        Optional<Order> orderOpt = this.orderService.getOrderById(orderId);
        if (orderOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Không tìm thấy đơn hàng");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Order order = orderOpt.get();
        // Only allow TRANSFER payment method
        if (order.getPaymentMethod() != PaymentMethod.TRANSFER) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Phương thức thanh toán không hợp lệ. Chỉ chấp nhận TRANSFER");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Generate payment URL
        String orderInfo = "Thanh toan don hang: " + orderId;
        String paymentUrl = this.vnPayService.createPaymentUrl(orderId, order.getTotalPrice(), orderInfo);

        // Update order with payment URL
        order.setPaymentUrl(paymentUrl);
        orderService.updateOrder(order);

        // Return payment URL
        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public RedirectView vnpayReturn(@RequestParam Map<String, String> params) {
        log.info("VNPay return with params: {}", params);
        
        // Process VNPay return
        Optional<Order> processedOrder = vnPayService.processPaymentReturn(params);
        
        // Redirect URL depends on payment result
        String redirectUrl;
        
        if (processedOrder.isPresent()) {
            // Payment successful - chỉ redirect về trang chỉ định
            Order order = processedOrder.get();
            redirectUrl = frontendPaymentResultUrl + "?status=success&orderId=" + order.getId();
        } else {
            // Payment failed
            String orderId = params.getOrDefault("vnp_TxnRef", "unknown");
            String errorMessage = "Thanh toán không thành công";
            
            try {
                redirectUrl = frontendPaymentResultUrl + "?status=failed&orderId=" + orderId + 
                        "&message=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                redirectUrl = frontendPaymentResultUrl + "?status=failed&orderId=" + orderId;
                log.error("Error encoding error message", e);
            }
        }
        
        return new RedirectView(redirectUrl);
    }
    
    /**
     * API trả về thông tin thanh toán của đơn hàng
     */
    @GetMapping("/payment-info/{orderId}")
    @ApiMessage("Lấy thông tin thanh toán đơn hàng thành công")
    public ResponseEntity<Map<String, Object>> getPaymentInfo(@PathVariable Long orderId) {
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không tìm thấy đơn hàng");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        Order order = orderOpt.get();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("orderId", order.getId());
        response.put("paymentStatus", order.getPaymentStatus());
        response.put("paymentMethod", order.getPaymentMethod());
        response.put("paymentMessage", order.getPaymentMessage());
        response.put("totalPrice", order.getTotalPrice());
        response.put("transactionNo", order.getTransactionNo());
        
        return ResponseEntity.ok(response);
    }
} 