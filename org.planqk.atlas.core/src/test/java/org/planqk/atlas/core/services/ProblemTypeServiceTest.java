package org.planqk.atlas.core.services;

import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ProblemTypeRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class ProblemTypeServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ProblemTypeService problemTypeService;

    @Autowired
    private ProblemTypeRepository problemTypeRepository;
    @Autowired
    private AlgorithmRepository algorithmRepository;

}
