package com.infor.cloudsuite.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: bcrow
 * Date: 10/27/11 10:15 AM
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = PasswordsMatchValidator.class)
public @interface PasswordsMatch {

    String message() default "{com.infor.cloudsuite.passwordMismatch}";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default {};

    boolean trimmed() default true;
}
