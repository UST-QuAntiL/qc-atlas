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

package org.planqk.atlas.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.planqk.atlas.core.services.AlgorithmRelationTypeService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationTypeDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlgorithmRelationTypeController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
@Slf4j
public class AlgorithmRelationTypeControllerTest {

    @MockBean
    private AlgorithmRelationTypeService algorithmRelationTypeService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    private AlgorithmRelationType algorithmRelationType1;
    private AlgorithmRelationType algorithmRelationType2;
    private AlgorithmRelationTypeDto algoRelationType1Dto;

    @BeforeEach
    public void initialize() {
        algorithmRelationType1 = new AlgorithmRelationType();
        algorithmRelationType1.setId(UUID.randomUUID());
        algorithmRelationType1.setName("relationType1");
        algorithmRelationType2 = new AlgorithmRelationType();
        algorithmRelationType2.setId(UUID.randomUUID());
        algorithmRelationType2.setName("relationType2");

        algoRelationType1Dto = ModelMapperUtils.convert(algorithmRelationType1, AlgorithmRelationTypeDto.class);
    }

    @Test
    @SneakyThrows
    public void createAlgorithmRelationType_returnBadRequest() {
        AlgorithmRelationTypeDto algorithmRelationTypeDto = new AlgorithmRelationTypeDto();
        algorithmRelationTypeDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationTypeController.class)
                .createAlgorithmRelationType(null));

        mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(algorithmRelationTypeDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void createAlgorithmRelationType_returnCreated() {
        var id = algoRelationType1Dto.getId();
        algoRelationType1Dto.setId(null);

        doReturn(algorithmRelationType1).when(algorithmRelationTypeService).create(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationTypeController.class)
                .createAlgorithmRelationType(null));

        MvcResult result = mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(algoRelationType1Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<AlgorithmRelationDto> type = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(type.getContent().getId(), id);
    }

    @Test
    @SneakyThrows
    public void updateAlgorithmRelationType_returnBadRequest() {
        AlgorithmRelationTypeDto algorithmRelationTypeDto = new AlgorithmRelationTypeDto();
        algorithmRelationTypeDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationTypeController.class)
                .updateAlgorithmRelationType(UUID.randomUUID(), null));

        mockMvc.perform(put(url)
                .content(mapper.writeValueAsString(algorithmRelationTypeDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void updateAlgorithmRelationType_returnOk() {
        doReturn(algorithmRelationType1).when(algorithmRelationTypeService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationTypeController.class)
                .updateAlgorithmRelationType(UUID.randomUUID(), null));

        MvcResult result = mockMvc
                .perform(put(url)
                        .content(mapper.writeValueAsString(algoRelationType1Dto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmRelationDto> type = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(type.getContent().getId(), algoRelationType1Dto.getId());
    }

    @Test
    @SneakyThrows
    public void getAlgorithmRelationTypes_withEmptyAlgoRelationTypeList() {
        doReturn(Page.empty()).when(algorithmRelationTypeService).findAll(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationTypeController.class)
                .getAlgorithmRelationTypes(ListParameters.getDefault()));

        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var providers = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algoRelationTypes", AlgorithmRelationDto.class);
        assertEquals(providers.size(), 0);
    }

    @Test
    @SneakyThrows
    public void getAlgorithmRelationTypes_withTwoAlgoRelationTypeList() {
        List<AlgorithmRelationType> algoRelationList = new ArrayList<>();
        algoRelationList.add(algorithmRelationType1);
        algoRelationList.add(algorithmRelationType2);

        Page<AlgorithmRelationType> algoRelationPage = new PageImpl<>(algoRelationList);
        Page<AlgorithmRelationTypeDto> algoRelationDtoPage = ModelMapperUtils.convertPage(algoRelationPage,
                AlgorithmRelationTypeDto.class);

        doReturn(algoRelationPage).when(algorithmRelationTypeService).findAll(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationTypeController.class)
                .getAlgorithmRelationTypes(ListParameters.getDefault()));

        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var providers = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algoRelationTypes", AlgorithmRelationDto.class);
        assertEquals(providers.size(), 2);
    }

    @Test
    @SneakyThrows
    public void getAlgorithmRelationTypeById_returnNotFound() {
        doThrow(new NoSuchElementException()).when(algorithmRelationTypeService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationTypeController.class)
                .getAlgorithmRelationType(algorithmRelationType1.getId()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getAlgorithmRelationTypeById_returnAlgoRelationType() {
        doReturn(algorithmRelationType1).when(algorithmRelationTypeService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationTypeController.class)
                .getAlgorithmRelationType(algorithmRelationType1.getId()));

        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmRelationDto> algoRelationTypeDto = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(algoRelationTypeDto.getContent().getId(), algorithmRelationType1.getId());
    }

    @Test
    @SneakyThrows
    public void deleteAlgorithmRelationType_returnNotFound() {
        doThrow(NoSuchElementException.class).when(algorithmRelationTypeService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationTypeController.class)
                .deleteAlgorithmRelationType(algorithmRelationType1.getId()));

        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteAlgorithmRelationType_returnOk() {
        doNothing().when(algorithmRelationTypeService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmRelationTypeController.class)
                .deleteAlgorithmRelationType(algorithmRelationType1.getId()));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
