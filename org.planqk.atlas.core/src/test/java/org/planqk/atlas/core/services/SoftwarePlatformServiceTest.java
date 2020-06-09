package org.planqk.atlas.core.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
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
    private SoftwarePlatformRepository softwarePlatformRepository;

    @Test
    void testAddSoftwarePlatform_WithoutRelations() throws MalformedURLException {
        SoftwarePlatform softwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");

        var storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);
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

        var storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);
        assertSoftwarePlatformEquality(storedSoftwarePlatform, softwarePlatform);
        assertCloudServiceEquality(storedSoftwarePlatform, cloudService);
    }

    @Test
    void testAddSoftwarePlatform_WithBackends() {
        // TODO: When backend is implemented
    }

    @Test
    void testFindSoftwarePlatformById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            softwarePlatformService.findById(UUID.randomUUID());
        });
    }

    @Test
    void testFindSoftwarePlatformById_ElementFound() throws MalformedURLException {
        SoftwarePlatform softwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");

        var storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);

        softwarePlatformService.findById(storedSoftwarePlatform.getId());

        assertSoftwarePlatformEquality(storedSoftwarePlatform, softwarePlatform);
    }

    @Test
    void testDeleteSoftwarePlatform_WithoutRelations() throws MalformedURLException {
        SoftwarePlatform softwarePlatform = getGenericTestSoftwarePlatformWithoutRelations("testSoftwarePlatform");

        var storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);
        softwarePlatformService.findById(storedSoftwarePlatform.getId());

        softwarePlatformService.delete(storedSoftwarePlatform.getId());

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            softwarePlatformService.findById(storedSoftwarePlatform.getId());
        });
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

        var storedSoftwarePlatform = softwarePlatformService.save(softwarePlatform);
        softwarePlatformService.findById(storedSoftwarePlatform.getId());

        softwarePlatformService.delete(storedSoftwarePlatform.getId());

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            softwarePlatformService.findById(storedSoftwarePlatform.getId());
        });

        storedSoftwarePlatform.getSupportedCloudServices().forEach(cs -> {
            cloudServiceService.findById(cs.getId());
        });
    }

    @Test
    void testDeleteSoftwarePlatform_WithBackends() {
        // TODO: When backend is implemented
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
