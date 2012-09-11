package com.infor.cloudsuite.dto;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * User: bcrow
 * Date: 10/27/11 10:46 AM
 */
public class ContraintViolationDto<T> {
    private boolean hasViolations;
    private Set<ConstraintViolation<T>> violations;

    public boolean isHasViolations() {
        return hasViolations;
    }

    public void setHasViolations(boolean hasViolations) {
        this.hasViolations = hasViolations;
    }

    public Set<ConstraintViolation<T>> getViolations() {
        if (violations == null) {
            violations = new HashSet<>();
        }
        return violations;
    }

    public void setViolations(Set<ConstraintViolation<T>> violations) {
        this.violations = violations;
    }
}
