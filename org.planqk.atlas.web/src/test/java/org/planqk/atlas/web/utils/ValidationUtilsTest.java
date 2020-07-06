package org.planqk.atlas.web.utils;

import org.planqk.atlas.core.model.ComputingResourcePropertyDataType;
import org.planqk.atlas.web.dtos.ComputingResourcePropertyDto;
import org.planqk.atlas.web.dtos.ComputingResourcePropertyTypeDto;
import org.planqk.atlas.web.exceptions.InvalidResourceTypeValueException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationUtilsTest {

    @Test
    void validate_ComputationResourceProperty_Valid() {
        var type = new ComputingResourcePropertyTypeDto();
        type.setDatatype(ComputingResourcePropertyDataType.FLOAT);
        var resource = new ComputingResourcePropertyDto();
        resource.setType(type);
        resource.setValue("10.0");

        ValidationUtils.validateComputingResourceProperty(resource);
    }

    @Test
    void validate_ComputationResourceProperty_Invalid() {
        var type = new ComputingResourcePropertyTypeDto();
        type.setDatatype(ComputingResourcePropertyDataType.FLOAT);
        var resource = new ComputingResourcePropertyDto();
        resource.setType(type);
        resource.setValue("10-0");

        assertThrows(InvalidResourceTypeValueException.class, () -> {
            ValidationUtils.validateComputingResourceProperty(resource);
        });
    }
}
