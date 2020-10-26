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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.planqk.atlas.core.util.ServiceTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComputeResourceServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ComputeResourceService computeResourceService;

    @Autowired
    private ComputeResourcePropertyService computeResourcePropertyService;

    @Autowired
    private ComputeResourcePropertyTypeService computeResourcePropertyTypeService;

    @Autowired
    private SoftwarePlatformService softwarePlatformService;

    @Autowired
    private CloudServiceService cloudServiceService;

    @Autowired
    private LinkingService linkingService;

    @Test
    void createComputeResource() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");

        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        assertThat(storedComputeResource.getId()).isNotNull();
        ServiceTestUtils.assertComputeResourceEquality(storedComputeResource, computeResource);
    }

    @Test
    void createComputeResource_WithComputingResourceProperty() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");
        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        assertDoesNotThrow(() -> computeResourceService.findById(storedComputeResource.getId()));

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
        assertThrows(NoSuchElementException.class, () ->
            computeResourceService.findById(UUID.randomUUID()));
    }

    @Test
    void findComputeResourceById_ElementFound() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");

        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        storedComputeResource = computeResourceService.findById(storedComputeResource.getId());

        assertThat(storedComputeResource.getId()).isNotNull();
        ServiceTestUtils.assertComputeResourceEquality(storedComputeResource, computeResource);
    }

    @Test
    void updateComputeResource_ElementNotFound() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");
        computeResource.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () ->
            computeResourceService.update(computeResource));
    }

    @Test
    void updateComputeResource_ElementFound() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");
        ComputeResource compareComputeResource = getFullComputeResource("computeResourceName");

        var storedComputeResource = computeResourceService.create(computeResource);
        compareComputeResource.setId(storedComputeResource.getId());
        String editName = "editedComputeResourceName";
        storedComputeResource.setName(editName);

        var editedComputeResource = computeResourceService.update(storedComputeResource);

        assertThat(editedComputeResource.getId()).isNotNull();
        assertThat(editedComputeResource.getId()).isEqualTo(compareComputeResource.getId());
        assertThat(editedComputeResource.getName()).isNotEqualTo(compareComputeResource.getName());
        assertThat(editedComputeResource.getName()).isEqualTo(editName);
        assertThat(editedComputeResource.getQuantumComputationModel()).isEqualTo(compareComputeResource.getQuantumComputationModel());
        assertThat(editedComputeResource.getTechnology()).isEqualTo(compareComputeResource.getTechnology());
        assertThat(editedComputeResource.getVendor()).isEqualTo(compareComputeResource.getVendor());
    }

    @Test
    void deleteComputeResource_NoReferences() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");

        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        assertDoesNotThrow(() -> computeResourceService.findById(storedComputeResource.getId()));

        computeResourceService.delete(storedComputeResource.getId());

        assertThrows(NoSuchElementException.class, () ->
            computeResourceService.findById(storedComputeResource.getId()));
    }

    @Test
    void deleteComputeResource_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> computeResourceService.delete(UUID.randomUUID()));
    }

    // TODO write one with link to cloud service and/or software platform
    @Test
    void deleteComputeResource_HasReferences() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");
        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        assertDoesNotThrow(() -> computeResourceService.findById(storedComputeResource.getId()));

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

        assertThrows(NoSuchElementException.class, () ->
            computeResourceService.findById(storedComputeResource.getId()));

        // Test if references are removed
        assertThrows(NoSuchElementException.class, () ->
            computeResourcePropertyService.findById(storedProperty.getId()));
    }

    @Test
    void findLinkedSoftwarePlatforms() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");
        computeResource = computeResourceService.create(computeResource);

        SoftwarePlatform softwarePlatform1 = new SoftwarePlatform();
        softwarePlatform1.setName("softwarePlatformName1");
        softwarePlatform1 = softwarePlatformService.create(softwarePlatform1);
        linkingService.linkSoftwarePlatformAndComputeResource(softwarePlatform1.getId(), computeResource.getId());
        SoftwarePlatform softwarePlatform2 = new SoftwarePlatform();
        softwarePlatform2.setName("softwarePlatformName1");
        softwarePlatform2 = softwarePlatformService.create(softwarePlatform2);
        linkingService.linkSoftwarePlatformAndComputeResource(softwarePlatform2.getId(), computeResource.getId());

        var softwarePlatforms = computeResourceService
            .findLinkedSoftwarePlatforms(computeResource.getId(), Pageable.unpaged());

        assertThat(softwarePlatforms.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findLinkedCloudServices() {
        ComputeResource computeResource = getFullComputeResource("computeResourceName");
        computeResource = computeResourceService.create(computeResource);

        CloudService cloudService1 = new CloudService();
        cloudService1.setName("cloudServiceName1");
        cloudService1 = cloudServiceService.create(cloudService1);
        linkingService.linkCloudServiceAndComputeResource(cloudService1.getId(), computeResource.getId());
        CloudService cloudService2 = new CloudService();
        cloudService2.setName("cloudServiceName1");
        cloudService2 = cloudServiceService.create(cloudService2);
        linkingService.linkCloudServiceAndComputeResource(cloudService2.getId(), computeResource.getId());

        var cloudServices = computeResourceService
            .findLinkedCloudServices(computeResource.getId(), Pageable.unpaged());

        assertThat(cloudServices.getTotalElements()).isEqualTo(2);
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
