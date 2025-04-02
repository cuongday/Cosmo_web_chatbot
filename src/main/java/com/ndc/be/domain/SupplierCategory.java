package com.ndc.be.domain;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.ndc.be.util.SecurityUtil;
import java.time.Instant;


@Entity
@Table(name = "supplier_categories")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;

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
