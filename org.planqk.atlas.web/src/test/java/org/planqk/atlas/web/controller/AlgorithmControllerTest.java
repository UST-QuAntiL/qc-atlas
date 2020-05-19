/********************************************************************************
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
import java.util.Optional;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmListDto;
import org.planqk.atlas.web.utils.DtoEntityConverter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AlgorithmControllerTest {

    @Mock
    private AlgorithmService algorithmService;
    @Mock
    private DtoEntityConverter modelConverter;

    @InjectMocks
    private AlgorithmController algorithmController;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private int page = 0;
    private int size = 2;
    private Pageable pageable = PageRequest.of(page, size);

    @Before
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(algorithmController).build();
        mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
    }

    @Test
    public void setupTest() {
        assertNotNull(mockMvc);
    }

    @Test
    public void getAlgorithms_withoutPagination() throws Exception {
        when(algorithmService.findAll(Pageable.unpaged())).thenReturn(Page.empty());
        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void getAlgorithms_withEmptyAlgorithmList() throws Exception {
        when(algorithmService.findAll(pageable)).thenReturn(Page.empty());
        when(modelConverter.convert(Page.empty())).thenReturn(new AlgorithmListDto());
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
        ReflectionTestUtils.setField(algorithm1, "id", 1L);
        ReflectionTestUtils.setField(algorithm1, "computationModel", ComputationModel.CLASSIC);
        algorithmList.add(algorithm1);

        Algorithm algorithm2 = new Algorithm();
        ReflectionTestUtils.setField(algorithm2, "id", 2L);
        ReflectionTestUtils.setField(algorithm2, "computationModel", ComputationModel.CLASSIC);
        algorithmList.add(algorithm2);
        
        AlgorithmListDto resultList = new AlgorithmListDto();
        resultList.add(AlgorithmDto.Converter.convert(algorithm1));
        resultList.add(AlgorithmDto.Converter.convert(algorithm2));

        when(algorithmService.findAll(pageable)).thenReturn(new PageImpl<>(algorithmList));
        when(modelConverter.convert(new PageImpl<>(algorithmList))).thenReturn(resultList);

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
        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/5")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void getAlgorithm_returnAlgorithm() throws Exception {
        Algorithm algorithm = new Algorithm();
        ReflectionTestUtils.setField(algorithm, "id", 5L);
        ReflectionTestUtils.setField(algorithm, "computationModel", ComputationModel.CLASSIC);
        when(algorithmService.findById(5L)).thenReturn(Optional.of(algorithm));
        when(modelConverter.convert(algorithm)).thenReturn(AlgorithmDto.Converter.convert(algorithm));

        MvcResult result = mockMvc.perform(get("/" + Constants.ALGORITHMS + "/5")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        AlgorithmDto response = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmDto.class);
        assertEquals(response.getId(), Long.valueOf(5L));
    }

    @Test
    public void createAlgorithm_returnBadRequest() throws Exception {
        AlgorithmDto algorithmDto = new AlgorithmDto();
        mockMvc.perform(post("/" + Constants.ALGORITHMS + "/")
                .content(mapper.writeValueAsString(algorithmDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void createAlgorithm_returnAlgorithm() throws Exception {
        AlgorithmDto algorithmDto = new AlgorithmDto();
        ReflectionTestUtils.setField(algorithmDto, "name", "Shor");
        ReflectionTestUtils.setField(algorithmDto, "computationModel", ComputationModel.CLASSIC);
        Algorithm algorithm = AlgorithmDto.Converter.convert(algorithmDto);
        
        when(modelConverter.convert(any(AlgorithmDto.class))).thenReturn(AlgorithmDto.Converter.convert(algorithmDto));
        when(algorithmService.save(any(Algorithm.class))).thenReturn(algorithm);
        when(modelConverter.convert(any(Algorithm.class))).thenReturn(algorithmDto);
        
        System.out.println(mapper.writeValueAsString(algorithmDto));

        MvcResult result = mockMvc.perform(post("/" + Constants.ALGORITHMS + "/")
                .content(mapper.writeValueAsString(algorithmDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        AlgorithmDto response = mapper.readValue(result.getResponse().getContentAsString(), AlgorithmDto.class);
        assertEquals(response.getName(), algorithmDto.getName());
    }
}
