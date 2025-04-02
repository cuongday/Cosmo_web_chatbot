package com.ndc.be.controller;

import com.ndc.be.domain.ImportHistory;
import com.ndc.be.domain.request.ImportHistoryRequest;
import com.ndc.be.domain.response.ImportHistoryResponse;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.service.ImportService;
import com.ndc.be.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/imports")
@RequiredArgsConstructor
public class ImportController {
    private final ImportService importService;

    @PostMapping
    @PreAuthorize("hasAnyRole('admin', 'employee')")
    @ApiMessage("Tạo lịch sử nhập hàng thành công")
    public ResponseEntity<ImportHistoryResponse> createImportHistory(@Valid @RequestBody ImportHistoryRequest request) {
        ImportHistory entity = this.importService.createImportHistory(request);
        ImportHistoryResponse response = this.importService.toImportHistoryResponse(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin')")
    @ApiMessage("Lấy thông tin lịch sử nhập hàng thành công")
    public ResponseEntity<ImportHistoryResponse> getImportHistoryById(@PathVariable("id") Long id) {
        ImportHistory entity = importService.getImportHistoryById(id);
        ImportHistoryResponse response = importService.toImportHistoryResponse(entity);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin')")
    @ApiMessage("Lấy danh sách lịch sử nhập hàng thành công")
    public ResponseEntity<ResultPaginationDTO> getAllImportHistories(
            @Filter Specification<ImportHistory> importSpec,
            Pageable pageable) {
        
        ResultPaginationDTO result = this.importService.getAllImportHistories(importSpec, pageable);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin')")
    @ApiMessage("Cập nhật lịch sử nhập hàng thành công")
    public ResponseEntity<ImportHistoryResponse> updateImportHistory(
            @PathVariable Long id,
            @Valid @RequestBody ImportHistoryRequest request) {
        ImportHistory entity = this.importService.updateImportHistory(id, request);
        ImportHistoryResponse response = importService.toImportHistoryResponse(entity);
        return ResponseEntity.ok(response);
    }

    // @DeleteMapping("/{id}")
    // @PreAuthorize("hasAnyRole('admin')")
    // @ApiMessage("Xóa lịch sử nhập hàng thành công")
    // public ResponseEntity<Void> deleteImportHistory(@PathVariable Long id) {
    //     this.importService.deleteImportHistory(id);
    //     return ResponseEntity.noContent().build();
    // }

    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAnyRole('admin')")
    @ApiMessage("Lấy danh sách lịch sử nhập hàng theo nhà cung cấp thành công")
    public ResponseEntity<List<ImportHistoryResponse>> getImportHistoriesBySupplier(@PathVariable("supplierId") Long supplierId) {
        List<ImportHistory> entities = importService.getImportHistoriesBySupplier(supplierId);
        List<ImportHistoryResponse> responses = importService.toImportHistoryResponseList(entities);
        return ResponseEntity.ok(responses);
    }
} 