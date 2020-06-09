package org.planqk.atlas.core.services;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class CloudServiceServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private CloudServiceService cloudServiceService;

    @Autowired
    private CloudServiceRepository cloudServiceRepository;

    @Test
    void testAddCloudService_WithoutBackends() {

    }

    @Test
    void testAddCloudService_WithBackends() {
        // TODO: When backend is implemented
    }

    @Test
    void testFindCloudServiceById_ElementNotFound() {

    }

    @Test
    void testFindCloudServiceById_ElementFound() {

    }

    @Test
    void testDeleteCloudService_WithoutBackends() {

    }

    @Test
    void testDeleteSoftwarePlatform_WithBackends() {
        // TODO: When backend is implemented
    }
}
