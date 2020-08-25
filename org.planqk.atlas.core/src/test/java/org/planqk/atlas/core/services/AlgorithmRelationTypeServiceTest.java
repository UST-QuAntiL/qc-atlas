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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class AlgorithmRelationTypeServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private AlgorithmRelationTypeService algorithmRelationTypeService;
    @Autowired
    private AlgorithmService algorithmService;

    @Test
    void testAddAlgoRelationType() {
        AlgorithmRelationType relationType = getGenericAlgoRelationType("testRelation");

        AlgorithmRelationType storedRelationType = algorithmRelationTypeService.create(relationType);

        assertThat(storedRelationType.getId()).isNotNull();
        assertThat(storedRelationType.getName()).isEqualTo(relationType.getName());
    }

    @Test
    void testUpdateAlgoRelationType_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                algorithmRelationTypeService.update(null));
    }

    @Test
    void testUpdateAlgoRelationType_ElementFound() {
        AlgorithmRelationType relationType = getGenericAlgoRelationType("testRelation");
        AlgorithmRelationType compareRelationType = getGenericAlgoRelationType("testRelation");

        AlgorithmRelationType storedRelationType = algorithmRelationTypeService.create(relationType);
        compareRelationType.setId(storedRelationType.getId());
        String editName = "editedRelation";
        storedRelationType.setName(editName);
        storedRelationType = algorithmRelationTypeService.update(storedRelationType);

        assertThat(storedRelationType.getId()).isNotNull();
        assertThat(storedRelationType.getId()).isEqualTo(compareRelationType.getId());
        assertThat(storedRelationType.getName()).isNotEqualTo(compareRelationType.getName());
        assertThat(storedRelationType.getName()).isEqualTo(editName);
    }

    @Test
    void testFindAlgoRelationTypeById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                algorithmRelationTypeService.findById(UUID.randomUUID()));
    }

    @Test
    void testFindAlgoRelationTypeById_ElementFound() {
        AlgorithmRelationType relationType = getGenericAlgoRelationType("testRelation");

        AlgorithmRelationType storedRelationType = algorithmRelationTypeService.create(relationType);

        storedRelationType = algorithmRelationTypeService.findById(storedRelationType.getId());

        assertThat(storedRelationType.getId()).isNotNull();
        assertThat(storedRelationType.getName()).isEqualTo(relationType.getName());
    }

    @Test
    void testFindAll() {
        AlgorithmRelationType relationType1 = getGenericAlgoRelationType("testRelationType1");
        algorithmRelationTypeService.create(relationType1);
        AlgorithmRelationType relationType2 = getGenericAlgoRelationType("testRelationType2");
        algorithmRelationTypeService.create(relationType2);

        List<AlgorithmRelationType> algorithmRelationTypes = algorithmRelationTypeService.findAll(Pageable.unpaged()).getContent();

        assertThat(algorithmRelationTypes.size()).isEqualTo(2);
    }

//    @Test
//    void testDeleteAlgoRelationType_HasReferences() {
//        Algorithm sourceAlgorithm = new ClassicAlgorithm();
//        sourceAlgorithm.setName("sourceAlgorithm");
//        sourceAlgorithm.setComputationModel(ComputationModel.CLASSIC);
//        Algorithm storedSourceAlgorithm = algorithmService.save(sourceAlgorithm);
//        Algorithm targetAlgorithm = new ClassicAlgorithm();
//        targetAlgorithm.setName("sourceAlgorithm");
//        targetAlgorithm.setComputationModel(ComputationModel.CLASSIC);
//        algorithmService.save(targetAlgorithm);
//
//        AlgoRelationType relationType = getGenericAlgoRelationType("testRelation");
//        AlgoRelationType storedRelationType = algoRelationTypeService.save(relationType);
//
//        AlgorithmRelation algorithmRelation = new AlgorithmRelation();
//        algorithmRelation.setDescription("testRelationDescription");
//        algorithmRelation.setSourceAlgorithm(sourceAlgorithm);
//        algorithmRelation.setTargetAlgorithm(targetAlgorithm);
//        algorithmRelation.setAlgoRelationType(storedRelationType);
//
//        algorithmService.addOrUpdateAlgorithmRelation(storedSourceAlgorithm.getId(), algorithmRelation);
//
//        Assertions.assertThrows(ConsistencyException.class, () -> algoRelationTypeService.delete(storedRelationType.getId()));
//
//        Assertions.assertDoesNotThrow(() -> algoRelationTypeService.findById(storedRelationType.getId()));
//    }

    @Test
    void testDeleteAlgoRelationType_NoReferences() {
        AlgorithmRelationType relationType = getGenericAlgoRelationType("testRelation");
        AlgorithmRelationType storedRelationType = algorithmRelationTypeService.create(relationType);

        Assertions.assertDoesNotThrow(() -> algorithmRelationTypeService.findById(storedRelationType.getId()));

        algorithmRelationTypeService.delete(storedRelationType.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                algorithmRelationTypeService.findById(storedRelationType.getId()));
    }

    private AlgorithmRelationType getGenericAlgoRelationType(String typeName) {
        AlgorithmRelationType algorithmRelationType = new AlgorithmRelationType();
        algorithmRelationType.setName(typeName);
        return algorithmRelationType;
    }

}
