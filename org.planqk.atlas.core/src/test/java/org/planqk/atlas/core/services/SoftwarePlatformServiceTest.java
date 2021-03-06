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
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.planqk.atlas.core.util.ServiceTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SoftwarePlatformServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private SoftwarePlatformService softwarePlatformService;

    @Autowired
    private CloudServiceService cloudServiceService;

    @Autowired
    private ComputeResourceService computeResourceService;

    @Autowired
    private ImplementationService implementationService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private LinkingService linkingService;

    @Test
    void createSoftwarePlatform() {
        SoftwarePlatform softwarePlatform = getFullSoftwarePlatform("softwarePlatformName");

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.create(softwarePlatform);

        assertThat(storedSoftwarePlatform.getId()).isNotNull();
        ServiceTestUtils.assertSoftwarePlatformEquality(storedSoftwarePlatform, softwarePlatform);
    }

    @Test
    void findAllSoftwarePlatforms() {
        SoftwarePlatform softwarePlatform1 = getFullSoftwarePlatform("softwarePlatformName1");
        softwarePlatformService.create(softwarePlatform1);
        SoftwarePlatform softwarePlatform2 = getFullSoftwarePlatform("softwarePlatformName2");
        softwarePlatformService.create(softwarePlatform2);

        List<SoftwarePlatform> softwarePlatforms = softwarePlatformService.findAll(Pageable.unpaged()).getContent();

        assertThat(softwarePlatforms.size()).isEqualTo(2);
    }

    @Test
    void searchAllSoftwarePlatformsByName() {
        SoftwarePlatform softwarePlatform1 = getFullSoftwarePlatform("softwarePlatformName1");
        softwarePlatformService.create(softwarePlatform1);
        SoftwarePlatform softwarePlatform2 = getFullSoftwarePlatform("softwarePlatformName2");
        softwarePlatformService.create(softwarePlatform2);

        List<SoftwarePlatform> softwarePlatforms = softwarePlatformService
                .searchAllByName("1", Pageable.unpaged()).getContent();

        assertThat(softwarePlatforms.size()).isEqualTo(1);
    }

    @Test
    void findSoftwarePlatformById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.findById(UUID.randomUUID()));
    }

    @Test
    void findSoftwarePlatformById_ElementFound() {
        SoftwarePlatform softwarePlatform = getFullSoftwarePlatform("softwarePlatformName");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.create(softwarePlatform);

        storedSoftwarePlatform = softwarePlatformService.findById(storedSoftwarePlatform.getId());

        assertThat(storedSoftwarePlatform.getId()).isNotNull();
        ServiceTestUtils.assertSoftwarePlatformEquality(storedSoftwarePlatform, softwarePlatform);
    }

    @Test
    void updateSoftwarePlatform_ElementNotFound() {
        SoftwarePlatform softwarePlatform = getFullSoftwarePlatform("softwarePlatformName");
        softwarePlatform.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.update(softwarePlatform));
    }

    @Test
    void updateSoftwarePlatform_ElementFound() {
        SoftwarePlatform softwarePlatform = getFullSoftwarePlatform("softwarePlatformName");
        SoftwarePlatform storedSoftwarePlatform = getFullSoftwarePlatform("softwarePlatformName");

        SoftwarePlatform storedEditedSoftwarePlatform = softwarePlatformService.create(softwarePlatform);
        storedSoftwarePlatform.setId(storedEditedSoftwarePlatform.getId());
        String editName = "editedSoftwarePlatformName";
        storedEditedSoftwarePlatform.setName(editName);
        storedEditedSoftwarePlatform = softwarePlatformService.update(storedEditedSoftwarePlatform);

        assertThat(storedEditedSoftwarePlatform.getId()).isEqualTo(storedSoftwarePlatform.getId());
        assertThat(storedEditedSoftwarePlatform.getName()).isNotEqualTo(storedSoftwarePlatform.getName());
        assertThat(storedEditedSoftwarePlatform.getName()).isEqualTo(editName);
        assertThat(storedEditedSoftwarePlatform.getLink()).isEqualTo(storedSoftwarePlatform.getLink());
        assertThat(storedEditedSoftwarePlatform.getVersion()).isEqualTo(storedSoftwarePlatform.getVersion());
        assertThat(storedEditedSoftwarePlatform.getLicence()).isEqualTo(storedSoftwarePlatform.getLicence());
    }

    @Test
    void deleteSoftwarePlatform_NoReferences() {
        SoftwarePlatform softwarePlatform = getFullSoftwarePlatform("softwarePlatformName");

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.create(softwarePlatform);

        assertDoesNotThrow(() -> softwarePlatformService.findById(storedSoftwarePlatform.getId()));

        softwarePlatformService.delete(storedSoftwarePlatform.getId());

        assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.findById(storedSoftwarePlatform.getId()));
    }

    @Test
    void deleteSoftwarePlatform_HasReferences() {
        SoftwarePlatform softwarePlatform = getFullSoftwarePlatform("softwarePlatformName");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.create(softwarePlatform);

        assertDoesNotThrow(() -> softwarePlatformService.findById(storedSoftwarePlatform.getId()));

        // Add Implementation Reference
        Algorithm algorithm = new Algorithm();
        algorithm = algorithmService.create(algorithm);

        Implementation implementation = new Implementation();
        implementation.setName("implementationName");
        Implementation storedImplementation = implementationService.create(implementation, algorithm.getId());
        linkingService.linkImplementationAndSoftwarePlatform(storedImplementation.getId(), storedSoftwarePlatform.getId());

        // Add Cloud Service Reference
        CloudService cloudService = new CloudService();
        cloudService.setName("cloudServiceName");
        CloudService storedCloudService = cloudServiceService.create(cloudService);
        linkingService.linkSoftwarePlatformAndCloudService(storedSoftwarePlatform.getId(), storedCloudService.getId());

        // Add Compute Resource Reference
        ComputeResource computeResource = new ComputeResource();
        computeResource.setName("ComputeResourceName");
        ComputeResource storedComputeResource = computeResourceService.create(computeResource);
        linkingService.linkSoftwarePlatformAndComputeResource(storedSoftwarePlatform.getId(), storedComputeResource.getId());

        // Delete
        softwarePlatformService.delete(storedSoftwarePlatform.getId());

        assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.findById(storedSoftwarePlatform.getId()));

        // Test if references are removed
        assertThat(implementationService.findById(storedImplementation.getId()).getSoftwarePlatforms().size()).isEqualTo(0);
        assertThat(cloudServiceService.findById(storedCloudService.getId()).getSoftwarePlatforms().size()).isEqualTo(0);
        assertThat(computeResourceService.findById(storedComputeResource.getId()).getSoftwarePlatforms().size()).isEqualTo(0);
    }

    @Test
    void findLinkedImplementations() {
        SoftwarePlatform softwarePlatform = getFullSoftwarePlatform("softwarePlatformName");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.create(softwarePlatform);

        Algorithm algorithm = new Algorithm();
        algorithm = algorithmService.create(algorithm);

        Set<Implementation> storedImplementations = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Implementation implementation = new Implementation();
            implementation.setName("implementationName" + i);
            Implementation storedImplementation = implementationService.create(implementation, algorithm.getId());
            storedImplementations.add(storedImplementation);
            linkingService.linkImplementationAndSoftwarePlatform(storedImplementation.getId(), storedSoftwarePlatform.getId());
        }
        Set<Implementation> implementations = softwarePlatformService.findLinkedImplementations(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(implementations.size()).isEqualTo(10);
        implementations.forEach(implementation -> assertThat(storedImplementations.contains(implementation)).isTrue());
    }

    @Test
    void findLinkedCloudServices() {
        SoftwarePlatform softwarePlatform = getFullSoftwarePlatform("softwarePlatformName");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.create(softwarePlatform);

        Set<CloudService> storedCloudServices = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            CloudService cloudService = new CloudService();
            cloudService.setName("cloudServiceName" + i);
            cloudService.setProvider("provider");
            try {
                cloudService.setUrl(new URL("http://example.com"));
            } catch (MalformedURLException ignored) {
            }
            cloudService.setCostModel("costModel");
            CloudService storedCloudService = cloudServiceService.create(cloudService);
            storedCloudServices.add(storedCloudService);
            linkingService.linkSoftwarePlatformAndCloudService(storedSoftwarePlatform.getId(), storedCloudService.getId());
        }
        Set<CloudService> cloudServices = softwarePlatformService.findLinkedCloudServices(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(cloudServices.size()).isEqualTo(10);
        cloudServices.forEach(cloudService -> assertThat(storedCloudServices.contains(cloudService)).isTrue());
    }

    @Test
    void findLinkedComputeResources() {
        SoftwarePlatform softwarePlatform = getFullSoftwarePlatform("softwarePlatformName");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.create(softwarePlatform);

        Set<ComputeResource> storedComputeResources = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            ComputeResource computeResource = new ComputeResource();
            computeResource.setName("computeResource" + i);
            computeResource.setVendor("vendor");
            computeResource.setTechnology("technology");
            computeResource.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
            ComputeResource storedComputeResource = computeResourceService.create(computeResource);
            storedComputeResources.add(storedComputeResource);
            linkingService.linkSoftwarePlatformAndComputeResource(storedSoftwarePlatform.getId(), storedComputeResource.getId());
        }
        Set<ComputeResource> computeResources = softwarePlatformService.findLinkedComputeResources(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(computeResources.size()).isEqualTo(10);
        computeResources.forEach(computeResource -> assertThat(storedComputeResources.contains(computeResource)).isTrue());
    }

    @Test
    void checkIfImplementationIsLinkedToSoftwarePlatform_IsLinked() {
        SoftwarePlatform softwarePlatform = getFullSoftwarePlatform("softwarePlatformName");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.create(softwarePlatform);

        Algorithm algorithm = new Algorithm();
        algorithm = algorithmService.create(algorithm);

        Implementation implementation = new Implementation();
        implementation.setName("implementationName");
        Implementation storedImplementation = implementationService.create(implementation, algorithm.getId());

        linkingService.linkImplementationAndSoftwarePlatform(storedImplementation.getId(), storedSoftwarePlatform.getId());

        assertDoesNotThrow(() -> softwarePlatformService
                .checkIfImplementationIsLinkedToSoftwarePlatform(softwarePlatform.getId(), implementation.getId()));
    }

    @Test
    void checkIfImplementationIsLinkedToSoftwarePlatform_IsNotLinked() {
        SoftwarePlatform softwarePlatform = getFullSoftwarePlatform("softwarePlatformName");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.create(softwarePlatform);

        Algorithm algorithm = new Algorithm();
        algorithm = algorithmService.create(algorithm);

        Implementation implementation = new Implementation();
        implementation.setName("implementationName");
        Implementation storedImplementation = implementationService.create(implementation, algorithm.getId());

        assertThrows(NoSuchElementException.class, () -> softwarePlatformService
                .checkIfImplementationIsLinkedToSoftwarePlatform(softwarePlatform.getId(), implementation.getId()));
    }

    private SoftwarePlatform getFullSoftwarePlatform(String name) {
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setName(name);
        try {
            softwarePlatform.setLink(new URL("http://example.com"));
        } catch (MalformedURLException ignored) {
        }
        softwarePlatform.setVersion("version v1");
        softwarePlatform.setLicence("licence");
        return softwarePlatform;
    }
}
