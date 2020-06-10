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

import org.planqk.atlas.core.model.QuantumResource;
import org.planqk.atlas.core.services.QuantumResourceService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.linkassembler.QuantumResourceAssembler;
import org.planqk.atlas.web.linkassembler.QuantumResourceTypeAssembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(QuantumResourceController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class QuantumResourceControllerTest {

    @TestConfiguration
    public static class TestConfig {

        @Bean
        public QuantumResourceTypeAssembler quantumResourceTypeAssembler() {
            return new QuantumResourceTypeAssembler();
        }

        @Bean
        public QuantumResourceAssembler quantumResourceAssembler() {
            return new QuantumResourceAssembler();
        }
    }

    @MockBean
    private QuantumResourceService resourceService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = ObjectMapperUtils.newTestMapper();
    }
}
