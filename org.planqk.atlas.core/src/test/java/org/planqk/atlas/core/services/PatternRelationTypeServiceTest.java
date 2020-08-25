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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.PatternRelationRepository;
import org.planqk.atlas.core.repository.PatternRelationTypeRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
public class PatternRelationTypeServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private PatternRelationTypeService service;
    @Autowired
    private PatternRelationTypeRepository repo;
    @Autowired
    private PatternRelationRepository relationRepo;
    @Autowired
    private AlgorithmRepository algoRepo;

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
    void createType() {
        PatternRelationType storedType1 = service.create(type1);

        assertFalse(Objects.isNull(storedType1.getId()));
        assertEquals(storedType1.getName(), type1.getName());
        assertTrue(repo.findById(storedType1.getId()).isPresent());
    }

    @Test
    void createType_updateType() {
        PatternRelationType storedType1 = service.create(type1);
        storedType1.setName(type1Updated.getName());
        PatternRelationType updatedType1 = service.update(storedType1);
        assertEquals(updatedType1.getId(), storedType1.getId());
        assertEquals(updatedType1.getName(), type1Updated.getName());
        assertTrue(repo.findById(updatedType1.getId()).isPresent());
    }

    @Test
    void updateType_notFound() {
        type1Updated.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () ->
            service.update(type1Updated));
    }

    @Test
    void getType_returnType() {
        PatternRelationType storedType1 = service.create(type1);

        assertFalse(Objects.isNull(service.findById(storedType1.getId())));
        assertEquals(service.findById(storedType1.getId()).getName(), type1.getName());
    }

    @Test
    void getType_notFound() {
        assertThrows(NoSuchElementException.class, () -> {
            service.findById(UUID.randomUUID());
        });
    }

    @Test
    void getTypes_empty() {
        Page<PatternRelationType> typesPaged = service.findAll(pageable);

        assertTrue(typesPaged.getContent().isEmpty());
    }

    @Test
    void getTypes_returnTwo() {
        service.create(type1);
        service.create(type2);
        Page<PatternRelationType> typesPaged = service.findAll(pageable);

        assertTrue(typesPaged.getContent().size() == 2);
    }

    @Test
    void delete_success() {
        PatternRelationType createType = service.create(type1);

        assertFalse(Objects.isNull(createType.getId()));
        assertTrue(repo.findById(createType.getId()).isPresent());

        service.delete(createType.getId());
        assertTrue(repo.findById(createType.getId()).isEmpty());
    }

    @Test
    void delete_consistencyError() {
        Algorithm storedAlgorithm = algoRepo.save(algorithm);
        relation.setAlgorithm(storedAlgorithm);

        assertTrue(algoRepo.findById(storedAlgorithm.getId()).isPresent());

        PatternRelationType storedType = repo.save(type1);

        assertTrue(repo.findById(storedType.getId()).isPresent());

        PatternRelation storedRelation = relationRepo.save(relation);

        assertTrue(relationRepo.findById(storedRelation.getId()).isPresent());

        assertThrows(ConsistencyException.class, () -> {
            service.delete(storedType.getId());
        });
    }

    @Test
    void delete_noContent() {
        assertThrows(EmptyResultDataAccessException.class, () -> {
            service.delete(UUID.randomUUID());
        });
    }
}
