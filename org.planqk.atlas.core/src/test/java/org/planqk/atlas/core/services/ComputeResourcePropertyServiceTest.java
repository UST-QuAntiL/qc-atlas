/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.exceptions.InvalidResourceTypeValueException;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.model.QuantumImplementation;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.planqk.atlas.core.util.ServiceTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComputeResourcePropertyServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ComputeResourcePropertyService computeResourcePropertyService;

    @Autowired
    private ComputeResourcePropertyTypeService computeResourcePropertyTypeService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private ImplementationService implementationService;

    @Autowired
    private ComputeResourceService computeResourceService;

    @Test
    void createComputeResourceProperty() {
        var resourceType = getCreatedComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService.create(resource);

        assertThat(storedResource.getId()).isNotNull();
        ServiceTestUtils.assertComputeResourcePropertyEquality(storedResource, resource);
    }

    @Test
    void findComputeResourcePropertyById_ElementFound() {
        var resourceType = getCreatedComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService.create(resource);

        storedResource = computeResourcePropertyService.findById(storedResource.getId());

        assertThat(storedResource.getId()).isNotNull();
        ServiceTestUtils.assertComputeResourcePropertyEquality(storedResource, resource);
    }

    @Test
    void findComputeResourcePropertyById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService.findById(UUID.randomUUID()));
    }

    @Test
    void updateComputeResourcePropertyById_ElementFound() {
        var propertyType = getCreatedComputeResourcePropertyType();

        var property = getFullComputeResourceProperty("0.1");
        property.setComputeResourcePropertyType(propertyType);
        var compareProperty = getFullComputeResourceProperty("0.1");
        compareProperty.setComputeResourcePropertyType(propertyType);

        var storedProperty = computeResourcePropertyService.create(property);

        compareProperty.setId(storedProperty.getId());

        var editedPropertyType = getCreatedComputeResourcePropertyType("editComputeResourcePropertyTypeName");
        var editedValue = "0.5";
        storedProperty.setComputeResourcePropertyType(editedPropertyType);
        storedProperty.setValue(editedValue);

        var editedProperty = computeResourcePropertyService.update(storedProperty);

        assertThat(editedProperty.getId()).isEqualTo(compareProperty.getId());

        assertThat(editedProperty.getValue()).isEqualTo(editedValue);
        assertThat(editedProperty.getValue()).isNotEqualTo(compareProperty.getValue());

        assertThat(editedProperty.getComputeResourcePropertyType().getId())
            .isNotEqualTo(compareProperty.getComputeResourcePropertyType().getId());
        assertThat(editedProperty.getComputeResourcePropertyType().getId()).isEqualTo(editedPropertyType.getId());

        assertThat(editedProperty.getComputeResourcePropertyType().getName())
            .isNotEqualTo(compareProperty.getComputeResourcePropertyType().getName());
        assertThat(editedProperty.getComputeResourcePropertyType().getName()).isEqualTo(editedPropertyType.getName());

        assertThat(editedProperty.getComputeResourcePropertyType().getDescription())
            .isEqualTo(compareProperty.getComputeResourcePropertyType().getDescription());
        assertThat(editedProperty.getComputeResourcePropertyType().getDatatype())
            .isEqualTo(compareProperty.getComputeResourcePropertyType().getDatatype());
    }

    @Test
    void updateComputeResourcePropertyById_ValidationFail() {
        var propertyType = getCreatedComputeResourcePropertyType();

        var property = getFullComputeResourceProperty("0.1");
        property.setComputeResourcePropertyType(propertyType);

        var storedProperty = computeResourcePropertyService.create(property);

        var editedPropertyType = getCreatedComputeResourcePropertyType("editComputeResourcePropertyTypeName");
        var editedValue = "0-5";
        storedProperty.setComputeResourcePropertyType(editedPropertyType);
        storedProperty.setValue(editedValue);

        assertThrows(InvalidResourceTypeValueException.class, () ->
            computeResourcePropertyService.update(storedProperty));
    }

    @Test
    void updateComputeResourcePropertyById_ValidationFailTypeNotFound() {
        var propertyType = getCreatedComputeResourcePropertyType();

        var property = getFullComputeResourceProperty("0.1");
        property.setComputeResourcePropertyType(propertyType);

        var storedProperty = computeResourcePropertyService.create(property);

        var editedPropertyType = new ComputeResourcePropertyType();
        editedPropertyType.setName("editComputeResourcePropertyTypeName");
        editedPropertyType.setId(UUID.randomUUID());
        var editedValue = "0.5";
        storedProperty.setComputeResourcePropertyType(editedPropertyType);
        storedProperty.setValue(editedValue);

        assertThrows(NoSuchElementException.class, () ->
            computeResourcePropertyService.update(storedProperty));
    }

    @Test
    void updateComputeResourcePropertyById_ElementNotFound() {
        var resourceType = getCreatedComputeResourcePropertyType();
        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);
        resource.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService.update(resource));
    }

    @Test
    void deleteComputeResourceProperty_ElementFound() {
        var resourceType = getCreatedComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService.create(resource);

        assertDoesNotThrow(() -> computeResourcePropertyService.findById(storedResource.getId()));

        computeResourcePropertyService.delete(storedResource.getId());

        assertThrows(NoSuchElementException.class, () ->
            computeResourcePropertyService.findById(storedResource.getId()));
    }

    @Test
    void deleteComputeResourceProperty_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () ->
            computeResourcePropertyService.delete(UUID.randomUUID()));
    }

    @Test
    void deleteComputeResourceProperty_ByAlgorithmDelete() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumAlgorithm algorithm = getCreatedQuantumAlgorithm("quantumAlgorithmName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToAlgorithm(algorithm.getId(), resource);

        assertDoesNotThrow(() -> computeResourcePropertyService.findById(storedResource.getId()));

        algorithmService.delete(algorithm.getId());

        assertThrows(NoSuchElementException.class, () ->
            computeResourcePropertyService.findById(storedResource.getId()));
    }

    @Test
    void deleteComputeResourceProperty_ByImplementationDelete() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumImplementation implementation = getCreatedQuantumImplementation("quantumImplementationName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToImplementation(implementation.getId(), resource);

        assertDoesNotThrow(() -> computeResourcePropertyService.findById(storedResource.getId()));

        implementationService.delete(implementation.getId());

        assertThrows(NoSuchElementException.class, () ->
            computeResourcePropertyService.findById(storedResource.getId()));
    }

    @Test
    void deleteComputeResourceProperty_ByComputeResourceDelete() {
        var resourceType = getCreatedComputeResourcePropertyType();
        ComputeResource computeResource = getCreatedComputeResource("computeResourceName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToComputeResource(computeResource.getId(), resource);

        assertDoesNotThrow(() -> computeResourcePropertyService.findById(storedResource.getId()));

        computeResourceService.delete(computeResource.getId());

        assertThrows(NoSuchElementException.class, () ->
            computeResourcePropertyService.findById(storedResource.getId()));
    }

    @Test
    void findComputeResourcePropertiesOfAlgorithm() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumAlgorithm algorithm = getCreatedQuantumAlgorithm("quantumAlgorithmName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        computeResourcePropertyService.addComputeResourcePropertyToAlgorithm(algorithm.getId(), resource);

        var resources = computeResourcePropertyService.findComputeResourcePropertiesOfAlgorithm(
            algorithm.getId(), Pageable.unpaged()).getContent();

        assertThat(resources.size()).isEqualTo(1);
    }

    @Test
    void findComputeResourcePropertiesOfImplementation() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumImplementation implementation = getCreatedQuantumImplementation("quantumImplementationName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        computeResourcePropertyService.addComputeResourcePropertyToImplementation(implementation.getId(), resource);

        var resources = computeResourcePropertyService.findComputeResourcePropertiesOfImplementation(
            implementation.getId(), Pageable.unpaged()).getContent();

        assertThat(resources.size()).isEqualTo(1);
    }

    @Test
    void findComputeResourcePropertiesOfComputeResource() {
        var resourceType = getCreatedComputeResourcePropertyType();
        ComputeResource computeResource = getCreatedComputeResource("computeResourceName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        computeResourcePropertyService.addComputeResourcePropertyToComputeResource(computeResource.getId(), resource);

        var resources = computeResourcePropertyService.findComputeResourcePropertiesOfComputeResource(
            computeResource.getId(), Pageable.unpaged()).getContent();

        assertThat(resources.size()).isEqualTo(1);
    }

    @Test
    void addComputeResourcePropertyToAlgorithm_PropertyExists() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumAlgorithm algorithm = getCreatedQuantumAlgorithm("quantumAlgorithmName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);
        resource = computeResourcePropertyService.create(resource);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToAlgorithm(algorithm.getId(), resource);

        var resultAlgorithm = (QuantumAlgorithm) algorithmService.findById(algorithm.getId());

        assertThat(storedResource.getId()).isEqualTo(resource.getId());
        ServiceTestUtils.assertComputeResourcePropertyEquality(storedResource, resource);

        assertThat(resultAlgorithm.getRequiredComputeResourceProperties().size()).isEqualTo(1);
        resultAlgorithm.getRequiredComputeResourceProperties().forEach(resultResource -> {
            assertThat(storedResource.getId()).isEqualTo(resultResource.getId());
            assertDoesNotThrow(() -> computeResourcePropertyService.findById(resultResource.getId()));
            assertThat(storedResource.getComputeResourcePropertyType().getId())
                .isEqualTo(resultResource.getComputeResourcePropertyType().getId());
        });
    }

    @Test
    void addComputeResourcePropertyToAlgorithm_PropertyNotExists() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumAlgorithm algorithm = getCreatedQuantumAlgorithm("quantumAlgorithmName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToAlgorithm(algorithm.getId(), resource);

        var resultAlgorithm = (QuantumAlgorithm) algorithmService.findById(algorithm.getId());

        assertThat(storedResource.getId()).isNotNull();
        ServiceTestUtils.assertComputeResourcePropertyEquality(storedResource, resource);

        assertThat(resultAlgorithm.getRequiredComputeResourceProperties().size()).isEqualTo(1);
        resultAlgorithm.getRequiredComputeResourceProperties().forEach(resultResource -> {
            assertThat(storedResource.getId()).isEqualTo(resultResource.getId());
            assertDoesNotThrow(() -> computeResourcePropertyService.findById(resultResource.getId()));
            assertThat(storedResource.getComputeResourcePropertyType().getId())
                .isEqualTo(resultResource.getComputeResourcePropertyType().getId());
        });
    }

    @Test
    void addComputeResourcePropertyToAlgorithm_AlgorithmNotExists() {
        var resourceType = getCreatedComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService
            .addComputeResourcePropertyToAlgorithm(UUID.randomUUID(), resource));
    }

    @Test
    void addComputeResourcePropertyToImplementation_PropertyExists() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumImplementation implementation = getCreatedQuantumImplementation("quantumImplementationName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);
        resource = computeResourcePropertyService.create(resource);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToImplementation(implementation.getId(), resource);

        var resultImplementation = (QuantumImplementation) implementationService.findById(implementation.getId());

        assertThat(storedResource.getId()).isNotNull();
        ServiceTestUtils.assertComputeResourcePropertyEquality(storedResource, resource);

        assertThat(resultImplementation.getRequiredComputeResourceProperties().size()).isEqualTo(1);
        resultImplementation.getRequiredComputeResourceProperties().forEach(resultResource -> {
            assertThat(storedResource.getId()).isEqualTo(resultResource.getId());
            assertDoesNotThrow(() -> computeResourcePropertyService.findById(resultResource.getId()));
            assertThat(storedResource.getComputeResourcePropertyType().getId())
                .isEqualTo(resultResource.getComputeResourcePropertyType().getId());
        });
    }

    @Test
    void addComputeResourcePropertyToImplementation_PropertyNotExists() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumImplementation implementation = getCreatedQuantumImplementation("quantumImplementationName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToImplementation(implementation.getId(), resource);

        var resultImplementation = (QuantumImplementation) implementationService.findById(implementation.getId());

        assertThat(storedResource.getId()).isNotNull();
        ServiceTestUtils.assertComputeResourcePropertyEquality(storedResource, resource);

        assertThat(resultImplementation.getRequiredComputeResourceProperties().size()).isEqualTo(1);
        resultImplementation.getRequiredComputeResourceProperties().forEach(resultResource -> {
            assertThat(storedResource.getId()).isEqualTo(resultResource.getId());
            assertDoesNotThrow(() -> computeResourcePropertyService.findById(resultResource.getId()));
            assertThat(storedResource.getComputeResourcePropertyType().getId())
                .isEqualTo(resultResource.getComputeResourcePropertyType().getId());
        });
    }

    @Test
    void addComputeResourcePropertyToImplementation_ImplementationNotExists() {
        var resourceType = getCreatedComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService
            .addComputeResourcePropertyToImplementation(UUID.randomUUID(), resource));
    }

    @Test
    void addComputeResourcePropertyToComputeResource_PropertyExists() {
        var resourceType = getCreatedComputeResourcePropertyType();
        ComputeResource computeResource = getCreatedComputeResource("computeResourceName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);
        resource = computeResourcePropertyService.create(resource);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToComputeResource(computeResource.getId(), resource);

        var resultComputeResource = computeResourceService.findById(computeResource.getId());

        assertThat(storedResource.getId()).isNotNull();
        ServiceTestUtils.assertComputeResourcePropertyEquality(storedResource, resource);

        assertThat(resultComputeResource.getProvidedComputingResourceProperties().size()).isEqualTo(1);
        resultComputeResource.getProvidedComputingResourceProperties().forEach(resultResource -> {
            assertThat(storedResource.getId()).isEqualTo(resultResource.getId());
            assertDoesNotThrow(() -> computeResourcePropertyService.findById(resultResource.getId()));
            assertThat(storedResource.getComputeResourcePropertyType().getId())
                .isEqualTo(resultResource.getComputeResourcePropertyType().getId());
        });
    }

    @Test
    void addComputeResourcePropertyToComputeResource_PropertyNotExists() {
        var resourceType = getCreatedComputeResourcePropertyType();
        ComputeResource computeResource = getCreatedComputeResource("computeResourceName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToComputeResource(computeResource.getId(), resource);

        var resultComputeResource = computeResourceService.findById(computeResource.getId());

        assertThat(storedResource.getId()).isNotNull();
        ServiceTestUtils.assertComputeResourcePropertyEquality(storedResource, resource);

        assertThat(resultComputeResource.getProvidedComputingResourceProperties().size()).isEqualTo(1);
        resultComputeResource.getProvidedComputingResourceProperties().forEach(resultResource -> {
            assertThat(storedResource.getId()).isEqualTo(resultResource.getId());
            assertDoesNotThrow(() -> computeResourcePropertyService.findById(resultResource.getId()));
            assertThat(storedResource.getComputeResourcePropertyType().getId())
                .isEqualTo(resultResource.getComputeResourcePropertyType().getId());
        });
    }

    @Test
    void addComputeResourcePropertyToComputeResource_ComputeResourceNotExists() {
        var resourceType = getCreatedComputeResourcePropertyType();

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService
            .addComputeResourcePropertyToComputeResource(UUID.randomUUID(), resource));
    }

    @Test
    void checkIfComputeResourcePropertyIsOfAlgorithm_IsOfElement() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumAlgorithm algorithm = getCreatedQuantumAlgorithm("quantumAlgorithmName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToAlgorithm(algorithm.getId(), resource);

        var resultAlgorithm = (QuantumAlgorithm) algorithmService.findById(algorithm.getId());

        assertDoesNotThrow(() -> computeResourcePropertyService
            .checkIfComputeResourcePropertyIsOfAlgorithm(resultAlgorithm.getId(), storedResource.getId()));
    }

    @Test
    void checkIfComputeResourcePropertyIsOfAlgorithm_IsOfDifferentElement() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumAlgorithm algorithm = getCreatedQuantumAlgorithm("quantumAlgorithmName");
        QuantumImplementation implementation = getCreatedQuantumImplementation("quantumImplementationName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToImplementation(implementation.getId(), resource);

        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService
            .checkIfComputeResourcePropertyIsOfAlgorithm(algorithm.getId(), storedResource.getId()));
    }

    @Test
    void checkIfComputeResourcePropertyIsOfAlgorithm_IsNotOfElement() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumAlgorithm algorithm = getCreatedQuantumAlgorithm("quantumAlgorithmName");
        QuantumAlgorithm resourceAlgorithm = getCreatedQuantumAlgorithm("quantumResourceAlgorithmName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToAlgorithm(resourceAlgorithm.getId(), resource);

        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService
            .checkIfComputeResourcePropertyIsOfAlgorithm(algorithm.getId(), storedResource.getId()));
    }

    @Test
    void checkIfComputeResourcePropertyIsOfImplementation_IsOfElement() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumImplementation implementation = getCreatedQuantumImplementation("quantumImplementationName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToImplementation(implementation.getId(), resource);

        var resultImplementation = (QuantumImplementation) implementationService.findById(implementation.getId());

        assertDoesNotThrow(() -> computeResourcePropertyService
            .checkIfComputeResourcePropertyIsOfImplementation(resultImplementation.getId(), storedResource.getId()));
    }

    @Test
    void checkIfComputeResourcePropertyIsOfImplementation_IsOfDifferentElement() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumImplementation implementation = getCreatedQuantumImplementation("quantumImplementationName");
        QuantumAlgorithm algorithm = getCreatedQuantumAlgorithm("quantumAlgorithmName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToAlgorithm(algorithm.getId(), resource);

        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService
            .checkIfComputeResourcePropertyIsOfImplementation(implementation.getId(), storedResource.getId()));
    }

    @Test
    void checkIfComputeResourcePropertyIsOfImplementation_IsNotOfElement() {
        var resourceType = getCreatedComputeResourcePropertyType();
        QuantumImplementation implementation = getCreatedQuantumImplementation("quantumImplementationName");
        QuantumImplementation resourceImplementation = getCreatedQuantumImplementation("quantumResourceImplementationName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToImplementation(resourceImplementation.getId(), resource);

        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService
            .checkIfComputeResourcePropertyIsOfImplementation(implementation.getId(), storedResource.getId()));
    }

    @Test
    void checkIfComputeResourcePropertyIsOfComputeResource_IsOfElement() {
        var resourceType = getCreatedComputeResourcePropertyType();
        ComputeResource computeResource = getCreatedComputeResource("computeResourceName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToComputeResource(computeResource.getId(), resource);

        var resultComputeResource = computeResourceService.findById(computeResource.getId());

        assertDoesNotThrow(() -> computeResourcePropertyService
            .checkIfComputeResourcePropertyIsOfComputeResource(resultComputeResource.getId(), storedResource.getId()));
    }

    @Test
    void checkIfComputeResourcePropertyIsOfComputeResource_IsOfDifferentElement() {
        var resourceType = getCreatedComputeResourcePropertyType();
        ComputeResource computeResource = getCreatedComputeResource("computeResourceName");
        QuantumAlgorithm algorithm = getCreatedQuantumAlgorithm("quantumAlgorithmName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToAlgorithm(algorithm.getId(), resource);

        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService
            .checkIfComputeResourcePropertyIsOfComputeResource(computeResource.getId(), storedResource.getId()));
    }

    @Test
    void checkIfComputeResourcePropertyIsOfComputeResource_IsNotOfElement() {
        var resourceType = getCreatedComputeResourcePropertyType();
        ComputeResource computeResource = getCreatedComputeResource("computeResourceName");
        ComputeResource resourceComputeResource = getCreatedComputeResource("resourceComputeResourceName");

        var resource = getFullComputeResourceProperty("0.1");
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = computeResourcePropertyService
            .addComputeResourcePropertyToComputeResource(resourceComputeResource.getId(), resource);

        assertThrows(NoSuchElementException.class, () -> computeResourcePropertyService
            .checkIfComputeResourcePropertyIsOfComputeResource(computeResource.getId(), storedResource.getId()));
    }

    private ComputeResourceProperty getFullComputeResourceProperty(String value) {
        var computeResourceProperty = new ComputeResourceProperty();

        computeResourceProperty.setValue(value);

        return computeResourceProperty;
    }

    private ComputeResourcePropertyType getCreatedComputeResourcePropertyType() {
        var computeResourcePropertyType = getCreatedComputeResourcePropertyType("computeResourcePropertyTypeName");

        return computeResourcePropertyTypeService.create(computeResourcePropertyType);
    }

    private ComputeResourcePropertyType getCreatedComputeResourcePropertyType(String name) {
        var computeResourcePropertyType = new ComputeResourcePropertyType();

        computeResourcePropertyType.setDescription("description");
        computeResourcePropertyType.setName(name);
        computeResourcePropertyType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        return computeResourcePropertyTypeService.create(computeResourcePropertyType);
    }

    private QuantumAlgorithm getCreatedQuantumAlgorithm(String name) {
        QuantumAlgorithm algorithm = new QuantumAlgorithm();

        algorithm.setName(name);
        algorithm.setComputationModel(ComputationModel.QUANTUM);
        algorithm.setQuantumComputationModel(QuantumComputationModel.GATE_BASED);

        return (QuantumAlgorithm) algorithmService.create(algorithm);
    }

    private QuantumImplementation getCreatedQuantumImplementation(String name) {
        QuantumAlgorithm algorithm = getCreatedQuantumAlgorithm("quantumAlgorithmName");
        QuantumImplementation implementation = new QuantumImplementation();

        implementation.setName(name);
        implementation.setImplementedAlgorithm(algorithm);

        return (QuantumImplementation) implementationService.create(implementation, algorithm.getId());
    }

    private ComputeResource getCreatedComputeResource(String name) {
        ComputeResource computeResource = new ComputeResource();

        computeResource.setName(name);
        computeResource.setQuantumComputationModel(QuantumComputationModel.GATE_BASED);

        return computeResourceService.create(computeResource);
    }
}
