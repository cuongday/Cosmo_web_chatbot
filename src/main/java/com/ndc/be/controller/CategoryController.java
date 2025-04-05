package com.ndc.be.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import com.ndc.be.domain.Category;
import com.ndc.be.domain.mapper.CategoryMapper;
import com.ndc.be.domain.request.CreateCategoryDTO;
import com.ndc.be.domain.request.UpdateCategoryDTO;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.service.CategoryService;
import com.ndc.be.util.annotation.ApiMessage;
import com.ndc.be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PreAuthorize("hasAnyRole('admin', 'employee')")
    @GetMapping("")
    @ApiMessage("Lấy tất cả danh mục")
    public ResponseEntity<ResultPaginationDTO> getCategories(
        @Filter Specification<Category> categorySpec,
        Pageable pageable
    ) {
        ResultPaginationDTO rs = this.categoryService.handleGetCategory(categorySpec, pageable);
        return ResponseEntity.ok(rs);
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping(value = "")
    @ApiMessage("Tạo danh mục mới")
    public ResponseEntity<Category> createCategory(
        @Valid @ModelAttribute CreateCategoryDTO categoryDTO
    ) throws IdInvalidException {
        boolean isNameExist = this.categoryService.existsByName(categoryDTO.getName());
        if(isNameExist) {
            throw new IdInvalidException("Tên danh mục đã tồn tại");
        }
        
        Category category = this.categoryMapper.toEntity(categoryDTO);
        
        Category newCategory = this.categoryService.handleCreateCategory(category, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping(value = "/{id}")
    @ApiMessage("Cập nhật danh mục theo id")
    public ResponseEntity<Category> updateCategory(
        @PathVariable("id") Long id,
        @Valid @ModelAttribute UpdateCategoryDTO categoryDTO
    ) throws IdInvalidException {
        Category category = this.categoryService.fetchCategoryById(id);
        if(category == null) {
            throw new IdInvalidException("Danh mục với id = " + id + " không tồn tại");
        }   
        this.categoryMapper.updateEntityFromDto(categoryDTO, category);
        Category updatedCategory = this.categoryService.handleUpdateCategory(id, category, null);
        return ResponseEntity.ok(updatedCategory);
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    @ApiMessage("Xóa danh mục theo id")
    public ResponseEntity<Void> deleteCategory(
        @PathVariable("id") Long id
    ) throws IdInvalidException {
        Category currentCategory = this.categoryService.fetchCategoryById(id);
        if(currentCategory == null) {
            throw new IdInvalidException("Danh mục với id = " + id + " không tồn tại");
        }
        this.categoryService.handleDeleteCategory(id);
        return ResponseEntity.ok(null);
    }

    @PreAuthorize("hasAnyRole('admin', 'employee')")
    @GetMapping("/{id}")
    @ApiMessage("Lấy danh mục theo id")
    public ResponseEntity<Category> getCategoryById(
        @PathVariable("id") Long id
    ) throws IdInvalidException {
        Category category = this.categoryService.fetchCategoryById(id);
        if(category == null) {
            throw new IdInvalidException("Danh mục với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(category);
    }
}