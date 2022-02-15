/*******************************************************************************
 * Copyright (c) 2020-2022 the qc-atlas contributors.
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ToscaApplication;
import org.planqk.atlas.core.services.ToscaApplicationService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.ToscaApplicationDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ToscaApplicationController.class)
@ExtendWith(MockitoExtension.class)
@EnableLinkAssemblers
@AutoConfigureMockMvc
class ToscaApplicationControllerTest {
    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    private final int page = 0;

    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    @MockBean
    private ToscaApplicationService toscaApplicationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkBuilderService linkBuilderService;

    @Test
    public void addToscaApplication_returnCreated() throws Exception{

        UUID uuid = UUID.randomUUID();
        String name = "Test Name";

        var returnedResource = new ToscaApplication();
        returnedResource.setName(name);
        returnedResource.setId(uuid);

        doReturn(returnedResource).when(toscaApplicationService).createFromFile(any(), any());

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        MockPart namePart = new MockPart("name", "name", name.getBytes());
        namePart.getHeaders().setContentType(MediaType.TEXT_PLAIN);


        mockMvc.perform(
                multipart(
                        linkBuilderService.urlStringTo(
                                methodOn(ToscaApplicationController.class).createApplication(null,null)
                        )
                ).file(file).part(namePart)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.name").value(name));;
    }

    @Test
    public void updateToscaApplication_returnNotFound() throws Exception {
        var resource = new ToscaApplicationDto();
        resource.setId(UUID.randomUUID());
        resource.setName("Hello World");

        doThrow(new NoSuchElementException()).when(toscaApplicationService).update(any());

        mockMvc.perform(
                put(
                        linkBuilderService.urlStringTo(
                                methodOn(ToscaApplicationController.class).updateApplication(UUID.randomUUID(), null)
                        )
                ).content(mapper.writeValueAsString(resource))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void updateToscaApplication_returnBadRequest() throws Exception {
        var resource = new ToscaApplicationDto();
        resource.setId(UUID.randomUUID());
        resource.setName("Hello World");

        mockMvc.perform(
                put(
                        linkBuilderService.urlStringTo(
                                methodOn(ToscaApplicationController.class).updateApplication(UUID.randomUUID(), null)
                        )
                ).content(mapper.writeValueAsString(resource))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateToscaApplication_returnOk() throws Exception {
        var resource = new ToscaApplicationDto();
        resource.setId(UUID.randomUUID());
        resource.setName("Hello World");

        var returnedResource = new ToscaApplication();
        returnedResource.setName(resource.getName());
        returnedResource.setId(resource.getId());

        doReturn(returnedResource).when(toscaApplicationService).update(any());

        mockMvc.perform(
                        put(
                                linkBuilderService.urlStringTo(
                                        methodOn(ToscaApplicationController.class).updateApplication(UUID.randomUUID(), null)
                                )
                        ).content(mapper.writeValueAsString(resource))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(resource.getId().toString()))
                .andExpect(jsonPath("$.name").value(resource.getName()));
    }


}
