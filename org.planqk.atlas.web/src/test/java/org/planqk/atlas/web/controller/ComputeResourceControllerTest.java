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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ComputeResourceService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyTypeDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@WebMvcTest(ComputeResourceController.class)
@ExtendWith(MockitoExtension.class)
@EnableLinkAssemblers
@AutoConfigureMockMvc
public class ComputeResourceControllerTest {

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    private final int page = 0;

    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    @MockBean
    private ComputeResourceService computeResourceService;

    @MockBean
    private ComputeResourcePropertyService computeResourcePropertyService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkBuilderService linkBuilderService;

    @Test
    public void addComputeResource_returnBadRequest() throws Exception {
        var resource = new ComputeResourceDto();
        resource.setId(UUID.randomUUID());

        mockMvc.perform(
            post(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class).createComputeResource(null)
                )
            ).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void addComputeResource_returnCreated() throws Exception {
        var resource = new ComputeResourceDto();
        resource.setName("Hello World");

        var returnedResource = new ComputeResource();
        returnedResource.setName(resource.getName());
        returnedResource.setId(UUID.randomUUID());

        doReturn(returnedResource).when(computeResourceService).create(any());

        mockMvc.perform(
            post(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class).createComputeResource(null)
                )
            ).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(returnedResource.getId().toString()))
            .andExpect(jsonPath("$.name").value(returnedResource.getName()));
    }

    @Test
    public void updateComputeResource_returnNotFound() throws Exception {
        var resource = new ComputeResourceDto();
        resource.setId(UUID.randomUUID());
        resource.setName("Hello World");

        doThrow(new NoSuchElementException()).when(computeResourceService).update(any());

        mockMvc.perform(
            put(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class).updateComputeResource(UUID.randomUUID(), null)
                )
            ).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void updateComputeResource_returnBadRequest() throws Exception {
        var resource = new ComputeResourceDto();
        resource.setId(UUID.randomUUID());

        mockMvc.perform(
            put(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class).updateComputeResource(UUID.randomUUID(), null)
                )
            ).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateComputeResource_returnOk() throws Exception {
        var resource = new ComputeResourceDto();
        resource.setId(UUID.randomUUID());
        resource.setName("Hello World");

        var returnedResource = new ComputeResource();
        returnedResource.setName(resource.getName());
        returnedResource.setId(resource.getId());

        doReturn(returnedResource).when(computeResourceService).update(any());

        mockMvc.perform(
            put(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class).updateComputeResource(UUID.randomUUID(), null)
                )
            ).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(resource.getId().toString()))
            .andExpect(jsonPath("$.name").value(resource.getName()));
    }

    @Test
    void getComputeResource_returnOk() throws Exception {
        var resource = new ComputeResource();
        resource.setId(UUID.randomUUID());
        resource.setName("Test");

        doReturn(resource).when(computeResourceService).findById(any());

        mockMvc.perform(
            get(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class).getComputeResource(resource.getId())
                )
            ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(resource.getId().toString()))
            .andExpect(jsonPath("$.name").value(resource.getName()));
    }

    @Test
    void getComputeResource_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(computeResourceService).findById(any());

        mockMvc.perform(
            get(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class).getComputeResource(UUID.randomUUID())
                )
            ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void listComputeResources_empty() throws Exception {
        doReturn(Page.empty()).when(computeResourceService).findAll(any());

        var mvcResult = mockMvc.perform(
            get(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class)
                        .getComputeResources(
                            new ListParameters(pageable, null))
                )
            ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    void searchComputeResources_empty() throws Exception {
        doReturn(Page.empty()).when(computeResourceService).searchAllByName(any(), any());

        var mvcResult = mockMvc.perform(
            get(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class)
                        .getComputeResources(
                            new ListParameters(pageable, "hello"))
                )
            ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
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
        doReturn(new PageImpl<>(inputList)).when(computeResourceService).findAll(any());

        var mvcResult = mockMvc.perform(
            get(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class)
                        .getComputeResources(
                            new ListParameters(pageable, null))
                )
            ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var dtoElements = ObjectMapperUtils.mapResponseToList(
            mvcResult.getResponse().getContentAsString(),
            "computeResources",
            ComputeResourceDto.class
        );
        assertThat(dtoElements.size()).isEqualTo(inputList.size());
        // Ensure every element in the input array also exists in the output array.
        inputList.forEach(e -> {
            assertThat(dtoElements.stream().filter(dtoElem -> e.getId().equals(dtoElem.getId())).count()).isEqualTo(1);
        });
    }

    @Test
    void listComputationResourceProperties_empty() throws Exception {
        doReturn(Page.empty()).when(computeResourcePropertyService)
            .findComputeResourcePropertiesOfComputeResource(any(), any());

        var mvcResult = mockMvc.perform(
            get(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class)
                        .getComputeResourcePropertiesOfComputeResource(UUID.randomUUID(),
                            new ListParameters(pageable, null))
                )
            ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    void listComputationResourceProperties_notEmpty() throws Exception {
        var inputList = new ArrayList<ComputeResourceProperty>();
        var type = new ComputeResourcePropertyType();
        type.setId(UUID.randomUUID());
        type.setName("test");
        type.setDatatype(ComputeResourcePropertyDataType.STRING);
        for (int i = 0; i < 50; i++) {
            var element = new ComputeResourceProperty();
            element.setValue("Test Element " + i);
            element.setId(UUID.randomUUID());
            element.setComputeResourcePropertyType(type);
            inputList.add(element);
        }
        doReturn(new PageImpl<>(inputList)).when(computeResourcePropertyService)
            .findComputeResourcePropertiesOfComputeResource(any(), any());

        var mvcResult = mockMvc.perform(
            get(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class)
                        .getComputeResourcePropertiesOfComputeResource(UUID.randomUUID(),
                            new ListParameters(pageable, null))
                )
            ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var dtoElements = ObjectMapperUtils.mapResponseToList(
            mvcResult.getResponse().getContentAsString(),
            "computeResourceProperties",
            ComputeResourceDto.class
        );
        assertThat(dtoElements.size()).isEqualTo(inputList.size());
        // Ensure every element in the input array also exists in the output array.
        inputList.forEach(e -> {
            assertThat(dtoElements.stream().filter(dtoElem -> e.getId().equals(dtoElem.getId())).count()).isEqualTo(1);
        });
    }

    @Test
    void deleteCloudService_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(computeResourceService).delete(any());
        mockMvc.perform(
            delete(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class).deleteComputeResource(UUID.randomUUID())
                )
            ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void deleteCloudService_returnNoContent() throws Exception {
        doNothing().when(computeResourceService).delete(any());
        mockMvc.perform(
            delete(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class).deleteComputeResource(UUID.randomUUID())
                )
            ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    void deleteCloudService_returnBadRequest() throws Exception {
        doThrow(new EntityReferenceConstraintViolationException("")).when(computeResourceService).delete(any());
        mockMvc.perform(
            delete(
                linkBuilderService.urlStringTo(
                    methodOn(ComputeResourceController.class).deleteComputeResource(UUID.randomUUID())
                )
            ).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getSoftwarePlatformsOfComputeResource_returnOk() {
        var platform = new SoftwarePlatform();
        platform.setName("test");
        platform.setId(UUID.randomUUID());

        doReturn(new PageImpl<>(List.of(platform))).when(computeResourceService).findLinkedSoftwarePlatforms(any(), any());

        var url = linkBuilderService.urlStringTo(
            methodOn(ComputeResourceController.class)
                .getSoftwarePlatformsOfComputeResource(UUID.randomUUID(), ListParameters.getDefault())
        );
        mockMvc.perform(
            get(url).accept(MediaType.APPLICATION_JSON)
        ).andExpect(jsonPath("$._embedded.softwarePlatforms[0].id").value(platform.getId().toString()))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getSoftwarePlatformsOfComputeResource_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourceService).findLinkedSoftwarePlatforms(any(), any());

        var url = linkBuilderService.urlStringTo(
            methodOn(ComputeResourceController.class)
                .getSoftwarePlatformsOfComputeResource(UUID.randomUUID(), ListParameters.getDefault())
        );
        mockMvc.perform(
            get(url).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getCloudServicesOfComputeResource_returnOk() {
        var svc = new CloudService();
        svc.setName("test");
        svc.setId(UUID.randomUUID());

        doReturn(new PageImpl<>(List.of(svc))).when(computeResourceService).findLinkedCloudServices(any(), any());

        var url = linkBuilderService.urlStringTo(
            methodOn(ComputeResourceController.class)
                .getCloudServicesOfComputeResource(UUID.randomUUID(), ListParameters.getDefault())
        );
        mockMvc.perform(
            get(url).accept(MediaType.APPLICATION_JSON)
        ).andExpect(jsonPath("$._embedded.cloudServices[0].id").value(svc.getId().toString()))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getCloudServicesOfComputeResource_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourceService).findLinkedCloudServices(any(), any());

        var url = linkBuilderService.urlStringTo(
            methodOn(ComputeResourceController.class)
                .getCloudServicesOfComputeResource(UUID.randomUUID(), ListParameters.getDefault())
        );
        mockMvc.perform(
            get(url).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertiesOfComputeResource_EmptyList_returnOk() {
        doReturn(new PageImpl<>(List.of())).when(computeResourcePropertyService)
            .findComputeResourcePropertiesOfComputeResource(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .getComputeResourcePropertiesOfComputeResource(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(jsonPath("$._embedded.computeResourceProperties").doesNotExist())
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertiesOfComputeResource_SingleElement_returnOk() {
        var type = new ComputeResourcePropertyType();
        type.setName("test");
        type.setId(UUID.randomUUID());
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var res = new ComputeResourceProperty();
        res.setComputeResourcePropertyType(type);
        res.setValue("1.3");
        res.setId(UUID.randomUUID());

        doReturn(new PageImpl<>(List.of(res))).when(computeResourcePropertyService)
            .findComputeResourcePropertiesOfComputeResource(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .getComputeResourcePropertiesOfComputeResource(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(jsonPath("$._embedded.computeResourceProperties[0].id").value(res.getId().toString()))
            .andExpect(jsonPath("$._embedded.computeResourceProperties[0].type.id").value(type.getId().toString()))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertiesOfComputeResource_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService)
            .findComputeResourcePropertiesOfComputeResource(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .getComputeResourcePropertiesOfComputeResource(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertyOfComputeResource_SingleElement_returnOk() {
        var type = new ComputeResourcePropertyType();
        type.setName("test");
        type.setId(UUID.randomUUID());
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var res = new ComputeResourceProperty();
        res.setComputeResourcePropertyType(type);
        res.setValue("1.3");
        res.setId(UUID.randomUUID());

        doReturn(res).when(computeResourcePropertyService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .getComputeResourcePropertyOfComputeResource(UUID.randomUUID(), res.getId()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(res.getId().toString()))
            .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertyOfComputeResource_UnknownProperty_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .getComputeResourcePropertyOfComputeResource(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteComputeResourcePropertyOfComputeResource_returnNoContent() {
        doNothing().when(computeResourcePropertyService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .deleteComputeResourcePropertyOfComputeResource(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deleteComputeResourcePropertyOfComputeResource_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .deleteComputeResourcePropertyOfComputeResource(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createComputeResourcePropertyForComputeResource_returnCreated() {
        var type = new ComputeResourcePropertyType();
        type.setName("test");
        type.setId(UUID.randomUUID());
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var res = new ComputeResourceProperty();
        res.setComputeResourcePropertyType(type);
        res.setValue("1.3");
        res.setId(UUID.randomUUID());

        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setId(type.getId());
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue(res.getValue());
        resDto.setType(typeDto);

        doReturn(res).when(computeResourcePropertyService).addComputeResourcePropertyToComputeResource(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .createComputeResourcePropertyForComputeResource(UUID.randomUUID(), null));
        mockMvc.perform(
            post(url)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(res.getId().toString()))
            .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
        ;
    }

    @Test
    @SneakyThrows
    void createComputeResourcePropertyForComputeResource_returnBadRequest() {
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("test");
        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .createComputeResourcePropertyForComputeResource(UUID.randomUUID(), null));
        mockMvc.perform(
            post(url)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isBadRequest())
        ;
    }

    @Test
    @SneakyThrows
    void createComputeResourcePropertyForComputeResource_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService)
            .addComputeResourcePropertyToComputeResource(any(), any());

        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setId(UUID.randomUUID());
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("123");
        resDto.setType(typeDto);

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .createComputeResourcePropertyForComputeResource(UUID.randomUUID(), null));
        mockMvc.perform(
            post(url)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isNotFound())
        ;
    }

    @Test
    @SneakyThrows
    void updateComputeResourcePropertyOfComputeResource_returnOk() {
        var type = new ComputeResourcePropertyType();
        type.setName("test");
        type.setId(UUID.randomUUID());
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var res = new ComputeResourceProperty();
        res.setId(UUID.randomUUID());
        res.setComputeResourcePropertyType(type);
        res.setValue("1.3");
        res.setId(UUID.randomUUID());

        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setId(type.getId());
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue(res.getValue());
        resDto.setType(typeDto);

        doReturn(res).when(computeResourcePropertyService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .updateComputeResourcePropertyOfComputeResource(UUID.randomUUID(), res.getId(), null));
        mockMvc.perform(
            put(url)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(res.getId().toString()))
            .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
        ;
    }

    @Test
    @SneakyThrows
    void updateComputeResourcePropertyOfComputeResource_returnBadRequest() {
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("123");

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .updateComputeResourcePropertyOfComputeResource(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(
            put(url)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isBadRequest())
        ;
    }

    @Test
    @SneakyThrows
    void updateComputeResourcePropertyOfComputeResource_UnknownProperty_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService).update(any());

        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setId(UUID.randomUUID());
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("123");
        resDto.setType(typeDto);

        var url = linkBuilderService.urlStringTo(methodOn(ComputeResourceController.class)
            .updateComputeResourcePropertyOfComputeResource(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(
            put(url)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isNotFound())
        ;
    }
}
