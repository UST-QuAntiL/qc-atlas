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

package org.planqk.atlas.web.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmListDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationListDto;
import org.planqk.atlas.web.utils.DtoEntityConverter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AlgorithmControllerTest {

    @Mock
    private AlgorithmService algorithmService;
    @Mock
    private DtoEntityConverter modelConverter;

    @InjectMocks
    private AlgorithmController algorithmController;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private int page = 0;
    private int size = 2;
    private Pageable pageable = PageRequest.of(page, size);
    
    private Algorithm algorithm1;
    private Algorithm algorithm2;
    private AlgorithmRelation algorithmRelation1;
    private AlgorithmRelationDto algorithmRelation1Dto;
    private AlgorithmDto algorithm1Dto;
    private AlgorithmDto algorithm2Dto;


    @Before
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(algorithmController).build();
        mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        
        algorithm1 = new Algorithm();
        algorithm1.setId(UUID.randomUUID());
        algorithm1.setName("alg1");
        algorithm1.setComputationModel(ComputationModel.CLASSIC);

        algorithm2 = new Algorithm();
        algorithm2.setId(UUID.randomUUID());
        algorithm2.setName("alg2");
        algorithm2.setComputationModel(ComputationModel.CLASSIC);
        algorithm2.setAlgorithmRelations(new HashSet<>());

        algorithmRelation1 = new AlgorithmRelation();
        algorithmRelation1.setId(UUID.randomUUID());
        algorithmRelation1.setSourceAlgorithm(algorithm1);
        algorithmRelation1.setTargetAlgorithm(algorithm2);
        algorithmRelation1.setAlgoRelationType(new AlgoRelationType());
        AlgorithmRelation algorithmRelation2 = new AlgorithmRelation();
        algorithmRelation2.setId(UUID.randomUUID());
        algorithmRelation2.setSourceAlgorithm(algorithm1);
        algorithmRelation2.setTargetAlgorithm(algorithm2);
        algorithmRelation2.setAlgoRelationType(new AlgoRelationType());
        Set<AlgorithmRelation> relations = new HashSet<>();
        relations.add(algorithmRelation1);
        relations.add(algorithmRelation2);
        algorithm1.setAlgorithmRelations(relations);

        algorithm1Dto = AlgorithmDto.Converter.convert(algorithm1);
        algorithm1Dto.setId(UUID.randomUUID());
        algorithm1Dto.setComputationModel(ComputationModel.CLASSIC);
        
        algorithm2Dto = AlgorithmDto.Converter.convert(algorithm2);
        algorithm2Dto.setId(UUID.randomUUID());
        algorithm2Dto.setComputationModel(ComputationModel.CLASSIC);
        
        algorithmRelation1Dto = AlgorithmRelationDto.Converter.convert(algorithmRelation1);

        when(modelConverter.convert(any(Algorithm.class))).thenReturn(algorithm1Dto);
        when(modelConverter.convert(any(AlgorithmDto.class))).thenReturn(algorithm1);
        when(modelConverter.convert(any(AlgorithmRelation.class))).thenReturn(algorithmRelation1Dto);
        when(modelConverter.convert(any(AlgorithmRelationDto.class))).thenReturn(algorithmRelation1);
        when(modelConverter.convert(algorithm1Dto)).thenReturn(algorithm1);
        when(modelConverter.convert(algorithm2Dto)).thenReturn(algorithm2);
        when(modelConverter.convert(algorithm1)).thenReturn(algorithm1Dto);
        when(modelConverter.convert(algorithm2)).thenReturn(algorithm2Dto);

    	when(algorithmService.findById(any(UUID.class))).thenReturn(Optional.empty());
    	when(algorithmService.findById(algorithm1.getId())).thenReturn(Optional.of(algorithm1));
    	when(algorithmService.findById(algorithm2.getId())).thenReturn(Optional.of(algorithm2));
    	
    }

    @Test
    public void setupTest() {
        assertNotNull(mockMvc);
        assertNotNull(algorithmController);
    }

    @Test
    public void getAlgorithms_withoutPagination() throws Exception {
        when(algorithmService.findAll(Pageable.unpaged())).thenReturn(Page.empty());
        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void getAlgorithms_withEmptyAlgorithmList() throws Exception {
        when(algorithmService.findAll(pageable)).thenReturn(Page.empty());
        when(modelConverter.convert(Page.empty())).thenReturn(new AlgorithmListDto());
        MvcResult result = mockMvc.perform(get("/" + Constants.ALGORITHMS + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        AlgorithmListDto algorithmListDto = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmListDto.class);
        assertEquals(algorithmListDto.getAlgorithmDtos().size(), 0);
    }

    @Test
    public void getAlgorithms_withTwoAlgorithmList() throws Exception {
        List<Algorithm> algorithmList = new ArrayList<>();

        AlgorithmListDto resultList = new AlgorithmListDto();
        resultList.add(this.algorithm1Dto);
        resultList.add(this.algorithm2Dto);

        when(algorithmService.findAll(pageable)).thenReturn(new PageImpl<>(algorithmList));
        when(modelConverter.convert(new PageImpl<>(algorithmList))).thenReturn(resultList);

        MvcResult result = mockMvc.perform(get("/" + Constants.ALGORITHMS + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        AlgorithmListDto algorithmListDto = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmListDto.class);
        assertEquals(algorithmListDto.getAlgorithmDtos().size(), 2);
    }

    @Test
    public void getAlgorithm_returnNotFound() throws Exception {
    	
        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/" + UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void getAlgorithm_returnAlgorithm() throws Exception {

        MvcResult result = mockMvc.perform(get("/" + Constants.ALGORITHMS + "/" + this.algorithm1.getId())
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        AlgorithmDto response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), AlgorithmDto.class);
        assertEquals(response.getId(), this.algorithm1Dto.getId());
    }

    @Test
    public void createAlgorithm_returnBadRequest() throws Exception {
    	AlgorithmDto algoDto = new AlgorithmDto();
    	algoDto.setId(UUID.randomUUID());
        mockMvc.perform(post("/" + Constants.ALGORITHMS + "/")
                .content(mapper.writeValueAsString(algoDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void createAlgorithm_returnAlgorithm() throws Exception {

    	when(algorithmService.save(algorithm1)).thenReturn(algorithm1);

        MvcResult result = mockMvc.perform(post("/" + Constants.ALGORITHMS + "/")
                .content(mapper.writeValueAsString(this.algorithm1Dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        AlgorithmDto response = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmDto.class);
        assertEquals(response.getName(), this.algorithm1Dto.getName());
    }
    
    @Test
    public void updateAlgorithm_returnBadRequest() throws Exception {
    	
    	AlgorithmDto algoDto = new AlgorithmDto();
    	algoDto.setId(UUID.randomUUID());
    	mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{id}", algoDto.getId())
    			.content(mapper.writeValueAsString(algoDto))
    			.contentType(MediaType.APPLICATION_JSON)
    			.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
    
    @Test
    public void updateAlgorithm_returnAlgorithm() throws Exception {

    	when(algorithmService.update(algorithm1.getId(), algorithm1)).thenReturn(algorithm1);

        MvcResult result = mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{id}", this.algorithm1.getId())
                .content(mapper.writeValueAsString(this.algorithm1Dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        AlgorithmDto response = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmDto.class);
        assertEquals(response.getName(), this.algorithm1Dto.getName());
    }
    
    @Test
    public void deleteAlgorithm_notFound() throws Exception {
    	
    	mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{id}", UUID.randomUUID()))
    			.andExpect(status().isNotFound());
    }
    
    @Test
    public void deleteAlgorithm_returnOk() throws Exception {	
    	
    	mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{id}", this.algorithm1.getId()))
    			.andExpect(status().isOk()).andReturn();
    }

    @Test
    public void getAlgorithmRelations_returnNotFound() throws Exception {
    	
    	mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{id}", UUID.randomUUID()))
    			.andExpect(status().isNotFound());
    }

    @Test
    public void getAlgorithmRelations_withEmptyAlgorithmRelationList() throws Exception {
    	
        MvcResult result = mockMvc.perform(get("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS, this.algorithm2.getId())
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        AlgorithmRelationListDto algorithmRelationListDto = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmRelationListDto.class);
        assertEquals(algorithmRelationListDto.getAlgorithmRelationDtos().size(), 0);
    }

    @Test
    public void getAlgorithmRelations_withTwoAlgorithmRelationList() throws Exception {

        MvcResult result = mockMvc.perform(get("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS, this.algorithm1.getId())
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        AlgorithmRelationListDto algorithmRelationListDto = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmRelationListDto.class);
        assertEquals(algorithmRelationListDto.getAlgorithmRelationDtos().size(), 2);
    }
    
    @Test
    public void updateAlgorithmRelation_returnBadRequest() throws Exception {
    	
    	Algorithm algo = new Algorithm();
    	algo.setId(UUID.randomUUID());
    	mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS, algo.getId())
    			.content(mapper.writeValueAsString(this.algorithmRelation1Dto))
    			.contentType(MediaType.APPLICATION_JSON)
    			.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
    
    @Test
    public void updateAlgorithmRelation_returnNotFound() throws Exception {
    	
    	mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS, UUID.randomUUID())
    			.content(mapper.writeValueAsString(this.algorithmRelation1Dto))
    			.contentType(MediaType.APPLICATION_JSON)
    			.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
    
    @Test
    public void updateAlgorithmRelation_returnAlgorithmRelation() throws Exception {

    	when(algorithmService.addUpdateAlgorithmRelation(any(UUID.class),any(AlgorithmRelation.class))).thenReturn(algorithmRelation1);

        MvcResult result = mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS, this.algorithm1.getId())
                .content(mapper.writeValueAsString(this.algorithmRelation1Dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        AlgorithmRelationDto response = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmRelationDto.class);
        assertEquals(response.getSourceAlgorithm().getName(), this.algorithm1Dto.getName());
    }
    
    @Test
    public void deleteAlgorithmRelation_notModified() throws Exception {

    	when(algorithmService.deleteAlgorithmRelation(any(UUID.class),any(UUID.class))).thenReturn(false);
    	
    	mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS +
    			"/{algorithmRelation_id}", UUID.randomUUID(), this.algorithmRelation1.getId()))
    			.andExpect(status().isNotModified());
    }
    
    @Test
    public void deleteAlgorithmRelation_returnOk() throws Exception {

    	when(algorithmService.deleteAlgorithmRelation(algorithm1.getId(),algorithmRelation1.getId())).thenReturn(true);
    	
    	mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS +
    			"/{relation_id}", this.algorithm1.getId(), this.algorithmRelation1.getId()))
    			.andExpect(status().isOk()).andReturn();
    }
}
