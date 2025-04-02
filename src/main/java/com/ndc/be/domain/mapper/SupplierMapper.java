package com.ndc.be.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.ndc.be.domain.Supplier;
import com.ndc.be.domain.request.CreateSupplierDTO;
import com.ndc.be.domain.request.UpdateSupplierDTO;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    
    // Ánh xạ từ CreateSupplierDTO sang Supplier, bỏ qua trường image
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "supplierCategories", ignore = true)
    @Mapping(target = "importHistories", ignore = true)
    @Mapping(target = "products", ignore = true)
    Supplier toEntity(CreateSupplierDTO dto);
    
    // Cập nhật Supplier từ UpdateSupplierDTO
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "supplierCategories", ignore = true)
    @Mapping(target = "importHistories", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntityFromDto(UpdateSupplierDTO dto, @MappingTarget Supplier supplier);
}