/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.PatternRelationTypeService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.PatternRelationTypeDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatternRelationController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class PatternRelationControllerTest {

    @MockBean
    private PatternRelationService patternRelationService;
    @MockBean
    private AlgorithmService algorithmService;
    @MockBean
    private PatternRelationTypeService patternRelationTypeService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);

    private List<PatternRelation> relationList;
    private Page<PatternRelation> relationPage;
    private Page<PatternRelationDto> relationPageDto;

    private PatternRelation relation1;
    private PatternRelation relation2;
    private PatternRelation missingReqParamRelation;
    private PatternRelation relationUpdated;
    private PatternRelationDto relation1Dto;
    private PatternRelationDto relation2Dto;
    private PatternRelationDto missingReqParamRelationDto;
    private PatternRelationDto relationUpdatedDto;
    private PatternRelationType type1;
    private PatternRelationType type2;
    private PatternRelationTypeDto type1Dto;
    private PatternRelationTypeDto type2Dto;
    private Algorithm algorithm1;
    private Algorithm algorithm2;
    private AlgorithmDto algorithm1Dto;
    private AlgorithmDto algorithm2Dto;

    @BeforeEach
    public void initialize() {
        // Generate UUIDs
        UUID relationId1 = UUID.randomUUID();
        UUID relationId2 = UUID.randomUUID();
        UUID algoId1 = UUID.randomUUID();
        UUID algoId2 = UUID.randomUUID();
        UUID typeId1 = UUID.randomUUID();
        UUID typeId2 = UUID.randomUUID();

        // Init Algorithms and DTOs
        algorithm1 = new Algorithm();
        algorithm1.setId(algoId1);
        algorithm1.setName("Algorithm1");
        algorithm1.setComputationModel(ComputationModel.CLASSIC);
        algorithm2 = new Algorithm();
        algorithm2.setId(algoId2);
        algorithm2.setName("Algorithm2");
        algorithm2.setComputationModel(ComputationModel.CLASSIC);

        algorithm1Dto = ModelMapperUtils.convert(algorithm1, AlgorithmDto.class);
        algorithm2Dto = ModelMapperUtils.convert(algorithm2, AlgorithmDto.class);

        // Init Types and DTOs
        type1 = new PatternRelationType();
        type1.setId(typeId1);
        type1.setName("PatternType1");
        type2 = new PatternRelationType();
        type2.setId(typeId2);
        type2.setName("PatternType2");

        type1Dto = ModelMapperUtils.convert(type1, PatternRelationTypeDto.class);
        type2Dto = ModelMapperUtils.convert(type2, PatternRelationTypeDto.class);

        // Init Relations and DTOs and Pages
        relation1 = new PatternRelation();
        relation1.setId(relationId1);
        relation1.setAlgorithm(algorithm1);
        relation1.setPattern(URI.create("http://www.pattern1.de"));
        relation1.setDescription("Description1");
        relation1.setPatternRelationType(type1);
        relation2 = new PatternRelation();
        relation2.setId(relationId2);
        relation2.setAlgorithm(algorithm2);
        relation2.setPattern(URI.create("http://www.pattern2.de"));
        relation2.setDescription("Description2");
        relation2.setPatternRelationType(type2);
        relationUpdated = new PatternRelation();
        relationUpdated.setId(relationId1);
        relationUpdated.setAlgorithm(algorithm1);
        relationUpdated.setPattern(URI.create("http://www.pattern1Updated.de"));
        relationUpdated.setDescription("Description1Updated");
        relationUpdated.setPatternRelationType(type2);
        missingReqParamRelation = new PatternRelation();

        relation1Dto = ModelMapperUtils.convert(relation1, PatternRelationDto.class);
        relation2Dto = ModelMapperUtils.convert(relation2, PatternRelationDto.class);
        missingReqParamRelationDto = ModelMapperUtils.convert(missingReqParamRelation, PatternRelationDto.class);
        relationUpdatedDto = ModelMapperUtils.convert(relationUpdated, PatternRelationDto.class);

        relationList = new ArrayList<>();
        relationList.add(relation1);
        relationList.add(relation2);

        relationPage = new PageImpl<>(relationList);
        relationPageDto = ModelMapperUtils.convertPage(relationPage, PatternRelationDto.class);
    }

    @Test
    public void createRelation_returnRelation() throws Exception {
        // Ignore annontations when writing Java objects to Json to enable writing WRITE_ONLY field which are required as input
        mapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        var id = relation1Dto.getId();
        relation1Dto.setId(null);

        when(patternRelationService.create(any())).thenReturn(relation1);

        var url = linkBuilderService.urlStringTo(methodOn(PatternRelationController.class)
                .createPatternRelation(relation1Dto));
        MvcResult result = mockMvc
                .perform(post(url).content(mapper.writeValueAsString(relation1Dto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
        EntityModel<PatternRelationDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<PatternRelationDto>>() {
                });

        assertEquals(response.getContent().getId(), id);
    }

    @Test
    public void createRelation_returnBadRequest() throws Exception {
        var url = linkBuilderService.urlStringTo(methodOn(PatternRelationController.class)
                .createPatternRelation(relation1Dto));
        mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(missingReqParamRelationDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void createRelation_returnAlgorithmNotFound() throws Exception {
        // Ignore annontations when writing Java objects to Json to enable writing WRITE_ONLY field which are required as input
        mapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        relation1Dto.setId(null);

        when(patternRelationService.create(any())).thenThrow(NoSuchElementException.class);

        var url = linkBuilderService.urlStringTo(methodOn(PatternRelationController.class)
                .createPatternRelation(relation1Dto));
        mockMvc.perform(post(url).content(mapper.writeValueAsString(relation1Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRelationsPaged_returnRelationsPaged() throws Exception {
        when(patternRelationService.findAll(pageable)).thenReturn(relationPage);

        var url = linkBuilderService.urlStringTo(methodOn(PatternRelationController.class)
                .getPatternRelations(new ListParameters(pageable, null)));
        MvcResult result = mockMvc
                .perform(get(url).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "patternRelations", PatternRelationDto.class);

        assertEquals(resultList.size(), 2);
        assertEquals(resultList.get(0).getDescription(), relation1Dto.getDescription());
        assertEquals(resultList.get(1).getDescription(), relation2Dto.getDescription());
        assertEquals(resultList.get(0).getPatternRelationType().getName(), relation1Dto.getPatternRelationType().getName());
        assertEquals(resultList.get(1).getPatternRelationType().getName(), relation2Dto.getPatternRelationType().getName());
        assertEquals(resultList.get(0).getPattern(), relation1Dto.getPattern());
        assertEquals(resultList.get(1).getPattern(), relation2Dto.getPattern());
    }

    @Test
    public void getRelation_returnRelation() throws Exception {
        when(patternRelationService.findById(relation1.getId())).thenReturn(relation1);

        var url = linkBuilderService.urlStringTo(methodOn(PatternRelationController.class)
                .getPatternRelation(relation1.getId()));
        MvcResult result = mockMvc.perform(
                get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<PatternRelationDto> response = new ObjectMapper().readValue(
                result.getResponse().getContentAsString(), new TypeReference<EntityModel<PatternRelationDto>>() {
                });

        assertEquals(response.getContent().getPatternRelationType(), type1Dto);
        assertEquals(response.getContent().getId(), relation1Dto.getId());
        assertEquals(response.getContent().getDescription(), relation1Dto.getDescription());
        assertEquals(response.getContent().getPattern(), relation1Dto.getPattern());
    }

    @Test
    public void getRelation_returnNotFound() throws Exception {
        when(patternRelationService.findById(any())).thenThrow(NoSuchElementException.class);

        var url = linkBuilderService.urlStringTo(methodOn(PatternRelationController.class)
                .getPatternRelation(relation1.getId()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateRelation_returnRelation() throws Exception {
        // Ignore annontations when writing Java objects to Json to enable writing WRITE_ONLY field which are required as input
        mapper.configure(MapperFeature.USE_ANNOTATIONS, false);

        when(patternRelationService.update(any())).thenReturn(relationUpdated);

        var url = linkBuilderService.urlStringTo(methodOn(PatternRelationController.class)
                .updatePatternRelation(relation1.getId(), relation1Dto));
        MvcResult result = mockMvc.perform(put(url)
                .content(mapper.writeValueAsString(relation1Dto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
        EntityModel<PatternRelationDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<PatternRelationDto>>() {
                });

        assertEquals(response.getContent().getPatternRelationType(), type2Dto);
        assertEquals(response.getContent().getDescription(), relationUpdatedDto.getDescription());
        assertEquals(response.getContent().getId(), relationUpdatedDto.getId());
        assertEquals(response.getContent().getPattern(), relationUpdatedDto.getPattern());
    }

    @Test
    public void updateRelation_returnBadRequest() throws Exception {
        when(patternRelationService.update(relation1)).thenReturn(relationUpdated);

        var url = linkBuilderService.urlStringTo(methodOn(PatternRelationController.class)
                .updatePatternRelation(relation1.getId(), relation1Dto));
        mockMvc.perform(put(url)
                .content(mapper.writeValueAsString(missingReqParamRelation)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void updateRelation_returnNotFound() throws Exception {
        // Ignore annontations when writing Java objects to Json to enable writing WRITE_ONLY field which are required as input
        mapper.configure(MapperFeature.USE_ANNOTATIONS, false);

        when(patternRelationService.update(any())).thenThrow(NoSuchElementException.class);

        var url = linkBuilderService.urlStringTo(methodOn(PatternRelationController.class)
                .updatePatternRelation(relation1.getId(), relation1Dto));
        mockMvc.perform(put(url)
                .content(mapper.writeValueAsString(relation1Dto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void deleteRelation_returnOk() throws Exception {
        doNothing().when(patternRelationService).delete(relation1.getId());

        var url = linkBuilderService.urlStringTo(methodOn(PatternRelationController.class)
                .deletePatternRelation(relation1.getId()));
        mockMvc.perform(delete(url)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
    }

    @Test
    public void deleteRelation_returnNotFound() throws Exception {
        doThrow(NoSuchElementException.class).when(patternRelationService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(PatternRelationController.class)
                .deletePatternRelation(relation1.getId()));
        mockMvc.perform(delete(url)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }
}
