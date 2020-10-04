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
import java.util.List;
import java.util.UUID;

import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.core.services.LinkingService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationAreaController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
@Slf4j
public class ApplicationAreaControllerTest {

    @MockBean
    private ApplicationAreaService applicationAreaService;
    @MockBean
    private LinkingService linkingService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

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

        var page = ObjectMapperUtils.getPageInfo(result.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.applicationAreas[0].id").value(area.getId().toString()))
                .andExpect(jsonPath("$._embedded.applicationAreas[0].name").value(area.getName()))
                .andReturn();

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

        MvcResult result = mockMvc
                .perform(
                        post(
                                linkBuilderService.urlStringTo(
                                        methodOn(ApplicationAreaController.class)
                                                .getApplicationAreas(new ListParameters(PageRequest.of(1, 10), "hellp"))
                                )
                        ).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.applicationAreas[0].id").value(area.getId().toString()))
                .andExpect(jsonPath("$._embedded.applicationAreas[0].name").value(area.getName()))
                .andReturn();

        var page = ObjectMapperUtils.getPageInfo(result.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(1);
        assertThat(page.getNumber()).isEqualTo(0);
    }
}
