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
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.ComputingResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputingResourcePropertyType;
import org.planqk.atlas.core.services.ComputingResourcePropertyService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ComputingResourcePropertyTypeDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComputingResourcePropertyTypeController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class ComputingResourcePropertyTypeControllerTest {
    @MockBean
    private ComputingResourcePropertyService resourceService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = ObjectMapperUtils.newTestMapper();
    }

    @Test
    void test_deleteType() throws Exception {
        doNothing().when(resourceService).deleteComputingResourcePropertyType(any());
        var url = "/" + Constants.COMPUTING_RESOURCE_PROPERTY_TYPES + "/" + UUID.randomUUID().toString();
        mockMvc.perform(delete(url)).andExpect(status().isOk());
    }

    @Test
    void test_deleteType_InvalidId() throws Exception {
        doThrow(new NoSuchElementException()).when(resourceService).deleteComputingResourcePropertyType(any());
        var url = "/" + Constants.COMPUTING_RESOURCE_PROPERTY_TYPES + "/" + UUID.randomUUID().toString();
        mockMvc.perform(delete(url)).andExpect(status().isNotFound());
    }

    @Test
    void test_getType_InvalidId() throws Exception {
        when(resourceService.findComputingResourcePropertyTypeById(any())).thenThrow(new NoSuchElementException());
        var url = "/" + Constants.COMPUTING_RESOURCE_PROPERTY_TYPES + "/" + UUID.randomUUID().toString();
        mockMvc.perform(get(url)).andExpect(status().isNotFound());
    }

    @Test
    void test_getType() throws Exception {
        var sampleType = new ComputingResourcePropertyType();
        sampleType.setId(UUID.randomUUID());
        sampleType.setName("Hello World");
        sampleType.setDatatype(ComputingResourcePropertyDataType.FLOAT);
        sampleType.setDescription("Test");

        when(resourceService.findComputingResourcePropertyTypeById(any())).thenReturn(sampleType);
        var url = "/" + Constants.COMPUTING_RESOURCE_PROPERTY_TYPES + "/" + UUID.randomUUID().toString();
        var result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        var dto = mapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<ComputingResourcePropertyTypeDto>>() {
                }
        ).getContent();

        assertThat(dto.getId()).isEqualTo(sampleType.getId());
        assertThat(dto.getDatatype()).isEqualTo(sampleType.getDatatype());
        assertThat(dto.getName()).isEqualTo(sampleType.getName());
        assertThat(dto.getDescription()).isEqualTo(sampleType.getDescription());
    }

    @Test
    void test_getTypes() throws Exception {
        var types = new ArrayList<ComputingResourcePropertyType>();
        for (int i = 0; i < 10; i++) {
            var sampleType = new ComputingResourcePropertyType();
            sampleType.setId(UUID.randomUUID());
            sampleType.setName("Hello World");
            sampleType.setDatatype(ComputingResourcePropertyDataType.FLOAT);
            sampleType.setDescription("Test");
            types.add(sampleType);
        }

        when(resourceService.findAllComputingResourcePropertyTypes(any())).thenReturn(new PageImpl<>(types));
        var url = "/" + Constants.COMPUTING_RESOURCE_PROPERTY_TYPES + "/";
        var result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(
                result.getResponse().getContentAsString(),
                "computingResourcePropertyTypeDtoes",
                ComputingResourcePropertyTypeDto.class
        );
        assertThat(resultList.size()).isEqualTo(10);

        var presentCount = resultList.stream().filter(e -> types.stream().anyMatch(b -> b.getId().equals(e.getId()))).count();
        assertThat(presentCount).isEqualTo(10);
    }
}