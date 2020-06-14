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

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudServiceServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private CloudServiceService cloudServiceService;

    @Test
    void testAddCloudService_WithoutBackends() throws MalformedURLException {
        CloudService cloudService = getGenericTestCloudServiceWithoutRelations("testCloudService");

        CloudService storedCloudService = cloudServiceService.save(cloudService);
        assertCloudServiceEquality(storedCloudService, cloudService);
    }

    @Test
    void testAddCloudService_WithBackends() {
        // TODO: When backend is implemented
    }

    @Test
    void testUpdateCloudService() throws MalformedURLException {
        CloudService cloudService = getGenericTestCloudServiceWithoutRelations("testCloudService");
        CloudService storedCloudService = getGenericTestCloudServiceWithoutRelations("testCloudService");

        CloudService storedEditedCloudService = cloudServiceService.save(cloudService);
        storedCloudService.setId(storedEditedCloudService.getId());
        String editName = "editedCloudService";
        storedEditedCloudService.setName(editName);
        storedEditedCloudService = cloudServiceService.createOrUpdate(storedEditedCloudService);

        assertThat(storedEditedCloudService.getId()).isEqualTo(storedCloudService.getId());
        assertThat(storedEditedCloudService.getName()).isNotEqualTo(storedCloudService.getName());
        assertThat(storedEditedCloudService.getName()).isEqualTo(editName);
        assertThat(storedEditedCloudService.getProvider()).isEqualTo(storedCloudService.getProvider());
        assertThat(storedEditedCloudService.getUrl()).isEqualTo(storedCloudService.getUrl());
        assertThat(storedEditedCloudService.getCostModel()).isEqualTo(storedCloudService.getCostModel());
    }

    @Test
    void testFindCloudServiceById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
            cloudServiceService.findById(UUID.randomUUID()));
    }

    @Test
    void testFindCloudServiceById_ElementFound() throws MalformedURLException {
        CloudService cloudService = getGenericTestCloudServiceWithoutRelations("testCloudService");

        CloudService storedCloudService = cloudServiceService.save(cloudService);

        storedCloudService = cloudServiceService.findById(storedCloudService.getId());

        assertCloudServiceEquality(storedCloudService, cloudService);
    }

    @Test
    void testFindAll() throws MalformedURLException {
        CloudService cloudService1 = getGenericTestCloudServiceWithoutRelations("testCloudService1");
        cloudServiceService.save(cloudService1);
        CloudService cloudService2 = getGenericTestCloudServiceWithoutRelations("testCloudService2");
        cloudServiceService.save(cloudService2);

        List<CloudService> cloudServices = cloudServiceService.findAll(Pageable.unpaged()).getContent();

        assertThat(cloudServices.size()).isEqualTo(2);
    }

    @Test
    void testDeleteCloudService_WithoutBackends() throws MalformedURLException {
        CloudService cloudService = getGenericTestCloudServiceWithoutRelations("testCloudService");

        CloudService storedCloudService = cloudServiceService.save(cloudService);

        Assertions.assertDoesNotThrow(() -> cloudServiceService.findById(storedCloudService.getId()));

        cloudServiceService.delete(storedCloudService.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
            cloudServiceService.findById(storedCloudService.getId()));
    }

    @Test
    void testDeleteSoftwarePlatform_WithBackends() {
        // TODO: When backend is implemented
    }

    private void assertCloudServiceEquality(CloudService dbCloudService, CloudService compareCloudService) {
        assertThat(dbCloudService.getId()).isNotNull();
        assertThat(dbCloudService.getName()).isEqualTo(compareCloudService.getName());
        assertThat(dbCloudService.getProvider()).isEqualTo(compareCloudService.getProvider());
        assertThat(dbCloudService.getUrl()).isEqualTo(compareCloudService.getUrl());
        assertThat(dbCloudService.getCostModel()).isEqualTo(compareCloudService.getCostModel());
    }

    private CloudService getGenericTestCloudServiceWithoutRelations(String name) throws MalformedURLException {
        CloudService cloudService = new CloudService();
        cloudService.setName(name);
        cloudService.setProvider("testProvider");
        cloudService.setUrl(new URL("http://example.com"));
        cloudService.setCostModel("testCostModel");
        return cloudService;
    }
}
