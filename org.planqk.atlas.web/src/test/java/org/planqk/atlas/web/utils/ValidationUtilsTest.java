package org.planqk.atlas.web.utils;

import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyTypeDto;
import org.planqk.atlas.web.exceptions.InvalidResourceTypeValueException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationUtilsTest {

    @Test
    void validate_ComputationResourceProperty_Valid() {
        var type = new ComputeResourcePropertyTypeDto();
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var resource = new ComputeResourcePropertyDto();
        resource.setType(type);
        resource.setValue("10.0");

        ValidationUtils.validateComputingResourceProperty(resource);
    }

    @Test
    void validate_ComputationResourceProperty_Invalid() {
        var type = new ComputeResourcePropertyTypeDto();
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var resource = new ComputeResourcePropertyDto();
        resource.setType(type);
        resource.setValue("10-0");

        assertThrows(InvalidResourceTypeValueException.class, () -> {
            ValidationUtils.validateComputingResourceProperty(resource);
        });
    }
}
