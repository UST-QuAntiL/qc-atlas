/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.data.domain.Sort.Direction.ASC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProblemTypeServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ProblemTypeService problemTypeService;

    @Autowired
    private AlgorithmService algorithmService;



    @Test
    void createProblemType() {
        ProblemType problemType = getFullProblemType("problemTypeName");

        ProblemType storedProblemType = problemTypeService.create(problemType);

        assertThat(storedProblemType.getId()).isNotNull();
        assertThat(storedProblemType.getName()).isEqualTo(problemType.getName());
        assertThat(storedProblemType.getParentProblemType()).isEqualTo(problemType.getParentProblemType());
    }

    @Test
    void findAllProblemTypes() {
        ProblemType problemType1 = getFullProblemType("problemTypeName1");
        problemTypeService.create(problemType1);
        ProblemType problemType2 = getFullProblemType("problemTypeName2");
        problemTypeService.create(problemType2);
        List<ProblemType> problemTypes = problemTypeService.findAll(Pageable.unpaged(), "").getContent();
        assertThat(problemTypes.size()).isEqualTo(2);
    }

    @Test
    void findAllSortedByParent(){

        int page = 0;
        int size = 10;
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(ASC,"parentProblemTypeName"));
        Sort sort = Sort.by(orders);
        Pageable pageable = PageRequest.of(page,size,sort);

        ProblemType grandParentProblemType = problemTypeService.create(
            getFullProblemType("aParentParentProblemTypeName")
        );

        ProblemType parentProblemType = getFullProblemType("bParentProblemTypeName");
        parentProblemType.setParentProblemType(grandParentProblemType.getId());
        parentProblemType = problemTypeService.create(parentProblemType);

        ProblemType problemType = getFullProblemType("cProblemTypeName");
        problemType.setParentProblemType(parentProblemType.getId());
        problemType = problemTypeService.create(problemType);

        List<ProblemType> problemTypes = problemTypeService.findAll(pageable, "").getContent();
        assertThat(problemTypes.get(0).getId()).isEqualTo(grandParentProblemType.getId());
        assertNull(problemTypes.get(0).getParentProblemType());
        assertThat(problemTypes.get(1).getId()).isEqualTo(parentProblemType.getId());
        assertThat(problemTypes.get(1).getParentProblemType()).isEqualTo(grandParentProblemType.getId());
        assertThat(problemTypes.get(2).getId()).isEqualTo(problemType.getId());
        assertThat(problemTypes.get(2).getParentProblemType()).isEqualTo(parentProblemType.getId());
    }

    @Test
    void findProblemTypeById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> problemTypeService.findById(UUID.randomUUID()));
    }

    @Test
    void findProblemTypeById_ElementFound() {
        ProblemType problemType = getFullProblemType("problemTypeName");

        ProblemType storedProblemType = problemTypeService.create(problemType);
        storedProblemType = problemTypeService.findById(storedProblemType.getId());

        assertThat(storedProblemType.getId()).isNotNull();
        assertThat(storedProblemType.getName()).isEqualTo(problemType.getName());
        assertThat(storedProblemType.getParentProblemType()).isEqualTo(problemType.getParentProblemType());
    }

    @Test
    void updateProblemType_ElementNotFound() {
        ProblemType problemType = getFullProblemType("problemTypeName");
        problemType.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () -> {
            problemTypeService.update(problemType);
        });
    }

    @Test
    void updateProblemType_ElementFound() {
        ProblemType problemType = getFullProblemType("problemTypeName");
        ProblemType compareProblemType = getFullProblemType("problemTypeName");

        ProblemType storedProblemType = problemTypeService.create(problemType);
        compareProblemType.setId(storedProblemType.getId());
        storedProblemType.setName("editedProblemTypeName");
        ProblemType editedProblemType = problemTypeService.update(storedProblemType);

        assertThat(editedProblemType.getId()).isNotNull();
        assertThat(editedProblemType.getId()).isEqualTo(compareProblemType.getId());
        assertThat(editedProblemType.getName()).isNotEqualTo(compareProblemType.getName());
        assertThat(storedProblemType.getParentProblemType()).isEqualTo(problemType.getParentProblemType());
    }

    @Test
    void deleteProblemType_WithLinks() {
        ProblemType problemType = getFullProblemType("problemTypeName");
        Set<ProblemType> problemTypes = new HashSet<>();
        problemTypes.add(problemType);

        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmWithProblemType");
        algorithm.setComputationModel(ComputationModel.CLASSIC);
        algorithm.setProblemTypes(problemTypes);

        Algorithm storedAlgorithm = this.algorithmService.create(algorithm);

        Set<ProblemType> storedProblemTypes = storedAlgorithm.getProblemTypes();
        storedProblemTypes.forEach(pt -> {
            assertDoesNotThrow(() -> problemTypeService.findById(pt.getId()));
            assertThrows(EntityReferenceConstraintViolationException.class, () -> problemTypeService.delete(pt.getId()));
            assertDoesNotThrow(() -> problemTypeService.findById(pt.getId()));
        });
    }

    @Test
    void deleteProblemType_RemoveFromParents() {
        ProblemType problemTypeParent = getFullProblemType("parentProblemTypeName");
        ProblemType persistedProblemTypeParent = problemTypeService.create(problemTypeParent);
        ProblemType problemType = getFullProblemType("problemTypeName");
        problemType.setParentProblemType(persistedProblemTypeParent.getId());
        problemType = problemTypeService.create(problemType);

        assertThat(problemTypeService.findById(problemType.getId()).getParentProblemType()).isNotNull();
        assertThat(problemTypeService.findById(problemType.getId()).getParentProblemType())
                .isEqualTo(persistedProblemTypeParent.getId());

        problemTypeService.delete(problemTypeParent.getId());

        assertThrows(NoSuchElementException.class, () -> problemTypeService.findById(persistedProblemTypeParent.getId()));

        assertThat(problemTypeService.findById(problemType.getId()).getParentProblemType()).isNull();
    }

    @Test
    void deleteProblemType_NoLinks() {
        ProblemType problemType = getFullProblemType("problemTypeName");
        ProblemType storedProblemType = problemTypeService.create(problemType);

        assertDoesNotThrow(() -> problemTypeService.findById(storedProblemType.getId()));

        problemTypeService.delete(storedProblemType.getId());

        assertThrows(NoSuchElementException.class, () -> problemTypeService.findById(storedProblemType.getId()));
    }

    @Test
    void getParentTreeList_NoParent() {
        ProblemType problemType = getFullProblemType("problemTypeName");
        ProblemType persistedProblemType = problemTypeService.create(problemType);

        List<ProblemType> problemTypeList = problemTypeService.getParentList(persistedProblemType.getId());

        assertThat(problemTypeList.size()).isEqualTo(1);
    }

    @Test
    void getParentTreeList_ReturnTwoParents() {
        ProblemType problemType = getFullProblemType("problemTypeName");
        ProblemType parentProblemType = getFullProblemType("parentProblemTypeName");
        ProblemType parentParentProblemType = getFullProblemType("parentParentProblemTypeName");
        parentProblemType.setParentProblemType(problemTypeService.create(parentParentProblemType).getId());
        problemType.setParentProblemType(problemTypeService.create(parentProblemType).getId());
        ProblemType persistedProblemType = problemTypeService.create(problemType);

        List<ProblemType> problemTypeList = problemTypeService.getParentList(persistedProblemType.getId());

        assertThat(problemTypeList.size()).isEqualTo(3);
    }

    private ProblemType getFullProblemType(String name) {
        ProblemType problemType = new ProblemType();

        problemType.setName(name);

        return problemType;
    }
}
