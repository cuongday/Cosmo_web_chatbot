package com.ndc.be.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.ndc.be.domain.Product;
import com.ndc.be.domain.request.CreateProductDTO;
import com.ndc.be.domain.request.UpdateProductDTO;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    // Ánh xạ từ CreateProductDTO sang Product
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "sellPrice", target = "sellPrice")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "cartDetails", ignore = true)
    Product toEntity(CreateProductDTO dto);
    
    // Cập nhật Product từ UpdateProductDTO
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "sellPrice", target = "sellPrice")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "cartDetails", ignore = true)
    void updateEntityFromDto(UpdateProductDTO dto, @MappingTarget Product product);
} 