package com.ndc.be.service;

import com.ndc.be.domain.ImportDetail;
import com.ndc.be.domain.ImportHistory;
import com.ndc.be.domain.Product;
import com.ndc.be.domain.Supplier;
import com.ndc.be.domain.User;
import com.ndc.be.domain.request.ImportDetailRequest;
import com.ndc.be.domain.request.ImportHistoryRequest;
import com.ndc.be.domain.response.ImportDetailResponse;
import com.ndc.be.domain.response.ImportHistoryResponse;
import com.ndc.be.domain.response.ProductResponse;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.domain.response.SupplierSimpleResponse;
import com.ndc.be.domain.response.UserSimpleResponse;
import com.ndc.be.repository.ImportDetailRepository;
import com.ndc.be.repository.ImportHistoryRepository;
import com.ndc.be.repository.ProductRepository;
import com.ndc.be.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportService {
    private final ImportHistoryRepository importHistoryRepository;
    private final ImportDetailRepository importDetailRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserService userService;

    @Transactional
    public ImportHistory createImportHistory(ImportHistoryRequest request) {
        // Lấy thông tin nhà cung cấp
        Supplier supplier = this.supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhà cung cấp với ID: " + request.getSupplierId()));
        
        // Lấy thông tin người dùng hiện tại
        User currentUser = this.userService.getCurrentUser();
        
        // Tạo mới ImportHistory
        ImportHistory importHistory = ImportHistory.builder()
            .supplier(supplier)
            .user(currentUser)
            .totalPrice(0L) // Sẽ được cập nhật sau khi xử lý các chi tiết
            .build();
        
        importHistory = importHistoryRepository.save(importHistory);
        
        // Tổng giá trị nhập hàng
        long totalPrice = 0;
        
        // Xử lý chi tiết nhập hàng
        if (request.getImportDetails() != null && !request.getImportDetails().isEmpty()) {
            for (ImportDetailRequest detailRequest : request.getImportDetails()) {
                // Lấy thông tin sản phẩm
                Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + detailRequest.getProductId()));
                
                // Tính tổng giá cho mỗi chi tiết
                long itemTotalPrice = detailRequest.getPrice() * detailRequest.getQuantity();
                
                // Tạo chi tiết nhập hàng
                ImportDetail detail = ImportDetail.builder()
                    .importHistory(importHistory)
                    .product(product)
                    .price(detailRequest.getPrice())
                    .quantity(detailRequest.getQuantity())
                    .totalPrice(itemTotalPrice)
                    .build();
                
                importDetailRepository.save(detail);
                
                // Cập nhật tổng giá
                totalPrice += itemTotalPrice;
                
                // Cập nhật số lượng sản phẩm
                product.setQuantity(product.getQuantity() + detailRequest.getQuantity());
                this.productRepository.save(product);
            }
        }
        
        // Cập nhật tổng giá cho ImportHistory
        importHistory.setTotalPrice(totalPrice);
        return importHistoryRepository.save(importHistory);
    }

    public ImportHistory getImportHistoryById(Long id) {
        return importHistoryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch sử nhập hàng với ID: " + id));
    }

    public ResultPaginationDTO getAllImportHistories(Specification<ImportHistory> importSpec, Pageable pageable) {
        // Sử dụng Specification trong truy vấn
        Page<ImportHistory> pageImportHistory = this.importHistoryRepository.findAll(importSpec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        // Sửa lại cách lấy thông tin phân trang
        meta.setPage(pageable.getPageNumber() + 1);           // Cộng 1 để hiển thị từ trang 1
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageImportHistory.getTotalPages());
        meta.setTotal(pageImportHistory.getTotalElements());

        rs.setMeta(meta);
        // Chuyển đổi kết quả sang response object
        rs.setResult(pageImportHistory.getContent().stream()
                .map(this::toImportHistoryResponse)
                .collect(Collectors.toList()));

        return rs;
    }

    // public Page<ImportHistoryResponse> getAllImportHistories(Specification<ImportHistory> spec, Pageable pageable) {
    //     return importHistoryRepository.findAll(spec, pageable)
    //         .map(importHistory -> {
    //             List<ImportDetail> details = importDetailRepository.findByImportHistory(importHistory);
    //             return mapToImportHistoryResponse(importHistory, details);
    //         });
    // }

    @Transactional
    public ImportHistory updateImportHistory(Long id, ImportHistoryRequest request) {
        ImportHistory importHistory = importHistoryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch sử nhập hàng với ID: " + id));
        
        // Lấy thông tin nhà cung cấp
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhà cung cấp với ID: " + request.getSupplierId()));
        
        // Cập nhật thông tin nhà cung cấp
        importHistory.setSupplier(supplier);
        
        // Lấy danh sách chi tiết hiện tại
        List<ImportDetail> currentDetails = importDetailRepository.findByImportHistory(importHistory);
        
        // Xóa các chi tiết hiện tại và hoàn trả số lượng sản phẩm
        for (ImportDetail detail : currentDetails) {
            Product product = detail.getProduct();
            product.setQuantity(product.getQuantity() - detail.getQuantity());
            productRepository.save(product);
            
            this.importDetailRepository.delete(detail);
        }
        
        // Tổng giá trị nhập hàng
        long totalPrice = 0;
        
        // Xử lý chi tiết nhập hàng mới
        if (request.getImportDetails() != null && !request.getImportDetails().isEmpty()) {
            for (ImportDetailRequest detailRequest : request.getImportDetails()) {
                // Lấy thông tin sản phẩm
                Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + detailRequest.getProductId()));
                
                // Tính tổng giá cho mỗi chi tiết
                long itemTotalPrice = detailRequest.getPrice() * detailRequest.getQuantity();
                
                // Tạo chi tiết nhập hàng
                ImportDetail detail = ImportDetail.builder()
                    .importHistory(importHistory)
                    .product(product)
                    .price(detailRequest.getPrice())
                    .quantity(detailRequest.getQuantity())
                    .totalPrice(itemTotalPrice)
                    .build();
                
                importDetailRepository.save(detail);
                
                // Cập nhật tổng giá
                totalPrice += itemTotalPrice;
                
                // Cập nhật số lượng sản phẩm
                product.setQuantity(product.getQuantity() + detailRequest.getQuantity());
                productRepository.save(product);
            }
        }
        
        // Cập nhật tổng giá cho ImportHistory
        importHistory.setTotalPrice(totalPrice);
        return importHistoryRepository.save(importHistory);
    }

    // @Transactional
    // public void deleteImportHistory(Long id) {
    //     ImportHistory importHistory = importHistoryRepository.findById(id)
    //         .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch sử nhập hàng với ID: " + id));
        
    //     // Lấy danh sách chi tiết
    //     List<ImportDetail> details = importDetailRepository.findByImportHistory(importHistory);
        
    //     // Xóa các chi tiết và hoàn trả số lượng sản phẩm
    //     for (ImportDetail detail : details) {
    //         Product product = detail.getProduct();
    //         product.setQuantity(product.getQuantity() - detail.getQuantity());
    //         productRepository.save(product);
            
    //         importDetailRepository.delete(detail);
    //     }
        
    //     // Xóa lịch sử nhập hàng
    //     importHistoryRepository.delete(importHistory);
    // }

    public List<ImportHistory> getImportHistoriesBySupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhà cung cấp với ID: " + supplierId));
        
        return this.importHistoryRepository.findBySupplier(supplier);
    }

    // Phương thức chuyển đổi entity sang response objects
    
    public ImportHistoryResponse toImportHistoryResponse(ImportHistory importHistory) {
        List<ImportDetail> details = importDetailRepository.findByImportHistory(importHistory);
        return mapToImportHistoryResponse(importHistory, details);
    }
    
    public List<ImportHistoryResponse> toImportHistoryResponseList(List<ImportHistory> importHistories) {
        return importHistories.stream()
            .map(importHistory -> {
                List<ImportDetail> details = importDetailRepository.findByImportHistory(importHistory);
                return mapToImportHistoryResponse(importHistory, details);
            })
            .collect(Collectors.toList());
    }

    // Phương thức hỗ trợ chuyển đổi ImportHistory thành ImportHistoryResponse
    private ImportHistoryResponse mapToImportHistoryResponse(ImportHistory importHistory, List<ImportDetail> details) {
        // Map user info
        UserSimpleResponse userResponse = UserSimpleResponse.builder()
            .id(importHistory.getUser().getId())
            .username(importHistory.getUser().getUsername())
            .fullname(importHistory.getUser().getName())
            .build();
        
        // Map supplier info
        SupplierSimpleResponse supplierResponse = SupplierSimpleResponse.builder()
            .id(importHistory.getSupplier().getId())
            .name(importHistory.getSupplier().getName())
            .description(importHistory.getSupplier().getDescription())
            .build();
        
        // Map import details
        List<ImportDetailResponse> detailResponses = details.stream()
            .map(this::mapToImportDetailResponse)
            .collect(Collectors.toList());
        
        // Create import history response
        return ImportHistoryResponse.builder()
            .id(importHistory.getId())
            .user(userResponse)
            .supplier(supplierResponse)
            .importDetails(detailResponses)
            .totalPrice(importHistory.getTotalPrice())
            .createdAt(importHistory.getCreatedAt())
            .updatedAt(importHistory.getUpdatedAt())
            .createdBy(importHistory.getCreatedBy())
            .updatedBy(importHistory.getUpdatedBy())
            .build();
    }

    // Phương thức hỗ trợ chuyển đổi ImportDetail thành ImportDetailResponse
    private ImportDetailResponse mapToImportDetailResponse(ImportDetail detail) {
        // Map product info
        ProductResponse productResponse = ProductResponse.builder()
            .id(detail.getProduct().getId())
            .name(detail.getProduct().getName())
            .description(detail.getProduct().getDescription())
            .buyPrice(detail.getProduct().getBuyPrice())
            .sellPrice(detail.getProduct().getSellPrice())
            .quantity(detail.getProduct().getQuantity())
            .image(detail.getProduct().getImage())
            .status(detail.getProduct().getStatus())
            .date(detail.getProduct().getDate())
            .createdAt(detail.getProduct().getCreatedAt())
            .updatedAt(detail.getProduct().getUpdatedAt())
            .createdBy(detail.getProduct().getCreatedBy())
            .updatedBy(detail.getProduct().getUpdatedBy())
            .build();
        
        // Create import detail response
        return ImportDetailResponse.builder()
            .id(detail.getId())
            .price(detail.getPrice())
            .quantity(detail.getQuantity())
            .totalPrice(detail.getTotalPrice())
            .importHistoryId(detail.getImportHistory().getId())
            .product(productResponse)
            .createdAt(detail.getCreatedAt())
            .updatedAt(detail.getUpdatedAt())
            .createdBy(detail.getCreatedBy())
            .updatedBy(detail.getUpdatedBy())
            .build();
    }
} 