package org.planqk.atlas.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.services.AlgoRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;
//import org.planqk.atlas.web.dtos.AlgoRelationTypeListDto;
import org.planqk.atlas.web.utils.DtoEntityConverter;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebMvcTest
public class AlgoRelationTypeControllerTest {

	@Mock
	AlgoRelationTypeService algoRelationTypeService;

	@InjectMocks
	AlgoRelationTypeController algoRelationTypeController;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private int page = 0;
    private int size = 2;
    private Pageable pageable = PageRequest.of(page, size);

    private AlgoRelationType algoRelationType1;
    private AlgoRelationType algoRelationType2;
    private AlgoRelationTypeDto algoRelationType1Dto;
    private AlgoRelationTypeDto algoRelationType2Dto;

	@Before
	public void initialize() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(algoRelationTypeController).build();
        mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

        algoRelationType1 = new AlgoRelationType();
        algoRelationType1.setId(UUID.randomUUID());
        algoRelationType1.setName("relationType1");
        algoRelationType2 = new AlgoRelationType();
        algoRelationType2.setId(UUID.randomUUID());
        algoRelationType2.setName("relationType2");

        algoRelationType1Dto = ModelMapperUtils.convert(algoRelationType1, AlgoRelationTypeDto.class);
        algoRelationType2Dto = ModelMapperUtils.convert(algoRelationType2, AlgoRelationTypeDto.class);

				when(modelConverter.convert(algoRelationType1, AlgoRelationTypeDto.class)).thenReturn(algoRelationType1Dto);
				when(modelConverter.convert(algoRelationType1Dto, AlgoRelationType.class)).thenReturn(algoRelationType1);
        when(algoRelationTypeService.findById(any(UUID.class))).thenReturn(Optional.empty());
        when(algoRelationTypeService.findById(algoRelationType1.getId())).thenReturn(Optional.of(algoRelationType1));
	}

	public void setupTest() {
		assertNotNull(mockMvc);
		assertNotNull(algoRelationTypeController);
	}

	@Test
	public void createAlgoRelationType_returnBadRequest() throws Exception {

		AlgoRelationTypeDto algoRelationTypeDto = new AlgoRelationTypeDto();
		algoRelationTypeDto.setId(UUID.randomUUID());
		mockMvc.perform(post("/" + Constants.ALGO_RELATION_TYPES + "/")
				.content(mapper.writeValueAsString(algoRelationTypeDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	public void createAlgoRelationType_returnCreate() throws Exception {

		when(algoRelationTypeService.save(algoRelationType1)).thenReturn(algoRelationType1);

		MvcResult result = mockMvc.perform(post("/" + Constants.ALGO_RELATION_TYPES + "/")
				.content(mapper.writeValueAsString(algoRelationType1Dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

		AlgoRelationTypeDto type = mapper.readValue(result.getResponse().getContentAsString(), AlgoRelationTypeDto.class);
		assertEquals(algoRelationType1Dto.getId(), type.getId());
	}

	@Test
	public void updateAlgoRelationType_returnBadRequest() throws Exception {

		AlgoRelationTypeDto algoRelationTypeDto = new AlgoRelationTypeDto();
		algoRelationTypeDto.setId(UUID.randomUUID());
		mockMvc.perform(put("/" + Constants.ALGO_RELATION_TYPES + "/{id}",algoRelationTypeDto.getId())
				.content(mapper.writeValueAsString(algoRelationTypeDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	public void updateAlgoRelationType_returnOk() throws Exception {

		when(algoRelationTypeService.update(algoRelationType1.getId(), algoRelationType1)).thenReturn(algoRelationType1);

		MvcResult result = mockMvc.perform(put("/" + Constants.ALGO_RELATION_TYPES + "/{id}",algoRelationType1.getId())
				.content(mapper.writeValueAsString(algoRelationType1Dto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		AlgoRelationTypeDto type = mapper.readValue(result.getResponse().getContentAsString(), AlgoRelationTypeDto.class);
		assertEquals(algoRelationType1Dto.getId(), type.getId());
	}

	@Test
	public void getAlgoRelationTypes_withEmptyAlgoRelationTypeList() throws Exception {

		when(algoRelationTypeService.findAll(pageable)).thenReturn(new PageImpl<>(new ArrayList<AlgoRelationType>()));

		MvcResult result = mockMvc.perform(get("/" + Constants.ALGO_RELATION_TYPES + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

//		AlgoRelationTypeListDto algoRelationTypeListDto = mapper.readValue(result.getResponse().getContentAsString(), AlgoRelationTypeListDto.class);
//		assertEquals(0, algoRelationTypeListDto.getAlgoRelationTypeDtos().size());
	}

	@Test
	public void getAlgoRelationTypes_withTwoAlgoRelationTypeList() throws Exception {

		List<AlgoRelationType> algoRelationList = new ArrayList<>();
		algoRelationList.add(algoRelationType1);
		algoRelationList.add(algoRelationType2);

		when(algoRelationTypeService.findAll(pageable)).thenReturn(new PageImpl<>(algoRelationList));

		MvcResult result = mockMvc.perform(get("/" + Constants.ALGO_RELATION_TYPES + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

//		AlgoRelationTypeListDto algoRelationTypeListDto = mapper.readValue(result.getResponse().getContentAsString(), AlgoRelationTypeListDto.class);
//		assertEquals(2, algoRelationTypeListDto.getAlgoRelationTypeDtos().size());
	}

	@Test
	public void getAlgoRelationTypeById_returnNotFound() throws Exception {

		when(algoRelationTypeService.findById(algoRelationType1.getId())).thenThrow(NotFoundException.class);

		mockMvc.perform(get("/" + Constants.ALGO_RELATION_TYPES + "/{id}", this.algoRelationType1.getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	public void getAlgoRelationTypeById_returnAlgoRelationType() throws Exception {

		when(algoRelationTypeService.findById(algoRelationType1.getId())).thenReturn(algoRelationType1);

		MvcResult result = mockMvc.perform(get("/" + Constants.ALGO_RELATION_TYPES + "/{id}", this.algoRelationType1.getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		AlgoRelationTypeDto algoRelationTypeDto = mapper.readValue(result.getResponse().getContentAsString(), AlgoRelationTypeDto.class);
		assertEquals(algoRelationType1.getId(), algoRelationTypeDto.getId());
	}

	@Test
	public void deleteAlgoRelationType_returnNotFound() throws Exception {

		mockMvc.perform(delete("/" + Constants.ALGO_RELATION_TYPES + "/{id}", this.algoRelationType1.getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	public void deleteAlgoRelationType_returnOk() throws Exception {

		mockMvc.perform(delete("/" + Constants.ALGO_RELATION_TYPES + "/{id}", this.algoRelationType1.getId())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

}
