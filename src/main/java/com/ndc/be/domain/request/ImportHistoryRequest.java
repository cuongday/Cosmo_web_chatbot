package com.ndc.be.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportHistoryRequest {
    @NotNull(message = "ID nhà cung cấp không được để trống")
    private Long supplierId;

    @Valid
    private List<ImportDetailRequest> importDetails;
} 