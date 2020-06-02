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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmListDto;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AlgorithmController.class})
@ExtendWith( {MockitoExtension.class})
@AutoConfigureMockMvc
public class AlgorithmControllerTest {

    @MockBean
    private AlgorithmService algorithmService;

    @Autowired
    private MockMvc mockMvc;

    private int page = 0;
    private int size = 2;
    private Pageable pageable = PageRequest.of(page, size);

    private ObjectMapper mapper;

    @BeforeEach
    public void init() {
        mapper = ObjectMapperUtils.newTestMapper();
    }

    @Test
    public void getAlgorithms_withoutPagination() throws Exception {
        var element = Optional.of("Hello World").orElseThrow(() -> new RuntimeException("Bla bla"));
        when(algorithmService.findAll(Pageable.unpaged())).thenReturn(Page.empty());
        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void getAlgorithms_withEmptyAlgorithmList() throws Exception {
        when(algorithmService.findAll(pageable)).thenReturn(Page.empty());
        MvcResult result = mockMvc.perform(get("/" + Constants.ALGORITHMS + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        AlgorithmListDto algorithmListDto = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmListDto.class);
        assertEquals(algorithmListDto.getAlgorithmDtos().size(), 0);
    }

    @Test
    public void getAlgorithms_withTwoAlgorithmList() throws Exception {
        List<Algorithm> algorithmList = new ArrayList<>();

        Algorithm algorithm1 = new Algorithm();
        algorithm1.setId(UUID.randomUUID());
        algorithmList.add(algorithm1);

        Algorithm algorithm2 = new Algorithm();
        algorithm2.setId(UUID.randomUUID());
        algorithmList.add(algorithm2);

        when(algorithmService.findAll(pageable)).thenReturn(new PageImpl<>(algorithmList));

        MvcResult result = mockMvc.perform(get("/" + Constants.ALGORITHMS + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        AlgorithmListDto algorithmListDto = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmListDto.class);
        assertEquals(algorithmListDto.getAlgorithmDtos().size(), 2);
    }

    @Test
    public void getAlgorithm_returnNotFound() throws Exception {
        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/" + UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void getAlgorithm_returnAlgorithm() throws Exception {
        Algorithm algorithm = new Algorithm();
        UUID algoId = UUID.randomUUID();
        algorithm.setId(algoId);
        when(algorithmService.findById(algoId)).thenReturn(Optional.of(algorithm));

        MvcResult result = mockMvc.perform(get("/" + Constants.ALGORITHMS + "/" + algoId)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        AlgorithmDto response = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmDto.class);
        assertEquals(response.getId(), algoId);
    }

    @Test
    public void createAlgorithm_returnBadRequest() throws Exception {
        AlgorithmDto algorithmDto = new AlgorithmDto();
        mockMvc.perform(post("/" + Constants.ALGORITHMS + "/")
                .content(new ObjectMapper().writeValueAsString(algorithmDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void createAlgorithm_returnAlgorithm() throws Exception {
        AlgorithmDto algorithmDto = new AlgorithmDto();
        algorithmDto.setName("Shor");
        Algorithm algorithm = AlgorithmDto.Converter.convert(algorithmDto);
        when(algorithmService.save(algorithm)).thenReturn(algorithm);

        MvcResult result = mockMvc.perform(post("/" + Constants.ALGORITHMS + "/")
                .content(new ObjectMapper().writeValueAsString(algorithmDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        AlgorithmDto response = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmDto.class);
        assertEquals(response.getName(), algorithmDto.getName());
    }
}
