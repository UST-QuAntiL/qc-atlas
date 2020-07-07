/********************************************************************************
 * Copyright (c) 2020 University of Stuttgart
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.core.services;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    void testFindAll() {
        ProblemType problemType1 = getGenericProblemType("testProblemType1");
        problemTypeService.save(problemType1);
        ProblemType problemType2 = getGenericProblemType("testProblemType2");
        problemTypeService.save(problemType2);

        List<ProblemType> problemTypes = problemTypeService.findAll(Pageable.unpaged()).getContent();

        assertThat(problemTypes.size()).isEqualTo(2);
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
            Assertions.assertThrows(ConsistencyException.class, () -> problemTypeService.delete(pt));
            Assertions.assertDoesNotThrow(() -> problemTypeService.findById(pt.getId()));
        });
    }

    @Test
    void testDeleteProblemType_NoReferences() {
        ProblemType problemType = getGenericProblemType("referencedProblemType");
        ProblemType storedProblemType = problemTypeService.save(problemType);

        Assertions.assertDoesNotThrow(() -> problemTypeService.findById(storedProblemType.getId()));

        problemTypeService.delete(storedProblemType);

        Assertions.assertThrows(NoSuchElementException.class, () -> problemTypeService.findById(storedProblemType.getId()));
    }

    @Test
    void testGetParentTreeList_NoParent() {
        ProblemType problemType = getGenericProblemType("referencedProblemType");
        ProblemType persistedProblemType = problemTypeService.save(problemType);

        List<ProblemType> problemTypeList = problemTypeService.getParentTreeList(persistedProblemType.getId());

        assertThat(problemTypeList.size()).isEqualTo(1);
    }

    @Test
    void testGetParentTreeList_ReturnTwoParents() {
        ProblemType problemType = getGenericProblemType("referencedProblemType");
        ProblemType parentProblemType = getGenericProblemType("referencedParentProblemType");
        ProblemType parentParentProblemType = getGenericProblemType("referencedParentParentProblemType");
        parentProblemType.setParentProblemType(problemTypeService.save(parentParentProblemType).getId());
        problemType.setParentProblemType(problemTypeService.save(parentProblemType).getId());
        ProblemType persistedProblemType = problemTypeService.save(problemType);

        List<ProblemType> problemTypeList = problemTypeService.getParentTreeList(persistedProblemType.getId());

        assertThat(problemTypeList.size()).isEqualTo(3);
    }

    private ProblemType getGenericProblemType(String name) {
        ProblemType problemType = new ProblemType();
        problemType.setName(name);
        problemType.setParentProblemType(UUID.randomUUID());
        return problemType;
    }
}
