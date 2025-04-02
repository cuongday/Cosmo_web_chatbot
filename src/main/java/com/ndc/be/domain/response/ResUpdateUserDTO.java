package com.ndc.be.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import com.ndc.be.util.constant.GenderEnum;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ResUpdateUserDTO {
    Long id;
    String name;
    GenderEnum gender;
    String address;
    int age;
    Instant updatedAt;
    String avatar;

}
