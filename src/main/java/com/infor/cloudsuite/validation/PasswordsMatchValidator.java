package com.infor.cloudsuite.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.infor.cloudsuite.dto.PasswordCompleter;

/**
 * User: bcrow
 * Date: 10/27/11 10:19 AM
 */
public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, PasswordCompleter> {

    private boolean trimmed;

    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {
        trimmed = constraintAnnotation.trimmed();
    }

    @Override
    public boolean isValid(PasswordCompleter value, ConstraintValidatorContext context) {
        String password = value.getPassword();
        String password2 = value.getPassword2();

        if (password == null && password2 == null) {
            return true;
        }
        if (password == null || password2 == null) {
            return false;
        }

        if (trimmed) {
            password = password.trim();
            password2 = password2.trim();
        }

        return password.equals(password2);
    }
}
