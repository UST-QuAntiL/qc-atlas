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
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ImplementationGlobalController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class ImplementationGlobalControllerTest {

    @MockBean
    private ImplementationService implementationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkBuilderService linkBuilderService;

    private ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @Test
    @SneakyThrows
    void getImplementations_EmptyList_returnOk() {
        doReturn(new PageImpl<>(List.of())).when(implementationService).findAll(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
                .getImplementations(ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON)
        ).andExpect(jsonPath("$._embedded.implementations").doesNotExist())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getImplementations_SingleElement_returnOk() {
        var impl = new Implementation();
        impl.setName("test-i");
        impl.setId(UUID.randomUUID());
        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());
        impl.setImplementedAlgorithm(algo);

        doReturn(new PageImpl<>(List.of(impl))).when(implementationService).findAll(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
                .getImplementations(ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON)
        ).andExpect(jsonPath("$._embedded.implementations[0].name").value(impl.getName()))
                .andExpect(jsonPath("$._embedded.implementations[0].implementedAlgorithmId").value(algo.getId().toString()))
                .andExpect(jsonPath("$._embedded.implementations[0].id").value(impl.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void getImplementation_returnOk() {
        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());
        algo.setName("dummy");
        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());
        impl.setImplementedAlgorithm(algo);

        doReturn(impl).when(implementationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
                .getImplementation(impl.getId()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(impl.getId().toString()))
                .andExpect(jsonPath("$.implementedAlgorithmId").value(algo.getId().toString()));
    }
}
