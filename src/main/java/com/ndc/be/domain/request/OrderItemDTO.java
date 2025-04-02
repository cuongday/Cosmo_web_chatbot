package com.ndc.be.domain.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    @NotNull(message = "Mã sản phẩm không được để trống")
    private Long productId;
    
    @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0")
    private int quantity;
    
    @Min(value = 0, message = "Giá bán sản phẩm phải lớn hơn hoặc bằng 0")
    private long sellPrice;
} 