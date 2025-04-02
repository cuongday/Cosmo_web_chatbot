package com.ndc.be.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ndc.be.domain.Category;
import com.ndc.be.domain.Supplier;
import com.ndc.be.domain.SupplierCategory;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.repository.CategoryRepository;
import com.ndc.be.repository.SupplierCategoryRepository;
import com.ndc.be.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final CloudinaryService cloudinaryService;
    private final CategoryRepository categoryRepository;
    private final SupplierCategoryRepository supplierCategoryRepository;

    @Transactional
    public Supplier handleCreateSupplier(Supplier supplier, MultipartFile imageFile, List<Long> categoryIds) {
        // Xử lý file ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = this.cloudinaryService.uploadImage(imageFile);
            supplier.setImage(imageUrl);
        }
        
        // Lưu supplier trước để có ID
        Supplier savedSupplier = supplierRepository.save(supplier);
        
        // Xử lý danh sách category
        if (categoryIds != null && !categoryIds.isEmpty()) {
            for (Long categoryId : categoryIds) {
                Optional<Category> categoryOpt = this.categoryRepository.findById(categoryId);
                if (categoryOpt.isPresent()) {
                    SupplierCategory supplierCategory = new SupplierCategory();
                    supplierCategory.setSupplier(savedSupplier);
                    supplierCategory.setCategory(categoryOpt.get());
                    this.supplierCategoryRepository.save(supplierCategory);
                }
            }
        }
        
        return savedSupplier;
    }

    @Transactional
    public void handleDeleteSupplier(Long id) {
        // Tìm tất cả các mối quan hệ và xóa chúng
        Supplier supplier = fetchSupplierById(id);
        if (supplier != null) {
            List<SupplierCategory> relations = supplierCategoryRepository.findBySupplier(supplier);
            supplierCategoryRepository.deleteAll(relations);
        }
        
        // Sau đó xóa supplier
        this.supplierRepository.deleteById(id);
    }

    public Supplier fetchSupplierById(Long id) {
        Optional<Supplier> supplierOptional = this.supplierRepository.findById(id);
        return supplierOptional.orElse(null);
    }

    public ResultPaginationDTO handleGetSupplier(Specification<Supplier> supplierSpec, Pageable pageable) {
        Page<Supplier> pageSupplier = this.supplierRepository.findAll(supplierSpec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageSupplier.getTotalPages());
        meta.setTotal(pageSupplier.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageSupplier.getContent());
        return rs;
    }

    @Transactional
    public Supplier handleUpdateSupplier(Long id, Supplier supplier, MultipartFile imageFile, List<Long> categoryIds) {
        System.out.println("Nhận được categoryIds: " + categoryIds);
        
        // Xử lý file ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = this.cloudinaryService.uploadImage(imageFile);
            supplier.setImage(imageUrl);
        }
        
        // Đảm bảo ID đúng
        supplier.setId(id);
        
        // Lưu supplier
        Supplier updatedSupplier = this.supplierRepository.save(supplier);
        supplierRepository.flush();
        
        // Xử lý danh sách category
        if (categoryIds != null) {
            // Xóa tất cả các mối quan hệ hiện tại
            List<SupplierCategory> existingRelations = supplierCategoryRepository.findBySupplier(updatedSupplier);
            supplierCategoryRepository.deleteAll(existingRelations);
            
            // Tạo mối quan hệ mới
            for (Long categoryId : categoryIds) {
                Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
                if (categoryOpt.isPresent()) {
                    SupplierCategory supplierCategory = new SupplierCategory();
                    supplierCategory.setSupplier(updatedSupplier);
                    supplierCategory.setCategory(categoryOpt.get());
                    supplierCategory.setCreatedAt(Instant.now());
                    supplierCategoryRepository.save(supplierCategory);
                }
            }
        }
        
        // Truy vấn lại từ DB để có dữ liệu mới nhất
        return supplierRepository.findById(id).orElse(null);
    }

    public boolean existsByName(String name) {
        return this.supplierRepository.existsByName(name);
    }
    
    public List<Long> getCategoryIdsBySupplier(Long supplierId) {
        Supplier supplier = fetchSupplierById(supplierId);
        if (supplier == null) {
            return new ArrayList<>();
        }
        
        List<SupplierCategory> relations = supplierCategoryRepository.findBySupplier(supplier);
        return relations.stream()
                .map(rel -> rel.getCategory().getId())
                .toList();
    }
}
