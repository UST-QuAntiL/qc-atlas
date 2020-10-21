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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.services.AlgorithmRelationService;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationTypeDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlgorithmRelationController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
@Slf4j
public class AlgorithmRelationControllerTest {

    @MockBean
    private AlgorithmRelationService algorithmRelationService;

    @MockBean
    private AlgorithmService algorithmService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();


    @Test
    @SneakyThrows
    void getAlgorithmRelationsOfAlgorithm_EmptyList_returnOk() {
        doReturn(new PageImpl<AlgorithmRelation>(List.of())).when(algorithmService).findLinkedAlgorithmRelations(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .getAlgorithmRelationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.algorithmRelations").doesNotExist());
    }

    @Test
    @SneakyThrows
    void getAlgorithmRelationsOfAlgorithm_SingleElement_returnOk() {
        Algorithm sourceAlgorithm = new Algorithm();
        sourceAlgorithm.setId(UUID.randomUUID());
        sourceAlgorithm.setName("sourceAlgorithmName");
        sourceAlgorithm.setComputationModel(ComputationModel.CLASSIC);

        Algorithm targetAlgorithm = new Algorithm();
        targetAlgorithm.setId(UUID.randomUUID());
        targetAlgorithm.setName("targetAlgorithmName");
        targetAlgorithm.setComputationModel(ComputationModel.CLASSIC);

        AlgorithmRelationType type = new AlgorithmRelationType();
        type.setId(UUID.randomUUID());
        type.setName("typeName");

        AlgorithmRelation algorithmRelation = new AlgorithmRelation();
        algorithmRelation.setId(UUID.randomUUID());
        algorithmRelation.setSourceAlgorithm(sourceAlgorithm);
        algorithmRelation.setTargetAlgorithm(targetAlgorithm);
        algorithmRelation.setAlgorithmRelationType(type);

        doReturn(new PageImpl<>(List.of(algorithmRelation))).when(algorithmService).findLinkedAlgorithmRelations(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .getAlgorithmRelationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.algorithmRelations[0].id").value(algorithmRelation.getId().toString()))
                .andExpect(jsonPath("$._embedded.algorithmRelations[0].algoRelationType.id")
                        .value(type.getId().toString()))
                .andExpect(jsonPath("$._embedded.algorithmRelations[0].sourceAlgorithmId")
                        .value(sourceAlgorithm.getId().toString()))
                .andExpect(jsonPath("$._embedded.algorithmRelations[0].targetAlgorithmId")
                        .value(targetAlgorithm.getId().toString()));
    }

    @Test
    @SneakyThrows
    void getAlgorithmRelationsOfAlgorithm_returnNotFound() {
        doThrow(NoSuchElementException.class).when(algorithmService).findLinkedAlgorithmRelations(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .getAlgorithmRelationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createAlgorithmRelation_returnCreated() {
        Algorithm sourceAlgorithm = new Algorithm();
        sourceAlgorithm.setId(UUID.randomUUID());
        sourceAlgorithm.setName("sourceAlgorithmName");
        sourceAlgorithm.setComputationModel(ComputationModel.CLASSIC);

        Algorithm targetAlgorithm = new Algorithm();
        targetAlgorithm.setId(UUID.randomUUID());
        targetAlgorithm.setName("targetAlgorithmName");
        targetAlgorithm.setComputationModel(ComputationModel.CLASSIC);

        AlgorithmRelationType type = new AlgorithmRelationType();
        type.setId(UUID.randomUUID());
        type.setName("typeName");

        AlgorithmRelation algorithmRelation = new AlgorithmRelation();
        algorithmRelation.setSourceAlgorithm(sourceAlgorithm);
        algorithmRelation.setTargetAlgorithm(targetAlgorithm);
        algorithmRelation.setAlgorithmRelationType(type);
        AlgorithmRelationDto relationDto = ModelMapperUtils.convert(algorithmRelation, AlgorithmRelationDto.class);

        doReturn(algorithmRelation).when(algorithmRelationService).create(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .createAlgorithmRelation(sourceAlgorithm.getId(), null));

        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(relationDto))
        ).andExpect(jsonPath("$.id").isEmpty())
                .andExpect(jsonPath("$.sourceAlgorithmId").value(sourceAlgorithm.getId().toString()))
                .andExpect(jsonPath("$.targetAlgorithmId").value(targetAlgorithm.getId().toString()))
                .andExpect(jsonPath("$.algoRelationType.id").value(type.getId().toString()))
                .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    void createAlgorithmRelation_returnBadRequest() {
        AlgorithmRelationDto relationDto = new AlgorithmRelationDto();
        relationDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .createAlgorithmRelation(UUID.randomUUID(), null));

        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(relationDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createAlgorithmRelation_returnNotFound() {
        AlgorithmRelationTypeDto typeDto = new AlgorithmRelationTypeDto();
        typeDto.setId(UUID.randomUUID());
        typeDto.setName("typeName");

        AlgorithmRelationDto relationDto = new AlgorithmRelationDto();
        relationDto.setAlgorithmRelationType(typeDto);
        UUID sourceId = UUID.randomUUID();
        relationDto.setSourceAlgorithmId(sourceId);
        relationDto.setTargetAlgorithmId(UUID.randomUUID());

        doThrow(new NoSuchElementException()).when(algorithmRelationService).create(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .createAlgorithmRelation(sourceId, null));

        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(relationDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void updateAlgorithmRelation_returnOk() {
        Algorithm sourceAlgorithm = new Algorithm();
        sourceAlgorithm.setId(UUID.randomUUID());
        sourceAlgorithm.setName("sourceAlgorithmName");
        sourceAlgorithm.setComputationModel(ComputationModel.CLASSIC);

        Algorithm targetAlgorithm = new Algorithm();
        targetAlgorithm.setId(UUID.randomUUID());
        targetAlgorithm.setName("targetAlgorithmName");
        targetAlgorithm.setComputationModel(ComputationModel.CLASSIC);

        AlgorithmRelationType type = new AlgorithmRelationType();
        type.setId(UUID.randomUUID());
        type.setName("typeName");

        AlgorithmRelation algorithmRelation = new AlgorithmRelation();
        algorithmRelation.setId(UUID.randomUUID());
        algorithmRelation.setSourceAlgorithm(sourceAlgorithm);
        algorithmRelation.setTargetAlgorithm(targetAlgorithm);
        algorithmRelation.setAlgorithmRelationType(type);
        AlgorithmRelationDto relationDto = ModelMapperUtils.convert(algorithmRelation, AlgorithmRelationDto.class);

        doReturn(algorithmRelation).when(algorithmRelationService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .updateAlgorithmRelation(sourceAlgorithm.getId(), algorithmRelation.getId(), null));

        mockMvc.perform(put(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(relationDto))
        ).andExpect(jsonPath("$.id").value(algorithmRelation.getId().toString()))
                .andExpect(jsonPath("$.sourceAlgorithmId").value(sourceAlgorithm.getId().toString()))
                .andExpect(jsonPath("$.targetAlgorithmId").value(targetAlgorithm.getId().toString()))
                .andExpect(jsonPath("$.algoRelationType.id").value(type.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void updateAlgorithmRelation_returnBadRequest() {
        AlgorithmRelationDto relationDto = new AlgorithmRelationDto();
        relationDto.setId(null);

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .updateAlgorithmRelation(UUID.randomUUID(), UUID.randomUUID(), null));

        mockMvc.perform(put(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(relationDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateAlgorithmRelation_returnNotFound() {
        AlgorithmRelationTypeDto typeDto = new AlgorithmRelationTypeDto();
        typeDto.setId(UUID.randomUUID());
        typeDto.setName("typeName");

        AlgorithmRelationDto relationDto = new AlgorithmRelationDto();
        relationDto.setId(UUID.randomUUID());
        relationDto.setAlgorithmRelationType(typeDto);
        UUID sourceId = UUID.randomUUID();
        relationDto.setSourceAlgorithmId(sourceId);
        relationDto.setTargetAlgorithmId(UUID.randomUUID());

        doThrow(new NoSuchElementException()).when(algorithmRelationService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .updateAlgorithmRelation(sourceId, relationDto.getId(), null));

        mockMvc.perform(put(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(relationDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteAlgorithmRelation_returnNoContent() {
        doNothing().when(algorithmRelationService).checkIfAlgorithmIsInAlgorithmRelation(any(), any());
        doNothing().when(algorithmRelationService).delete(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .deleteAlgorithmRelation(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deleteAlgorithmRelation_returnNotFound() {
        doThrow(NoSuchElementException.class).when(algorithmRelationService).checkIfAlgorithmIsInAlgorithmRelation(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .deleteAlgorithmRelation(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getAlgorithmRelation_returnOk() {
        Algorithm sourceAlgorithm = new Algorithm();
        sourceAlgorithm.setId(UUID.randomUUID());
        sourceAlgorithm.setName("sourceAlgorithmName");
        sourceAlgorithm.setComputationModel(ComputationModel.CLASSIC);

        Algorithm targetAlgorithm = new Algorithm();
        targetAlgorithm.setId(UUID.randomUUID());
        targetAlgorithm.setName("targetAlgorithmName");
        targetAlgorithm.setComputationModel(ComputationModel.CLASSIC);

        AlgorithmRelationType type = new AlgorithmRelationType();
        type.setId(UUID.randomUUID());
        type.setName("typeName");

        AlgorithmRelation algorithmRelation = new AlgorithmRelation();
        algorithmRelation.setId(UUID.randomUUID());
        algorithmRelation.setSourceAlgorithm(sourceAlgorithm);
        algorithmRelation.setTargetAlgorithm(targetAlgorithm);
        algorithmRelation.setAlgorithmRelationType(type);

        doNothing().when(algorithmRelationService).checkIfAlgorithmIsInAlgorithmRelation(any(), any());
        doReturn(algorithmRelation).when(algorithmRelationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .getAlgorithmRelation(sourceAlgorithm.getId(), algorithmRelation.getId()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(algorithmRelation.getId().toString()))
                .andExpect(jsonPath("$.sourceAlgorithmId").value(sourceAlgorithm.getId().toString()))
                .andExpect(jsonPath("$.targetAlgorithmId").value(targetAlgorithm.getId().toString()))
                .andExpect(jsonPath("$.algoRelationType.id").value(type.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getAlgorithmRelation_returnNotFound() {
        doThrow(NoSuchElementException.class).when(algorithmRelationService).checkIfAlgorithmIsInAlgorithmRelation(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationController.class)
                .getAlgorithmRelation(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
