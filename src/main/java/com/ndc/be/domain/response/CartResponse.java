package com.ndc.be.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long id;
    private UserSimpleResponse user;
    private List<CartDetailResponse> cartDetails;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
} 