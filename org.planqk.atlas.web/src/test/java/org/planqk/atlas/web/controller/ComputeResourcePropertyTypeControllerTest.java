/*******************************************************************************
 * Copyright (c) 2020-2021 the qc-atlas contributors.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ComputeResourcePropertyTypeService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyTypeDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@WebMvcTest(ComputeResourcePropertyTypeController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class ComputeResourcePropertyTypeControllerTest {

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @MockBean
    private ComputeResourcePropertyService resourceService;

    @MockBean
    private ComputeResourcePropertyTypeService computeResourcePropertyTypeService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkBuilderService linkBuilderService;

    @Test
    @SneakyThrows
    void getResourcePropertyTypes_returnList() {
        var types = new ArrayList<ComputeResourcePropertyType>();
        for (int i = 0; i < 10; i++) {
            var sampleType = new ComputeResourcePropertyType();
            sampleType.setId(UUID.randomUUID());
            sampleType.setName("TypeName" + i);
            sampleType.setDatatype(ComputeResourcePropertyDataType.FLOAT);
            sampleType.setDescription("description");
            types.add(sampleType);
        }

        doReturn(new PageImpl<>(types)).when(computeResourcePropertyTypeService).findAll(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .getResourcePropertyTypes(ListParameters.getDefault()));

        var result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(
                result.getResponse().getContentAsString(),
                ComputeResourcePropertyTypeDto.class
        );
        assertThat(resultList.size()).isEqualTo(10);

        var presentCount = resultList.stream().filter(e -> types.stream().anyMatch(b -> b.getId().equals(e.getId()))).count();
        assertThat(presentCount).isEqualTo(10);
    }

    @Test
    @SneakyThrows
    void createComputingResourcePropertyType_returnCreated() {
        var type = new ComputeResourcePropertyType();
        type.setName("TypeName");
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        type.setDescription("description");
        ComputeResourcePropertyTypeDto typeDto = ModelMapperUtils.convert(type, ComputeResourcePropertyTypeDto.class);

        doReturn(type).when(computeResourcePropertyTypeService).create(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .createComputingResourcePropertyType(null));

        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDto))
        ).andExpect(jsonPath("$.id").isEmpty())
                .andExpect(jsonPath("$.datatype").value(type.getDatatype().toString()))
                .andExpect(jsonPath("$.name").value(type.getName()))
                .andExpect(jsonPath("$.description").value(type.getDescription()))
                .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    void createComputingResourcePropertyType_returnBadRequest() {
        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .createComputingResourcePropertyType(null));

        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createComputingResourcePropertyType_returnNotFound() {
        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setName("TypeName");
        typeDto.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        typeDto.setDescription("description");

        doThrow(NoSuchElementException.class).when(computeResourcePropertyTypeService).create(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .createComputingResourcePropertyType(null));

        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void updateComputingResourcePropertyType_returnOk() {
        var type = new ComputeResourcePropertyType();
        var typeId = UUID.randomUUID();
        type.setId(typeId);
        type.setName("TypeName");
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        type.setDescription("description");

        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setId(typeId);
        typeDto.setName("TypeName");
        typeDto.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        typeDto.setDescription("description");

        doReturn(type).when(computeResourcePropertyTypeService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .updateComputingResourcePropertyType(typeId, null));

        mockMvc.perform(put(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDto))
        ).andExpect(jsonPath("$.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.datatype").value(type.getDatatype().toString()))
                .andExpect(jsonPath("$.name").value(type.getName()))
                .andExpect(jsonPath("$.description").value(type.getDescription()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void updateComputingResourcePropertyType_returnBadRequest() {
        var typeDto = new ComputeResourcePropertyTypeDto();
        UUID typeId = UUID.randomUUID();

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .updateComputingResourcePropertyType(typeId, null));

        mockMvc.perform(put(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateComputingResourcePropertyType_returnNotFound() {
        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setName("TypeName");
        typeDto.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        typeDto.setDescription("description");

        doThrow(NoSuchElementException.class).when(computeResourcePropertyTypeService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .updateComputingResourcePropertyType(UUID.randomUUID(), null));

        mockMvc.perform(put(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(typeDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteComputingResourcePropertyType_returnNoContent() {
        doNothing().when(computeResourcePropertyTypeService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .deleteComputingResourcePropertyType(UUID.randomUUID()));

        mockMvc.perform(delete(url)).andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deleteComputingResourcePropertyType_returnBadRequest() {
        doThrow(new EntityReferenceConstraintViolationException("")).when(computeResourcePropertyTypeService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .deleteComputingResourcePropertyType(UUID.randomUUID()));

        mockMvc.perform(delete(url)).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void deleteComputingResourcePropertyType_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourcePropertyTypeService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .deleteComputingResourcePropertyType(UUID.randomUUID()));

        mockMvc.perform(delete(url)).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getComputingResourcePropertyType_returnOk() {
        var sampleType = new ComputeResourcePropertyType();
        sampleType.setId(UUID.randomUUID());
        sampleType.setName("TypeName");
        sampleType.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        sampleType.setDescription("description");

        doReturn(sampleType).when(computeResourcePropertyTypeService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .getComputingResourcePropertyType(UUID.randomUUID()));

        mockMvc.perform(get(url))
                .andExpect(jsonPath("$.id").value(sampleType.getId().toString()))
                .andExpect(jsonPath("$.datatype").value(sampleType.getDatatype().toString()))
                .andExpect(jsonPath("$.name").value(sampleType.getName()))
                .andExpect(jsonPath("$.description").value(sampleType.getDescription()))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @SneakyThrows
    void getComputingResourcePropertyType_returnNotFound() {
        when(computeResourcePropertyTypeService.findById(any())).thenThrow(new NoSuchElementException());
        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourcePropertyTypeController.class)
                .getComputingResourcePropertyType(UUID.randomUUID()));
        mockMvc.perform(get(url)).andExpect(status().isNotFound());
    }
}
