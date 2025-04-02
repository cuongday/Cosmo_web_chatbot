package com.ndc.be.domain.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportDetailRequest {
    @NotNull(message = "ID sản phẩm không được để trống")
    private Long productId;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Min(value = 0, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")
    private Long price;

    @NotNull(message = "Số lượng sản phẩm không được để trống")
    @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Tổng giá sản phẩm không được để trống")
    @Min(value = 0, message = "Tổng giá sản phẩm phải lớn hơn hoặc bằng 0")
    private Long totalPrice;
} 