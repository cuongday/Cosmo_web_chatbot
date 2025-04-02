package com.ndc.be.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCustomerDTO {
    @NotNull
    @Size(min = 2, max = 255)
    private String fullname;

    @NotNull
    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải bắt đầu bằng số 0 và có 10 ký tự")
    private String phone;
}