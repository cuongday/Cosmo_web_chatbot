package com.ndc.be.domain;

// import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndc.be.util.SecurityUtil;
import com.ndc.be.util.constant.GenderEnum;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    @Size(min = 2, message = "Tên phải có ít nhất 2 ký tự")
    String name;

    @NotNull
    @Size(min = 3, message = "Tên đăng nhập phải có ít nhất 3 ký tự")
    String username;

    @NotNull
    @Size(min = 3, message = "Password phải có ít nhất 3 ký tự")
    String password;
    @Enumerated(EnumType.STRING)
    GenderEnum gender;
    String address;
    String avatar;
    @Column(columnDefinition = "MEDIUMTEXT")
    String refreshToken;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ImportHistory> importHistories;

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
