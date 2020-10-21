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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class AlgorithmRelationTypeServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private AlgorithmRelationTypeService algorithmRelationTypeService;
    @Autowired
    private AlgorithmRelationService algorithmRelationService;
    @Autowired
    private AlgorithmService algorithmService;

    @Test
    void createAlgorithmRelationType() {
        AlgorithmRelationType relationType = getFullAlgorithmRelationType("relationTypeName");

        AlgorithmRelationType storedRelationType = algorithmRelationTypeService.create(relationType);

        assertThat(storedRelationType.getId()).isNotNull();
        assertThat(storedRelationType.getName()).isEqualTo(relationType.getName());
    }

    @Test
    void findAllAlgorithmRelationTypes() {
        AlgorithmRelationType relationType1 = getFullAlgorithmRelationType("relationTypeName1");
        algorithmRelationTypeService.create(relationType1);
        AlgorithmRelationType relationType2 = getFullAlgorithmRelationType("relationTypeName2");
        algorithmRelationTypeService.create(relationType2);

        List<AlgorithmRelationType> algorithmRelationTypes = algorithmRelationTypeService.findAll(Pageable.unpaged()).getContent();

        assertThat(algorithmRelationTypes.size()).isEqualTo(2);
    }

    @Test
    void findAlgorithmRelationTypeById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                algorithmRelationTypeService.findById(UUID.randomUUID()));
    }

    @Test
    void findAlgorithmRelationTypeById_ElementFound() {
        AlgorithmRelationType relationType = getFullAlgorithmRelationType("relationTypeName");

        AlgorithmRelationType storedRelationType = algorithmRelationTypeService.create(relationType);

        storedRelationType = algorithmRelationTypeService.findById(storedRelationType.getId());

        assertThat(storedRelationType.getId()).isNotNull();
        assertThat(storedRelationType.getName()).isEqualTo(relationType.getName());
    }

    @Test
    void UpdateAlgorithmRelationType_ElementNotFound() {
        AlgorithmRelationType relationType = getFullAlgorithmRelationType("relationTypeName");
        relationType.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () ->
                algorithmRelationTypeService.update(relationType));
    }

    @Test
    void updateAlgorithmRelationType_ElementFound() {
        AlgorithmRelationType relationType = getFullAlgorithmRelationType("relationTypeName");
        AlgorithmRelationType compareRelationType = getFullAlgorithmRelationType("relationTypeName");

        AlgorithmRelationType storedRelationType = algorithmRelationTypeService.create(relationType);
        compareRelationType.setId(storedRelationType.getId());
        String editName = "editedRelationTypeName";
        storedRelationType.setName(editName);
        storedRelationType = algorithmRelationTypeService.update(storedRelationType);

        assertThat(storedRelationType.getId()).isNotNull();
        assertThat(storedRelationType.getId()).isEqualTo(compareRelationType.getId());
        assertThat(storedRelationType.getName()).isNotEqualTo(compareRelationType.getName());
        assertThat(storedRelationType.getName()).isEqualTo(editName);
    }

    @Test
    void deleteAlgorithmRelationType_UsedInRelation() {
        Algorithm sourceAlgorithm = new ClassicAlgorithm();
        sourceAlgorithm.setName("sourceAlgorithm");
        sourceAlgorithm.setComputationModel(ComputationModel.CLASSIC);
        Algorithm storedSourceAlgorithm = algorithmService.create(sourceAlgorithm);

        Algorithm targetAlgorithm = new ClassicAlgorithm();
        targetAlgorithm.setName("targetAlgorithm");
        targetAlgorithm.setComputationModel(ComputationModel.CLASSIC);
        Algorithm storedTargetAlgorithm = algorithmService.create(targetAlgorithm);

        AlgorithmRelationType relationType = getFullAlgorithmRelationType("relationTypeName");
        AlgorithmRelationType storedRelationType = algorithmRelationTypeService.create(relationType);

        AlgorithmRelation algorithmRelation = new AlgorithmRelation();
        algorithmRelation.setDescription("relationDescription");
        algorithmRelation.setSourceAlgorithm(storedSourceAlgorithm);
        algorithmRelation.setTargetAlgorithm(storedTargetAlgorithm);
        algorithmRelation.setAlgorithmRelationType(storedRelationType);

        algorithmRelationService.create(algorithmRelation);

        assertThrows(EntityReferenceConstraintViolationException.class, () -> algorithmRelationTypeService.delete(storedRelationType.getId()));

        assertDoesNotThrow(() -> algorithmRelationTypeService.findById(storedRelationType.getId()));
    }

    @Test
    void deleteAlgorithmRelationType_Unused() {
        AlgorithmRelationType relationType = getFullAlgorithmRelationType("relationTypeName");
        AlgorithmRelationType storedRelationType = algorithmRelationTypeService.create(relationType);

        assertDoesNotThrow(() -> algorithmRelationTypeService.findById(storedRelationType.getId()));

        algorithmRelationTypeService.delete(storedRelationType.getId());

        assertThrows(NoSuchElementException.class, () ->
                algorithmRelationTypeService.findById(storedRelationType.getId()));
    }

    private AlgorithmRelationType getFullAlgorithmRelationType(String typeName) {
        AlgorithmRelationType algorithmRelationType = new AlgorithmRelationType();
        algorithmRelationType.setName(typeName);
        return algorithmRelationType;
    }
}
