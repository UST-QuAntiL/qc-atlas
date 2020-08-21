package org.planqk.atlas.web.utils;

import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.exceptions.InvalidResourceTypeValueException;

public class ValidationUtils {

    public static void validateComputingResourceProperty(ComputeResourcePropertyDto resourceDto) {
        if (!resourceDto.getType().getDatatype().isValid(resourceDto.getValue())) {
            throw new InvalidResourceTypeValueException("The value \"" + resourceDto.getValue() +
                    "\" is not valid for the Type " + resourceDto.getType().getDatatype().name());
        }
    }

    public static void validateComputingResourceProperty(ComputeResourceProperty resource) {
        if (!resource.getComputeResourcePropertyType().getDatatype().isValid(resource.getValue())) {
            throw new InvalidResourceTypeValueException("The value \"" + resource.getValue() +
                    "\" is not valid for the Type " + resource.getComputeResourcePropertyType().getDatatype().name());
        }
    }
}
