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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationAreaController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
@Slf4j
public class ApplicationAreaControllerTest {

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @MockBean
    private ApplicationAreaService applicationAreaService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkBuilderService linkBuilderService;

    @Test
    @SuppressWarnings("ConstantConditions")
    public void getApplicationAreas_EmptyList_returnOk() throws Exception {
        doReturn(new PageImpl<ApplicationArea>(new ArrayList<>())).when(applicationAreaService).findAll(any(), any());

        MvcResult result = mockMvc
                .perform(
                        get(
                                linkBuilderService.urlStringTo(
                                        methodOn(ApplicationAreaController.class)
                                                .getApplicationAreas(new ListParameters(PageRequest.of(1, 10), "hellp"))
                                )
                        ).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void getApplicationAreas_SingleElement_returnOk() throws Exception {
        var area = new ApplicationArea();
        area.setId(UUID.randomUUID());
        area.setName("test");

        doReturn(new PageImpl<ApplicationArea>(List.of(area))).when(applicationAreaService).findAll(any(), any());

        MvcResult result = mockMvc
                .perform(
                        get(
                                linkBuilderService.urlStringTo(
                                        methodOn(ApplicationAreaController.class)
                                                .getApplicationAreas(new ListParameters(PageRequest.of(1, 10), "hellp"))
                                )
                        ).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        List<ApplicationAreaDto> applicationAreaDtos = ObjectMapperUtils.mapResponseToList(result, ApplicationAreaDto.class);
        assertNotNull(applicationAreaDtos.get(0).getId());

        var page = ObjectMapperUtils.getPageInfo(result.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(1);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void createApplicationArea_returnCreated() throws Exception {
        var areaDto = new ApplicationAreaDto();
        areaDto.setName("test");

        var area = new ApplicationArea();
        area.setName(areaDto.getName());
        area.setId(UUID.randomUUID());

        doReturn(area).when(applicationAreaService).create(any());

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(ApplicationAreaController.class)
                                        .createApplicationArea(null)
                        )
                ).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(areaDto))
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(area.getId().toString()))
                .andExpect(jsonPath("$.name").value(area.getName()));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void createApplicationArea_returnBadRequest() throws Exception {
        var areaDto = new ApplicationAreaDto();
        areaDto.setName(null);

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(ApplicationAreaController.class)
                                        .createApplicationArea(null)
                        )
                ).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(areaDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void updateApplicationArea_returnCreated() throws Exception {
        var areaDto = new ApplicationAreaDto();
        areaDto.setName("test");

        var area = new ApplicationArea();
        area.setName(areaDto.getName());
        area.setId(UUID.randomUUID());

        doReturn(area).when(applicationAreaService).update(any());

        mockMvc.perform(
                put(
                        linkBuilderService.urlStringTo(
                                methodOn(ApplicationAreaController.class)
                                        .updateApplicationArea(area.getId(), null)
                        )
                ).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(areaDto))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(area.getId().toString()))
                .andExpect(jsonPath("$.name").value(area.getName()));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void updateApplicationArea_InvalidBody_returnBadRequest() throws Exception {
        var areaDto = new ApplicationAreaDto();
        areaDto.setId(UUID.randomUUID());
        areaDto.setName(null);
        mockMvc.perform(
                put(
                        linkBuilderService.urlStringTo(
                                methodOn(ApplicationAreaController.class)
                                        .updateApplicationArea(areaDto.getId(), null)
                        )
                ).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(areaDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void updateApplicationArea_returnNotFound() throws Exception {
        var areaDto = new ApplicationAreaDto();
        areaDto.setId(UUID.randomUUID());
        areaDto.setName("test");

        doThrow(new NoSuchElementException()).when(applicationAreaService).update(any());

        mockMvc.perform(
                put(
                        linkBuilderService.urlStringTo(
                                methodOn(ApplicationAreaController.class)
                                        .updateApplicationArea(areaDto.getId(), null)
                        )
                ).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(areaDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void deleteApplicationArea_returnNoContent() throws Exception {
        doNothing().when(applicationAreaService).delete(any());

        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(ApplicationAreaController.class)
                                        .deleteApplicationArea(UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void deleteApplicationArea_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(applicationAreaService).delete(any());

        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(ApplicationAreaController.class)
                                        .deleteApplicationArea(UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void getApplicationArea_returnOk() throws Exception {
        var area = new ApplicationArea();
        area.setId(UUID.randomUUID());
        area.setName("test");

        doReturn(area).when(applicationAreaService).findById(any());

        mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(ApplicationAreaController.class)
                                        .getApplicationArea(UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(jsonPath("$.id").value(area.getId().toString()))
                .andExpect(jsonPath("$.name").value(area.getName()))
                .andExpect(status().isOk());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void getApplicationArea_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(applicationAreaService).delete(any());

        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(ApplicationAreaController.class)
                                        .deleteApplicationArea(UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }
}
