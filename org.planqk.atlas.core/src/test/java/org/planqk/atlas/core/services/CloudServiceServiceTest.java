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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.planqk.atlas.core.util.ServiceTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloudServiceServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private CloudServiceService cloudServiceService;

    @Autowired
    private ComputeResourceService computeResourceService;

    @Autowired
    private SoftwarePlatformService softwarePlatformService;

    @Autowired
    private LinkingService linkingService;

    @Test
    void createCloudService() {
        CloudService cloudService = getFullCloudService("cloudServiceName");

        CloudService storedCloudService = cloudServiceService.create(cloudService);

        assertThat(storedCloudService.getId()).isNotNull();
        ServiceTestUtils.assertCloudServiceEquality(storedCloudService, cloudService);
    }

    @Test
    void createComputeResource_WithComputeResource() {

    }

    @Test
    void searchAllCloudServicesByName() {
        CloudService cloudService1 = getFullCloudService("cloudServiceName1");
        cloudServiceService.create(cloudService1);
        CloudService cloudService2 = getFullCloudService("cloudServiceName2");
        cloudServiceService.create(cloudService2);

        List<CloudService> cloudServices = cloudServiceService.searchAllByName("1", Pageable.unpaged()).getContent();

        assertThat(cloudServices.size()).isEqualTo(1);
    }

    @Test
    void findAllCloudServices() {
        CloudService cloudService1 = getFullCloudService("cloudServiceName1");
        cloudServiceService.create(cloudService1);
        CloudService cloudService2 = getFullCloudService("cloudServiceName2");
        cloudServiceService.create(cloudService2);

        List<CloudService> cloudServices = cloudServiceService.findAll(Pageable.unpaged()).getContent();

        assertThat(cloudServices.size()).isEqualTo(2);
    }

    @Test
    void findCloudServiceById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                cloudServiceService.findById(UUID.randomUUID()));
    }

    @Test
    void findCloudServiceById_ElementFound() {
        CloudService cloudService = getFullCloudService("cloudServiceName");

        CloudService storedCloudService = cloudServiceService.create(cloudService);

        storedCloudService = cloudServiceService.findById(storedCloudService.getId());

        assertThat(storedCloudService.getId()).isNotNull();
        ServiceTestUtils.assertCloudServiceEquality(storedCloudService, cloudService);
    }

    @Test
    void updateCloudService_ElementNotFound() {
        CloudService cloudService = getFullCloudService("cloudServiceName");
        cloudService.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () ->
                cloudServiceService.update(cloudService));
    }

    @Test
    void updateCloudService_ElementFound() {
        CloudService cloudService = getFullCloudService("cloudServiceName");
        CloudService storedCloudService = getFullCloudService("cloudServiceName");

        CloudService storedEditedCloudService = cloudServiceService.create(cloudService);
        storedCloudService.setId(storedEditedCloudService.getId());
        String editName = "editedCloudServiceName";
        storedEditedCloudService.setName(editName);
        storedEditedCloudService = cloudServiceService.update(storedEditedCloudService);

        assertThat(storedEditedCloudService.getId()).isEqualTo(storedCloudService.getId());
        assertThat(storedEditedCloudService.getName()).isNotEqualTo(storedCloudService.getName());
        assertThat(storedEditedCloudService.getName()).isEqualTo(editName);
        assertThat(storedEditedCloudService.getProvider()).isEqualTo(storedCloudService.getProvider());
        assertThat(storedEditedCloudService.getUrl()).isEqualTo(storedCloudService.getUrl());
        assertThat(storedEditedCloudService.getCostModel()).isEqualTo(storedCloudService.getCostModel());
    }

    @Test
    void deleteCloudService_NoReferences() {
        CloudService cloudService = getFullCloudService("cloudServiceName");

        CloudService storedCloudService = cloudServiceService.create(cloudService);

        assertDoesNotThrow(() -> cloudServiceService.findById(storedCloudService.getId()));

        cloudServiceService.delete(storedCloudService.getId());

        assertThrows(NoSuchElementException.class, () ->
                cloudServiceService.findById(storedCloudService.getId()));
    }

    @Test
    void deleteCloudService_HasReferences() {
        CloudService cloudService = getFullCloudService("cloudServiceName");
        CloudService storedCloudService = cloudServiceService.create(cloudService);

        assertDoesNotThrow(() -> cloudServiceService.findById(storedCloudService.getId()));

        // Link ComputeResource
        ComputeResource computeResource = new ComputeResource();
        computeResource.setName("computeResource");
        var storedComputeResource = computeResourceService.create(computeResource);
        linkingService.linkCloudServiceAndComputeResource(storedCloudService.getId(), computeResource.getId());

        // Link SoftwarePlatform
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setName("softwarePlatformName1");
        var storedSoftwarePlatform = softwarePlatformService.create(softwarePlatform);
        linkingService.linkSoftwarePlatformAndCloudService(softwarePlatform.getId(), cloudService.getId());

        // Delete
        cloudServiceService.delete(storedCloudService.getId());
        assertThrows(NoSuchElementException.class, () ->
                cloudServiceService.findById(storedCloudService.getId()));

        // Test if links are removed
        assertThat(computeResourceService.findById(storedComputeResource.getId())
                .getCloudServices().size()).isEqualTo(0);
        assertThat(softwarePlatformService.findById(storedSoftwarePlatform.getId())
                .getSupportedCloudServices().size()).isEqualTo(0);
    }

    @Test
    void deleteComputeResourceReference() {
        CloudService cloudService = getFullCloudService("cloudServiceName");
        CloudService storedCloudService = cloudServiceService.create(cloudService);

        ComputeResource computeResource = new ComputeResource();
        computeResource.setName("computeResource");
        computeResource.setVendor("vendor");
        computeResource.setTechnology("technology");
        computeResource.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
        ComputeResource storedComputeResource = computeResourceService.create(computeResource);

        linkingService.linkCloudServiceAndComputeResource(
                storedCloudService.getId(), storedComputeResource.getId());

        Set<ComputeResource> computeResources = cloudServiceService.findLinkedComputeResources(
                storedCloudService.getId(), Pageable.unpaged()).toSet();
        assertThat(computeResources.size()).isEqualTo(1);

        linkingService.unlinkCloudServiceAndComputeResource(
                storedCloudService.getId(), storedComputeResource.getId());

        computeResources = cloudServiceService.findLinkedComputeResources(
                storedCloudService.getId(), Pageable.unpaged()).toSet();
        assertThat(computeResources.size()).isEqualTo(0);
    }

    @Test
    void findLinkedSoftwarePlatforms() {
        CloudService cloudService = getFullCloudService("cloudServiceName");
        cloudService = cloudServiceService.create(cloudService);

        SoftwarePlatform softwarePlatform1 = new SoftwarePlatform();
        softwarePlatform1.setName("softwarePlatformName1");
        softwarePlatform1 = softwarePlatformService.create(softwarePlatform1);
        linkingService.linkSoftwarePlatformAndCloudService(softwarePlatform1.getId(), cloudService.getId());
        SoftwarePlatform softwarePlatform2 = new SoftwarePlatform();
        softwarePlatform2.setName("softwarePlatformName1");
        softwarePlatform2 = softwarePlatformService.create(softwarePlatform2);
        linkingService.linkSoftwarePlatformAndCloudService(softwarePlatform2.getId(), cloudService.getId());

        var softwarePlatforms = cloudServiceService
                .findLinkedSoftwarePlatforms(cloudService.getId(), Pageable.unpaged());

        assertThat(softwarePlatforms.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findLinkedComputeResources() {
        CloudService cloudService = getFullCloudService("cloudServiceName");
        cloudService = cloudServiceService.create(cloudService);

        ComputeResource computeResource1 = new ComputeResource();
        computeResource1.setName("cloudServiceName1");
        computeResource1 = computeResourceService.create(computeResource1);
        linkingService.linkCloudServiceAndComputeResource(cloudService.getId(), computeResource1.getId());
        ComputeResource computeResource2 = new ComputeResource();
        computeResource2.setName("cloudServiceName1");
        computeResource2 = computeResourceService.create(computeResource2);
        linkingService.linkCloudServiceAndComputeResource(cloudService.getId(), computeResource2.getId());

        var computeResources = cloudServiceService
                .findLinkedComputeResources(cloudService.getId(), Pageable.unpaged());

        assertThat(computeResources.getTotalElements()).isEqualTo(2);
    }

    private CloudService getFullCloudService(String name) {
        CloudService cloudService = new CloudService();
        cloudService.setName(name);
        cloudService.setProvider("provider");
        try {
            cloudService.setUrl(new URL("http://example.com"));
        } catch (MalformedURLException ignored) {
        }
        cloudService.setCostModel("costModel");
        return cloudService;
    }
}
