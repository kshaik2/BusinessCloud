package com.infor.cloudsuite.platform.components;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.infor.cloudsuite.dto.ContraintViolationDto;

/**
 * User: bcrow
 * Date: 10/27/11 10:42 AM
 */
@Component
public class ValidationProvider {

    @Autowired
    private ValidatorFactory validatorFactory;

    public <T> ContraintViolationDto<T> validate(T validateMe) {
        final Set<ConstraintViolation<T>> violations;
        violations = validatorFactory.getValidator().validate(validateMe);
        ContraintViolationDto<T> dto = new ContraintViolationDto<>();
        dto.setHasViolations(!violations.isEmpty());
        dto.setViolations(violations);
        return dto;
    }
}
