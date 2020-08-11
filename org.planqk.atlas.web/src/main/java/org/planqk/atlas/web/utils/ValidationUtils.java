package org.planqk.atlas.web.utils;

import javax.validation.Valid;

import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.exceptions.InvalidResourceTypeValueException;

import org.springframework.web.bind.annotation.RequestBody;

public class ValidationUtils {

    public static void validateComputingResourceProperty(@RequestBody @Valid ComputeResourcePropertyDto resourceDto) {
        if (!resourceDto.getType().getDatatype().isValid(resourceDto.getValue())) {
            throw new InvalidResourceTypeValueException("The value \"" + resourceDto.getType() +
                    "\" is not valid for the Type " + resourceDto.getType().getDatatype().name());
        }
    }
}
