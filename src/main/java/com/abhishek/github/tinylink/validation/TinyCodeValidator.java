package com.abhishek.github.tinylink.validation;

import com.abhishek.github.tinylink.config.TinyLinkConfiguration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TinyCodeValidator implements ConstraintValidator<ValidTinyCode, String> {

    @Autowired
    private TinyLinkConfiguration config;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        int length = value.length();
        int min = config.getTinyUrlCodeMinLength();
        int max = config.getTinyUrlCodeMaxLength();

        boolean isValid = length >= min && length <= max;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Tiny code must be between %d and %d characters", min, max)
            ).addConstraintViolation();
        }

        return isValid;
    }
}