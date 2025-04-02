package com.ndc.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ndc.be.domain.Order;
import com.ndc.be.domain.User;
import com.ndc.be.util.constant.PaymentMethod;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByUser(User user);
    List<Order> findByPaymentMethod(PaymentMethod paymentMethod);

    
} 