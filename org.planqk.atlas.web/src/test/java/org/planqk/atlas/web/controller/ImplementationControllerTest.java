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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.controller.mixin.ComputeResourcePropertyMixin;
import org.planqk.atlas.web.controller.mixin.PublicationMixin;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@WebMvcTest(controllers = ImplementationController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {PublicationMixin.class, ComputeResourcePropertyMixin.class})
})
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class ImplementationControllerTest {

    @MockBean
    private AlgorithmService algorithmService;
    @MockBean
    private ImplementationService implementationService;
    @MockBean
    private ComputeResourcePropertyService computeResourcePropertyService;
    @MockBean
    private PublicationService publicationService;
    @MockBean
    private SoftwarePlatformService softwarePlatformService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();
    private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/");

    @Test
    public void getOneImplForAlgo() throws Exception {
        UUID algoId = UUID.randomUUID();
        UUID implId = UUID.randomUUID();
        Algorithm algorithm = mockValidAlgorithmForImplCreation(algoId);
        Implementation implementation = mockValidMinimalImpl(implId);
        implementation.setImplementedAlgorithm(algorithm);
        List<Implementation> implementationList = new ArrayList<Implementation>();
        implementationList.add(implementation);

        Pageable pageable = PageRequest.of(0, 2);

        Page<Implementation> page = new PageImpl<Implementation>(implementationList, pageable,
                implementationList.size());

        when(implementationService.findByImplementedAlgorithm(eq(algoId), any(Pageable.class))).thenReturn(page);

        MvcResult mvcResult = mockMvc.perform(get(
                fromMethodCall(uriBuilder, on(ImplementationController.class).getImplementations(algoId)).toUriString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(mvcResult.getResponse().getContentAsString(),
                "implementations", ImplementationDto.class);

        assertEquals(implementation.getId(), resultList.get(0).getId());
        assertEquals(1, resultList.size());
    }

    @Test
    public void getMultipleImplForAlgo() throws Exception {
        UUID algoId = UUID.randomUUID();
        UUID implId1 = UUID.randomUUID();
        UUID implId2 = UUID.randomUUID();
        Algorithm algorithm = mockValidAlgorithmForImplCreation(algoId);
        Implementation implementation1 = mockValidMinimalImpl(implId1);
        Implementation implementation2 = mockValidMinimalImpl(implId2);
        implementation1.setImplementedAlgorithm(algorithm);
        implementation2.setImplementedAlgorithm(algorithm);
        List<Implementation> implementationList = new ArrayList<Implementation>();
        implementationList.add(implementation1);
        implementationList.add(implementation2);

        Pageable pageable = PageRequest.of(0, 2);

        Page<Implementation> page = new PageImpl<Implementation>(implementationList, pageable,
                implementationList.size());

        when(implementationService.findByImplementedAlgorithm(eq(algoId), any(Pageable.class))).thenReturn(page);

        MvcResult mvcResult = mockMvc.perform(get(
                fromMethodCall(uriBuilder, on(ImplementationController.class).getImplementations(algoId)).toUriString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(mvcResult.getResponse().getContentAsString(),
                "implementations", ImplementationDto.class);
        assertTrue(
                resultList.stream().map(impl -> impl.getId()).allMatch(id -> id.equals(implId1) || id.equals(implId2)));
        assertEquals(resultList.size(), implementationList.size());
    }

    @Test
    public void createImplWithCompleteInfosForAlgo() throws Exception {
        UUID algoId = UUID.randomUUID();
        UUID implId = UUID.randomUUID();
        Algorithm algorithm = mockValidAlgorithmForImplCreation(algoId);

        Implementation implementation = mockValidMinimalImpl(implId);
        implementation.setImplementedAlgorithm(algorithm);

        when(algorithmService.findById(any(UUID.class))).thenReturn(algorithm);

        MvcResult mvcResult = mockMvc
                .perform(post(fromMethodCall(uriBuilder,
                        on(ImplementationController.class).createImplementation(algoId, null)).toUriString())
                        .content(mapper.writeValueAsString(
                                ModelMapperUtils.convert(implementation, ImplementationDto.class)))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<ImplementationDto> implementationResult = new ObjectMapper().readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<EntityModel<ImplementationDto>>() {
                });
        assertEquals(implementationResult.getContent().getId(), implId);
    }

    @Test
    public void createImplWithMinimalInfosForAlgo() throws Exception {
        UUID algoId = UUID.randomUUID();
        UUID implId = UUID.randomUUID();
        Algorithm algorithm = mockValidAlgorithmForImplCreation(algoId);

        Implementation implementation = mockValidMinimalImpl(implId);
        implementation.setImplementedAlgorithm(algorithm);

        when(algorithmService.findById(any(UUID.class))).thenReturn(algorithm);

        MvcResult mvcResult = mockMvc
                .perform(post(fromMethodCall(uriBuilder,
                        on(ImplementationController.class).createImplementation(algoId, null)).toUriString())
                        .content(mapper.writeValueAsString(
                                ModelMapperUtils.convert(implementation, ImplementationDto.class)))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<ImplementationDto> createdImpl = new ObjectMapper().readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<EntityModel<ImplementationDto>>() {
                });
        assertEquals(createdImpl.getContent().getId(), implId);
    }

    private Implementation mockValidMinimalImpl(UUID implId) throws MalformedURLException {
        Implementation implementation = new Implementation();
        implementation.setName("implementation for Shor");
        implementation.setId(implId);

        when(implementationService.save(any(Implementation.class))).thenReturn(implementation);
        return implementation;
    }

    @Test
    public void createImplWithInvalidInfo() throws Exception {
        UUID algoId = UUID.randomUUID();
        UUID implId = UUID.randomUUID();
        mockValidAlgorithmForImplCreation(algoId);

        // specify an implementation:
        Implementation implementation = new Implementation();

        implementation.setId(implId);

        when(implementationService.save(any(Implementation.class))).thenReturn(implementation);

        mockMvc.perform(
                post(fromMethodCall(uriBuilder, on(ImplementationController.class).createImplementation(algoId, null))
                        .toUriString())
                        .content(mapper.writeValueAsString(
                                ModelMapperUtils.convert(implementation, ImplementationDto.class)))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    public void createImplForNonExistentAlgo() throws Exception {
        UUID nonExistentAlgoId = UUID.randomUUID();
        UUID implId = UUID.randomUUID();
        Implementation implementation = mockValidMinimalImpl(implId);
        // pretend algo is not found:
        when(algorithmService.findById(nonExistentAlgoId)).thenReturn(null);
        when(implementationService.save(any(Implementation.class))).thenReturn(implementation);

        mockMvc.perform(post(fromMethodCall(uriBuilder,
                on(ImplementationController.class).createImplementation(nonExistentAlgoId, null)).toUriString())
                .content(mapper
                        .writeValueAsString(ModelMapperUtils.convert(implementation, ImplementationDto.class)))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateImplementation() throws Exception {
        UUID algoId = UUID.randomUUID();
        UUID implId = UUID.randomUUID();
        Algorithm algorithm = mockValidAlgorithmForImplCreation(algoId);

        Implementation implementation = mockValidMinimalImpl(implId);
        implementation.setImplementedAlgorithm(algorithm);

        when(algorithmService.findById(algoId)).thenReturn(algorithm);

        mockMvc
                .perform(post(fromMethodCall(uriBuilder,
                        on(ImplementationController.class).createImplementation(algoId, null)).toUriString())
                        .content(mapper.writeValueAsString(
                                ModelMapperUtils.convert(implementation, ImplementationDto.class)))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        final var updateDto = ModelMapperUtils.convert(implementation, ImplementationDto.class);
        updateDto.setName("name2");
        updateDto.setDescription("test2");

        final var updatedImplementation = ModelMapperUtils.convert(updateDto, Implementation.class);

        when(implementationService.findById(implId)).thenReturn(implementation);
        when(implementationService.update(any(UUID.class), any(Implementation.class))).thenReturn(updatedImplementation);

        MvcResult mvcResult = mockMvc
                .perform(put(fromMethodCall(uriBuilder,
                        on(ImplementationController.class).updateImplementation(algoId, implId, updateDto)).toUriString())
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var updatedImpl = Json.mapper().readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<EntityModel<ImplementationDto>>() {
                });
        assertEquals(implId, updatedImpl.getContent().getId());
        assertEquals("name2", updatedImpl.getContent().getName());
        assertEquals("test2", updatedImpl.getContent().getDescription());
    }

    private Algorithm mockValidAlgorithmForImplCreation(UUID algoId) {
        Algorithm algorithm = new Algorithm();
        algorithm.setId(algoId);
        when(algorithmService.findById(algoId)).thenReturn(algorithm);
        return algorithm;
    }
}
