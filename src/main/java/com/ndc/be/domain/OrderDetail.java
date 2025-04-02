package com.ndc.be.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.ndc.be.util.SecurityUtil;

import java.time.Instant;
// import java.util.List;
@Entity
@Table(name = "order_details")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Min(value = 0, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")
    long price;

    @Min(value = 0, message = "Số lượng sản phẩm phải lớn hơn hoặc bằng 0")
    int quantity;

    @Min(value = 0, message = "Tổng giá sản phẩm phải lớn hơn hoặc bằng 0")
    long totalPrice;

    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }
}       
