package com.ndc.be.domain;



import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.Instant;


@Entity
@Table(name = "settings")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Size(min = 2, message = "Tên phải có ít nhất 2 ký tự")
    String name;

    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải bắt đầu bằng số 0 và có 10 ký tự")
    String phone;

    Instant openTime;
    Instant closeTime;

    @Size(min = 2, message = "Địa chỉ phải có ít nhất 2 ký tự")
    String address;
}
