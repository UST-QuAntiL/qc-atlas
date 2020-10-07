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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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

@WebMvcTest(value = ProblemTypeController.class)
@ExtendWith({MockitoExtension.class})
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class ProblemTypeControllerTest {

    @MockBean
    public ProblemTypeService problemTypeService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @Test
    @SneakyThrows
    void getProblemTypes_EmptyList_returnOk() {
        doReturn(new PageImpl<ProblemType>(List.of())).when(problemTypeService).findAll(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .getProblemTypes(ListParameters.getDefault()));
        mockMvc
                .perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.problemTypes").doesNotExist());
    }

    @Test
    @SneakyThrows
    void getProblemTypes_SingleElement_returnOk() {
        var probType = new ProblemType();
        probType.setId(UUID.randomUUID());
        probType.setName("test");

        doReturn(new PageImpl<>(List.of(probType))).when(problemTypeService).findAll(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .getProblemTypes(ListParameters.getDefault()));
        mockMvc
                .perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.problemTypes[0].id").value(probType.getId().toString()))
                .andExpect(jsonPath("$._embedded.problemTypes[0].name").value(probType.getName()));
    }

    @Test
    @SneakyThrows
    void createProblemType_returnCreated() {
        var problemTypeDto = new ProblemTypeDto();
        problemTypeDto.setId(UUID.randomUUID());
        problemTypeDto.setName("test");
        var probType = ModelMapperUtils.convert(problemTypeDto, ProblemType.class);
        problemTypeDto.setId(null);

        doReturn(probType).when(problemTypeService).create(any());
        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .createProblemType(null));
        mockMvc
                .perform(
                        post(url)
                                .accept(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(problemTypeDto))
                                .contentType(APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(probType.getId().toString()))
                .andExpect(jsonPath("$.name").value(probType.getName()));
    }

    @Test
    @SneakyThrows
    void createProblemType_returnBadRequest() {
        var problemTypeDto = new ProblemTypeDto();
        problemTypeDto.setId(null);
        problemTypeDto.setName(null);

        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .createProblemType(null));
        mockMvc
                .perform(
                        post(url)
                                .accept(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(problemTypeDto))
                                .contentType(APPLICATION_JSON)
                ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateProblemType_returnCreated() {
        var problemTypeDto = new ProblemTypeDto();
        problemTypeDto.setId(UUID.randomUUID());
        problemTypeDto.setName("test");
        var probType = ModelMapperUtils.convert(problemTypeDto, ProblemType.class);

        doReturn(probType).when(problemTypeService).update(any());
        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .updateProblemType(problemTypeDto.getId(), null));
        mockMvc
                .perform(
                        put(url)
                                .accept(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(problemTypeDto))
                                .contentType(APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(probType.getId().toString()))
                .andExpect(jsonPath("$.name").value(probType.getName()));
    }

    @Test
    @SneakyThrows
    void updateProblemType_returnBadRequest() {
        var problemTypeDto = new ProblemTypeDto();
        problemTypeDto.setId(null);
        problemTypeDto.setName(null);

        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .updateProblemType(UUID.randomUUID(), null));
        mockMvc
                .perform(
                        put(url)
                                .accept(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(problemTypeDto))
                                .contentType(APPLICATION_JSON)
                ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateProblemType_returnNotFound() {
        var problemTypeDto = new ProblemTypeDto();
        problemTypeDto.setId(UUID.randomUUID());
        problemTypeDto.setName("test");

        doThrow(new NoSuchElementException()).when(problemTypeService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .updateProblemType(problemTypeDto.getId(), null));
        mockMvc.perform(
                put(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(problemTypeDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteProblemType_returnNoContent() {
        doNothing().when(problemTypeService).delete(any());
        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .deleteProblemType(UUID.randomUUID()));
        mockMvc.perform(
                delete(url)
                        .accept(APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deleteProblemType_returnBadRequest() {
        doThrow(new EntityReferenceConstraintViolationException("")).when(problemTypeService).delete(any());
        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .deleteProblemType(UUID.randomUUID()));
        mockMvc.perform(
                delete(url)
                        .accept(APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void deleteProblemType_returnNotFound() {
        doThrow(new NoSuchElementException()).when(problemTypeService).delete(any());
        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .deleteProblemType(UUID.randomUUID()));
        mockMvc.perform(
                delete(url)
                        .accept(APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getProblemType_returnOk() {
        var problemType = new ProblemType();
        problemType.setId(UUID.randomUUID());
        problemType.setName("test");

        doReturn(problemType).when(problemTypeService).findById(any());
        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .getProblemType(problemType.getId()));
        mockMvc
                .perform(
                        get(url)
                                .accept(APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(problemType.getId().toString()))
                .andExpect(jsonPath("$.name").value(problemType.getName()));
    }

    @Test
    @SneakyThrows
    void getProblemType_returnNotFound() {
        doThrow(new NoSuchElementException()).when(problemTypeService).findById(any());
        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .getProblemType(UUID.randomUUID()));
        mockMvc.perform(
                get(url)
                        .accept(APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getProblemTypeParentList_returnOk() {
        var probType = new ProblemType();
        probType.setId(UUID.randomUUID());
        probType.setName("test");

        doReturn(List.of(probType)).when(problemTypeService).getParentList(any());
        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .getProblemTypeParentList(UUID.randomUUID()));
        mockMvc.perform(
                get(url)
                        .accept(APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.problemTypes[0].id").value(probType.getId().toString()))
                ;
    }

    @Test
    @SneakyThrows
    void getProblemTypeParentList_returnNotFound() {
        doThrow(new NoSuchElementException()).when(problemTypeService).getParentList(any());
        var url = linkBuilderService.urlStringTo(methodOn(ProblemTypeController.class)
                .getProblemTypeParentList(UUID.randomUUID()));
        mockMvc.perform(
                get(url)
                        .accept(APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }
}
