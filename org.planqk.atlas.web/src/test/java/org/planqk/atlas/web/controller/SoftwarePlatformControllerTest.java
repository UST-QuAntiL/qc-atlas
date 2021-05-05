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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {SoftwarePlatformController.class})
@ExtendWith({MockitoExtension.class})
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class SoftwarePlatformControllerTest {

    private final int page = 0;

    private final int size = 10;

    private final Pageable pageable = PageRequest.of(page, size);

    private final String softwarePlatformDtoJSONName = "softwarePlatforms";

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @MockBean
    private SoftwarePlatformService softwarePlatformService;

    @MockBean
    private LinkingService linkingService;

    @MockBean
    private ImplementationService implementationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkBuilderService linkBuilderService;

    @Test
    public void addSoftwarePlatform_returnBadRequest() throws Exception {
        SoftwarePlatformDto softwarePlatform = new SoftwarePlatformDto();
        softwarePlatform.setId(UUID.randomUUID());

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class).createSoftwarePlatform(null)
                        )
                ).content(mapper.writeValueAsString(softwarePlatform))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void addSoftwarePlatform_returnCreate() throws Exception {
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setName("test platform");
        SoftwarePlatformDto softwarePlatformDto = ModelMapperUtils.convert(softwarePlatform, SoftwarePlatformDto.class);

        when(softwarePlatformService.create(any())).thenReturn(softwarePlatform);

        MvcResult result = mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class).createSoftwarePlatform(null)
                        )
                ).content(mapper.writeValueAsString(softwarePlatformDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        EntityModel<SoftwarePlatformDto> resultDtoEntity = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(softwarePlatformDto.getId(), resultDtoEntity.getContent().getId());
        assertEquals(softwarePlatformDto.getName(), resultDtoEntity.getContent().getName());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void getSoftwarePlatforms_withEmptySet() throws Exception {
        when(softwarePlatformService.findAll(pageable)).thenReturn(Page.empty());

        MvcResult result = mockMvc
                .perform(
                        get(
                                linkBuilderService.urlStringTo(
                                        methodOn(SoftwarePlatformController.class).getSoftwarePlatforms(new ListParameters(pageable, null))
                                )
                        ).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(result.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void searchSoftwarePlatforms_withEmptySet() throws Exception {
        when(softwarePlatformService.searchAllByName(any(), any())).thenReturn(Page.empty());

        MvcResult result = mockMvc
                .perform(
                        get(
                                linkBuilderService.urlStringTo(
                                        methodOn(SoftwarePlatformController.class).getSoftwarePlatforms(new ListParameters(pageable, "hellp"))
                                )
                        ).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(result.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void getSoftwarePlatforms_withOneElement() throws Exception {
        List<SoftwarePlatform> softwarePlatforms = new ArrayList<>();
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setId(UUID.randomUUID());
        softwarePlatform.setName("test software platform");
        softwarePlatforms.add(softwarePlatform);

        Page<SoftwarePlatform> softwarePlatformPage = new PageImpl<>(softwarePlatforms);

        when(softwarePlatformService.findAll(pageable)).thenReturn(softwarePlatformPage);

        MvcResult result = mockMvc
                .perform(
                        get(
                                linkBuilderService.urlStringTo(
                                        methodOn(SoftwarePlatformController.class).getSoftwarePlatforms(new ListParameters(pageable, null))
                                )
                        ).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        List<SoftwarePlatformDto> softwarePlatformDtos =
                ObjectMapperUtils.mapResponseToList(result, SoftwarePlatformDto.class);

        assertEquals(softwarePlatformDtos.size(), 1);
        assertEquals(softwarePlatformDtos.get(0).getId(), softwarePlatform.getId());
        assertEquals(softwarePlatformDtos.get(0).getName(), softwarePlatform.getName());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void getSoftwarePlatforms_withMultipleElements() throws Exception {
        List<SoftwarePlatform> softwarePlatforms = new ArrayList<>();
        SoftwarePlatform softwarePlatform;
        for (int i = 0; i < size; i++) {
            softwarePlatform = new SoftwarePlatform();
            softwarePlatform.setId(UUID.randomUUID());
            softwarePlatform.setName("test software platform " + i);
            softwarePlatforms.add(softwarePlatform);
        }

        Page<SoftwarePlatform> softwarePlatformPage = new PageImpl<>(softwarePlatforms);

        when(softwarePlatformService.findAll(pageable)).thenReturn(softwarePlatformPage);

        MvcResult result = mockMvc
                .perform(
                        get(
                                linkBuilderService.urlStringTo(
                                        methodOn(SoftwarePlatformController.class).getSoftwarePlatforms(new ListParameters(pageable, null))
                                )
                        ).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        assertEquals(size, ObjectMapperUtils.mapResponseToList(result, SoftwarePlatformDto.class).size());
    }

    @Test
    public void getSoftwarePlatform_returnNotFound() throws Exception {
        UUID testId = UUID.randomUUID();
        when(softwarePlatformService.findById(testId)).thenThrow(NoSuchElementException.class);

        mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class).getSoftwarePlatform(testId)
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void getSoftwarePlatform_returnElement() throws Exception {
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setId(UUID.randomUUID());
        softwarePlatform.setName("test software platform");
        when(softwarePlatformService.findById(softwarePlatform.getId())).thenReturn(softwarePlatform);

        MvcResult result = mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class).getSoftwarePlatform(softwarePlatform.getId())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        EntityModel<SoftwarePlatformDto> softwarePlatformDtoEntity = mapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(softwarePlatformDtoEntity.getContent().getId(), softwarePlatform.getId());
        assertEquals(softwarePlatformDtoEntity.getContent().getName(), softwarePlatform.getName());
    }

    @Test
    public void deleteSoftwarePlatform_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(softwarePlatformService).delete(any());

        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class).deleteSoftwarePlatform(UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void deleteSoftwarePlatform_returnNoContent() throws Exception {
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setId(UUID.randomUUID());
        softwarePlatform.setName("test software platform");
        when(softwarePlatformService.findById(softwarePlatform.getId())).thenReturn(softwarePlatform);

        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class).deleteSoftwarePlatform(softwarePlatform.getId())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    void updateSoftwarePlatform_returnOk() throws Exception {
        var sampleInput = new SoftwarePlatformDto();
        sampleInput.setId(UUID.randomUUID());
        sampleInput.setName("Hello World");

        var returnValue = new SoftwarePlatform();
        returnValue.setId(sampleInput.getId());
        returnValue.setName(sampleInput.getName());

        doReturn(returnValue).when(softwarePlatformService).update(any());
        var mvcResult = mockMvc.perform(
                put(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .updateSoftwarePlatform(UUID.randomUUID(), null)
                        )
                ).content(mapper.writeValueAsBytes(sampleInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var response = mapper.readValue(mvcResult.getResponse().getContentAsString(), SoftwarePlatformDto.class);
        assertThat(response).isEqualTo(sampleInput);
    }

    @Test
    void updateSoftwarePlatform_returnNotFound() throws Exception {
        var sampleInput = new SoftwarePlatformDto();
        sampleInput.setId(UUID.randomUUID());
        sampleInput.setName("Hello World");
        doThrow(new NoSuchElementException()).when(softwarePlatformService).update(any());
        mockMvc.perform(
                put(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .updateSoftwarePlatform(UUID.randomUUID(), null)
                        )
                ).content(mapper.writeValueAsBytes(sampleInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void updateSoftwarePlatform_returnBadRequest() throws Exception {
        var sampleInput = new SoftwarePlatformDto();
        sampleInput.setId(UUID.randomUUID());
        sampleInput.setName(null);
        doThrow(new NoSuchElementException()).when(softwarePlatformService).update(any());
        mockMvc.perform(
                put(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .updateSoftwarePlatform(UUID.randomUUID(), null)
                        )
                ).content(mapper.writeValueAsBytes(sampleInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void listImplementations_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(softwarePlatformService).findLinkedImplementations(any(), any());

        mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .getImplementationsOfSoftwarePlatform(UUID.randomUUID(),
                                                new ListParameters(pageable, null))
                        )
                ).queryParam(Constants.PAGE, Integer.toString(page))
                        .queryParam(Constants.SIZE, Integer.toString(size))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void listCloudServices_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(softwarePlatformService).findLinkedCloudServices(any(), any());

        mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .getCloudServicesOfSoftwarePlatform(UUID.randomUUID(),
                                                new ListParameters(pageable, null))
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void listComputeResources_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(softwarePlatformService).findLinkedComputeResources(any(), any());

        mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .getComputeResourcesOfSoftwarePlatform(UUID.randomUUID(),
                                                new ListParameters(pageable, null))
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void listImplementations_empty() throws Exception {
        doReturn(Page.empty()).when(softwarePlatformService).findLinkedImplementations(any(), any());

        var mvcResult = mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .getImplementationsOfSoftwarePlatform(UUID.randomUUID(),
                                                new ListParameters(pageable, null))
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    void listCloudServices_empty() throws Exception {
        doReturn(Page.empty()).when(softwarePlatformService).findLinkedCloudServices(any(), any());

        var mvcResult = mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .getCloudServicesOfSoftwarePlatform(UUID.randomUUID(),
                                                new ListParameters(pageable, null))
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    void listComputeResources_empty() throws Exception {
        doReturn(Page.empty()).when(softwarePlatformService).findLinkedComputeResources(any(), any());

        var mvcResult = mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .getComputeResourcesOfSoftwarePlatform(UUID.randomUUID(),
                                                new ListParameters(pageable, null))
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    void listImplementations_notEmpty() throws Exception {
        var inputList = new ArrayList<Implementation>();
        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());
        for (int i = 0; i < 50; i++) {
            var element = new Implementation();
            element.setImplementedAlgorithm(algo);
            element.setName("Test Element " + i);
            element.setId(UUID.randomUUID());
            inputList.add(element);
        }
        doReturn(new PageImpl<>(inputList)).when(softwarePlatformService).findLinkedImplementations(any(), any());

        var mvcResult = mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .getImplementationsOfSoftwarePlatform(UUID.randomUUID(),
                                                new ListParameters(pageable, null))
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var dtoElements = ObjectMapperUtils.mapResponseToList(
                mvcResult.getResponse().getContentAsString(),
                ImplementationDto.class
        );
        assertThat(dtoElements.size()).isEqualTo(inputList.size());
        // Ensure every element in the input array also exists in the output array.
        inputList.forEach(e -> {
            assertThat(dtoElements.stream().filter(dtoElem -> e.getId().equals(dtoElem.getId())).count()).isEqualTo(1);
        });
    }

    @Test
    void listCloudServices_notEmpty() throws Exception {
        var inputList = new ArrayList<CloudService>();
        for (int i = 0; i < 50; i++) {
            var element = new CloudService();
            element.setName("Test Element " + i);
            element.setId(UUID.randomUUID());
            inputList.add(element);
        }
        doReturn(new PageImpl<>(inputList)).when(softwarePlatformService).findLinkedCloudServices(any(), any());

        var mvcResult = mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .getCloudServicesOfSoftwarePlatform(UUID.randomUUID(),
                                                new ListParameters(pageable, null))
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var dtoElements = ObjectMapperUtils.mapResponseToList(
                mvcResult.getResponse().getContentAsString(),
                CloudServiceDto.class
        );
        assertThat(dtoElements.size()).isEqualTo(inputList.size());
        // Ensure every element in the input array also exists in the output array.
        inputList.forEach(e -> {
            assertThat(dtoElements.stream().filter(dtoElem -> e.getId().equals(dtoElem.getId())).count()).isEqualTo(1);
        });
    }

    @Test
    void listComputeResources_notEmpty() throws Exception {
        var inputList = new ArrayList<ComputeResource>();
        for (int i = 0; i < 50; i++) {
            var element = new ComputeResource();
            element.setName("Test Element " + i);
            element.setId(UUID.randomUUID());
            inputList.add(element);
        }
        doReturn(new PageImpl<>(inputList)).when(softwarePlatformService).findLinkedComputeResources(any(), any());

        var mvcResult = mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .getComputeResourcesOfSoftwarePlatform(UUID.randomUUID(),
                                                new ListParameters(pageable, null))
                        )
                ).queryParam(Constants.PAGE, Integer.toString(page))
                        .queryParam(Constants.SIZE, Integer.toString(size))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var dtoElements = ObjectMapperUtils.mapResponseToList(
                mvcResult.getResponse().getContentAsString(),
                ComputeResourceDto.class
        );
        assertThat(dtoElements.size()).isEqualTo(inputList.size());
        // Ensure every element in the input array also exists in the output array.
        inputList.forEach(e -> {
            assertThat(dtoElements.stream().filter(dtoElem -> e.getId().equals(dtoElem.getId())).count()).isEqualTo(1);
        });
    }

    @Test
    void createReferenceToComputeResource_returnNoContent() throws Exception {
        CloudServiceDto cloudServiceDto = new CloudServiceDto();
        cloudServiceDto.setId(UUID.randomUUID());
        cloudServiceDto.setName("CloudService");

        doNothing().when(linkingService).linkSoftwarePlatformAndComputeResource(any(), any());

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndComputeResource(UUID.randomUUID(), new ComputeResourceDto())
                        )
                ).content(mapper.writeValueAsString(cloudServiceDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    void createReferenceToComputeResource_InvalidInput_returnBadRequest() throws Exception {
        CloudServiceDto cloudServiceDto = new CloudServiceDto();
        cloudServiceDto.setId(null);
        cloudServiceDto.setName("CloudService");

        doNothing().when(linkingService).linkSoftwarePlatformAndComputeResource(any(), any());

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndComputeResource(UUID.randomUUID(), new ComputeResourceDto())
                        )
                ).content(mapper.writeValueAsString(cloudServiceDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void deleteReferenceToComputeResource_returnNoContent() throws Exception {
        doNothing().when(linkingService).unlinkSoftwarePlatformAndComputeResource(any(), any());
        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .unlinkSoftwarePlatformAndComputeResource(UUID.randomUUID(), UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    void createReferenceToComputeResource_returnNotFound() throws Exception {
        CloudServiceDto cloudServiceDto = new CloudServiceDto();
        cloudServiceDto.setId(UUID.randomUUID());
        cloudServiceDto.setName("CloudService");

        doThrow(new NoSuchElementException()).when(linkingService).linkSoftwarePlatformAndComputeResource(any(), any());

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndComputeResource(UUID.randomUUID(), new ComputeResourceDto())
                        )
                ).content(mapper.writeValueAsString(cloudServiceDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void deleteReferenceToComputeResource_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkSoftwarePlatformAndComputeResource(any(), any());
        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .unlinkSoftwarePlatformAndComputeResource(UUID.randomUUID(), UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void createReferenceToComputeResource_returnBadRequest() throws Exception {
        doThrow(new EntityReferenceConstraintViolationException("")).when(linkingService).linkSoftwarePlatformAndComputeResource(any(), any());
        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndComputeResource(UUID.randomUUID(), new ComputeResourceDto())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void deleteReferenceToComputeResource_returnBadRequest() throws Exception {
        doThrow(new EntityReferenceConstraintViolationException("")).when(linkingService).unlinkSoftwarePlatformAndComputeResource(any(), any());
        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .unlinkSoftwarePlatformAndComputeResource(UUID.randomUUID(), UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createReferenceToCloudService_returnNoContent() throws Exception {
        CloudServiceDto cloudServiceDto = new CloudServiceDto();
        cloudServiceDto.setId(UUID.randomUUID());
        cloudServiceDto.setName("CloudService");

        doNothing().when(linkingService).linkSoftwarePlatformAndCloudService(any(), any());

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndCloudService(UUID.randomUUID(), null)
                        )
                ).content(mapper.writeValueAsString(cloudServiceDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    void createReferenceToCloudService_InvalidInput_returnBadRequest() throws Exception {
        CloudServiceDto cloudServiceDto = new CloudServiceDto();
        cloudServiceDto.setId(null);
        cloudServiceDto.setName("CloudService");

        doNothing().when(linkingService).linkSoftwarePlatformAndCloudService(any(), any());

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndCloudService(UUID.randomUUID(), null)
                        )
                ).content(mapper.writeValueAsString(cloudServiceDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void deleteReferenceToCloudService_returnNoContent() throws Exception {
        doNothing().when(linkingService).unlinkSoftwarePlatformAndCloudService(any(), any());
        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .unlinkSoftwarePlatformAndCloudService(UUID.randomUUID(), UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    void createReferenceToCloudService_returnNotFound() throws Exception {
        CloudServiceDto cloudServiceDto = new CloudServiceDto();
        cloudServiceDto.setId(UUID.randomUUID());
        cloudServiceDto.setName("CloudService");

        doThrow(new NoSuchElementException()).when(linkingService).linkSoftwarePlatformAndCloudService(any(), any());

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndCloudService(UUID.randomUUID(), new CloudServiceDto())
                        )
                ).content(mapper.writeValueAsString(cloudServiceDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void deleteReferenceToCloudService_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkSoftwarePlatformAndCloudService(any(), any());
        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .unlinkSoftwarePlatformAndCloudService(UUID.randomUUID(), UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void createReferenceToCloudService_returnBadRequest() throws Exception {
        doThrow(new EntityReferenceConstraintViolationException("")).when(linkingService).linkSoftwarePlatformAndCloudService(any(), any());
        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndCloudService(UUID.randomUUID(), new CloudServiceDto())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void deleteReferenceToCloudService_returnBadRequest() throws Exception {
        doThrow(new EntityReferenceConstraintViolationException("")).when(linkingService).unlinkSoftwarePlatformAndCloudService(any(), any());
        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .unlinkSoftwarePlatformAndCloudService(UUID.randomUUID(), UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    // Link Implementation
    @Test
    void createReferenceToImplementation_returnNoContent() throws Exception {
        doNothing()
                .when(linkingService)
                .linkImplementationAndSoftwarePlatform(any(), any());

        var implDto = new ImplementationDto();
        implDto.setId(UUID.randomUUID());

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndImplementation(UUID.randomUUID(), null)
                        )
                ).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isNoContent());
    }

    @Test
    void createReferenceToImplementation_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException())
                .when(linkingService)
                .linkImplementationAndSoftwarePlatform(any(), any());

        var implDto = new ImplementationDto();
        implDto.setId(UUID.randomUUID());

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndImplementation(UUID.randomUUID(), null)
                        )
                ).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    void createReferenceToImplementation_returnBadRequest() throws Exception {
        doThrow(new EntityReferenceConstraintViolationException(""))
                .when(linkingService)
                .linkImplementationAndSoftwarePlatform(any(), any());

        var implDto = new ImplementationDto();
        implDto.setId(UUID.randomUUID());

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndImplementation(UUID.randomUUID(), null)
                        )
                ).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createReferenceToImplementation_invalidInput_returnBadRequest() throws Exception {
        doThrow(new EntityReferenceConstraintViolationException(""))
                .when(linkingService)
                .linkImplementationAndSoftwarePlatform(any(), any());

        var implDto = new ImplementationDto();
        implDto.setId(null);

        mockMvc.perform(
                post(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .linkSoftwarePlatformAndImplementation(UUID.randomUUID(), null)
                        )
                ).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isBadRequest());
    }

    // Unlink Implementation
    @Test
    void deleteReferenceToImplementation_returnNoContent() throws Exception {
        doNothing().when(linkingService)
                .unlinkImplementationAndSoftwarePlatform(any(), any());
        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .unlinkSoftwarePlatformAndImplementation(UUID.randomUUID(), UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    void deleteReferenceToImplementation_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException())
                .when(linkingService)
                .unlinkImplementationAndSoftwarePlatform(any(), any());
        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .unlinkSoftwarePlatformAndImplementation(UUID.randomUUID(), UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void deleteReferenceToImplementation_returnBadRequest() throws Exception {
        doThrow(new EntityReferenceConstraintViolationException(""))
                .when(linkingService)
                .unlinkImplementationAndSoftwarePlatform(any(), any());
        mockMvc.perform(
                delete(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .unlinkSoftwarePlatformAndImplementation(UUID.randomUUID(), UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }
    // getImplementationOfSoftwarePlatform

    @Test
    void getImplementationOfSoftwarePlatform_returnOk() throws Exception {
        doNothing()
                .when(softwarePlatformService)
                .checkIfImplementationIsLinkedToSoftwarePlatform(any(), any());
        var impl = new Implementation();
        impl.setId(UUID.randomUUID());
        doReturn(impl).when(implementationService).findById(any());

        mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .getImplementationOfSoftwarePlatform(UUID.randomUUID(), impl.getId())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(impl.getId().toString()));
    }

    @Test
    void getImplementationOfSoftwarePlatform_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException())
                .when(softwarePlatformService)
                .checkIfImplementationIsLinkedToSoftwarePlatform(any(), any());
        mockMvc.perform(
                get(
                        linkBuilderService.urlStringTo(
                                methodOn(SoftwarePlatformController.class)
                                        .getImplementationOfSoftwarePlatform(UUID.randomUUID(), UUID.randomUUID())
                        )
                ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }
}
