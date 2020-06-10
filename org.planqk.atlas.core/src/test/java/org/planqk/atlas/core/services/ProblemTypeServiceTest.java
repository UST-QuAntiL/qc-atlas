package org.planqk.atlas.core.services;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ProblemTypeServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ProblemTypeService problemTypeService;
    @Autowired
    private AlgorithmService algorithmService;

    @Test
    void testAddProblemType() {
        ProblemType problemType = getGenericProblemType("testProblemType");

        ProblemType storedProblemType = problemTypeService.save(problemType);

        assertThat(storedProblemType.getId()).isNotNull();
        assertThat(storedProblemType.getName()).isEqualTo(problemType.getName());
        assertThat(storedProblemType.getParentProblemType()).isEqualTo(problemType.getParentProblemType());
    }

    @Test
    void testUpdateProblemType_ElementNotFound() {
        ProblemType problemType = getGenericProblemType("testProblemType");

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            problemTypeService.update(UUID.randomUUID(), problemType);
        });
    }

    @Test
    void testUpdateProblemType_ElementFound() {
        ProblemType problemType = getGenericProblemType("testProblemType");
        ProblemType compareProblemType = getGenericProblemType("testProblemType");

        ProblemType storedProblemType = problemTypeService.save(problemType);
        compareProblemType.setId(storedProblemType.getId());
        storedProblemType.setName("editedProblemType");
        ProblemType editedProblemType = problemTypeService.update(storedProblemType.getId(), storedProblemType);

        assertThat(editedProblemType.getId()).isNotNull();
        assertThat(editedProblemType.getId()).isEqualTo(compareProblemType.getId());
        assertThat(editedProblemType.getName()).isNotEqualTo(compareProblemType.getName());
        assertThat(storedProblemType.getParentProblemType()).isEqualTo(problemType.getParentProblemType());
    }

    @Test
    void testFindProblemTypeById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () -> problemTypeService.findById(UUID.randomUUID()));
    }

    @Test
    void testFindProblemTypeById_ElementFound() {
        ProblemType problemType = getGenericProblemType("testProblemType");

        ProblemType storedProblemType = problemTypeService.save(problemType);
        storedProblemType = problemTypeService.findById(storedProblemType.getId());

        assertThat(storedProblemType.getId()).isNotNull();
        assertThat(storedProblemType.getName()).isEqualTo(problemType.getName());
        assertThat(storedProblemType.getParentProblemType()).isEqualTo(problemType.getParentProblemType());
    }

    @Test
    void testDeleteProblemType_HasReferences() {
        ProblemType problemType = getGenericProblemType("referencedProblemType");
        Set<ProblemType> problemTypes = new HashSet<>();
        problemTypes.add(problemType);

        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmWithProblemType");
        algorithm.setComputationModel(ComputationModel.CLASSIC);
        algorithm.setProblemTypes(problemTypes);

        Algorithm storedAlgorithm = this.algorithmService.save(algorithm);

        Set<ProblemType> storedProblemTypes = storedAlgorithm.getProblemTypes();
        storedProblemTypes.forEach(pt -> {
            Assertions.assertDoesNotThrow(() -> problemTypeService.findById(pt.getId()));
            Assertions.assertThrows(ConsistencyException.class, () -> problemTypeService.delete(pt.getId()));
            Assertions.assertDoesNotThrow(() -> problemTypeService.findById(pt.getId()));
        });
    }

    @Test
    void testDeleteProblemType_NoReferences() {
        ProblemType problemType = getGenericProblemType("referencedProblemType");
        ProblemType storedProblemType = problemTypeService.save(problemType);

        Assertions.assertDoesNotThrow(() -> problemTypeService.findById(storedProblemType.getId()));

        problemTypeService.delete(storedProblemType.getId());

        Assertions.assertThrows(NoSuchElementException.class, () -> problemTypeService.findById(storedProblemType.getId()));
    }

    private ProblemType getGenericProblemType(String name) {
        ProblemType problemType = new ProblemType();
        problemType.setName(name);
        problemType.setParentProblemType(UUID.randomUUID());
        return problemType;
    }
}
