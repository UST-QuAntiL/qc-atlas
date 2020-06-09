package org.planqk.atlas.core.services;

import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class SoftwarePlatformServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private SoftwarePlatformService softwarePlatformService;

    @Autowired
    private SoftwarePlatformRepository softwarePlatformRepository;
    @Autowired
    private CloudServiceRepository cloudServiceRepository;
}
