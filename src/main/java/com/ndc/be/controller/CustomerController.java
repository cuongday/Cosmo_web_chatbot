package com.ndc.be.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ndc.be.domain.Customer;
import com.ndc.be.domain.request.CreateCustomerDTO;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.service.CustomerService;
import com.ndc.be.util.annotation.ApiMessage;
import com.ndc.be.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CustomerController {
    private final CustomerService customerService;

    @PreAuthorize("hasAnyRole('admin', 'employee')")
    @PostMapping("/customers")
    @ApiMessage("Create new customer")
    public ResponseEntity<Customer> createCustomer(@RequestBody @Valid CreateCustomerDTO customerDTO) throws IdInvalidException{
        if (this.customerService.existsByPhone(customerDTO.getPhone())) {
            throw new IdInvalidException("Số điện thoại đã tồn tại");
        }

        Customer customer =Customer.builder()
            .fullname(customerDTO.getFullname())
            .phone(customerDTO.getPhone())
            .point(0L)
            .isActive(true)
            .build();
        Customer newCustomer = this.customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
    }

    @PreAuthorize("hasAnyRole('admin', 'employee')")
    @GetMapping("/customers")
    @ApiMessage("Get all customers")
    public ResponseEntity<ResultPaginationDTO> getAllCustomers(
        @Filter Specification<Customer> customerSpec,
        Pageable pageable
    ) {
        ResultPaginationDTO rs = this.customerService.handleGetCustomer(customerSpec, pageable);
        return ResponseEntity.ok(rs);
    }

    @PreAuthorize("hasAnyRole('admin', 'employee')")
    @GetMapping("/customers/{id}")
    @ApiMessage("Get customer by id")
    public ResponseEntity<Customer> getCustomerById(@PathVariable ("id") Long id) throws IdInvalidException {
        Customer customer = this.customerService.fetchCustomerById(id);
        if (customer == null) {
            throw new IdInvalidException("Customer with id " + id + " not found");
        }
        return ResponseEntity.ok(customer);
    }

    @PreAuthorize("hasAnyRole('admin', 'employee')")
    @GetMapping("/customers/phone/{phone}")
    @ApiMessage("Get customer by phone")
    public ResponseEntity<Customer> getCustomerByPhone(@PathVariable ("phone") String phone) throws IdInvalidException {
        Customer customer = this.customerService.getCustomerByPhone(phone);
        if (customer == null) {
            throw new IdInvalidException("Customer with phone " + phone + " not found");
        }
        return ResponseEntity.ok(customer);
    }
}
