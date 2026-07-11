package com.abhishek.github.tinylink.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TinyCodeValidator.class)
@Documented
public @interface ValidTinyCode {
    String message() default "Invalid tiny code length";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}