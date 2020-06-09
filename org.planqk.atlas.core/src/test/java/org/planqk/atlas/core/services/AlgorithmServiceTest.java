package org.planqk.atlas.core.services;

import org.planqk.atlas.core.repository.AlgorithmRelationRepository;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class AlgorithmServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private AlgoRelationTypeService algoRelationTypeService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ProblemTypeService problemTypeService;

    @Autowired
    private AlgorithmRepository algorithmRepository;
    @Autowired
    private AlgorithmRelationRepository algorithmRelationRepository;

}
