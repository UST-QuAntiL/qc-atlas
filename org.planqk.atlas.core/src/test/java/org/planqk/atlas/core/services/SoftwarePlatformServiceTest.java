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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

public class SoftwarePlatformServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private SoftwarePlatformService softwarePlatformService;
    @Autowired
    private CloudServiceService cloudServiceService;
    @Autowired
    private ComputeResourceService computeResourceService;
    @Autowired
    private ImplementationService implementationService;

    @Test
    void createMinimalSoftwarePlatform() {
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setName("test software platform");

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);
        assertSoftwarePlatformEquality(storedSoftwarePlatform, softwarePlatform);
    }

    @Test
    void createMaximalSoftwarePlatform() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);
        assertSoftwarePlatformEquality(storedSoftwarePlatform, softwarePlatform);
    }

    @Test
    void addImplementationReference() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        Implementation implementation = new Implementation();
        implementation.setName("test implementation");
        Implementation storedImplementation = implementationService.save(implementation);

        softwarePlatformService.addImplementationReference(storedSoftwarePlatform.getId(), storedImplementation.getId());

        Set<Implementation> implementations = softwarePlatformService.findImplementations(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(implementations.size()).isEqualTo(1);
        assertThat(implementations.contains(storedImplementation)).isTrue();
    }

    @Test
    void addCloudServiceReference() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        CloudService cloudService = new CloudService();
        cloudService.setName("testCloudService");
        cloudService.setProvider("testProvider");
        try {
            cloudService.setUrl(new URL("http://example.com"));
        } catch (MalformedURLException ignored) {
        }
        cloudService.setCostModel("testCostModel");
        CloudService storedCloudService = cloudServiceService.save(cloudService);

        softwarePlatformService.addCloudServiceReference(storedSoftwarePlatform.getId(), storedCloudService.getId());

        Set<CloudService> cloudServices = softwarePlatformService.findCloudServices(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(cloudServices.size()).isEqualTo(1);
        assertThat(cloudServices.contains(storedCloudService)).isTrue();
    }

    @Test
    void addComputeResourceReference() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        ComputeResource computeResource = new ComputeResource();
        computeResource.setName("test compute resource");
        computeResource.setVendor("test vendor");
        computeResource.setTechnology("test technology");
        computeResource.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
        ComputeResource storedComputeResource = computeResourceService.save(computeResource);

        softwarePlatformService.addComputeResourceReference(
                storedSoftwarePlatform.getId(), storedComputeResource.getId());

        Set<ComputeResource> computeResources = softwarePlatformService.findComputeResources(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(computeResources.size()).isEqualTo(1);
        assertThat(computeResources.contains(storedComputeResource)).isTrue();
    }

    @Test
    void updateSoftwarePlatform_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.update(UUID.randomUUID(), null));
    }

    @Test
    void updateSoftwarePlatform_ElementFound() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");
        SoftwarePlatform storedSoftwarePlatform = getTestSoftwarePlatform("test software platform");

        SoftwarePlatform storedEditedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);
        storedSoftwarePlatform.setId(storedEditedSoftwarePlatform.getId());
        String editName = "edited software platform";
        storedEditedSoftwarePlatform.setName(editName);
        storedEditedSoftwarePlatform = softwarePlatformService.update(storedEditedSoftwarePlatform.getId(), storedEditedSoftwarePlatform);

        assertThat(storedEditedSoftwarePlatform.getId()).isEqualTo(storedSoftwarePlatform.getId());
        assertThat(storedEditedSoftwarePlatform.getName()).isNotEqualTo(storedSoftwarePlatform.getName());
        assertThat(storedEditedSoftwarePlatform.getName()).isEqualTo(editName);
        assertThat(storedEditedSoftwarePlatform.getLink()).isEqualTo(storedSoftwarePlatform.getLink());
        assertThat(storedEditedSoftwarePlatform.getVersion()).isEqualTo(storedSoftwarePlatform.getVersion());
        assertThat(storedEditedSoftwarePlatform.getLicence()).isEqualTo(storedSoftwarePlatform.getLicence());
    }

    @Test
    void findSoftwarePlatformById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.findById(UUID.randomUUID()));
    }

    @Test
    void findSoftwarePlatformById_ElementFound() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        storedSoftwarePlatform = softwarePlatformService.findById(storedSoftwarePlatform.getId());

        assertSoftwarePlatformEquality(storedSoftwarePlatform, softwarePlatform);
    }

    @Test
    void findAll() {
        SoftwarePlatform softwarePlatform1 = getTestSoftwarePlatform("test software platform1");
        softwarePlatformService.save(softwarePlatform1);
        SoftwarePlatform softwarePlatform2 = getTestSoftwarePlatform("test software platform2");
        softwarePlatformService.save(softwarePlatform2);

        List<SoftwarePlatform> softwarePlatforms = softwarePlatformService.findAll(Pageable.unpaged()).getContent();

        assertThat(softwarePlatforms.size()).isEqualTo(2);
    }

    @Test
    void searchAll() {
        SoftwarePlatform softwarePlatform1 = getTestSoftwarePlatform("test software platform1");
        softwarePlatformService.save(softwarePlatform1);
        SoftwarePlatform softwarePlatform2 = getTestSoftwarePlatform("test software platform2");
        softwarePlatformService.save(softwarePlatform2);

        List<SoftwarePlatform> softwarePlatforms = softwarePlatformService.searchAllByName("1", Pageable.unpaged()).getContent();

        assertThat(softwarePlatforms.size()).isEqualTo(1);
    }

    @Test
    void findImplementations() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        Set<Implementation> storedImplementations = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Implementation implementation = new Implementation();
            implementation.setName("test implementation" + i);
            Implementation storedImplementation = implementationService.save(implementation);
            storedImplementations.add(storedImplementation);
            softwarePlatformService.addImplementationReference(storedSoftwarePlatform.getId(), storedImplementation.getId());
        }
        Set<Implementation> implementations = softwarePlatformService.findImplementations(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(implementations.size()).isEqualTo(10);
        implementations.forEach(implementation -> assertThat(storedImplementations.contains(implementation)).isTrue());
    }

    @Test
    void findCloudServices() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        Set<CloudService> storedCloudServices = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            CloudService cloudService = new CloudService();
            cloudService.setName("testCloudService" + i);
            cloudService.setProvider("testProvider");
            try {
                cloudService.setUrl(new URL("http://example.com"));
            } catch (MalformedURLException ignored) {
            }
            cloudService.setCostModel("testCostModel");
            CloudService storedCloudService = cloudServiceService.save(cloudService);
            storedCloudServices.add(storedCloudService);
            softwarePlatformService.addCloudServiceReference(storedSoftwarePlatform.getId(), storedCloudService.getId());
        }
        Set<CloudService> cloudServices = softwarePlatformService.findCloudServices(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(cloudServices.size()).isEqualTo(10);
        cloudServices.forEach(cloudService -> assertThat(storedCloudServices.contains(cloudService)).isTrue());
    }

    @Test
    void findComputeResources() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        Set<ComputeResource> storedComputeResources = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            ComputeResource computeResource = new ComputeResource();
            computeResource.setName("test compute resource");
            computeResource.setVendor("test vendor");
            computeResource.setTechnology("test technology");
            computeResource.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
            ComputeResource storedComputeResource = computeResourceService.save(computeResource);
            storedComputeResources.add(storedComputeResource);
            softwarePlatformService.addComputeResourceReference(storedSoftwarePlatform.getId(), storedComputeResource.getId());
        }
        Set<ComputeResource> computeResources = softwarePlatformService.findComputeResources(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(computeResources.size()).isEqualTo(10);
        computeResources.forEach(computeResource -> assertThat(storedComputeResources.contains(computeResource)).isTrue());
    }

    @Test
    void deleteSoftwarePlatform_NoReferences() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("testSoftwarePlatform");

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        Assertions.assertDoesNotThrow(() -> softwarePlatformService.findById(storedSoftwarePlatform.getId()));

        softwarePlatformService.delete(storedSoftwarePlatform.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.findById(storedSoftwarePlatform.getId()));
    }

    @Test
    void deleteSoftwarePlatform_HasReferences() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("testSoftwarePlatform");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        Assertions.assertDoesNotThrow(() -> softwarePlatformService.findById(storedSoftwarePlatform.getId()));

        // Add Implementation Reference
        Implementation implementation = new Implementation();
        implementation.setName("test implementation");
        Implementation storedImplementation = implementationService.save(implementation);
        softwarePlatformService.addImplementationReference(storedSoftwarePlatform.getId(), storedImplementation.getId());

        // Add Cloud Service Reference
        CloudService cloudService = new CloudService();
        cloudService.setName("testCloudService");
        CloudService storedCloudService = cloudServiceService.save(cloudService);
        softwarePlatformService.addCloudServiceReference(storedSoftwarePlatform.getId(), storedCloudService.getId());

        // Add Compute Resource Reference
        ComputeResource computeResource = new ComputeResource();
        computeResource.setName("test compute resource");
        ComputeResource storedComputeResource = computeResourceService.save(computeResource);
        softwarePlatformService.addComputeResourceReference(storedSoftwarePlatform.getId(), storedComputeResource.getId());

        // Delete
        softwarePlatformService.delete(storedSoftwarePlatform.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.findById(storedSoftwarePlatform.getId()));

        // Test if references are removed
        assertThat(implementationService.findById(storedImplementation.getId()).getSoftwarePlatforms().size()).isEqualTo(0);
        assertThat(cloudServiceService.findById(storedCloudService.getId()).getSoftwarePlatforms().size()).isEqualTo(0);
        assertThat(computeResourceService.findById(storedComputeResource.getId()).getSoftwarePlatforms().size()).isEqualTo(0);
    }

    @Test
    void deleteImplementationReference() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        Implementation implementation = new Implementation();
        implementation.setName("test implementation");
        Implementation storedImplementation = implementationService.save(implementation);

        softwarePlatformService.addImplementationReference(storedSoftwarePlatform.getId(), storedImplementation.getId());

        Set<Implementation> implementations = softwarePlatformService.findImplementations(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(implementations.size()).isEqualTo(1);

        softwarePlatformService.deleteImplementationReference(storedSoftwarePlatform.getId(), storedImplementation.getId());

        implementations = softwarePlatformService.findImplementations(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(implementations.size()).isEqualTo(0);
    }

    @Test
    void deleteCloudServiceReference() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        CloudService cloudService = new CloudService();
        cloudService.setName("testCloudService");
        cloudService.setProvider("testProvider");
        try {
            cloudService.setUrl(new URL("http://example.com"));
        } catch (MalformedURLException ignored) {
        }
        cloudService.setCostModel("testCostModel");
        CloudService storedCloudService = cloudServiceService.save(cloudService);

        softwarePlatformService.addCloudServiceReference(storedSoftwarePlatform.getId(), storedCloudService.getId());

        Set<CloudService> cloudServices = softwarePlatformService.findCloudServices(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(cloudServices.size()).isEqualTo(1);

        softwarePlatformService.deleteCloudServiceReference(storedSoftwarePlatform.getId(), storedCloudService.getId());

        cloudServices = softwarePlatformService.findCloudServices(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(cloudServices.size()).isEqualTo(0);
    }

    @Test
    void deleteComputeResourceReference() {
        SoftwarePlatform softwarePlatform = getTestSoftwarePlatform("test software platform");
        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        ComputeResource computeResource = new ComputeResource();
        computeResource.setName("test compute resource");
        computeResource.setVendor("test vendor");
        computeResource.setTechnology("test technology");
        computeResource.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
        ComputeResource storedComputeResource = computeResourceService.save(computeResource);

        softwarePlatformService.addComputeResourceReference(
                storedSoftwarePlatform.getId(), storedComputeResource.getId());

        Set<ComputeResource> computeResources = softwarePlatformService.findComputeResources(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(computeResources.size()).isEqualTo(1);

        softwarePlatformService.deleteComputeResourceReference(
                storedSoftwarePlatform.getId(), storedComputeResource.getId());

        computeResources = softwarePlatformService.findComputeResources(
                storedSoftwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(computeResources.size()).isEqualTo(0);
    }

    private void assertSoftwarePlatformEquality(SoftwarePlatform dbSoftwarePlatform, SoftwarePlatform compareSoftwarePlatform) {
        assertThat(dbSoftwarePlatform.getId()).isNotNull();
        assertThat(dbSoftwarePlatform.getName()).isEqualTo(compareSoftwarePlatform.getName());
        assertThat(dbSoftwarePlatform.getLink()).isEqualTo(compareSoftwarePlatform.getLink());
        assertThat(dbSoftwarePlatform.getVersion()).isEqualTo(compareSoftwarePlatform.getVersion());
        assertThat(dbSoftwarePlatform.getLicence()).isEqualTo(compareSoftwarePlatform.getLicence());
    }

    private SoftwarePlatform getTestSoftwarePlatform(String name) {
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setName(name);
        try {
            softwarePlatform.setLink(new URL("http://example.com"));
        } catch (MalformedURLException ignored) {
        }
        softwarePlatform.setVersion("v1");
        softwarePlatform.setLicence("test licence");
        return softwarePlatform;
    }
}
