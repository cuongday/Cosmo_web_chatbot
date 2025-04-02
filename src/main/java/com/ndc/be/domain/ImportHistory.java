package com.ndc.be.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndc.be.util.SecurityUtil;
import java.time.Instant;
import java.util.List;


@Entity
@Table(name = "import_histories")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    Supplier supplier;

    @OneToMany(mappedBy = "importHistory")
    @JsonIgnore
    private List<ImportDetail> importDetails;

    long totalPrice;

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
