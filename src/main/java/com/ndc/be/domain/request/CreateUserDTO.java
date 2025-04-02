package com.ndc.be.domain.request;

import com.ndc.be.util.constant.GenderEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserDTO {
    @Size(min = 3, message = "Tên phải có ít nhất 3 ký tự")
    private String name;

    @NotNull
    private String username;
    
    @NotNull
    private String password;
    private GenderEnum gender;
    private String address;
    private Long roleId;
}
