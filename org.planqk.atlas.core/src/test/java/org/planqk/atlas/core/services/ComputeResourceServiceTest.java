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

import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ComputeResourceServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ComputeResourceService computeResourceService;
    @Autowired
    private ComputeResourcePropertyService computeResourcePropertyService;
    @Autowired
    private ComputeResourcePropertyTypeService computeResourcePropertyTypeService;

    @Test
    void createMinimalComputeResource() {
        ComputeResource computeResource = new ComputeResource();
        computeResource.setName("computeResourceName");

        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        assertComputeResourceEquality(storedComputeResource, computeResource);
    }

    @Test
    void createMaximalComputeResource() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");

        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        assertComputeResourceEquality(storedComputeResource, computeResource);
    }

    @Test
    void createComputeResource_WithComputingResourceProperty() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");
        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        Assertions.assertDoesNotThrow(() -> computeResourceService.findById(storedComputeResource.getId()));

        // Add Computing Resource Property Reference
        var computeResourceProperty = new ComputeResourceProperty();
        var computeResourcePropertyType = new ComputeResourcePropertyType();
        computeResourcePropertyType.setName("computeResourcePropertyTypeName");
        computeResourcePropertyType.setDatatype(ComputeResourcePropertyDataType.STRING);
        computeResourcePropertyType.setDescription("description");
        var storedType = computeResourcePropertyTypeService.create(computeResourcePropertyType);
        computeResourceProperty.setComputeResourcePropertyType(storedType);
        computeResourceProperty.setValue("value");

        var storedProperty = computeResourcePropertyService.addComputeResourcePropertyToComputeResource(
                storedComputeResource.getId(), computeResourceProperty);

        var storedComputeResourceWithReference = computeResourceService.findById(storedComputeResource.getId());

        assertThat(storedComputeResourceWithReference.getProvidedComputingResourceProperties().size()).isEqualTo(1);
    }

    @Test
    void findAllComputeResources() {
        ComputeResource computeResource1 = getFullComputeResource("computeResourceName1");
        ComputeResource computeResource2 = getFullComputeResource("computeResourceName2");
        computeResourceService.create(computeResource1);
        computeResourceService.create(computeResource2);

        List<ComputeResource> storedComputeResources = computeResourceService.findAll(Pageable.unpaged()).getContent();

        assertThat(storedComputeResources.size()).isEqualTo(2);
    }

    @Test
    void searchAllComputeResourcesByName() {
        ComputeResource computeResource1 = getFullComputeResource("computeResourceName1");
        ComputeResource computeResource2 = getFullComputeResource("computeResourceName2");
        computeResourceService.create(computeResource1);
        computeResourceService.create(computeResource2);

        List<ComputeResource> storedComputeResources = computeResourceService
                .searchAllByName("1", Pageable.unpaged()).getContent();

        assertThat(storedComputeResources.size()).isEqualTo(1);
    }

    @Test
    void findComputeResourceById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourceService.findById(UUID.randomUUID()));
    }

    @Test
    void findComputeResourceById_ElementFound() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");

        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        storedComputeResource = computeResourceService.findById(storedComputeResource.getId());

        assertComputeResourceEquality(storedComputeResource, computeResource);
    }

    @Test
    void updateComputeResource_ElementNotFound() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");
        computeResource.setId(UUID.randomUUID());
        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourceService.update(computeResource));
    }

    @Test
    void updateComputeResource_ElementFound() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");
        ComputeResource storedComputeResource = getFullComputeResource("computeResourceName");

        ComputeResource storedEditedComputeResource = computeResourceService.create(computeResource);
        storedComputeResource.setId(storedEditedComputeResource.getId());
        String editName = "editedComputeResourceName";
        storedEditedComputeResource.setName(editName);
        computeResourceService.create(storedEditedComputeResource);

        assertThat(storedEditedComputeResource.getId()).isNotNull();
        assertThat(storedEditedComputeResource.getId()).isEqualTo(storedComputeResource.getId());
        assertThat(storedEditedComputeResource.getName()).isNotEqualTo(storedComputeResource.getName());
        assertThat(storedEditedComputeResource.getQuantumComputationModel()).isEqualTo(storedComputeResource.getQuantumComputationModel());
        assertThat(storedEditedComputeResource.getTechnology()).isEqualTo(storedComputeResource.getTechnology());
        assertThat(storedEditedComputeResource.getVendor()).isEqualTo(storedComputeResource.getVendor());
    }

    @Test
    void deleteComputeResource_NoReferences() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");

        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        Assertions.assertDoesNotThrow(() -> computeResourceService.findById(storedComputeResource.getId()));

        computeResourceService.delete(storedComputeResource.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourceService.findById(storedComputeResource.getId()));
    }

    // @Test
    void deleteComputeResource_ElementNotFound() {
        // TODO
    }

    @Test
    void deleteComputeResource_HasReferences() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");
        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        Assertions.assertDoesNotThrow(() -> computeResourceService.findById(storedComputeResource.getId()));

        // Add Computing Resource Property Reference
        var computeResourceProperty = new ComputeResourceProperty();
        var computeResourcePropertyType = new ComputeResourcePropertyType();
        computeResourcePropertyType.setName("computeResourcePropertyTypeName");
        computeResourcePropertyType.setDatatype(ComputeResourcePropertyDataType.STRING);
        computeResourcePropertyType.setDescription("description");
        var storedType = computeResourcePropertyTypeService.create(computeResourcePropertyType);
        computeResourceProperty.setComputeResourcePropertyType(storedType);
        computeResourceProperty.setValue("value");

        var storedProperty = computeResourcePropertyService.addComputeResourcePropertyToComputeResource(
                storedComputeResource.getId(), computeResourceProperty);

        // Delete
        computeResourceService.delete(storedComputeResource.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourceService.findById(storedComputeResource.getId()));

        // Test if references are removed
        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourcePropertyService.findById(storedProperty.getId()));
    }

    private void assertComputeResourceEquality(ComputeResource dbComputeResource, ComputeResource compareComputeResource) {
        assertThat(dbComputeResource.getId()).isNotNull();
        assertThat(dbComputeResource.getName()).isEqualTo(compareComputeResource.getName());
        assertThat(dbComputeResource.getQuantumComputationModel()).isEqualTo(compareComputeResource.getQuantumComputationModel());
        assertThat(dbComputeResource.getTechnology()).isEqualTo(compareComputeResource.getTechnology());
        assertThat(dbComputeResource.getVendor()).isEqualTo(compareComputeResource.getVendor());
    }

    private ComputeResource getFullComputeResource(String name) {
        ComputeResource computeResource = new ComputeResource();
        computeResource.setName(name);
        computeResource.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
        computeResource.setTechnology("technology");
        computeResource.setVendor("vendor");
        return computeResource;
    }
}
