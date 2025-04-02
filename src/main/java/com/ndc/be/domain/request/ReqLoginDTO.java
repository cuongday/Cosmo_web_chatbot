package com.ndc.be.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ReqLoginDTO {

    @NotBlank(message = "Username không được để trống")
    String username;

    @NotBlank(message = "Password không được để trống")
    String password;
}
