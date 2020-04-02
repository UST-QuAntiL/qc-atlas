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

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.web.Constants;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AlgorithmControllerTest {

    @Mock
    private AlgorithmService algorithmService;

    @InjectMocks
    private AlgorithmController algorithmController;

    private MockMvc mockMvc;

    private int page = 0;
    private int size = 2;
    private Pageable pageable = PageRequest.of(page, size);

    @Before
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(algorithmController).build();
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
        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void getAlgorithms_withTwoAlgorithmList() throws Exception {
        List<Algorithm> algorithmList = new ArrayList<>();
        algorithmList.add(new Algorithm(1L));
        algorithmList.add(new Algorithm(2L));

        when(algorithmService.findAll(pageable)).thenReturn(new PageImpl<>(algorithmList));

        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
