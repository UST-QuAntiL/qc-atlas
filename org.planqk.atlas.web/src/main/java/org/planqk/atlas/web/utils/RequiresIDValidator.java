package org.planqk.atlas.web.utils;

import java.util.UUID;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequiresIDValidator implements ConstraintValidator<RequiresID, Identifyable> {
    @Override
    public boolean isValid(Identifyable identifyable, ConstraintValidatorContext constraintValidatorContext) {
        return identifyable.getId() != null && !identifyable.getId().equals(new UUID(0, 0));
    }
}
