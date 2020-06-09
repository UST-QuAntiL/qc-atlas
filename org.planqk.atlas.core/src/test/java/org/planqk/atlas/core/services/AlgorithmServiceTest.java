package org.planqk.atlas.core.services;

import org.junit.jupiter.api.Test;
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

    @Test
    void testAddAlgorithm_WithoutRelations() {

    }

    @Test
    void testAddAlgorithm_WithTags() {

    }

    @Test
    void testAddAlgorithm_WithProblemTypes() {

    }

    @Test
    void testUpdateAlgorithm_ElementFound() {

    }

    @Test
    void testUpdateAlgorithm_ElementNotFound() {

    }

    @Test
    void testUpdateAlgorithm_QuantumAlgorithm() {

    }

    @Test
    void testFindAlgorithmId_ElementNotFound() {

    }

    @Test
    void testFindAlgorithmById_ElementFound() {

    }

    @Test
    void testDeleteAlgorithm_WithoutRelations() {

    }

    @Test
    void testDeleteAlgorithm_WithRelations() {

    }

    @Test
    void testAddAlgorithmRelation() {

    }

    @Test
    void testAddOrUpdateAlgorithmRelation_AlgorithmOrRelationNotFound() {

    }

    @Test
    void testAddOrUpdateAlgorithmRelation_AddRelation() {

    }

    @Test
    void testAddOrUpdateAlgorithmRelation_UpdateRelation() {

    }

    @Test
    void testGetAlgorithmRelations() {

    }

    @Test
    void testDeleteAlgorithmRelation_ElementsNotFound() {

    }

    @Test
    void testDeleteAlgorithmRelation_ElementsFound() {

    }

}
