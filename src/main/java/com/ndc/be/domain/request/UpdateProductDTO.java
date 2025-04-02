package com.ndc.be.domain.request;

import java.time.Instant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDTO {
    @NotNull
    private Long id;
    
    @NotNull
    @Size(min = 2, message = "Tên phải có ít nhất 2 ký tự")
    private String name;

    private String description;

    @Min(value = 0, message = "Giá mua sản phẩm phải lớn hơn hoặc bằng 0")
    private Long buyPrice;
    
    @Min(value = 0, message = "Giá bán sản phẩm phải lớn hơn hoặc bằng 0")
    private Long sellPrice;

    private Integer quantity;
    private Instant date;
    private String status;
    
    @NotNull(message = "Nhà cung cấp không được để trống")
    private Long supplierId;
    
    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;
} 