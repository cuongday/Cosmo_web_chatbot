package com.ndc.be.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.ndc.be.domain.Category;
import com.ndc.be.domain.Supplier;
import com.ndc.be.domain.mapper.SupplierMapper;
import com.ndc.be.domain.request.CreateSupplierDTO;
import com.ndc.be.domain.request.UpdateSupplierDTO;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.service.CategoryService;
import com.ndc.be.service.SupplierService;
import com.ndc.be.util.annotation.ApiMessage;
import com.ndc.be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;
    private final SupplierMapper supplierMapper;
    private final CategoryService categoryService;

    @PreAuthorize("hasAnyRole('admin', 'employee')")
    @GetMapping("/suppliers")
    @ApiMessage("Get all suppliers")
    public ResponseEntity<ResultPaginationDTO> getSuppliers(
        @Filter Specification<Supplier> supplierSpec,
        Pageable pageable
    ) {
        ResultPaginationDTO rs = this.supplierService.handleGetSupplier(supplierSpec, pageable);
        return ResponseEntity.ok(rs);
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiMessage("Tạo nhà cung cấp thành công")
    public ResponseEntity<Supplier> createSupplier(
        @Valid @ModelAttribute CreateSupplierDTO supplierDTO,
        @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) throws IdInvalidException {
        boolean isNameExist = this.supplierService.existsByName(supplierDTO.getName());
        if(isNameExist) {
            throw new IdInvalidException("Tên nhà cung cấp đã tồn tại");
        }
        
        Supplier supplier = this.supplierMapper.toEntity(supplierDTO);
        
        Supplier newSupplier = this.supplierService.handleCreateSupplier(supplier, imageFile, supplierDTO.getCategoryIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(newSupplier);
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiMessage("Cập nhật nhà cung cấp thành công")
    public ResponseEntity<Supplier> updateSupplier(
        @PathVariable("id") Long id,
        @Valid @ModelAttribute UpdateSupplierDTO supplierDTO,
        @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) throws IdInvalidException {
        Supplier supplier = this.supplierService.fetchSupplierById(id);
        if(supplier == null) {
            throw new IdInvalidException("Nhà cung cấp với id = " + id + " không tồn tại");
        }   
        this.supplierMapper.updateEntityFromDto(supplierDTO, supplier);
        Supplier updatedSupplier = this.supplierService.handleUpdateSupplier(id, supplier, imageFile, supplierDTO.getCategoryIds());
        return ResponseEntity.ok(updatedSupplier);
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    @ApiMessage("Xóa nhà cung cấp thành công")
    public ResponseEntity<Void> deleteSupplier(@PathVariable("id") Long id) throws IdInvalidException {
        Supplier currentSupplier = this.supplierService.fetchSupplierById(id);
        if(currentSupplier == null) {
            throw new IdInvalidException("Nhà cung cấp với id = " + id + " không tồn tại");
        }
        this.supplierService.handleDeleteSupplier(id);
        return ResponseEntity.ok(null);
    }

    @PreAuthorize("hasAnyRole('admin', 'employee')")
    @GetMapping("/{id}")
    @ApiMessage("Lấy nhà cung cấp theo ID thành công")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable("id") Long id) throws IdInvalidException {
        Supplier supplier = this.supplierService.fetchSupplierById(id);
        if(supplier == null) {
            throw new IdInvalidException("Nhà cung cấp với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(supplier);
    }
    
    @PreAuthorize("hasAnyRole('admin', 'employee')")
    @GetMapping("/{id}/categories")
    @ApiMessage("Get categories of supplier")
    public ResponseEntity<List<Category>> getSupplierCategories(@PathVariable("id") Long id) throws IdInvalidException {
        Supplier supplier = this.supplierService.fetchSupplierById(id);
        if(supplier == null) {
            throw new IdInvalidException("Nhà cung cấp với id = " + id + " không tồn tại");
        }
        List<Long> categoryIds = this.supplierService.getCategoryIdsBySupplier(id);
        List<Category> categories = new ArrayList<>();
        for(Long categoryId : categoryIds) {
            Category category = this.categoryService.fetchCategoryById(categoryId);
            if(category != null) {
                categories.add(category);
            }
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("")
    @ApiMessage("Lấy danh sách nhà cung cấp thành công")
    public ResponseEntity<ResultPaginationDTO> getAllSuppliers(
            @Filter Specification<Supplier> spec,
            Pageable pageable
    ) {
        ResultPaginationDTO rs = this.supplierService.handleGetSupplier(spec, pageable);
        return ResponseEntity.ok(rs);
    }
}
