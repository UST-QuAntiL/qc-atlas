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

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternRelationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private PatternRelationService service;
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
        relation1 = new PatternRelation();
        relation1.setPattern(URI.create("http://www.relation1.de"));
        relation1.setDescription("Description1");

        // Init Relation2
        relation2 = new PatternRelation();
        relation2.setPattern(URI.create("http://www.relation2.de"));
        relation2.setDescription("Description2");

        // Init stored objects
        savedAlgorithm = algorithmService.save(algorithm);
        savedType = patternRelationTypeService.save(type);
    }

    @Test
    void createRelation_returnRelation() {
        // Fill and save relation with existing Type and Algorithm
        relation1.setAlgorithm(savedAlgorithm);
        relation1.setPatternRelationType(savedType);

        PatternRelation savedRelation = service.save(relation1);
        assertFalse(Objects.isNull(service.findById(savedRelation.getId())));
    }

    @Test
    void createRelationAndTypeOnFly_returnRelation() {
        // Fill and save relation with existing algorithm but new type
        relation1.setAlgorithm(savedAlgorithm);
        relation1.setPatternRelationType(type);

        PatternRelation savedRelation = service.save(relation1);
        assertFalse(Objects.isNull(service.findById(savedRelation.getId())));
        // PatternRelationType should have been created on the fly
        assertFalse(
                Objects.isNull(patternRelationTypeService.findById(savedRelation.getPatternRelationType().getId())));
    }

    @Test
    void createRelation_invalidRelationAlgoIdNull() {
        // Fill non saved algorithm (without id)
        algorithm.setId(null);
        relation1.setAlgorithm(algorithm);
        relation1.setPatternRelationType(type);

        assertThrows(NoSuchElementException.class, () -> {
            service.save(relation1);
        });
    }

    @Test
    void createRelation_invalidRelationAlgoDoesNotExist() {
        // Fill algorithm with random ID
        algorithm.setId(UUID.randomUUID());
        relation1.setAlgorithm(algorithm);
        relation1.setPatternRelationType(type);

        assertThrows(NoSuchElementException.class, () -> {
            service.save(relation1);
        });
    }

    @Test
    void updateRelation_notFound() {
        // Fill algorithm with random ID
        relation1.setId(UUID.randomUUID());
        relation1.setAlgorithm(savedAlgorithm);
        relation1.setPatternRelationType(savedType);

        assertThrows(NoSuchElementException.class, () -> {
            service.update(relation1);
        });
    }

    @Test
    void updateRelation_returnRelation() {
        // Fill and save relation with existing algorithm but new type
        relation1.setAlgorithm(savedAlgorithm);
        relation1.setPatternRelationType(type);

        // Update relation
        PatternRelation savedRelation = service.save(relation1);
        savedRelation.setDescription("UpdatedDescription");
        savedRelation.setPattern(URI.create("https://www.updated.com"));

        PatternRelation updatedRelation = service.update(savedRelation);

        assertEquals(updatedRelation.getDescription(), savedRelation.getDescription());
        assertEquals(updatedRelation.getPattern(), savedRelation.getPattern());
    }

    @Test
    void findAll_empty() {
        Page<PatternRelation> relations = service.findAll(pageable);
        assertTrue(relations.getContent().isEmpty());
    }

    @Test
    void findAll_returnTwo() {
        relation1.setAlgorithm(savedAlgorithm);
        relation1.setPatternRelationType(savedType);
        relation2.setAlgorithm(savedAlgorithm);
        relation2.setPatternRelationType(savedType);

        service.save(relation1);
        service.save(relation2);
        Page<PatternRelation> relations = service.findAll(pageable);
        assertEquals(relations.getContent().size(), 2);
    }

    @Test
    void findOne_returnOne() {
        relation1.setAlgorithm(savedAlgorithm);
        relation1.setPatternRelationType(savedType);

        PatternRelation savedRelation = service.save(relation1);

        assertEquals(service.findById(savedRelation.getId()).getId(), savedRelation.getId());
    }

    @Test
    void findOne_notFound() {
        assertThrows(NoSuchElementException.class, () -> {
            service.findById(UUID.randomUUID());
        });
    }

    @Test
    void delete_noContent() {
        assertThrows(EmptyResultDataAccessException.class, () -> {
            service.delete(UUID.randomUUID());
        });
    }

    @Test
    void delete_success() {
        relation1.setAlgorithm(savedAlgorithm);
        relation1.setPatternRelationType(savedType);

        PatternRelation savedRelation = service.save(relation1);
        assertEquals(1, algorithmService.findById(savedAlgorithm.getId()).getRelatedPatterns().size());

        service.delete(savedRelation.getId());
        assertEquals(0, algorithmService.findById(savedAlgorithm.getId()).getRelatedPatterns().size());
    }
}
