package com.ndc.be.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.ndc.be.domain.Category;
import com.ndc.be.domain.request.CreateCategoryDTO;
import com.ndc.be.domain.request.UpdateCategoryDTO;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    // Ánh xạ từ CreateCategoryDTO sang Category
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "supplierCategories", ignore = true)
    Category toEntity(CreateCategoryDTO dto);
    
    // Cập nhật Category từ UpdateCategoryDTO
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "supplierCategories", ignore = true)
    void updateEntityFromDto(UpdateCategoryDTO dto, @MappingTarget Category category);
} 