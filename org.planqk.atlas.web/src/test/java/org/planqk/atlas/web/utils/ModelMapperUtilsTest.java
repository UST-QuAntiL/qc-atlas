package org.planqk.atlas.web.utils;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ModelMapperUtilsTest {

	private Algorithm algorithm;
	private AlgorithmDto algorithmDto;

	@Before
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

		Set<ProblemType> problemTypes = new HashSet<>();

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

		Set<ProblemTypeDto> problemTypesDto = new HashSet<>();

		ProblemTypeDto type1Dto = new ProblemTypeDto();
		type1Dto.setId(problemTypeId);
		type1Dto.setName("ProblemType1");

		ProblemTypeDto type2Dto = new ProblemTypeDto();
		type2Dto.setId(problemTypeId2);
		type2Dto.setName("ProblemType2");

		problemTypesDto.add(type1Dto);
		problemTypesDto.add(type2Dto);

		algorithmDto.setProblemTypes(problemTypesDto);
	}
	
	@Test
	public void testModelMapper_entityToDto() {
		AlgorithmDto mappedDto = ModelMapperUtils.convert(algorithm, AlgorithmDto.class);
		
		assertEquals(mappedDto.getId(), algorithmDto.getId());
		assertEquals(mappedDto.getName(), algorithmDto.getName());
		assertEquals(mappedDto.getProblem(), algorithmDto.getProblem());
		assertEquals(mappedDto.getComputationModel(), algorithmDto.getComputationModel());
		assertEquals(mappedDto.getProblemTypes().size(), algorithmDto.getProblemTypes().size());
		assertEquals(mappedDto.getProblemTypes(),  algorithmDto.getProblemTypes());
	}
	
	@Test
	public void testModelMapper_dtoToEntity() {
		Algorithm mappedEntity = ModelMapperUtils.convert(algorithmDto, Algorithm.class);
		
		assertEquals(mappedEntity.getId(), algorithm.getId());
		assertEquals(mappedEntity.getName(), algorithm.getName());
		assertEquals(mappedEntity.getProblem(), algorithm.getProblem());
		assertEquals(mappedEntity.getComputationModel(), algorithm.getComputationModel());
		assertEquals(mappedEntity.getProblemTypes().size(), algorithm.getProblemTypes().size());
		assertEquals(mappedEntity.getProblemTypes(),  algorithm.getProblemTypes());
	}

}
