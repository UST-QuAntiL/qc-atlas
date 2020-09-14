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

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class PatternRelationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private PatternRelationService patternRelationService;
    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private PatternRelationTypeService patternRelationTypeService;

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);

    private Algorithm algorithm;
    private PatternRelationType type;
    private PatternRelation relation1;
    private PatternRelation relation2;

    private PatternRelationType savedType;
    private Algorithm savedAlgorithm;

    @BeforeEach
    public void initialize() {
        // Init Algorithm
        algorithm = new ClassicAlgorithm();
        algorithm.setName("Algorithm");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        // Init Type
        type = new PatternRelationType();
        type.setName("PatternRelationType");

        // Init Relation
        relation1 = getFullPatternRelation("description1");

        // Init Relation2
        relation2 = getFullPatternRelation("description2");
    }

    @Test
    void createPatternRelation() {
        savedAlgorithm = algorithmService.create(algorithm);
        relation1.setAlgorithm(savedAlgorithm);

        savedType = patternRelationTypeService.create(type);
        relation1.setPatternRelationType(savedType);

        PatternRelation savedRelation = patternRelationService.create(relation1);

        assertThat(savedRelation.getId()).isNotNull();
        assertThat(savedRelation.getPattern()).isEqualTo(relation1.getPattern());
        assertThat(savedRelation.getDescription()).isEqualTo(relation1.getDescription());

        assertDoesNotThrow(() -> patternRelationService.findById(savedRelation.getId()));
    }

    @Test
    void createPatternRelation_TypeNotFound() {
        savedAlgorithm = algorithmService.create(algorithm);
        relation1.setAlgorithm(savedAlgorithm);

        type.setId(UUID.randomUUID());
        relation1.setPatternRelationType(type);

        assertThrows(NoSuchElementException.class, () -> patternRelationService.create(relation1));
    }

    @Test
    void createPatternRelation_AlgorithmNotFound() {
        algorithm.setId(UUID.randomUUID());
        relation1.setAlgorithm(algorithm);

        savedType = patternRelationTypeService.create(type);
        relation1.setPatternRelationType(savedType);

        assertThrows(NoSuchElementException.class, () -> patternRelationService.create(relation1));
    }

    @Test
    void findAllPatternRelations_empty() {
        Page<PatternRelation> relations = patternRelationService.findAll(pageable);
        assertThat(relations.getContent()).isEmpty();
    }

    @Test
    void findAllPatternRelations_returnTwo() {
        savedAlgorithm = algorithmService.create(algorithm);
        relation1.setAlgorithm(savedAlgorithm);
        relation2.setAlgorithm(savedAlgorithm);

        savedType = patternRelationTypeService.create(type);
        relation1.setPatternRelationType(savedType);
        relation2.setPatternRelationType(savedType);

        patternRelationService.create(relation1);
        patternRelationService.create(relation2);

        Page<PatternRelation> relations = patternRelationService.findAll(pageable);
        assertThat(relations.getContent().size()).isEqualTo(2);
    }

    @Test
    void findPatternRelationById_ElementFound() {
        savedAlgorithm = algorithmService.create(algorithm);
        relation1.setAlgorithm(savedAlgorithm);

        savedType = patternRelationTypeService.create(type);
        relation1.setPatternRelationType(savedType);

        PatternRelation savedRelation = patternRelationService.create(relation1);

        savedRelation = patternRelationService.findById(savedRelation.getId());

        assertThat(savedRelation.getId()).isNotNull();
        assertThat(savedRelation.getPattern()).isEqualTo(relation1.getPattern());
        assertThat(savedRelation.getDescription()).isEqualTo(relation1.getDescription());
    }

    @Test
    void findPatternRelationById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                patternRelationService.findById(UUID.randomUUID()));
    }

    @Test
    void updatePatternRelation_ElementNotFound() {
        relation1.setId(UUID.randomUUID());

        assertThrows(NoSuchElementException.class, () ->
                patternRelationService.update(relation1));
    }

    @Test
    void updatePatternRelation_ElementFound() {
        savedAlgorithm = algorithmService.create(algorithm);
        relation1.setAlgorithm(savedAlgorithm);

        savedType = patternRelationTypeService.create(type);
        relation1.setPatternRelationType(savedType);

        PatternRelation savedRelation = patternRelationService.create(relation1);

        var editedDescription = "updatedDescription";
        var editedPattern = URI.create("https://www.updated.com");
        savedRelation.setDescription(editedDescription);
        savedRelation.setPattern(editedPattern);

        PatternRelation updatedRelation = patternRelationService.update(savedRelation);

        assertThat(updatedRelation.getId()).isEqualTo(savedRelation.getId());
        assertThat(updatedRelation.getDescription()).isEqualTo(savedRelation.getDescription());
        assertThat(updatedRelation.getDescription()).isEqualTo(editedDescription);
        assertThat(updatedRelation.getPattern()).isEqualTo(savedRelation.getPattern());
        assertThat(updatedRelation.getPattern()).isEqualTo(editedPattern);
    }

    @Test
    void deletePatternRelation_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                patternRelationService.delete(UUID.randomUUID()));
    }

    @Test
    void deletePatternRelation_ElementFound() {
        savedAlgorithm = algorithmService.create(algorithm);
        relation1.setAlgorithm(savedAlgorithm);

        savedType = patternRelationTypeService.create(type);
        relation1.setPatternRelationType(savedType);

        PatternRelation savedRelation = patternRelationService.create(relation1);

        assertDoesNotThrow(() -> patternRelationService.findById(savedRelation.getId()));

        patternRelationService.delete(savedRelation.getId());

        assertThrows(NoSuchElementException.class, () -> patternRelationService.findById(savedRelation.getId()));
    }

    private PatternRelation getFullPatternRelation(String description) {
        var patternRelation = new PatternRelation();

        patternRelation.setPattern(URI.create("http://www.example.de"));
        patternRelation.setDescription(description);

        return patternRelation;
    }
}
