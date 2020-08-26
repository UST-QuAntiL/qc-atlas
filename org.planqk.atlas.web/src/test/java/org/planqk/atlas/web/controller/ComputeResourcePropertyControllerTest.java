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

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ComputeResourcePropertyTypeService;
import org.planqk.atlas.web.controller.mixin.ComputeResourcePropertyMixin;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@WebMvcTest(value = ComputeResourcePropertyController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ComputeResourcePropertyMixin.class})
})
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class ComputeResourcePropertyControllerTest {

    @MockBean
    private ComputeResourcePropertyService resourceService;
    @MockBean
    private ComputeResourcePropertyTypeService resourceTypeService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();
    private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/");

    @Test
    void deleteResourceProperty_returnOk() throws Exception {
        doNothing().when(resourceService).delete(any());
        var url = fromMethodCall(uriBuilder, on(ComputeResourcePropertyController.class)
                .deleteComputeResourceProperty(UUID.randomUUID())).toUriString();
        mockMvc.perform(delete(url)).andExpect(status().isOk());
    }

    @Test
    void deleteResourceProperty_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(resourceService).delete(any());
        var url = fromMethodCall(uriBuilder, on(ComputeResourcePropertyController.class)
                .deleteComputeResourceProperty(UUID.randomUUID())).toUriString();
        mockMvc.perform(delete(url)).andExpect(status().isNotFound());
    }

    @Test
    void getResource_returnNotFound() throws Exception {
        when(resourceService.findById(any())).thenThrow(new NoSuchElementException());
        var url = fromMethodCall(uriBuilder, on(ComputeResourcePropertyController.class)
                .getComputeResourceProperty(UUID.randomUUID())).toUriString();
        mockMvc.perform(get(url)).andExpect(status().isNotFound());
    }

    @Test
    void getResource_returnOk() throws Exception {
        var sampleType = new ComputeResourcePropertyType();
        sampleType.setId(UUID.randomUUID());
        sampleType.setName("Hello World");
        sampleType.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        sampleType.setDescription("Test");
        var sampleResource = new ComputeResourceProperty();
        sampleResource.setId(UUID.randomUUID());
        sampleResource.setComputeResourcePropertyType(sampleType);

        when(resourceService.findById(any())).thenReturn(sampleResource);
        var url = fromMethodCall(uriBuilder, on(ComputeResourcePropertyController.class)
                .deleteComputeResourceProperty(UUID.randomUUID())).toUriString();
        var result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        var dto = mapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<ComputeResourceProperty>>() {
                }
        ).getContent();

        assertThat(dto.getId()).isEqualTo(sampleResource.getId());
    }
}
