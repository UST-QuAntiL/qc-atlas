package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class AlgoRelationTypeServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private AlgoRelationTypeService algoRelationTypeService;
    @Autowired
    private AlgorithmService algorithmService;

    @Test
    void testAddAlgoRelationType() {
        AlgoRelationType relationType = getGenericAlgoRelationType("testRelation");

        AlgoRelationType storedRelationType = algoRelationTypeService.save(relationType);

        assertThat(storedRelationType.getId()).isNotNull();
        assertThat(storedRelationType.getName()).isEqualTo(relationType.getName());
    }

    @Test
    void testUpdateAlgoRelationType_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                algoRelationTypeService.update(UUID.randomUUID(), null));
    }

    @Test
    void testUpdateAlgoRelationType_ElementFound() {
        AlgoRelationType relationType = getGenericAlgoRelationType("testRelation");
        AlgoRelationType compareRelationType = getGenericAlgoRelationType("testRelation");

        AlgoRelationType storedRelationType = algoRelationTypeService.save(relationType);
        compareRelationType.setId(storedRelationType.getId());
        String editName = "editedRelation";
        storedRelationType.setName(editName);
        storedRelationType = algoRelationTypeService.save(storedRelationType);

        assertThat(storedRelationType.getId()).isNotNull();
        assertThat(storedRelationType.getId()).isEqualTo(compareRelationType.getId());
        assertThat(storedRelationType.getName()).isNotEqualTo(compareRelationType.getName());
        assertThat(storedRelationType.getName()).isEqualTo(editName);
    }

    @Test
    void testFindAlgoRelationTypeById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                algoRelationTypeService.findById(UUID.randomUUID()));
    }

    @Test
    void testFindAlgoRelationTypeById_ElementFound() {
        AlgoRelationType relationType = getGenericAlgoRelationType("testRelation");

        AlgoRelationType storedRelationType = algoRelationTypeService.save(relationType);

        storedRelationType = algoRelationTypeService.findById(storedRelationType.getId());

        assertThat(storedRelationType.getId()).isNotNull();
        assertThat(storedRelationType.getName()).isEqualTo(relationType.getName());
    }

    @Test
    void testDeleteAlgoRelationType_HasReferences() {
        Algorithm sourceAlgorithm = new ClassicAlgorithm();
        sourceAlgorithm.setName("sourceAlgorithm");
        sourceAlgorithm.setComputationModel(ComputationModel.CLASSIC);
        Algorithm storedSourceAlgorithm = algorithmService.save(sourceAlgorithm);
        Algorithm targetAlgorithm = new ClassicAlgorithm();
        targetAlgorithm.setName("sourceAlgorithm");
        targetAlgorithm.setComputationModel(ComputationModel.CLASSIC);
        algorithmService.save(targetAlgorithm);

        AlgoRelationType relationType = getGenericAlgoRelationType("testRelation");
        AlgoRelationType storedRelationType = algoRelationTypeService.save(relationType);

        AlgorithmRelation algorithmRelation = new AlgorithmRelation();
        algorithmRelation.setDescription("testRelationDescription");
        algorithmRelation.setSourceAlgorithm(sourceAlgorithm);
        algorithmRelation.setTargetAlgorithm(targetAlgorithm);
        algorithmRelation.setAlgoRelationType(storedRelationType);

        algorithmService.addOrUpdateAlgorithmRelation(storedSourceAlgorithm.getId(), algorithmRelation);

        Assertions.assertThrows(ConsistencyException.class, () -> algoRelationTypeService.delete(storedRelationType.getId()));

        Assertions.assertDoesNotThrow(() -> algoRelationTypeService.findById(storedRelationType.getId()));
    }

    @Test
    void testDeleteAlgoRelationType_NoReferences() {
        AlgoRelationType relationType = getGenericAlgoRelationType("testRelation");
        AlgoRelationType storedRelationType = algoRelationTypeService.save(relationType);

        Assertions.assertDoesNotThrow(() -> algoRelationTypeService.findById(storedRelationType.getId()));

        algoRelationTypeService.delete(storedRelationType.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                algoRelationTypeService.findById(storedRelationType.getId()));
    }

    private AlgoRelationType getGenericAlgoRelationType(String typeName) {
        AlgoRelationType algoRelationType = new AlgoRelationType();
        algoRelationType.setName(typeName);
        return algoRelationType;
    }

}
