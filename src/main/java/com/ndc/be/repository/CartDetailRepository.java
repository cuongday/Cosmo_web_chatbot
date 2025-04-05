package com.ndc.be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ndc.be.domain.Cart;
import com.ndc.be.domain.CartDetail;
import com.ndc.be.domain.Product;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long>, JpaSpecificationExecutor<CartDetail> {
    List<CartDetail> findByCart(Cart cart);
    List<CartDetail> findByProduct(Product product);
} 