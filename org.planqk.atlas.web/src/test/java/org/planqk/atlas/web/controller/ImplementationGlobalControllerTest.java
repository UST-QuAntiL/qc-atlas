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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.services.FileService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

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

    @Test
    public void testCreateFileForImplementation_returnOk() throws Exception {
        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        byte[] testFile = new byte[20];
        final MockMultipartFile file = new MockMultipartFile("file", testFile);
        doReturn(new File()).when(fileService).create(file);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
            .createFileForImplementation(impl.getId(), file));

        // call
        ResultActions resultActions = mockMvc.perform(multipart(path).file(file));

        // test
        resultActions.andExpect(status().isCreated());
        Mockito.verify(fileService, times(1)).create(file);
    }

    @Test
    public void testGetAllFilesOfImplementation_response_OK() throws Exception {
        // Given
        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        final ListParameters listParameters = new ListParameters(this.pageable, null);
        when(implementationService.findLinkedFiles(impl.getId(), listParameters.getPageable())).thenReturn(Page.empty());

        // When
        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
            .getAllFilesOfImplementation(impl.getId(), listParameters));
        ResultActions result = mockMvc.perform(get(path).accept(MediaType.APPLICATION_JSON));


        // Then
        result.andExpect(status().isOk());
        Mockito.verify(implementationService, times(1)).findLinkedFiles(impl.getId(), listParameters.getPageable());
    }

    @Test
    public void testGetFileOfImplementation_response_OK() throws Exception {
        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        var file = new File();
        file.setId(UUID.randomUUID());
        file.setImplementation(impl);
        file.setMimeType("img/png");

        when(fileService.findById(file.getId())).thenReturn(file);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
            .getFileOfImplementation(impl.getId(), file.getId()));

        MvcResult result = mockMvc.perform(get(path)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
            "file", File.class);
        assertEquals(0, resultList.size());

        Mockito.verify(fileService, times(1)).findById(file.getId());
    }

    @Test
    public void testDownloadFileContent_response_OK() throws Exception {
        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        var file = new File();
        file.setId(UUID.randomUUID());
        file.setImplementation(impl);
        file.setMimeType("img/png");

        when(fileService.findById(file.getId())).thenReturn(file);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
            .downloadFileContent(impl.getId(), file.getId()));

        // call
        mockMvc.perform(get(path)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        // test
        Mockito.verify(fileService, times(1)).findById(file.getId());
    }

    @Test
    public void testDeleteFile_response_no_content() throws Exception {
        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        var file = new File();
        file.setId(UUID.randomUUID());
        file.setImplementation(impl);
        file.setMimeType("img/png");

        doNothing().when(fileService).delete(file.getId());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
            .deleteFileOfImplementation(impl.getId(), file.getId()));
        mockMvc.perform(delete(url))
            .andExpect(status().isNoContent()).andReturn();

        Mockito.verify(fileService, times(1)).delete(file.getId());
    }

}
