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

import org.planqk.atlas.core.services.AlgorithmRelationService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlgorithmRelationController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
@Slf4j
public class AlgorithmRelationControllerTest {

    @MockBean
    private AlgorithmRelationService algorithmRelationService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();


    @Test
    @SneakyThrows
    void getAlgorithmRelationsOfAlgorithm_returnOk() {

    }

    @Test
    @SneakyThrows
    void getAlgorithmRelationsOfAlgorithm_returnNotFound() {

    }

    @Test
    @SneakyThrows
    void createAlgorithmRelation_returnCreated() {

    }

    @Test
    @SneakyThrows
    void createAlgorithmRelation_returnBadRequest() {

    }

    @Test
    @SneakyThrows
    void createAlgorithmRelation_returnNotFound() {

    }

    @Test
    @SneakyThrows
    void updateAlgorithmRelation_returnOk() {

    }

    @Test
    @SneakyThrows
    void updateAlgorithmRelation_returnBadRequest() {

    }

    @Test
    @SneakyThrows
    void updateAlgorithmRelation_returnNotFound() {

    }

    @Test
    @SneakyThrows
    void deleteAlgorithmRelation_returnNoContent() {

    }

    @Test
    @SneakyThrows
    void deleteAlgorithmRelation_returnNotFound() {

    }

    @Test
    @SneakyThrows
    void getAlgorithmRelation_returnOk() {

    }

    @Test
    @SneakyThrows
    void getAlgorithmRelation_returnNotFound() {

    }
}
