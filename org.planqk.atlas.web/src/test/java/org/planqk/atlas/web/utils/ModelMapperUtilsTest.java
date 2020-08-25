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

package org.planqk.atlas.web.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.Assert.assertEquals;

public class ModelMapperUtilsTest {

    private Algorithm algorithm;
    private AlgorithmDto algorithmDto;

    private Set<ProblemType> problemTypes;
    private Set<ProblemTypeDto> problemTypesDto;

    private Pageable pageable = PageRequest.of(0, 2);
    private Page<ProblemType> pagedProblemTypes;
    private Page<ProblemTypeDto> pagedProblemTypesDto;

    @BeforeEach
    public void initialize() {
        // Prepare IDs
        UUID id = UUID.randomUUID();
        UUID problemTypeId = UUID.randomUUID();
        UUID problemTypeId2 = UUID.randomUUID();

        // Init Objects
        algorithm = new Algorithm();
        algorithmDto = new AlgorithmDto();

        // Fill Algorithm Object
        algorithm.setId(id);
        algorithm.setName("Algorithm1");
        algorithm.setProblem("Problem1");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        problemTypes = new HashSet<>();

        ProblemType type1 = new ProblemType();
        type1.setId(problemTypeId);
        type1.setName("ProblemType1");

        ProblemType type2 = new ProblemType();
        type2.setId(problemTypeId2);
        type2.setName("ProblemType2");

        problemTypes.add(type1);
        problemTypes.add(type2);

        algorithm.setProblemTypes(problemTypes);

        // Fill AlgorithmDto Object
        algorithmDto.setId(id);
        algorithmDto.setName("Algorithm1");
        algorithmDto.setProblem("Problem1");
        algorithmDto.setComputationModel(ComputationModel.CLASSIC);

        problemTypesDto = new HashSet<>();

        ProblemTypeDto type1Dto = new ProblemTypeDto();
        type1Dto.setId(problemTypeId);
        type1Dto.setName("ProblemType1");

        ProblemTypeDto type2Dto = new ProblemTypeDto();
        type2Dto.setId(problemTypeId2);
        type2Dto.setName("ProblemType2");

        problemTypesDto.add(type1Dto);
        problemTypesDto.add(type2Dto);

        // Generate Page objects
        pagedProblemTypes = new PageImpl<ProblemType>(new ArrayList<>(problemTypes), pageable, problemTypes.size());
        pagedProblemTypesDto = new PageImpl<ProblemTypeDto>(new ArrayList<>(problemTypesDto), pageable,
                problemTypesDto.size());
    }

    @Test
    public void testModelMapper_entityToDto() {
        AlgorithmDto mappedDto = ModelMapperUtils.convert(algorithm, AlgorithmDto.class);

        assertEquals(mappedDto.getId(), algorithmDto.getId());
        assertEquals(mappedDto.getName(), algorithmDto.getName());
        assertEquals(mappedDto.getProblem(), algorithmDto.getProblem());
        assertEquals(mappedDto.getComputationModel(), algorithmDto.getComputationModel());
    }

    @Test
    public void testModelMapper_dtoToEntity() {
        Algorithm mappedEntity = ModelMapperUtils.convert(algorithmDto, Algorithm.class);

        assertEquals(mappedEntity.getId(), algorithm.getId());
        assertEquals(mappedEntity.getName(), algorithm.getName());
        assertEquals(mappedEntity.getProblem(), algorithm.getProblem());
        assertEquals(mappedEntity.getComputationModel(), algorithm.getComputationModel());
    }

    @Test
    public void testModelMapper_entityPageToDtoPage() {
        Page<ProblemTypeDto> mappedDtoPage = ModelMapperUtils.convertPage(pagedProblemTypesDto, ProblemTypeDto.class);

        assertEquals(mappedDtoPage, pagedProblemTypesDto);
    }

    @Test
    public void testModelMapper_dtoPageToEntityPage() {
        Page<ProblemType> mappedPage = ModelMapperUtils.convertPage(pagedProblemTypes, ProblemType.class);

        assertEquals(mappedPage, pagedProblemTypes);
    }

    @Test
    public void testInheritance() {
        QuantumAlgorithm alg = new QuantumAlgorithm();
        alg.setName("QuantumName");
        alg.setComputationModel(ComputationModel.QUANTUM);
        alg.setNisqReady(true);
        alg.setSpeedUp("SpeedUp1");

        PatternRelationType type = new PatternRelationType();
        type.setName("TypeName");

        PatternRelation relation = new PatternRelation();
        relation.setAlgorithm(alg);
        relation.setPatternRelationType(type);
        relation.setPattern(URI.create("http://www.test.com"));
        relation.setDescription("Description");

        PatternRelationDto dto = ModelMapperUtils.convert(relation, PatternRelationDto.class);

        assertEquals(dto.getDescription(), relation.getDescription());
        assertEquals(dto.getPattern(), relation.getPattern());
    }
}
