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

import org.planqk.atlas.core.model.Backend;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SoftwarePlatformServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private SoftwarePlatformService softwarePlatformService;
    @Autowired
    private CloudServiceService cloudServiceService;
    @Autowired
    private BackendService backendService;

    @Test
    void testAddSoftwarePlatform_WithoutRelations() throws MalformedURLException {
        SoftwarePlatform softwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);
        assertSoftwarePlatformEquality(storedSoftwarePlatform, softwarePlatform);
    }

    @Test
    void testAddSoftwarePlatform_WithCloudServices() throws MalformedURLException {
        SoftwarePlatform softwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");

        Set<CloudService> cloudServices = new HashSet<>();
        CloudService cloudService = new CloudService();
        cloudService.setName("testCloudService");
        cloudService.setProvider("testProvider");
        cloudService.setUrl(new URL("http://example.com"));
        cloudService.setCostModel("testCostModel");
        cloudServices.add(cloudService);

        softwarePlatform.setSupportedCloudServices(cloudServices);

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);
        assertSoftwarePlatformEquality(storedSoftwarePlatform, softwarePlatform);
        assertCloudServiceEquality(storedSoftwarePlatform, cloudService);
    }

    @Test
    void testAddSoftwarePlatform_WithBackends() throws MalformedURLException {
        SoftwarePlatform softwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");

        Set<Backend> backends = new HashSet<>();
        Backend backend = new Backend();
        backend.setName("testBackend");
        backends.add(backend);

        softwarePlatform.setSupportedBackends(backends);

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);
        assertSoftwarePlatformEquality(storedSoftwarePlatform, softwarePlatform);

        storedSoftwarePlatform.getSupportedBackends().forEach(b -> {
            assertThat(b.getId()).isNotNull();
            assertThat(b.getName()).isEqualTo(backend.getName());
            Assertions.assertDoesNotThrow(() -> backendService.findById(b.getId()));
        });

        assertThat(storedSoftwarePlatform.getSupportedBackends().size()).isEqualTo(1);
    }

    @Test
    void testUpdateSoftwarePlatform_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.update(UUID.randomUUID(), null));
    }

    @Test
    void testUpdateSoftwarePlatform_ElementFound() throws MalformedURLException {
        SoftwarePlatform softwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");
        SoftwarePlatform storedSoftwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");

        SoftwarePlatform storedEditedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);
        storedSoftwarePlatform.setId(storedEditedSoftwarePlatform.getId());
        String editName = "editedSoftwarePlatform";
        storedEditedSoftwarePlatform.setName(editName);
        storedEditedSoftwarePlatform = softwarePlatformService.update(storedEditedSoftwarePlatform.getId(), storedEditedSoftwarePlatform);

        assertThat(storedEditedSoftwarePlatform.getId()).isEqualTo(storedSoftwarePlatform.getId());
        assertThat(storedEditedSoftwarePlatform.getName()).isNotEqualTo(storedSoftwarePlatform.getName());
        assertThat(storedEditedSoftwarePlatform.getName()).isEqualTo(editName);
        assertThat(storedEditedSoftwarePlatform.getLink()).isEqualTo(storedSoftwarePlatform.getLink());
        assertThat(storedEditedSoftwarePlatform.getVersion()).isEqualTo(storedSoftwarePlatform.getVersion());
    }

    @Test
    void testFindSoftwarePlatformById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
            softwarePlatformService.findById(UUID.randomUUID()));
    }

    @Test
    void testFindSoftwarePlatformById_ElementFound() throws MalformedURLException {
        SoftwarePlatform softwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        storedSoftwarePlatform = softwarePlatformService.findById(storedSoftwarePlatform.getId());

        assertSoftwarePlatformEquality(storedSoftwarePlatform, softwarePlatform);
    }

    @Test
    void testFindAll() throws MalformedURLException {
        SoftwarePlatform softwarePlatform1 = getGenericTestSoftwarePlatformWithoutRelations("testCloudService1");
        softwarePlatformService.save(softwarePlatform1);
        SoftwarePlatform softwarePlatform2 = getGenericTestSoftwarePlatformWithoutRelations("testCloudService2");
        softwarePlatformService.save(softwarePlatform2);

        List<SoftwarePlatform> softwarePlatforms = softwarePlatformService.findAll(Pageable.unpaged()).getContent();

        assertThat(softwarePlatforms.size()).isEqualTo(2);
    }

    @Test
    void testDeleteSoftwarePlatform_WithoutRelations() throws MalformedURLException {
        SoftwarePlatform softwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        Assertions.assertDoesNotThrow(() -> softwarePlatformService.findById(storedSoftwarePlatform.getId()));

        softwarePlatformService.delete(storedSoftwarePlatform.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.findById(storedSoftwarePlatform.getId()));
    }

    @Test
    void testDeleteSoftwarePlatform_WithCloudServices() throws MalformedURLException {
        SoftwarePlatform softwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");

        Set<CloudService> cloudServices = new HashSet<>();
        CloudService cloudService = new CloudService();
        cloudService.setName("testCloudService");
        cloudService.setProvider("testProvider");
        cloudService.setUrl(new URL("http://example.com"));
        cloudService.setCostModel("testCostModel");
        cloudServices.add(cloudService);
        softwarePlatform.setSupportedCloudServices(cloudServices);

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        Assertions.assertDoesNotThrow(() -> softwarePlatformService.findById(storedSoftwarePlatform.getId()));

        softwarePlatformService.delete(storedSoftwarePlatform.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.findById(storedSoftwarePlatform.getId()));

        storedSoftwarePlatform.getSupportedCloudServices().forEach(cs ->
                cloudServiceService.findById(cs.getId()));
    }

    @Test
    void testDeleteSoftwarePlatform_WithBackends() throws MalformedURLException {
        SoftwarePlatform softwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");

        Set<Backend> backends = new HashSet<>();
        Backend backend = new Backend();
        backend.setName("testBackend");
        backends.add(backend);

        softwarePlatform.setSupportedBackends(backends);

        SoftwarePlatform storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        Assertions.assertDoesNotThrow(() -> softwarePlatformService.findById(storedSoftwarePlatform.getId()));
        storedSoftwarePlatform.getSupportedBackends().forEach(b -> {
            Assertions.assertDoesNotThrow(() -> backendService.findById(b.getId()));
        });

        softwarePlatformService.delete(storedSoftwarePlatform.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                softwarePlatformService.findById(storedSoftwarePlatform.getId()));
        storedSoftwarePlatform.getSupportedBackends().forEach(b -> {
            Assertions.assertDoesNotThrow(() -> backendService.findById(b.getId()));
        });
    }

    private void assertSoftwarePlatformEquality(SoftwarePlatform dbSoftwarePlatform, SoftwarePlatform compareSoftwarePlatform) {
        assertThat(dbSoftwarePlatform.getId()).isNotNull();
        assertThat(dbSoftwarePlatform.getName()).isEqualTo(compareSoftwarePlatform.getName());
        assertThat(dbSoftwarePlatform.getLink()).isEqualTo(compareSoftwarePlatform.getLink());
        assertThat(dbSoftwarePlatform.getVersion()).isEqualTo(compareSoftwarePlatform.getVersion());
    }

    private void assertCloudServiceEquality(SoftwarePlatform dbSoftwarePlatform, CloudService compareCloudService) {
        var storedCloudServices = dbSoftwarePlatform.getSupportedCloudServices();
        storedCloudServices.forEach(cs -> {
            assertThat(cs.getId()).isNotNull();
            assertThat(cs.getName()).isEqualTo(compareCloudService.getName());
            assertThat(cs.getProvider()).isEqualTo(compareCloudService.getProvider());
            assertThat(cs.getUrl()).isEqualTo(compareCloudService.getUrl());
            assertThat(cs.getCostModel()).isEqualTo(compareCloudService.getCostModel());
        });
    }

    private SoftwarePlatform getGenericTestSoftwarePlatformWithoutRelations(String name) throws MalformedURLException {
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setName(name);
        softwarePlatform.setLink(new URL("http://example.com"));
        softwarePlatform.setVersion("v1");
        return softwarePlatform;
    }
}
