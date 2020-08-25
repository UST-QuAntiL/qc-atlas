/*******************************************************************************
 * Copyright (c) 2020 University of Stuttgart
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.web.utils;

import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyTypeDto;
import org.planqk.atlas.web.exceptions.InvalidResourceTypeValueException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationUtilsTest {

    @Test
    void validate_ComputationResourcePropertyDto_Valid() {
        var type = new ComputeResourcePropertyTypeDto();
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var resource = new ComputeResourcePropertyDto();
        resource.setType(type);
        resource.setValue("10.0");

        ValidationUtils.validateComputingResourceProperty(resource);
    }

    @Test
    void validate_ComputationResourcePropertyDto_Invalid() {
        var type = new ComputeResourcePropertyTypeDto();
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var resource = new ComputeResourcePropertyDto();
        resource.setType(type);
        resource.setValue("10-0");

        assertThrows(InvalidResourceTypeValueException.class, () -> {
            ValidationUtils.validateComputingResourceProperty(resource);
        });
    }

    @Test
    void validate_ComputationResourceProperty_Valid() {
        var type = new ComputeResourcePropertyType();
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(type);
        resource.setValue("10.0");

        ValidationUtils.validateComputingResourceProperty(resource);
    }

    @Test
    void validate_ComputationResourceProperty_Invalid() {
        var type = new ComputeResourcePropertyType();
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(type);
        resource.setValue("10-0");

        assertThrows(InvalidResourceTypeValueException.class, () -> {
            ValidationUtils.validateComputingResourceProperty(resource);
        });
    }
}
