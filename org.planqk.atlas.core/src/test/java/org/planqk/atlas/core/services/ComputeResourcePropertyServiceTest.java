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

import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.ComputeResourcePropertyRepository;
import org.planqk.atlas.core.repository.ComputeResourcePropertyTypeRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class ComputeResourcePropertyServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ComputeResourcePropertyService computeResourcePropertyService;
    @Autowired
    private ComputeResourcePropertyTypeService computeResourcePropertyTypeService;
    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private ComputeResourcePropertyTypeRepository computeResourcePropertyTypeRepository;
    @Autowired
    private ComputeResourcePropertyRepository computeResourcePropertyRepository;

    @Test
    void createComputeResourceProperty() {
        var resourceType = getFullComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("value");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService.create(resource);

        assertComputeResourcePropertyEquality(storedResource, resource);
    }

    @Test
    void findComputeResourcePropertyById_ElementFound() {
        var resourceType = getFullComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("value");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService.create(resource);

        storedResource = computeResourcePropertyService.findById(storedResource.getId());

        assertComputeResourcePropertyEquality(storedResource, resource);
    }

    @Test
    void findComputeResourcePropertyById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService.findById(UUID.randomUUID()));
    }

    @Test
    void updateComputeResourcePropertyById_ElementFound() {
        // TODO
    }

    @Test
    void updateComputeResourcePropertyById_ElementNotFound() {
        var resource = getFullComputeResourceProperty("value");
        resource.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService.update(resource));
    }

    @Test
    void deleteComputeResourceProperty_ElementFound() {
        var resourceType = getFullComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("value");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService.create(resource);

        Assertions.assertDoesNotThrow(() -> computeResourcePropertyService.findById(storedResource.getId()));

        computeResourcePropertyService.delete(storedResource.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourcePropertyService.findById(storedResource.getId()));
    }

    @Test
    void deleteComputeResourceProperty_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourcePropertyService.delete(UUID.randomUUID()));
    }

    @Test
    void deleteComputeResourceProperty_ByAlgorithmDelete() {
        var resourceType = getFullComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("value");
        resource.setComputeResourcePropertyType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("quantumAlgorithmName");
        algo.setSpeedUp("speedUp");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.create(algo);

        var storedResource = computeResourcePropertyService.addComputeResourcePropertyToAlgorithm(storedAlgo.getId(), resource);

        algorithmService.delete(storedAlgo.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourcePropertyService.findById(storedResource.getId()));
    }

    @Test
    void deleteComputeResourceProperty_ByImplementationDelete() {
        var resourceType = getFullComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("value");
        resource.setComputeResourcePropertyType(resourceType);
        // TODO
    }

    @Test
    void deleteComputeResourceProperty_ByComputeResourceDelete() {
        var resourceType = getFullComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("value");
        resource.setComputeResourcePropertyType(resourceType);
        // TODO
    }

    @Test
    void findComputeResourcePropertiesOfAlgorithm() {
        var resourceType = getFullComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("value");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService.create(resource);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("quantumAlgorithmName");
        algo.setSpeedUp("speedUp");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.create(algo);

        computeResourcePropertyService.addComputeResourcePropertyToAlgorithm(storedAlgo.getId(), storedResource);

        var resources = computeResourcePropertyService.findComputeResourcePropertiesOfAlgorithm(
                storedAlgo.getId(), Pageable.unpaged()).getContent();

        assertThat(resources.size()).isEqualTo(1);
    }

    @Test
    void findComputeResourcePropertiesOfImplementation() {
        // TODO
    }

    @Test
    void findComputeResourcePropertiesOfComputeResource() {
        // TODO
    }

    @Test
    void addComputeResourcePropertyToAlgorithm_PropertyExists() {
        var resourceType = getFullComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("value");
        resource.setComputeResourcePropertyType(resourceType);
        var storedResource = computeResourcePropertyService.create(resource);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("quantumAlgorithmName");
        algo.setSpeedUp("speedUp");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.create(algo);

        var addedResource = computeResourcePropertyService
                .addComputeResourcePropertyToAlgorithm(storedAlgo.getId(), storedResource);

        var resultAlgo = ((QuantumAlgorithm) algorithmService.findById(algo.getId()));

        assertThat(storedResource.getId()).isEqualTo(addedResource.getId());
        assertComputeResourcePropertyEquality(storedResource, addedResource);

        assertEquals(1, resultAlgo.getRequiredComputeResourceProperties().size());
        resultAlgo.getRequiredComputeResourceProperties().forEach(resultResource -> {
            assertEquals(storedResource.getId(), resultResource.getId());
            Assertions.assertDoesNotThrow(() -> computeResourcePropertyService.findById(resultResource.getId()));
            assertEquals(storedResource.getComputeResourcePropertyType().getId(), resultResource.getComputeResourcePropertyType().getId());
        });
    }

    @Test
    void addComputeResourcePropertyToAlgorithm_PropertyNotExists() {
        // TODO
    }

    @Test
    void addComputeResourcePropertyToAlgorithm_AlgorithmNotExists() {
        // TODO
    }

    @Test
    void addComputeResourcePropertyToImplementation_PropertyExists() {
        // TODO
    }

    @Test
    void addComputeResourcePropertyToImplementation_PropertyNotExists() {
        // TODO
    }

    @Test
    void addComputeResourcePropertyToImplementation_ImplementationNotExists() {
        // TODO
    }

    @Test
    void addComputeResourcePropertyToComputeResource_PropertyExists() {
        // TODO
    }

    @Test
    void addComputeResourcePropertyToComputeResource_PropertyNotExists() {
        // TODO
    }

    @Test
    void addComputeResourcePropertyToComputeResource_ComputeResourceNotExists() {
        // TODO
    }

    private void assertComputeResourcePropertyEquality(
            ComputeResourceProperty persistedProperty, ComputeResourceProperty property) {
        assertThat(persistedProperty.getId()).isNotNull();
        assertThat(persistedProperty.getValue()).isEqualTo(property.getValue());
        assertThat(persistedProperty.getComputeResourcePropertyType().getId())
                .isEqualTo(property.getComputeResourcePropertyType().getId());
        assertThat(persistedProperty.getComputeResourcePropertyType().getName())
                .isEqualTo(property.getComputeResourcePropertyType().getName());
        assertThat(persistedProperty.getComputeResourcePropertyType().getDescription())
                .isEqualTo(property.getComputeResourcePropertyType().getDescription());
        assertThat(persistedProperty.getComputeResourcePropertyType().getDatatype())
                .isEqualTo(property.getComputeResourcePropertyType().getDatatype());
    }

    private ComputeResourceProperty getFullComputeResourceProperty(String value) {
        var computeResourceProperty = new ComputeResourceProperty();

        computeResourceProperty.setValue(value);

        return computeResourceProperty;
    }

    private ComputeResourcePropertyType getFullComputeResourcePropertyType() {
        var computeResourcePropertyType = new ComputeResourcePropertyType();

        computeResourcePropertyType.setDescription("description");
        computeResourcePropertyType.setName("computeResourcePropertyTypeName");
        computeResourcePropertyType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        return computeResourcePropertyTypeService.create(computeResourcePropertyType);
    }
}
