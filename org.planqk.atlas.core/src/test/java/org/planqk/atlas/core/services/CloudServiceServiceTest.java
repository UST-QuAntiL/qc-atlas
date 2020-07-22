/********************************************************************************
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudServiceServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private CloudServiceService cloudServiceService;
    @Autowired
    private ComputeResourceService computeResourceService;

    @Test
    void createMinimalCloudService() {
        CloudService cloudService = new CloudService();
        cloudService.setName("test cloud service");

        CloudService storedCloudService = cloudServiceService.save(cloudService);
        assertCloudServiceEquality(storedCloudService, cloudService);
    }

    @Test
    void createMaximalCloudService() {
        CloudService cloudService = getTestCloudService("test cloud service");

        CloudService storedCloudService = cloudServiceService.save(cloudService);
        assertCloudServiceEquality(storedCloudService, cloudService);
    }

    @Test
    void addComputeResourceReference() {
        CloudService cloudService = getTestCloudService("test cloud service");
        CloudService storedCloudService = cloudServiceService.save(cloudService);

        ComputeResource computeResource = new ComputeResource();
        computeResource.setName("test compute resource");
        computeResource.setVendor("test vendor");
        computeResource.setTechnology("test technology");
        computeResource.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
        ComputeResource storedComputeResource = computeResourceService.save(computeResource);

        cloudServiceService.addComputeResourceReference(
                storedCloudService.getId(), storedComputeResource.getId());

        Set<ComputeResource> computeResources = cloudServiceService.findComputeResources(
                storedCloudService.getId(), Pageable.unpaged()).toSet();

        assertThat(computeResources.size()).isEqualTo(1);
        assertThat(computeResources.contains(storedComputeResource)).isTrue();
    }

    @Test
    void updateCloudService_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                cloudServiceService.update(UUID.randomUUID(), null));
    }

    @Test
    void updateCloudService_ElementFound() {
        CloudService cloudService = getTestCloudService("test cloud service");
        CloudService storedCloudService = getTestCloudService("test cloud service");

        CloudService storedEditedCloudService = cloudServiceService.save(cloudService);
        storedCloudService.setId(storedEditedCloudService.getId());
        String editName = "edited cloud service";
        storedEditedCloudService.setName(editName);
        storedEditedCloudService = cloudServiceService.update(storedEditedCloudService.getId(), storedEditedCloudService);

        assertThat(storedEditedCloudService.getId()).isEqualTo(storedCloudService.getId());
        assertThat(storedEditedCloudService.getName()).isNotEqualTo(storedCloudService.getName());
        assertThat(storedEditedCloudService.getName()).isEqualTo(editName);
        assertThat(storedEditedCloudService.getProvider()).isEqualTo(storedCloudService.getProvider());
        assertThat(storedEditedCloudService.getUrl()).isEqualTo(storedCloudService.getUrl());
        assertThat(storedEditedCloudService.getCostModel()).isEqualTo(storedCloudService.getCostModel());
    }

    @Test
    void findCloudServiceById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
            cloudServiceService.findById(UUID.randomUUID()));
    }

    @Test
    void findCloudServiceById_ElementFound() {
        CloudService cloudService = getTestCloudService("test cloud service");

        CloudService storedCloudService = cloudServiceService.save(cloudService);

        storedCloudService = cloudServiceService.findById(storedCloudService.getId());

        assertCloudServiceEquality(storedCloudService, cloudService);
    }

    @Test
    void findAll() {
        CloudService cloudService1 = getTestCloudService("test cloud service1");
        cloudServiceService.save(cloudService1);
        CloudService cloudService2 = getTestCloudService("test cloud service2");
        cloudServiceService.save(cloudService2);

        List<CloudService> cloudServices = cloudServiceService.findAll(Pageable.unpaged()).getContent();

        assertThat(cloudServices.size()).isEqualTo(2);
    }

    @Test
    void deleteCloudService_NoReferences() {
        CloudService cloudService = getTestCloudService("test cloud service");

        CloudService storedCloudService = cloudServiceService.save(cloudService);

        Assertions.assertDoesNotThrow(() -> cloudServiceService.findById(storedCloudService.getId()));

        cloudServiceService.delete(storedCloudService.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
            cloudServiceService.findById(storedCloudService.getId()));
    }

    @Test
    void deleteSoftwarePlatform_HasReferences() {
        CloudService cloudService = getTestCloudService("test cloud service");
        CloudService storedCloudService = cloudServiceService.save(cloudService);

        Assertions.assertDoesNotThrow(() -> cloudServiceService.findById(storedCloudService.getId()));

        // Add Compute Resource Reference
        ComputeResource computeResource = new ComputeResource();
        computeResource.setName("test compute resource");
        ComputeResource storedComputeResource = computeResourceService.save(computeResource);
        cloudServiceService.addComputeResourceReference(storedCloudService.getId(), storedComputeResource.getId());

        // Delete
        cloudServiceService.delete(storedCloudService.getId());
        Assertions.assertThrows(NoSuchElementException.class, () ->
                cloudServiceService.findById(storedCloudService.getId()));

        // Test if references are removed
        assertThat(computeResourceService.findById(storedComputeResource.getId()).getCloudServices().size()).isEqualTo(0);
    }

    @Test
    void deleteComputeResourceReference() {
        CloudService cloudService = getTestCloudService("test cloud service");
        CloudService storedCloudService = cloudServiceService.save(cloudService);

        ComputeResource computeResource = new ComputeResource();
        computeResource.setName("test compute resource");
        computeResource.setVendor("test vendor");
        computeResource.setTechnology("test technology");
        computeResource.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
        ComputeResource storedComputeResource = computeResourceService.save(computeResource);

        cloudServiceService.addComputeResourceReference(
                storedCloudService.getId(), storedComputeResource.getId());

        Set<ComputeResource> computeResources = cloudServiceService.findComputeResources(
                storedCloudService.getId(), Pageable.unpaged()).toSet();
        assertThat(computeResources.size()).isEqualTo(1);

        cloudServiceService.deleteComputeResourceReference(
                storedCloudService.getId(), storedComputeResource.getId());

        computeResources = cloudServiceService.findComputeResources(
                storedCloudService.getId(), Pageable.unpaged()).toSet();
        assertThat(computeResources.size()).isEqualTo(0);
    }

    private void assertCloudServiceEquality(CloudService dbCloudService, CloudService compareCloudService) {
        assertThat(dbCloudService.getId()).isNotNull();
        assertThat(dbCloudService.getName()).isEqualTo(compareCloudService.getName());
        assertThat(dbCloudService.getProvider()).isEqualTo(compareCloudService.getProvider());
        assertThat(dbCloudService.getUrl()).isEqualTo(compareCloudService.getUrl());
        assertThat(dbCloudService.getCostModel()).isEqualTo(compareCloudService.getCostModel());
    }

    private CloudService getTestCloudService(String name) {
        CloudService cloudService = new CloudService();
        cloudService.setName(name);
        cloudService.setProvider("test provider");
        try {
            cloudService.setUrl(new URL("http://example.com"));
        } catch (MalformedURLException ignored){}
        cloudService.setCostModel("test cost model");
        return cloudService;
    }
}
