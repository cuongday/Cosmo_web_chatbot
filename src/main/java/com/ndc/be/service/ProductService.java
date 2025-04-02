package com.ndc.be.service;

import com.ndc.be.domain.Category;
import com.ndc.be.domain.Product;
import com.ndc.be.domain.Supplier;
import com.ndc.be.domain.mapper.ProductMapper;
import com.ndc.be.domain.request.CreateProductDTO;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.repository.CategoryRepository;
import com.ndc.be.repository.ProductRepository;
import com.ndc.be.repository.SupplierRepository;
import com.ndc.be.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductMapper productMapper;

    // Lấy tất cả sản phẩm
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    // Lấy danh sách sản phẩm phân trang
    public ResultPaginationDTO getAll(Specification<Product> productSpec, Pageable pageable) {
        Page<Product> pageProduct = productRepository.findAll(productSpec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageProduct.getTotalPages());
        meta.setTotal(pageProduct.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageProduct.getContent());
        return rs;
    }

    // Lấy sản phẩm theo ID
    public Product getById(Long id) throws IdInvalidException {
        return productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy sản phẩm với ID: " + id));
    }

    // Kiểm tra xem sản phẩm có tồn tại không
    public Product fetchProductById(Long id) {
        Optional<Product> productOptional = this.productRepository.findById(id);
        return productOptional.orElse(null);
    }

    // Kiểm tra tên sản phẩm đã tồn tại chưa
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    // Tạo sản phẩm mới
    @Transactional
    public Product handleCreateProduct(CreateProductDTO dto, MultipartFile imageFile) throws IdInvalidException {
        // Tạo entity từ DTO
        Product product = productMapper.toEntity(dto);
        
        // Lấy category từ ID
        Optional<Category> categoryOptional = categoryRepository.findById(dto.getCategoryId());
        if (categoryOptional.isEmpty()) {
            throw new IdInvalidException("Không tìm thấy danh mục với ID: " + dto.getCategoryId());
        }
        
        // Lấy supplier từ ID
        Optional<Supplier> supplierOptional = supplierRepository.findById(dto.getSupplierId());
        if (supplierOptional.isEmpty()) {
            throw new IdInvalidException("Không tìm thấy nhà cung cấp với ID: " + dto.getSupplierId());
        }

        product.setCategory(categoryOptional.get());
        product.setSupplier(supplierOptional.get());

        // Xử lý tải lên hình ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = this.cloudinaryService.uploadImage(imageFile);
            product.setImage(imageUrl);
        }

        return productRepository.save(product);
    }

    // Cập nhật sản phẩm
    @Transactional
    public Product handleUpdateProduct(Long id, Product product, MultipartFile imageFile) throws IdInvalidException {
        Product existingProduct = this.fetchProductById(id);
        if (existingProduct == null) {
            throw new IdInvalidException("Không tìm thấy sản phẩm với ID: " + id);
        }
        
        // Lấy category từ ID
        Optional<Category> categoryOptional = categoryRepository.findById(product.getCategory().getId());
        if (categoryOptional.isEmpty()) {
            throw new IdInvalidException("Không tìm thấy danh mục với ID: " + product.getCategory().getId());
        }
        
        // Lấy supplier từ ID
        Optional<Supplier> supplierOptional = supplierRepository.findById(product.getSupplier().getId());
        if (supplierOptional.isEmpty()) {
            throw new IdInvalidException("Không tìm thấy nhà cung cấp với ID: " + product.getSupplier().getId());
        }

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setBuyPrice(product.getBuyPrice());
        existingProduct.setSellPrice(product.getSellPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setStatus(product.getStatus());
        existingProduct.setCategory(categoryOptional.get());
        existingProduct.setSupplier(supplierOptional.get());
        existingProduct.setDate(product.getDate());

        // Xử lý tải lên hình ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = this.cloudinaryService.uploadImage(imageFile);
            existingProduct.setImage(imageUrl);
        }
        
        return this.productRepository.save(existingProduct);
    }

    // Xóa sản phẩm
    @Transactional
    public void handleDeleteProduct(Long id) throws IdInvalidException {
        if (!productRepository.existsById(id)) {
            throw new IdInvalidException("Không tìm thấy sản phẩm với ID: " + id);
        }
        this.productRepository.deleteById(id);
    }

    // Lấy sản phẩm theo danh mục
    public List<Product> getByCategory(Long categoryId) throws IdInvalidException {
        Optional<Category> categoryOptional = this.categoryRepository.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            throw new IdInvalidException("Không tìm thấy danh mục với ID: " + categoryId);
        }
        return this.productRepository.findByCategory(categoryOptional.get());
    }

    // Lấy sản phẩm theo nhà cung cấp
    public List<Product> getBySupplier(Long supplierId) throws IdInvalidException {
        Optional<Supplier> supplierOptional = supplierRepository.findById(supplierId);
        if (supplierOptional.isEmpty()) {
            throw new IdInvalidException("Không tìm thấy nhà cung cấp với ID: " + supplierId);
        }
        return this.productRepository.findBySupplier(supplierOptional.get());
    }
} 