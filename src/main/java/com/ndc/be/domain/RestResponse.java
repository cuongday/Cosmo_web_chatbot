package com.ndc.be.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T> {
    private int statusCode;
    private String error;

    //message có thể là String, hoặc arrayList
    private Object message;
    private T data;


}
