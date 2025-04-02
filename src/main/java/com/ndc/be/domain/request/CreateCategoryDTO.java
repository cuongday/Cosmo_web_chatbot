package com.ndc.be.domain.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryDTO {
    @Size(min = 2, message = "Tên danh mục phải có ít nhất 2 ký tự")
    private String name;
    
    private String description;
} 