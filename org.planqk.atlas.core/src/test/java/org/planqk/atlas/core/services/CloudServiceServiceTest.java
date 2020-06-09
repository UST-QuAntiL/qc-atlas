package org.planqk.atlas.core.services;

import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class CloudServiceServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private CloudServiceService cloudServiceService;

    @Autowired
    private CloudServiceRepository cloudServiceRepository;
}
