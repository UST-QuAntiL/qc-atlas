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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.PatternRelationTypeRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
public class PatternRelationTypeServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private PatternRelationTypeService patternRelationTypeService;
    @Autowired
    private PatternRelationTypeRepository patternRelationTypeRepository;
    @Autowired
    private PatternRelationService patternRelationService;
    @Autowired
    private AlgorithmRepository algorithmRepository;

    private PatternRelationType type1;
    private PatternRelationType type2;
    private PatternRelationType type1Updated;
    private Algorithm algorithm;
    private PatternRelation relation;

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);

    @BeforeEach
    public void initialize() {
        // Fill Type-Objects
        type1 = new PatternRelationType();
        type1.setName("PatternType1");
        type2 = new PatternRelationType();
        type2.setName("PatternType2");
        type1Updated = new PatternRelationType();
        type1Updated.setName("PatternType1Updated");

        // Fill Algorithm
        algorithm = new ClassicAlgorithm();
        algorithm.setName("Algorithm");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        // Fill Relation
        relation = new PatternRelation();
        relation.setDescription("Description1");
        relation.setPattern(URI.create("http://www.pattern.com"));
        relation.setAlgorithm(algorithm);
        relation.setPatternRelationType(type1);
    }

    @Test
    void createPatternRelationType() {
        PatternRelationType storedType1 = patternRelationTypeService.create(type1);

        assertThat(storedType1.getId()).isNotNull();
        assertThat(storedType1.getName()).isEqualTo(type1.getName());
        Assertions.assertDoesNotThrow(() -> patternRelationTypeService.findById(storedType1.getId()));
    }

    @Test
    void findAll_empty() {
        Page<PatternRelationType> typesPaged = patternRelationTypeService.findAll(pageable);

        assertThat(typesPaged.getContent()).isEmpty();
    }

    @Test
    void findAll_returnTwo() {
        patternRelationTypeService.create(type1);
        patternRelationTypeService.create(type2);
        Page<PatternRelationType> typesPaged = patternRelationTypeService.findAll(pageable);

        assertThat(typesPaged.getContent().size()).isEqualTo(2);
    }

    @Test
    void getPatternRelationTypeById_ElementFound() {
        PatternRelationType storedType1 = patternRelationTypeService.create(type1);

        storedType1 = patternRelationTypeService.findById(storedType1.getId());

        assertThat(storedType1.getId()).isNotNull();
        assertThat(storedType1.getName()).isEqualTo(type1.getName());
    }

    @Test
    void getPatternRelationTypeById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> patternRelationTypeService.findById(UUID.randomUUID()));
    }

    @Test
    void updatePatternRelationType_ElementFound() {
        PatternRelationType storedType1 = patternRelationTypeService.create(type1);
        storedType1.setName(type1Updated.getName());
        PatternRelationType updatedType1 = patternRelationTypeService.update(storedType1);
        assertEquals(updatedType1.getId(), storedType1.getId());
        assertEquals(updatedType1.getName(), type1Updated.getName());
        assertTrue(patternRelationTypeRepository.findById(updatedType1.getId()).isPresent());
    }

    @Test
    void updatePatternRelationType_ElementNotFound() {
        type1.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () -> patternRelationTypeService.update(type1));
    }

    @Test
    void deletePatternRelationType_UsedInRelation() {
        Algorithm storedAlgorithm = algorithmRepository.save(algorithm);
        relation.setAlgorithm(storedAlgorithm);

        PatternRelationType storedType1 = patternRelationTypeRepository.save(type1);

        Assertions.assertDoesNotThrow(() -> patternRelationTypeService.findById(storedType1.getId()));

        PatternRelation storedRelation = patternRelationService.create(relation);

        assertThrows(ConsistencyException.class, () ->
            patternRelationTypeService.delete(storedType1.getId()));
    }

    @Test
    void deletePatternRelationType_UnusedElementFound() {
        PatternRelationType storedType1 = patternRelationTypeService.create(type1);

        Assertions.assertDoesNotThrow(() -> patternRelationTypeService.findById(storedType1.getId()));

        patternRelationTypeService.delete(storedType1.getId());

        assertThrows(NoSuchElementException.class, () -> patternRelationTypeService.findById(storedType1.getId()));
    }

    @Test
    void deletePatternRelationType_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () ->
            patternRelationTypeService.delete(UUID.randomUUID()));
    }
}
