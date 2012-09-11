package com.infor.cloudsuite.validation;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.infor.cloudsuite.platform.components.DomainChecker;

/**
 * User: bcrow
 * Date: 11/9/11 4:24 PM
 */
public class DomainBlacklistValidator implements ConstraintValidator<DomainBlacklist, String> {

    @Resource
    private DomainChecker domainChecker;

    @Override
    public void initialize(DomainBlacklist constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null ||
                domainChecker == null ||
                domainChecker.hasValidDomain(value);
    }
}
