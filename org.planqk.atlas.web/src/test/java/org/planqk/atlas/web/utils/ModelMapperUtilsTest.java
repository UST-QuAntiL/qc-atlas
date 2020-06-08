package org.planqk.atlas.web.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.web.dtos.AlgorithmDto;
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

        algorithmDto.setProblemTypes(problemTypesDto);

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
        assertEquals(mappedDto.getProblemTypes().size(), algorithmDto.getProblemTypes().size());
        assertEquals(mappedDto.getProblemTypes(), algorithmDto.getProblemTypes());
    }

    @Test
    public void testModelMapper_dtoToEntity() {
        Algorithm mappedEntity = ModelMapperUtils.convert(algorithmDto, Algorithm.class);

        assertEquals(mappedEntity.getId(), algorithm.getId());
        assertEquals(mappedEntity.getName(), algorithm.getName());
        assertEquals(mappedEntity.getProblem(), algorithm.getProblem());
        assertEquals(mappedEntity.getComputationModel(), algorithm.getComputationModel());
        assertEquals(mappedEntity.getProblemTypes().size(), algorithm.getProblemTypes().size());
        assertEquals(mappedEntity.getProblemTypes(), algorithm.getProblemTypes());
    }

    @Test
    public void testModelMapper_entitySetToDtoSet() {
        Set<ProblemTypeDto> mappedDtoSet = ModelMapperUtils.convertSet(problemTypes, ProblemTypeDto.class);

        assertEquals(mappedDtoSet, problemTypesDto);
    }

    @Test
    public void testModelMapper_dtoSetToEntitySet() {
        Set<ProblemType> mappedSet = ModelMapperUtils.convertSet(problemTypesDto, ProblemType.class);

        assertEquals(mappedSet, problemTypes);
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
}
