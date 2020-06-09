package org.planqk.atlas.core.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CloudServiceServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private CloudServiceService cloudServiceService;

    @Autowired
    private CloudServiceRepository cloudServiceRepository;

    @Test
    void testAddCloudService_WithoutBackends() throws MalformedURLException {
        CloudService cloudService = getGenericTestCloudServiceWithoutRelations("testCloudService");

        var storedCloudService = cloudServiceService.save(cloudService);
        assertCloudServiceEquality(storedCloudService, cloudService);
    }

    @Test
    void testAddCloudService_WithBackends() {
        // TODO: When backend is implemented
    }

    @Test
    void testFindCloudServiceById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            cloudServiceService.findById(UUID.randomUUID());
        });
    }

    @Test
    void testFindCloudServiceById_ElementFound() throws MalformedURLException {
        CloudService cloudService = getGenericTestCloudServiceWithoutRelations("testCloudService");

        var storedCloudService = cloudServiceService.save(cloudService);

        cloudServiceService.findById(storedCloudService.getId());

        assertCloudServiceEquality(storedCloudService, cloudService);
    }

    @Test
    void testDeleteCloudService_WithoutBackends() throws MalformedURLException {
        CloudService cloudService = getGenericTestCloudServiceWithoutRelations("testCloudService");

        var storedCloudService = cloudServiceService.save(cloudService);
        cloudServiceService.findById(storedCloudService.getId());

        cloudServiceService.delete(storedCloudService.getId());

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            cloudServiceService.findById(storedCloudService.getId());
        });
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
