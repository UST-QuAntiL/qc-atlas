package org.planqk.atlas.core.services;

import org.junit.jupiter.api.Test;
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

    @Test
    void testAddAlgoRelationType() {

    }

    @Test
    void testUpdateAlgoRelationType_ElementNotFound() {

    }

    @Test
    void testUpdateAlgoRelationType_ElementFound() {

    }

    @Test
    void testFindAlgoRelationTypeById_ElementNotFound() {

    }

    @Test
    void testFindAlgoRelationTypeById_ElementFound() {

    }

    @Test
    void testDeleteAlgoRelationType_HasReferences() {

    }

    @Test
    void testDeleteAlgoRelationType_NoReferences() {

    }

}
