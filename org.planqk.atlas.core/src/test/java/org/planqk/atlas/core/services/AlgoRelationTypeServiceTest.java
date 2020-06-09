package org.planqk.atlas.core.services;

import org.planqk.atlas.core.repository.AlgoRelationTypeRepository;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class AlgoRelationTypeServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private AlgoRelationTypeService algoRelationTypeService;

    @Autowired
    private AlgoRelationTypeRepository algoRelationTypeRepository;
    @Autowired
    private AlgorithmRelationRepository algorithmRelationRepository;

}
