package com.ndc.be.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndc.be.util.SecurityUtil;
import com.ndc.be.util.constant.PaymentMethod;
import com.ndc.be.util.constant.PaymentStatus;

import java.time.Instant;
import java.util.List;


@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Min(value = 0, message = "Số tiền thanh toán phải lớn hơn hoặc bằng 0")
    long totalPrice;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    String paymentUrl;
    String transactionNo;
    String paymentMessage;

    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "order")
    @JsonIgnore
    private List<OrderDetail> orderDetails;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (this.paymentStatus == null) {
            this.paymentStatus = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }
}
