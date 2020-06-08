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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationAssembler;
import org.planqk.atlas.web.linkassembler.ProblemTypeAssembler;
import org.planqk.atlas.web.linkassembler.TagAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlgorithmController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class AlgorithmControllerTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ProblemTypeAssembler problemTypeAssembler() {
            return new ProblemTypeAssembler();
        }

        @Bean
        public TagAssembler tagAssembler() {
            return new TagAssembler();
        }
    }

    @MockBean
    private AlgorithmService algorithmService;
    @MockBean
    private PagedResourcesAssembler<AlgorithmDto> paginationAssembler;
    @MockBean
    private AlgorithmAssembler algorithmAssembler;
    @MockBean
    private AlgorithmRelationAssembler algorithmRelationAssembler;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);

    private Algorithm algorithm1;
    private Algorithm algorithm2;
    private AlgorithmRelation algorithmRelation1;
    private AlgorithmRelationDto algorithmRelation1Dto;
    private AlgorithmDto algorithm1Dto;
    private AlgorithmDto algorithm2Dto;
    private Set<AlgorithmRelation> algorithmRelations;

    @BeforeEach
    public void initialize() {
        mapper = ObjectMapperUtils.newTestMapper();

        Set<ProblemType> problemTypes = new HashSet<>();

        ProblemType type1 = new ProblemType();
        type1.setId(UUID.randomUUID());
        type1.setName("ProblemType1");
        problemTypes.add(type1);

        AlgoRelationType relType1 = new AlgoRelationType();
        relType1.setName("RelationType1");

        algorithm1 = new ClassicAlgorithm();
        algorithm1.setId(UUID.randomUUID());
        algorithm1.setName("alg1");
        algorithm1.setComputationModel(ComputationModel.CLASSIC);

        algorithm2 = new ClassicAlgorithm();
        algorithm2.setId(UUID.randomUUID());
        algorithm2.setName("alg2");
        algorithm2.setComputationModel(ComputationModel.CLASSIC);
        algorithm2.setAlgorithmRelations(new HashSet<>());

        algorithmRelation1 = new AlgorithmRelation();
        algorithmRelation1.setId(UUID.randomUUID());
        algorithmRelation1.setSourceAlgorithm(algorithm1);
        algorithmRelation1.setTargetAlgorithm(algorithm2);
        algorithmRelation1.setAlgoRelationType(relType1);
        AlgorithmRelation algorithmRelation2 = new AlgorithmRelation();
        algorithmRelation2.setId(UUID.randomUUID());
        algorithmRelation2.setSourceAlgorithm(algorithm1);
        algorithmRelation2.setTargetAlgorithm(algorithm2);
        algorithmRelation2.setAlgoRelationType(relType1);
        algorithmRelations = new HashSet<>();
        algorithmRelations.add(algorithmRelation1);
        algorithmRelations.add(algorithmRelation2);

        algorithm1.setAlgorithmRelations(algorithmRelations);
        algorithm1.setProblemTypes(problemTypes);

        algorithm2.setProblemTypes(problemTypes);

        // Generate DTOs from above Entities
        algorithm1Dto = ModelMapperUtils.convert(algorithm1, AlgorithmDto.class);
        algorithm2Dto = ModelMapperUtils.convert(algorithm2, AlgorithmDto.class);

        algorithmRelation1Dto = ModelMapperUtils.convert(algorithmRelation1, AlgorithmRelationDto.class);

//    	when(algorithmService.findById(any(UUID.class))).thenReturn(Optional.empty());
        when(algorithmService.findById(algorithm1.getId())).thenReturn(algorithm1);
        when(algorithmService.findById(algorithm2.getId())).thenReturn(algorithm2);
    }

    @Test
    public void getAlgorithms_withoutPagination() throws Exception {
        when(algorithmService.findAll(Pageable.unpaged())).thenReturn(Page.empty());
        when(paginationAssembler.toModel(ArgumentMatchers.any()))
                .thenReturn(HateoasUtils.generatePagedModel(Page.empty()));
        doNothing().when(algorithmAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<AlgorithmDto>>>any());

        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAlgorithms_withEmptyAlgorithmList() throws Exception {
        when(algorithmService.findAll(pageable)).thenReturn(Page.empty());
        when(paginationAssembler.toModel(ArgumentMatchers.any()))
                .thenReturn(HateoasUtils.generatePagedModel(Page.empty()));
        doNothing().when(algorithmAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<AlgorithmDto>>>any());

        MvcResult result = mockMvc
                .perform(get("/" + Constants.ALGORITHMS + "/").queryParam(Constants.PAGE, Integer.toString(page))
                        .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algorithmDtoes", AlgorithmDto.class);
        assertEquals(0, resultList.size());
    }

    @Test
    public void getAlgorithms_withTwoAlgorithmList() throws Exception {
        List<Algorithm> algorithmList = new ArrayList<>();
        algorithmList.add(algorithm1);
        algorithmList.add(algorithm2);

        Page<Algorithm> pageAlg = new PageImpl<>(algorithmList);
        Page<AlgorithmDto> pageAlgDto = ModelMapperUtils.convertPage(pageAlg, AlgorithmDto.class);

        when(algorithmService.findAll(pageable)).thenReturn(pageAlg);
        when(paginationAssembler.toModel(ArgumentMatchers.any()))
                .thenReturn(HateoasUtils.generatePagedModel(pageAlgDto));
        doNothing().when(algorithmAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<AlgorithmDto>>>any());

        MvcResult result = mockMvc
                .perform(get("/" + Constants.ALGORITHMS + "/").queryParam(Constants.PAGE, Integer.toString(page))
                        .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "classicAlgorithmDtoes", AlgorithmDto.class);
        assertEquals(2, resultList.size());
    }

    @Test
    public void getAlgorithm_returnNotFound() throws Exception {
        when(algorithmService.findById(any(UUID.class))).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/" + UUID.randomUUID()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAlgorithm_returnAlgorithm() throws Exception {
        when(algorithmService.findById(any(UUID.class))).thenReturn(algorithm1);
        doNothing().when(algorithmAssembler).addLinks(ArgumentMatchers.<EntityModel<AlgorithmDto>>any());

        MvcResult result = mockMvc
                .perform(get("/" + Constants.ALGORITHMS + "/" + algorithm1.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmDto> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<AlgorithmDto>>() {
                });
        assertEquals(response.getContent().getId(), algorithm1Dto.getId());
    }

    @Test
    public void createAlgorithm_returnBadRequest() throws Exception {
        AlgorithmDto algoDto = new AlgorithmDto();
        algoDto.setId(UUID.randomUUID());
        mockMvc.perform(post("/" + Constants.ALGORITHMS + "/").content(mapper.writeValueAsString(algoDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        algoDto.setName("algoDto");
        mockMvc.perform(post("/" + Constants.ALGORITHMS + "/").content(mapper.writeValueAsString(algoDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAlgorithm_returnAlgorithm() throws Exception {
        when(algorithmService.save(algorithm1)).thenReturn(algorithm1);
        doNothing().when(algorithmAssembler).addLinks(ArgumentMatchers.<EntityModel<AlgorithmDto>>any());

        MvcResult result = mockMvc
                .perform(post("/" + Constants.ALGORITHMS + "/").content(mapper.writeValueAsString(algorithm1Dto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<AlgorithmDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<AlgorithmDto>>() {
                });
        assertEquals(response.getContent().getName(), this.algorithm1Dto.getName());
    }

    @Test
    public void updateAlgorithm_returnBadRequest() throws Exception {
        AlgorithmDto algoDto = new AlgorithmDto();
        algoDto.setId(UUID.randomUUID());

        mockMvc.perform(
                put("/" + Constants.ALGORITHMS + "/{id}", algoDto.getId()).content(mapper.writeValueAsString(algoDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        algoDto.setName("algoDto");

        mockMvc.perform(post("/" + Constants.ALGORITHMS + "/").content(mapper.writeValueAsString(algoDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateAlgorithm_returnAlgorithm() throws Exception {
        when(algorithmService.update(algorithm1.getId(), algorithm1)).thenReturn(algorithm1);
        doNothing().when(algorithmAssembler).addLinks(ArgumentMatchers.<EntityModel<AlgorithmDto>>any());

        MvcResult result = mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{id}", algorithm1.getId())
                .content(mapper.writeValueAsString(algorithm1Dto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<AlgorithmDto>>() {
                });
        assertEquals(response.getContent().getName(), algorithm1Dto.getName());
    }

    @Test
    public void deleteAlgorithm_notFound() throws Exception {
        doThrow(new NoSuchElementException()).when(algorithmService).delete(any(UUID.class));

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
        when(algorithmService.getAlgorithmRelations(any(UUID.class))).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/{id}/" + Constants.ALGORITHM_RELATIONS, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAlgorithmRelations_withEmptyAlgorithmRelationList() throws Exception {
        when(algorithmService.getAlgorithmRelations(any(UUID.class))).thenReturn(new HashSet<>());
        doNothing().when(algorithmRelationAssembler)
                .addLinks(ArgumentMatchers.<CollectionModel<EntityModel<AlgorithmRelationDto>>>any());
        doNothing().when(algorithmAssembler).addAlgorithmRelationLink(ArgumentMatchers.any(), any(UUID.class));

        MvcResult result = mockMvc
                .perform(get("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                        algorithm2.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algorithmRelationDtoes", AlgorithmRelationDto.class);
        assertEquals(0, resultList.size());
    }

    @Test
    public void getAlgorithmRelations_withTwoAlgorithmRelationList() throws Exception {
        when(algorithmService.getAlgorithmRelations(any(UUID.class))).thenReturn(algorithm1.getAlgorithmRelations());
        doNothing().when(algorithmRelationAssembler)
                .addLinks(ArgumentMatchers.<CollectionModel<EntityModel<AlgorithmRelationDto>>>any());
        doNothing().when(algorithmAssembler).addAlgorithmRelationLink(ArgumentMatchers.any(), any(UUID.class));

        MvcResult result = mockMvc
                .perform(get("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                        algorithm1.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algorithmRelationDtoes", AlgorithmRelationDto.class);
        assertEquals(2, resultList.size());
    }

    @Test
    public void updateAlgorithmRelation_returnBadRequest() throws Exception {
        Algorithm algo = new Algorithm();
        mockMvc.perform(
                put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS, algo.getId())
                        .content(mapper.writeValueAsString(this.algorithmRelation1Dto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        AlgorithmRelationDto algoRelationDto = new AlgorithmRelationDto();
        mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                algorithm1.getId()).content(mapper.writeValueAsString(algoRelationDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        algoRelationDto.setSourceAlgorithm(algorithm1Dto);
        mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                algorithm1.getId()).content(mapper.writeValueAsString(algoRelationDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        algoRelationDto.setTargetAlgorithm(algorithm2Dto);
        mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                algorithm1.getId()).content(mapper.writeValueAsString(algoRelationDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateAlgorithmRelation_returnNotFound() throws Exception {
        when(algorithmService.addOrUpdateAlgorithmRelation(any(UUID.class), any(AlgorithmRelation.class)))
                .thenThrow(new NoSuchElementException());

        mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                UUID.randomUUID()).content(mapper.writeValueAsString(algorithmRelation1Dto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateAlgorithmRelation_returnAlgorithmRelation() throws Exception {
        when(algorithmService.addOrUpdateAlgorithmRelation(any(UUID.class), any(AlgorithmRelation.class)))
                .thenReturn(algorithmRelation1);
        doNothing().when(algorithmRelationAssembler)
                .addLinks(ArgumentMatchers.<EntityModel<AlgorithmRelationDto>>any());

        MvcResult result = mockMvc
                .perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                        algorithm1.getId()).content(mapper.writeValueAsString(algorithmRelation1Dto))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmRelationDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<AlgorithmRelationDto>>() {
                });
        assertEquals(response.getContent().getSourceAlgorithm().getName(), algorithm1Dto.getName());
    }

//    @Test
//    public void deleteAlgorithmRelation_notModified() throws Exception {
//
//    	when(algorithmService.deleteAlgorithmRelation(any(UUID.class),any(UUID.class))).thenReturn(false);
//
//    	mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS +
//    			"/{algorithmRelation_id}", UUID.randomUUID(), this.algorithmRelation1.getId()))
//    			.andExpect(status().isNotModified());
//    }

    @Test
    public void deleteAlgorithmRelation_returnOk() throws Exception {

        doNothing().when(algorithmService).deleteAlgorithmRelation(algorithm1.getId(), algorithmRelation1.getId());

        mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS
                + "/{relation_id}", algorithm1.getId(), algorithmRelation1.getId())).andExpect(status().isOk())
                .andReturn();
    }
}
