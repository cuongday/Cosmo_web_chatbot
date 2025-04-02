package com.ndc.be.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation để định nghĩa thông điệp API thành công
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiMessage {
    /**
     * Thông điệp để hiển thị khi API thành công
     */
    String value();
}
