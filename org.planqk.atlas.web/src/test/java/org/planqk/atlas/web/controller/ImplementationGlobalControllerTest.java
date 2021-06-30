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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

import org.hibernate.envers.DefaultRevisionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.services.FileService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.RevisionDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.envers.repository.support.DefaultRevisionMetadata;
import org.springframework.data.history.Revision;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@WebMvcTest(controllers = ImplementationGlobalController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class ImplementationGlobalControllerTest {

    private final int page = 0;

    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    @MockBean
    private ImplementationService implementationService;

    @MockBean
    private FileService fileService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @Test
    @SneakyThrows
    void getImplementations_EmptyList_returnOk() {
        doReturn(new PageImpl<>(List.of())).when(implementationService).findAll(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
                .getImplementations(ListParameters.getDefault()));
        MvcResult mvcResult = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        assertEquals(ObjectMapperUtils.mapResponseToList(mvcResult, ImplementationDto.class).size(), 0);
    }

    @Test
    @SneakyThrows
    void getImplementations_SingleElement_returnOk() {
        var impl = new Implementation();
        impl.setName("test-i");
        impl.setId(UUID.randomUUID());
        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());
        impl.addAlgorithm(algo);

        doReturn(new PageImpl<>(List.of(impl))).when(implementationService).findAll(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
                .getImplementations(ListParameters.getDefault()));
        MvcResult mvcResult = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        ImplementationDto implementationDto = ObjectMapperUtils.mapResponseToList(mvcResult, ImplementationDto.class).get(0);
        assertEquals(implementationDto.getName(), impl.getName());
        assertEquals(implementationDto.getId(), impl.getId());
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
        impl.addAlgorithm(algo);

        doReturn(impl).when(implementationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
                .getImplementation(impl.getId()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(impl.getId().toString()))
                .andExpect(jsonPath("$.implementedAlgorithmId").value(algo.getId().toString()));
    }

    @Test
    @SneakyThrows
    void getImplementationRevisions_SingleElement_returnOk() {
        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        Instant instant = Instant.now();
        DefaultRevisionEntity defaultRevisionEntity = new DefaultRevisionEntity();
        defaultRevisionEntity.setId(new Random().nextInt());
        defaultRevisionEntity.setTimestamp(instant.toEpochMilli());
        DefaultRevisionMetadata defaultRevisionMetadata = new DefaultRevisionMetadata(defaultRevisionEntity);
        Revision<Integer, Implementation> implementationRevision = Revision.of(defaultRevisionMetadata, impl);
        Page<Revision<Integer, Implementation>> pageImplementationRevision = new PageImpl<>(List.of(implementationRevision));

        doReturn(pageImplementationRevision).when(implementationService).findImplementationRevisions(any(),any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
                .getImplementationRevisions(UUID.randomUUID() ,new ListParameters(pageable, null)));

        MvcResult result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result, RevisionDto.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.from(ZoneOffset.UTC));

        assertEquals(resultList.get(0).getRevisionNumber(),defaultRevisionEntity.getId());
        assertEquals(resultList.get(0).getRevisionInstant(), formatter.format(instant));
    }

    @Test
    @SneakyThrows
    void getImplementationRevisions_returnNotFound() {
        doThrow(NoSuchElementException.class).when(implementationService).findImplementationRevisions(any(),any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
                .getImplementationRevisions(UUID.randomUUID(), new ListParameters(pageable, null)));

        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getImplementationRevision_returnOk() {
        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        DefaultRevisionEntity defaultRevisionEntity = new DefaultRevisionEntity();
        DefaultRevisionMetadata defaultRevisionMetadata = new DefaultRevisionMetadata(defaultRevisionEntity);
        Revision<Integer, Implementation> implementationRevision = Revision.of(defaultRevisionMetadata, impl);

        doReturn(implementationRevision).when(implementationService).findImplementationRevision(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
                .getImplementationRevision(UUID.randomUUID(), new Random().nextInt()));
        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();


        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(impl.getId().toString()));
    }

    @Test
    @SneakyThrows
    void getImplementationRevision_returnNotFound() {

        doThrow(NoSuchElementException.class).when(implementationService).findImplementationRevision(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
                .getImplementationRevision(UUID.randomUUID(), new Random().nextInt()));

        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
