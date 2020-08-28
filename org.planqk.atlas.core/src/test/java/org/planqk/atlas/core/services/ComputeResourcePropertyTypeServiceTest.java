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

package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class ComputeResourcePropertyTypeServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ComputeResourcePropertyService computeResourcePropertyService;
    @Autowired
    private ComputeResourcePropertyTypeService computeResourcePropertyTypeService;

    @Test
    void createComputeResourcePropertyType() {
        var propertyType = getFullComputeResourcePropertyType("computeResourcePropertyTypeName");

        var storedType = computeResourcePropertyTypeService.create(propertyType);

        assertPropertyTypeEquality(storedType, propertyType);
    }

    @Test
    void findComputeResourcePropertyTypeById_ElementFound() {
        var propertyType = getFullComputeResourcePropertyType("computeResourcePropertyTypeName");

        var storedType = computeResourcePropertyTypeService.create(propertyType);

        storedType = computeResourcePropertyTypeService.findById(storedType.getId());

        assertPropertyTypeEquality(storedType, propertyType);
    }

    @Test
    void findComputeResourcePropertyTypeById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyTypeService.findById(UUID.randomUUID()));
    }

    // @Test
    void updateComputeResourcePropertyType_ElementFound() {
        // TODO
    }

    @Test
    void updateComputeResourcePropertyType_ElementNotFound() {
        var propertyType = getFullComputeResourcePropertyType("computeResourcePropertyTypeName");
        propertyType.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyTypeService.update(propertyType));
    }

    @Test
    void deleteComputeResourcePropertyType_NoLinks() {
        var propertyType = getFullComputeResourcePropertyType("computeResourcePropertyTypeName");

        var storedType = computeResourcePropertyTypeService.create(propertyType);

        Assertions.assertDoesNotThrow(() -> computeResourcePropertyTypeService.findById(storedType.getId()));

        computeResourcePropertyTypeService.delete(storedType.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourcePropertyTypeService.findById(storedType.getId()));
    }

    // @Test
    void deleteComputeResourcePropertyType_ElementNotFound() {
        // TODO
    }

    @Test
    void deleteComputeResourcePropertyType_WithLinks() {
        var propertyType = getFullComputeResourcePropertyType("computeResourcePropertyTypeName");
        var storedType = computeResourcePropertyTypeService.create(propertyType);

        var property = new ComputeResourceProperty();
        property.setComputeResourcePropertyType(propertyType);

        var storedProperty = computeResourcePropertyService.create(property);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            this.computeResourcePropertyTypeService.delete(propertyType.getId());
        });

        Assertions.assertDoesNotThrow(() -> computeResourcePropertyTypeService.findById(storedType.getId()));
    }

    private void assertPropertyTypeEquality(ComputeResourcePropertyType persistedType, ComputeResourcePropertyType type){
        assertThat(persistedType.getId()).isNotNull();
        assertThat(persistedType.getName()).isEqualTo(type.getName());
        assertThat(persistedType.getDescription()).isEqualTo(type.getDescription());
        assertThat(persistedType.getDatatype()).isEqualTo(type.getDatatype());
    }

    private ComputeResourcePropertyType getFullComputeResourcePropertyType(String name) {
        var computeResourcePropertyType = new ComputeResourcePropertyType();

        computeResourcePropertyType.setDescription("description");
        computeResourcePropertyType.setName(name);
        computeResourcePropertyType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        return computeResourcePropertyType;
    }
}
