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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.ImplementationListDto;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ImplementationControllerTest {

    @Mock
    private AlgorithmService algorithmService;
    @Mock
    private ImplementationService implementationService;

    @InjectMocks
    private ImplementationController implementationController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @Before
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(implementationController).build();
    }

    @Test
    public void setupTest() {
        assertNotNull(mockMvc);
    }

    @Test
    public void getOneImplForAlgo() throws Exception {
        Long algoId = 1L;
        Long implId = 2L;
        Algorithm algorithm = mockValidAlgorithmForImplCreation(algoId);
        Implementation implementation = mockValidMinimalImpl(implId);
        implementation.setImplementedAlgorithm(algorithm);
        List<Implementation> implementationList = new ArrayList<Implementation>();
        implementationList.add(implementation);

        ImplementationListDto implementationListDto = new ImplementationListDto();
        implementationListDto.add(ImplementationDto.Converter.convert(implementation));
        Pageable pageable = PageRequest.of(0, 2);

        Page<Implementation> page = new PageImpl<Implementation>(implementationList, pageable, implementationList.size());
        when(implementationService.findAll(any(Pageable.class))).thenReturn(page);

        when(implementationService.save(any(Implementation.class))).thenReturn(implementation);

        MvcResult mvcResult = mockMvc.perform(get("/" + Constants.ALGORITHMS + "/" + algoId + "/"
                + Constants.IMPLEMENTATIONS + "/")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        ImplementationListDto implementationListResult = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), ImplementationListDto.class);
        assertEquals(implementationListResult.getImplementationDtos().stream().findFirst().get().getId(), implementation.getId());
        assertEquals(implementationListResult.getImplementationDtos().size(), 1);
    }

    @Test
    public void getMultipleImplForAlgo() throws Exception {
        Long algoId = 1L;
        Long implId1 = 2L;
        Long implId2 = 3L;
        Algorithm algorithm = mockValidAlgorithmForImplCreation(algoId);
        Implementation implementation1 = mockValidMinimalImpl(implId1);
        Implementation implementation2 = mockValidMinimalImpl(implId2);
        implementation1.setImplementedAlgorithm(algorithm);
        implementation2.setImplementedAlgorithm(algorithm);
        List<Implementation> implementationList = new ArrayList<Implementation>();
        implementationList.add(implementation1);
        implementationList.add(implementation2);

        ImplementationListDto implementationListDto = new ImplementationListDto();
        implementationListDto.add(ImplementationDto.Converter.convert(implementation1));
        implementationListDto.add(ImplementationDto.Converter.convert(implementation2));
        Pageable pageable = PageRequest.of(0, 2);

        Page<Implementation> page = new PageImpl<Implementation>(implementationList, pageable, implementationList.size());
        when(implementationService.findAll(any(Pageable.class))).thenReturn(page);

        when(implementationService.save(any(Implementation.class))).thenReturn(implementation1);

        MvcResult mvcResult = mockMvc.perform(get("/" + Constants.ALGORITHMS + "/" + algoId + "/"
                + Constants.IMPLEMENTATIONS + "/")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        ImplementationListDto implementationListResult = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), ImplementationListDto.class);
        assertTrue(implementationListResult.getImplementationDtos().stream().mapToLong(impl -> impl.getId()).allMatch(id -> id == implId1 || id == implId2));
        assertEquals(implementationListResult.getImplementationDtos().size(), implementationList.size());
    }

    @Test
    public void createImplWithCompleteInfosForAlgo() throws Exception {
        Long algoId = 1L;
        Long implId = 2L;
        Algorithm algorithm = mockValidAlgorithmForImplCreation(algoId);

        Implementation implementation = mockValidMinimalImpl(implId);
        implementation.setImplementedAlgorithm(algorithm);

        when(implementationService.save(any(Implementation.class))).thenReturn(implementation);

        MvcResult mvcResult = mockMvc.perform(post("/" + Constants.ALGORITHMS + "/" + algoId + "/"
                + Constants.IMPLEMENTATIONS + "/")
                .content(objectMapper.writeValueAsString(ImplementationDto.Converter.convert(implementation)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
    }

    @Test
    public void createImplWithMinimalInfosForAlgo() throws Exception {
        Long algoId = 1L;
        Long implId = 2L;
        Algorithm algorithm = mockValidAlgorithmForImplCreation(algoId);

        Implementation implementation = mockValidMinimalImpl(implId);
        implementation.setImplementedAlgorithm(algorithm);
        MvcResult mvcResult = mockMvc.perform(post("/" + Constants.ALGORITHMS + "/" + algoId + "/"
                + Constants.IMPLEMENTATIONS + "/")
                .content(objectMapper.writeValueAsString(ImplementationDto.Converter.convert(implementation)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        ImplementationDto createdImpl = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), ImplementationDto.class);
        assertEquals(createdImpl.getId(), implId);
    }

    private Implementation mockValidMinimalImpl(Long implId) throws MalformedURLException {
        Implementation implementation = new Implementation();
        implementation.setName("implementation for Shor");
        implementation.setId(implId);

        // set everything we need to set for a valid request:
        implementation.setFileLocation(new URL("https://wwww.uri/for/test/"));
        when(implementationService.save(any(Implementation.class))).thenReturn(implementation);
        return implementation;
    }

    @Test
    public void createImplWithInvalidInfo() throws Exception {
        Long algoId = 1L;
        Long implId = 2L;
        mockValidAlgorithmForImplCreation(algoId);

        // specify an implementation:
        Implementation implementation = new Implementation();
        implementation.setName("implementation for Shor");

        implementation.setId(implId);

        when(implementationService.save(any(Implementation.class))).thenReturn(implementation);

        mockMvc.perform(post("/" + Constants.ALGORITHMS + "/" + algoId + "/"
                + Constants.IMPLEMENTATIONS + "/")
                .content(objectMapper.writeValueAsString(ImplementationDto.Converter.convert(implementation)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    public void createImplForNonExistentAlgo() throws Exception {
        Long nonExistentAlgoId = 1L;
        Long implId = 2L;
        Implementation implementation = mockValidMinimalImpl(implId);
        // pretend algo is not found:
        when(algorithmService.findById(nonExistentAlgoId)).thenReturn(null);
        when(implementationService.save(any(Implementation.class))).thenReturn(implementation);

        mockMvc.perform(post("/" + Constants.ALGORITHMS + "/" + nonExistentAlgoId + "/"
                + Constants.IMPLEMENTATIONS + "/")
                .content(objectMapper.writeValueAsString(ImplementationDto.Converter.convert(implementation)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    private Algorithm mockValidAlgorithmForImplCreation(Long algoId) {
        Algorithm algorithm = new Algorithm();
        algorithm.setId(algoId);
        when(algorithmService.findById(algoId)).thenReturn(java.util.Optional.of(algorithm));
        return algorithm;
    }
}
