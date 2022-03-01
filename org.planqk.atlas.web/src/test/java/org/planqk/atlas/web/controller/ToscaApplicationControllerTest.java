/*******************************************************************************
 * Copyright (c) 2022 the qc-atlas contributors.
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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.ToscaApplication;
import org.planqk.atlas.core.services.ToscaApplicationService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ToscaApplicationDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    @SneakyThrows
    public void createApplication_returnCreated() {

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
                                        methodOn(ToscaApplicationController.class).createApplication(null, null)
                                )
                        ).file(file).part(namePart)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @SneakyThrows
    void getApplications_EmptyList_returnOk() {
        doReturn(new PageImpl<ToscaApplication>(List.of())).when(toscaApplicationService).findAll(any());

        var url = linkBuilderService.urlStringTo(methodOn(ToscaApplicationController.class)
                .getApplications(ListParameters.getDefault()));
        MvcResult mvcResult = mockMvc
                .perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        assertEquals(ObjectMapperUtils.mapResponseToList(mvcResult, ToscaApplicationDto.class).size(), 0);
    }

    @Test
    @SneakyThrows
    void getApplications_SingleElement_returnOk() {
        var toscaApplication = new ToscaApplication();
        toscaApplication.setId(UUID.randomUUID());
        toscaApplication.setName("name");
        toscaApplication.setToscaName("toscaName");
        toscaApplication.setToscaID("toscaID");
        toscaApplication.setToscaNamespace("toscaNameSpace");

        doReturn(new PageImpl<>(List.of(toscaApplication))).when(toscaApplicationService).findAll(any());

        var url = linkBuilderService.urlStringTo(methodOn(ToscaApplicationController.class)
                .getApplications(ListParameters.getDefault()));
        MvcResult mvcResult = mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        ToscaApplicationDto toscaApplicationDto = ObjectMapperUtils.mapResponseToList(mvcResult, ToscaApplicationDto.class).get(0);
        assertEquals(toscaApplicationDto.getId(), toscaApplication.getId());
        assertEquals(toscaApplicationDto.getToscaID(), toscaApplication.getToscaID());
        assertEquals(toscaApplicationDto.getToscaNamespace(), toscaApplication.getToscaNamespace());
        assertEquals(toscaApplicationDto.getToscaName(), toscaApplication.getToscaName());
    }

    @Test
    @SneakyThrows
    void getApplication_returnOk() {
        var toscaApplication = new ToscaApplication();
        toscaApplication.setId(UUID.randomUUID());
        toscaApplication.setName("name");
        toscaApplication.setToscaName("toscaName");
        toscaApplication.setToscaID("toscaID");
        toscaApplication.setToscaNamespace("toscaNameSpace");

        doReturn(toscaApplication).when(toscaApplicationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ToscaApplicationController.class)
                .getApplication(toscaApplication.getId()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(toscaApplication.getId().toString()))
                .andExpect(jsonPath("$.name").value(toscaApplication.getName()))
                .andExpect(jsonPath("$.toscaName").value(toscaApplication.getToscaName()))
                .andExpect(jsonPath("$.toscaID").value(toscaApplication.getToscaID()))
                .andExpect(jsonPath("$.toscaNamespace").value(toscaApplication.getToscaNamespace()));
    }

    @Test
    @SneakyThrows
    public void updateApplication_returnNotFound() {
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
    @SneakyThrows
    public void updateApplication_returnBadRequest() {
        var resource = new ToscaApplicationDto();
        resource.setId(UUID.randomUUID());

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
    @SneakyThrows
    public void updateApplication_returnOk() {
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

    @Test
    @SneakyThrows
    void deleteApplication_returnNoContent() {
        doNothing().when(toscaApplicationService).delete(any());
        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(ToscaApplicationController.class).deleteApplication(UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }
}
