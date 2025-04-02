package com.ndc.be.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ndc.be.domain.Customer;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.repository.CustomerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        return this.customerRepository.save(customer);
    }

    public Customer fetchCustomerById(Long id) {
        Optional<Customer> customerOptional = this.customerRepository.findById(id);
        if (customerOptional.isPresent()) {
            return customerOptional.get();
    }
        return null;
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer customer) {
       Customer currentCustomer = this.fetchCustomerById(id);
       if (currentCustomer != null) {
            currentCustomer.setFullname(customer.getFullname());
            currentCustomer.setPhone(customer.getPhone());
            currentCustomer.setPoint(customer.getPoint());
            currentCustomer.setActive(customer.isActive());
       }
       return this.customerRepository.save(currentCustomer);
    }

    public boolean existsByPhone(String phone) {
        return this.customerRepository.existsByPhone(phone);
    }



    public void handleDeleteCustomer(Long id) {
        Customer currentCustomer = this.fetchCustomerById(id);
        if (currentCustomer != null) {
            currentCustomer.setActive(false);
            this.customerRepository.save(currentCustomer);
        }
    }
    
    public ResultPaginationDTO handleGetCustomer(Specification<Customer> customerSpec, Pageable pageable) {
        Page<Customer> pageCustomer = this.customerRepository.findAll(customerSpec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageCustomer.getTotalPages());
        meta.setTotal(pageCustomer.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(pageCustomer.getContent());
        return rs;
    }

    public Customer getCustomerByPhone(String phone) {
        Optional<Customer> customerOptional = this.customerRepository.findByPhone(phone);
        if(customerOptional.isPresent()) {
            return customerOptional.get();
        }
        return null;
    }

    
}
