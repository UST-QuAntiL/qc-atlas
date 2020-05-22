package org.planqk.atlas.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.services.AlgoRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

@WebMvcTest
public class AlgoRelationTypeControllerTest {
	
	@Mock
	AlgoRelationTypeService algoRelationTypeService;
	
	@InjectMocks
	AlgoRelationTypeController algoRelationTypeController;

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    
    private AlgoRelationType algoRelationType;
    private AlgoRelationTypeDto algoRelationTypeDto;
	
	@Before
	public void initialize() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(algoRelationTypeController).build();
        mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        
        algoRelationType = new AlgoRelationType();
        algoRelationType.setId(UUID.randomUUID());
        algoRelationType.setName("relationType");
        
        algoRelationTypeDto = AlgoRelationTypeDto.Converter.convert(algoRelationType);
        
        when(algoRelationTypeService.save(any(AlgoRelationType.class))).thenReturn(algoRelationType);
	}
	
	public void setupTest() {
		assertNotNull(mockMvc);
		assertNotNull(algoRelationTypeController);
	}
	
	@Test
	public void createAlgoRelationType_returnCreate() throws Exception {
		
		MvcResult result = mockMvc.perform(post("/" + Constants.ALGO_RELATION_TYPES + "/")
				.content(mapper.writeValueAsString(algoRelationTypeDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
		
		AlgoRelationTypeDto type = mapper.readValue(result.getResponse().getContentAsString(), AlgoRelationTypeDto.class);
		assertEquals(algoRelationTypeDto.getName(), type.getName());
	}

}
