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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
    void findAllComputeResourcePropertyTypes() {
        var propertyType1 = getFullComputeResourcePropertyType("computeResourcePropertyTypeName1");
        computeResourcePropertyTypeService.create(propertyType1);
        var propertyType2 = getFullComputeResourcePropertyType("computeResourcePropertyTypeName2");
        computeResourcePropertyTypeService.create(propertyType2);

        List<ComputeResourcePropertyType> types = computeResourcePropertyTypeService.findAll(Pageable.unpaged()).getContent();

        assertThat(types.size()).isEqualTo(2);
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

    @Test
    void updateComputeResourcePropertyType_ElementFound() {
        var propertyType = getFullComputeResourcePropertyType("computeResourcePropertyTypeName");
        var comparePropertyType = getFullComputeResourcePropertyType("computeResourcePropertyTypeName");

        var storedType = computeResourcePropertyTypeService.create(propertyType);
        comparePropertyType.setId(storedType.getId());

        String editedName = "editedComputeResourcePropertyTypeName";
        storedType.setName(editedName);

        var editedType = computeResourcePropertyTypeService.update(storedType);

        assertThat(editedType.getId()).isNotNull();
        assertThat(editedType.getId()).isEqualTo(storedType.getId());
        assertThat(editedType.getName()).isNotEqualTo(comparePropertyType.getName());
        assertThat(editedType.getName()).isEqualTo(editedName);
        assertThat(editedType.getDescription()).isEqualTo(comparePropertyType.getDescription());
        assertThat(editedType.getDatatype()).isEqualTo(comparePropertyType.getDatatype());
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

        assertDoesNotThrow(() -> computeResourcePropertyTypeService.findById(storedType.getId()));

        computeResourcePropertyTypeService.delete(storedType.getId());

        assertThrows(NoSuchElementException.class, () ->
                computeResourcePropertyTypeService.findById(storedType.getId()));
    }

    @Test
    void deleteComputeResourcePropertyType_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                computeResourcePropertyTypeService.delete(UUID.randomUUID()));
    }

    @Test
    void deleteComputeResourcePropertyType_WithLinks() {
        var propertyType = getFullComputeResourcePropertyType("computeResourcePropertyTypeName");
        var storedType = computeResourcePropertyTypeService.create(propertyType);

        var property = new ComputeResourceProperty();
        property.setComputeResourcePropertyType(propertyType);

        var storedProperty = computeResourcePropertyService.create(property);

        assertThrows(EntityReferenceConstraintViolationException.class, () -> {
            this.computeResourcePropertyTypeService.delete(propertyType.getId());
        });

        assertDoesNotThrow(() -> computeResourcePropertyTypeService.findById(storedType.getId()));
    }

    private void assertPropertyTypeEquality(ComputeResourcePropertyType persistedType, ComputeResourcePropertyType type) {
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
