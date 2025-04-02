package com.ndc.be.domain.response;

import com.ndc.be.domain.Order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Order order;
    private String paymentUrl;
}
