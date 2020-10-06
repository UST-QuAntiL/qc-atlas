/*******************************************************************************
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

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.planqk.atlas.core.util.ServiceTestUtils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class AlgorithmRelationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private AlgorithmRelationTypeService algorithmRelationTypeService;
    @Autowired
    private AlgorithmRelationService algorithmRelationService;
    @Autowired
    private AlgorithmService algorithmService;

    @Test
    void createAlgorithmRelation() {
        Algorithm sourceAlgorithm = getCreatedAlgorithm("sourceAlgorithmName");
        Algorithm targetAlgorithm = getCreatedAlgorithm("targetAlgorithmName");

        var algorithmRelationType = getCreatedAlgorithmRelationType("algorithmRelationTypeName");

        AlgorithmRelation algorithmRelation = buildAlgorithmRelation(
                sourceAlgorithm, targetAlgorithm, algorithmRelationType, "description");

        var persistedAlgorithmRelation = algorithmRelationService.create(algorithmRelation);

        assertThat(persistedAlgorithmRelation.getId()).isNotNull();
        ServiceTestUtils.assertAlgorithmRelationEquality(persistedAlgorithmRelation, algorithmRelation);
    }

    @Test
    void createAlgorithmRelation_AlgorithmNotFound() {
        Algorithm sourceAlgorithm = new ClassicAlgorithm();
        sourceAlgorithm.setName("sourceAlgorithmName");
        sourceAlgorithm.setId(UUID.randomUUID());
        Algorithm targetAlgorithm = new ClassicAlgorithm();
        targetAlgorithm.setName("targetAlgorithmName");
        targetAlgorithm.setId(UUID.randomUUID());

        var algorithmRelationType = getCreatedAlgorithmRelationType("algorithmRelationTypeName");

        AlgorithmRelation algorithmRelation = buildAlgorithmRelation(
                sourceAlgorithm, targetAlgorithm, algorithmRelationType, "description");

        assertThrows(NoSuchElementException.class, () -> algorithmRelationService.create(algorithmRelation));
    }

    @Test
    void findAlgorithmRelationById_ElementFound() {
        Algorithm sourceAlgorithm = getCreatedAlgorithm("sourceAlgorithmName");
        Algorithm targetAlgorithm = getCreatedAlgorithm("targetAlgorithmName");

        var algorithmRelationType = getCreatedAlgorithmRelationType("algorithmRelationTypeName");

        AlgorithmRelation algorithmRelation = buildAlgorithmRelation(
                sourceAlgorithm, targetAlgorithm, algorithmRelationType, "description");

        var persistedAlgorithmRelation = algorithmRelationService.create(algorithmRelation);

        persistedAlgorithmRelation = algorithmRelationService.findById(persistedAlgorithmRelation.getId());

        assertThat(persistedAlgorithmRelation.getId()).isNotNull();
        ServiceTestUtils.assertAlgorithmRelationEquality(persistedAlgorithmRelation, algorithmRelation);
    }

    @Test
    void findAlgorithmRelationById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> algorithmRelationService.findById(UUID.randomUUID()));
    }

    @Test
    void updateAlgorithmRelation_ElementFound() {
        Algorithm sourceAlgorithm = getCreatedAlgorithm("sourceAlgorithmName");
        Algorithm targetAlgorithm = getCreatedAlgorithm("targetAlgorithmName");

        var algorithmRelationType = getCreatedAlgorithmRelationType("algorithmRelationTypeName");

        AlgorithmRelation algorithmRelation = buildAlgorithmRelation(
                sourceAlgorithm, targetAlgorithm, algorithmRelationType, "description");
        AlgorithmRelation compareAlgorithmRelation = buildAlgorithmRelation(
                sourceAlgorithm, targetAlgorithm, algorithmRelationType, "description");

        var persistedAlgorithmRelation = algorithmRelationService.create(algorithmRelation);
        compareAlgorithmRelation.setId(persistedAlgorithmRelation.getId());

        String editDescription = "editedDescription";
        var editedType = getCreatedAlgorithmRelationType("editedAlgorithmRelationTypeName");
        persistedAlgorithmRelation.setDescription(editDescription);
        persistedAlgorithmRelation.setAlgorithmRelationType(editedType);

        var updatedAlgorithmRelation = algorithmRelationService.update(persistedAlgorithmRelation);

        assertThat(updatedAlgorithmRelation.getId()).isNotNull();
        assertThat(updatedAlgorithmRelation.getId()).isEqualTo(compareAlgorithmRelation.getId());
        assertThat(updatedAlgorithmRelation.getDescription()).isNotEqualTo(compareAlgorithmRelation.getDescription());
        assertThat(updatedAlgorithmRelation.getDescription()).isEqualTo(editDescription);
        assertThat(updatedAlgorithmRelation.getAlgorithmRelationType().getId())
                .isNotEqualTo(compareAlgorithmRelation.getAlgorithmRelationType().getId());
        assertThat(updatedAlgorithmRelation.getAlgorithmRelationType().getId())
                .isEqualTo(editedType.getId());
        assertThat(updatedAlgorithmRelation.getAlgorithmRelationType().getName())
                .isNotEqualTo(compareAlgorithmRelation.getAlgorithmRelationType().getName());
        assertThat(updatedAlgorithmRelation.getAlgorithmRelationType().getName())
                .isEqualTo(editedType.getName());
        assertThat(updatedAlgorithmRelation.getSourceAlgorithm().getId())
                .isEqualTo(compareAlgorithmRelation.getSourceAlgorithm().getId());
        assertThat(updatedAlgorithmRelation.getTargetAlgorithm().getId())
                .isEqualTo(compareAlgorithmRelation.getTargetAlgorithm().getId());
    }

    @Test
    void updateAlgorithmRelation_ElementNotFound() {
        AlgorithmRelation algorithmRelation = buildAlgorithmRelation(
                null, null, null, "description");
        algorithmRelation.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () -> algorithmRelationService.update(algorithmRelation));
    }

    @Test
    void deleteAlgorithmRelation_ElementFound() {
        Algorithm sourceAlgorithm = getCreatedAlgorithm("sourceAlgorithmName");
        Algorithm targetAlgorithm = getCreatedAlgorithm("targetAlgorithmName");

        var algorithmRelationType = getCreatedAlgorithmRelationType("algorithmRelationTypeName");

        AlgorithmRelation algorithmRelation = buildAlgorithmRelation(
                sourceAlgorithm, targetAlgorithm, algorithmRelationType, "description");

        var persistedAlgorithmRelation = algorithmRelationService.create(algorithmRelation);

        assertDoesNotThrow(() -> algorithmRelationService.findById(persistedAlgorithmRelation.getId()));

        algorithmRelationService.delete(persistedAlgorithmRelation.getId());

        assertThrows(NoSuchElementException.class, () ->
                algorithmRelationService.findById(persistedAlgorithmRelation.getId()));
    }

    @Test
    void deleteAlgorithmRelation_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                algorithmRelationService.delete(UUID.randomUUID()));
    }

    @Test
    void checkIfAlgorithmIsInAlgorithmRelation_IsInRelation() {
        Algorithm sourceAlgorithm = getCreatedAlgorithm("sourceAlgorithmName");
        Algorithm targetAlgorithm = getCreatedAlgorithm("targetAlgorithmName");

        var algorithmRelationType = getCreatedAlgorithmRelationType("algorithmRelationTypeName");

        AlgorithmRelation algorithmRelation = buildAlgorithmRelation(
                sourceAlgorithm, targetAlgorithm, algorithmRelationType, "description");

        var persistedAlgorithmRelation = algorithmRelationService.create(algorithmRelation);

        assertDoesNotThrow(() -> algorithmRelationService
                .checkIfAlgorithmIsInAlgorithmRelation(sourceAlgorithm.getId(), algorithmRelation.getId()));
        assertDoesNotThrow(() -> algorithmRelationService
                .checkIfAlgorithmIsInAlgorithmRelation(targetAlgorithm.getId(), algorithmRelation.getId()));
    }

    @Test
    void checkIfAlgorithmIsInAlgorithmRelation_IsNotInRelation() {
        Algorithm sourceAlgorithm = getCreatedAlgorithm("sourceAlgorithmName");
        Algorithm targetAlgorithm = getCreatedAlgorithm("targetAlgorithmName");
        Algorithm checkAlgorithm = getCreatedAlgorithm("checkAlgorithmName");

        var algorithmRelationType = getCreatedAlgorithmRelationType("algorithmRelationTypeName");

        AlgorithmRelation algorithmRelation = buildAlgorithmRelation(
                sourceAlgorithm, targetAlgorithm, algorithmRelationType, "description");

        var persistedAlgorithmRelation = algorithmRelationService.create(algorithmRelation);

        assertThrows(NoSuchElementException.class, () -> algorithmRelationService
                .checkIfAlgorithmIsInAlgorithmRelation(checkAlgorithm.getId(), algorithmRelation.getId()));
    }

    private AlgorithmRelation buildAlgorithmRelation(
            Algorithm source, Algorithm target, AlgorithmRelationType type, String description) {
        AlgorithmRelation algorithmRelation = new AlgorithmRelation();
        algorithmRelation.setDescription(description);
        algorithmRelation.setSourceAlgorithm(source);
        algorithmRelation.setTargetAlgorithm(target);
        algorithmRelation.setAlgorithmRelationType(type);
        return algorithmRelation;
    }

    private Algorithm getCreatedAlgorithm(String name) {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName(name);
        return algorithmService.create(algorithm);
    }

    private AlgorithmRelationType getCreatedAlgorithmRelationType(String name) {
        AlgorithmRelationType algorithmRelationType = new AlgorithmRelationType();
        algorithmRelationType.setName(name);
        return algorithmRelationTypeService.create(algorithmRelationType);
    }
}
