package com.ndc.be.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportDetailResponse {
    private Long id;
    private Long price;
    private Integer quantity;
    private Long totalPrice;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long importHistoryId;
    private ProductResponse product;
} 