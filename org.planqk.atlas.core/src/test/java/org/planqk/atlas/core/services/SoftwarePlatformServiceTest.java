package org.planqk.atlas.core.services;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class SoftwarePlatformServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private SoftwarePlatformService softwarePlatformService;
    @Autowired
    private CloudServiceService cloudServiceService;

    @Autowired
    private SoftwarePlatformRepository softwarePlatformRepository;

    @Test
    void testAddSoftwarePlatform_WithoutRelations() {

    }

    @Test
    void testAddSoftwarePlatform_WithCloudServices() {

    }

    @Test
    void testAddSoftwarePlatform_WithBackends() {
        // TODO: When backend is implemented
    }

    @Test
    void testFindSoftwarePlatformById_ElementNotFound() {

    }

    @Test
    void testFindSoftwarePlatformById_ElementFound() {

    }

    @Test
    void testDeleteSoftwarePlatform_WithoutRelations() {

    }

    @Test
    void testDeleteSoftwarePlatform_WithCloudServices() {

    }

    @Test
    void testDeleteSoftwarePlatform_WithBackends() {
        // TODO: When backend is implemented
    }

}
