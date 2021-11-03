/*******************************************************************************
 * Copyright (c) 2021 the qc-atlas contributors.
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.LearningMethod;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.LearningMethodService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.LearningMethodDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(LearningMethodController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class LearningMethodControllerTest {

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    private final int page = 0;

    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    @MockBean
    private LearningMethodService learningMethodService;

    @MockBean
    private AlgorithmService algorithmService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkBuilderService linkBuilderService;

    private LearningMethod getTestLearningMethod() {
        LearningMethod learningMethod = new LearningMethod();
        learningMethod.setId(UUID.randomUUID());
        learningMethod.setName("supervised");
        return learningMethod;
    }

    private Algorithm getTestAlgorithm() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setComputationModel(ComputationModel.CLASSIC);
        algorithm.setName("QAOA");
        return algorithm;
    }

    @Test
    public void getAllLearningMethods_SingleElement_returnOk() throws Exception {
        List<LearningMethod> methods = new ArrayList<>();
        LearningMethod testLearningMethod = getTestLearningMethod();
        methods.add(testLearningMethod);

        Page<LearningMethod> methodPage = new PageImpl<>(methods);

        when(learningMethodService.findAll(any(), any())).thenReturn(methodPage);

        var url = linkBuilderService.urlStringTo(methodOn(LearningMethodController.class)
                .getLearningMethods(new ListParameters(pageable, null)));
        MvcResult result = mockMvc
                .perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                LearningMethodDto.class);
        assertEquals(1, resultList.size());
    }

    @Test
    public void getLearningMethods_EmptyList_returnOk() throws Exception {
        Pageable pageable = PageRequest.of(0, 2);
        when(learningMethodService.findAll(pageable, null)).thenReturn(Page.empty());

        var url = linkBuilderService.urlStringTo(methodOn(LearningMethodController.class)
                .getLearningMethods(new ListParameters(pageable, null)));
        MvcResult result = mockMvc
                .perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                LearningMethodDto.class);
        assertEquals(0, resultList.size());
    }

    @Test
    public void getLearningMethod_returnOk() throws Exception {
        LearningMethod testLearningMethod = getTestLearningMethod();
        Page<LearningMethod> learningMethods = new PageImpl<>(List.of(testLearningMethod));

        when(learningMethodService.findById(testLearningMethod.getId())).thenReturn(testLearningMethod);
        when(learningMethodService.findAll(any(), any())).thenReturn(learningMethods);

        var url = linkBuilderService.urlStringTo(methodOn(LearningMethodController.class).getLearningMethod(testLearningMethod.getId()));
        MvcResult mvcResult = mockMvc
                .perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        LearningMethodDto response = ObjectMapperUtils.mapMvcResultToDto(mvcResult, LearningMethodDto.class);
        assertEquals(response.getName(), testLearningMethod.getName());
        assertEquals(response.getId(), testLearningMethod.getId());
    }

    @Test
    public void getLearningMethod_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(learningMethodService).findById(any());
        var url = linkBuilderService.urlStringTo(methodOn(LearningMethodController.class)
                .getLearningMethod(getTestLearningMethod().getId()));
        mockMvc
                .perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createLearningMethod_returnCreated() throws Exception {
        LearningMethod testLearningMethod = new LearningMethod();
        testLearningMethod.setName("test");
        LearningMethodDto learningMethodDto = ModelMapperUtils.convert(testLearningMethod, LearningMethodDto.class);
        when(learningMethodService.create(testLearningMethod)).thenReturn(testLearningMethod);

        var url = linkBuilderService.urlStringTo(methodOn(LearningMethodController.class)
                .createLearningMethod(learningMethodDto));
        MvcResult result = mockMvc
                .perform(post(url).content(mapper.writeValueAsString(learningMethodDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        LearningMethodDto createdLearningMethod = ObjectMapperUtils.mapMvcResultToDto(result, LearningMethodDto.class);
        assertEquals(createdLearningMethod.getName(), testLearningMethod.getName());
        assertEquals(createdLearningMethod.getId(), testLearningMethod.getId());
    }

    @Test
    public void createLearningMethod_returnBadRequest() throws Exception {
        LearningMethod LearningMethod = getTestLearningMethod();
        LearningMethodDto LearningMethodDto = ModelMapperUtils.convert(LearningMethod, LearningMethodDto.class);
        LearningMethod.setName("");
        var url = linkBuilderService.urlStringTo(methodOn(LearningMethodController.class)
                .createLearningMethod(LearningMethodDto));
        mockMvc
                .perform(post(url).content(mapper.writeValueAsString(LearningMethodDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getLearningMethodsForAlgorithm_Empty() throws Exception {
        var algorithm = getTestAlgorithm();

        when(algorithmService.findLinkedLearningMethods(any(), any())).thenReturn(Page.empty());
        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class).
                getLearningMethodsOfAlgorithm(algorithm.getId(), new ListParameters(pageable, null)));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }
}
