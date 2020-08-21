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

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

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
        computeResource.setName("test compute resource");

        ComputeResource storedComputeResource = computeResourceService.save(computeResource);

        assertComputeResourceEquality(storedComputeResource, computeResource);
    }

    @Test
    void createMaximalComputeResource() {
        ComputeResource computeResource = getGenericTestComputeResource("test compute resource");

        ComputeResource storedComputeResource = computeResourceService.save(computeResource);

        assertComputeResourceEquality(storedComputeResource, computeResource);
    }

    @Test
    void createComputeResource_WithComputingResourceProperty() {
        ComputeResource computeResource = getGenericTestComputeResource("test compute resource");
        ComputeResource storedComputeResource = computeResourceService.save(computeResource);

        Assertions.assertDoesNotThrow(() -> computeResourceService.findById(storedComputeResource.getId()));

        // Add Computing Resource Property Reference
        var computingResourceProperty = new ComputeResourceProperty();
        var computingResourcePropertyType = new ComputeResourcePropertyType();
        computingResourcePropertyType.setName("test name");
        computingResourcePropertyType.setDatatype(ComputeResourcePropertyDataType.STRING);
        computingResourcePropertyType.setDescription("test description");
        var storedType = computeResourcePropertyTypeService.save(computingResourcePropertyType);
        computingResourceProperty.setComputeResourcePropertyType(storedType);
        computingResourceProperty.setValue("test value");

        var storedProperty = computeResourcePropertyService.addComputeResourcePropertyToComputeResource(
                storedComputeResource, computingResourceProperty);

        var storedComputeResourceWithReference = computeResourceService.findById(storedComputeResource.getId());

        assertThat(storedComputeResourceWithReference.getProvidedComputingResourceProperties().size()).isEqualTo(1);
    }

    @Test
    void updateComputeResource_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourceService.update(UUID.randomUUID(), null));
    }

    @Test
    void updateComputeResource_ElementFound() {
        ComputeResource computeResource = getGenericTestComputeResource("test compute resource");
        ComputeResource storedComputeResource = getGenericTestComputeResource("test compute resource");

        ComputeResource storedEditedComputeResource = computeResourceService.save(computeResource);
        storedComputeResource.setId(storedEditedComputeResource.getId());
        String editName = "edited compute resource";
        storedEditedComputeResource.setName(editName);
        computeResourceService.save(storedEditedComputeResource);

        assertThat(storedEditedComputeResource.getId()).isNotNull();
        assertThat(storedEditedComputeResource.getId()).isEqualTo(storedComputeResource.getId());
        assertThat(storedEditedComputeResource.getName()).isNotEqualTo(storedComputeResource.getName());
        assertThat(storedEditedComputeResource.getQuantumComputationModel()).isEqualTo(storedComputeResource.getQuantumComputationModel());
        assertThat(storedEditedComputeResource.getTechnology()).isEqualTo(storedComputeResource.getTechnology());
        assertThat(storedEditedComputeResource.getVendor()).isEqualTo(storedComputeResource.getVendor());
    }

    @Test
    void findComputeResourceById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourceService.findById(UUID.randomUUID()));
    }

    @Test
    void findComputeResourceById_ElementFound() {
        ComputeResource computeResource = getGenericTestComputeResource("test compute resource");

        ComputeResource storedComputeResource = computeResourceService.save(computeResource);

        storedComputeResource = computeResourceService.findById(storedComputeResource.getId());

        assertComputeResourceEquality(storedComputeResource, computeResource);
    }

    @Test
    void findAll() {
        Set<ComputeResource> computeResources = new HashSet<>();
        ComputeResource computeResource1 = getGenericTestComputeResource("test compute resource1");
        ComputeResource computeResource2 = getGenericTestComputeResource("test compute resource2");
        computeResources.add(computeResource1);
        computeResources.add(computeResource2);
        computeResourceService.saveOrUpdateAll(computeResources);

        List<ComputeResource> storedComputeResources = computeResourceService.findAll(Pageable.unpaged()).getContent();

        assertThat(storedComputeResources.size()).isEqualTo(2);
    }

    @Test
    void searchAll() {
        Set<ComputeResource> computeResources = new HashSet<>();
        ComputeResource computeResource1 = getGenericTestComputeResource("test compute resource1");
        ComputeResource computeResource2 = getGenericTestComputeResource("test compute resource2");
        computeResources.add(computeResource1);
        computeResources.add(computeResource2);
        computeResourceService.saveOrUpdateAll(computeResources);

        List<ComputeResource> storedComputeResources = computeResourceService.searchAllByName("1", Pageable.unpaged()).getContent();

        assertThat(storedComputeResources.size()).isEqualTo(1);
    }

    @Test
    void deleteComputeResource_NoReferences() {
        ComputeResource computeResource = getGenericTestComputeResource("test compute resource");

        ComputeResource storedComputeResource = computeResourceService.save(computeResource);

        Assertions.assertDoesNotThrow(() -> computeResourceService.findById(storedComputeResource.getId()));

        computeResourceService.delete(storedComputeResource.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourceService.findById(storedComputeResource.getId()));
    }

    @Test
    void deleteComputeResource_HasReferences() {
        ComputeResource computeResource = getGenericTestComputeResource("test compute resource");
        ComputeResource storedComputeResource = computeResourceService.save(computeResource);

        Assertions.assertDoesNotThrow(() -> computeResourceService.findById(storedComputeResource.getId()));

        // Add Computing Resource Property Reference
        var computingResourceProperty = new ComputeResourceProperty();
        var computingResourcePropertyType = new ComputeResourcePropertyType();
        computingResourcePropertyType.setName("test name");
        computingResourcePropertyType.setDatatype(ComputeResourcePropertyDataType.STRING);
        computingResourcePropertyType.setDescription("test description");
        var storedType = computeResourcePropertyTypeService.save(computingResourcePropertyType);
        computingResourceProperty.setComputeResourcePropertyType(storedType);
        computingResourceProperty.setValue("test value");

        var storedProperty = computeResourcePropertyService.addComputeResourcePropertyToComputeResource(
                storedComputeResource, computingResourceProperty);

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

    private ComputeResource getGenericTestComputeResource(String name) {
        ComputeResource computeResource = new ComputeResource();
        computeResource.setName(name);
        computeResource.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
        computeResource.setTechnology("test technology");
        computeResource.setVendor("test vendor");
        return computeResource;
    }
}
