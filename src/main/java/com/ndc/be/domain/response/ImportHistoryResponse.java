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
public class ImportHistoryResponse {
    private Long id;
    private UserSimpleResponse user;
    private SupplierSimpleResponse supplier;
    private List<ImportDetailResponse> importDetails;
    private Long totalPrice;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
} 