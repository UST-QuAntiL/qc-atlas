package org.planqk.atlas.web.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * This validator can be used to check if a child object (dto) has a valid UUID
 * meaning, the id is not null, and does not equal the all zero uuid
 *
 * This validator does not implement a null check on the
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiresIDValidator.class)
@Documented
public @interface RequiresID {
    String message() default "Invalid password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
