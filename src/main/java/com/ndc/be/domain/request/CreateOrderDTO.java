package com.ndc.be.domain.request;

import java.util.List;

import com.ndc.be.util.constant.PaymentMethod;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethod paymentMethod;
    
    private Long customerId;
    
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<OrderItemDTO> items;
} 