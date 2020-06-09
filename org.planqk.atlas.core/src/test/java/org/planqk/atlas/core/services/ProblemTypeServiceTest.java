package org.planqk.atlas.core.services;

import org.junit.jupiter.api.Test;
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

    @Test
    void testAddProblemType() {

    }

    @Test
    void testUpdateProblemType_ElementNotFound() {

    }

    @Test
    void testUpdateProblemType_ElementFound() {

    }

    @Test
    void testFindProblemTypeById_ElementNotFound() {

    }

    @Test
    void testFindProblemTypeById_ElementFound() {

    }

    @Test
    void testDeleteProblemType_HasReferences() {

    }

    @Test
    void testDeleteProblemType_NoReferences() {

    }
}
