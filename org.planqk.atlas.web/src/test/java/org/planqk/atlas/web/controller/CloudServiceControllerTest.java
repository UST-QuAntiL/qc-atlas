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

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.services.CloudServiceService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CloudServiceController.class})
@ExtendWith( {MockitoExtension.class})
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class CloudServiceControllerTest {

    @MockBean
    private CloudServiceService cloudServiceService;
    @MockBean
    private LinkingService linkingService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);

    @Test
    public void addCloudService_returnBadRequest() throws Exception {
        var resource = new CloudServiceDto();
        resource.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .createCloudService(null));

        mockMvc.perform(post(url).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void addCloudService_returnCreated() throws Exception {
        var service = new CloudServiceDto();
        service.setName("Hello World");

        var returnedService = new CloudService();
        returnedService.setName(service.getName());
        returnedService.setId(UUID.randomUUID());

        doReturn(returnedService).when(cloudServiceService).create(any());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .createCloudService(null));

        mockMvc.perform(post(url).content(mapper.writeValueAsString(service))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(returnedService.getId().toString()))
                .andExpect(jsonPath("$.name").value(returnedService.getName()));
    }

    @Test
    public void updateCloudService_returnNotFound() throws Exception {
        var resource = new CloudServiceDto();
        resource.setId(UUID.randomUUID());
        resource.setName("Hello World");

        doThrow(new NoSuchElementException()).when(cloudServiceService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .updateCloudService(UUID.randomUUID(), null));

        mockMvc.perform(put(url).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void updateCloudService_returnBadRequest() throws Exception {
        var resource = new CloudServiceDto();
        resource.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .updateCloudService(UUID.randomUUID(), null));

        mockMvc.perform(put(url).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateCloudService_returnOk() throws Exception {
        var resource = new CloudServiceDto();
        resource.setId(UUID.randomUUID());
        resource.setName("Hello World");

        var returnedResource = new CloudService();
        returnedResource.setName(resource.getName());
        returnedResource.setId(resource.getId());

        doReturn(returnedResource).when(cloudServiceService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .updateCloudService(UUID.randomUUID(), null));

        mockMvc.perform(put(url).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(resource.getId().toString()))
                .andExpect(jsonPath("$.name").value(resource.getName()));
    }

    @Test
    void getCloudService_returnOk() throws Exception {
        var resource = new CloudService();
        resource.setId(UUID.randomUUID());
        resource.setName("Test");

        doReturn(resource).when(cloudServiceService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .getCloudService(resource.getId()));

        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resource.getId().toString()))
                .andExpect(jsonPath("$.name").value(resource.getName()));
    }

    @Test
    void listCloudServices_empty() throws Exception {
        doReturn(Page.empty()).when(cloudServiceService).findAll(any());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .getCloudServices(new ListParameters(pageable, null)));

        var mvcResult = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    void searchCloudServices_empty() throws Exception {
        doReturn(Page.empty()).when(cloudServiceService).searchAllByName(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .getCloudServices(new ListParameters(pageable, "hello")));

        var mvcResult = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    void listCloudService_notEmpty() throws Exception {
        var inputList = new ArrayList<CloudService>();
        for (int i = 0; i < 50; i++) {
            var element = new CloudService();
            element.setName("Test Element " + i);
            element.setId(UUID.randomUUID());
            inputList.add(element);
        }
        doReturn(new PageImpl<>(inputList)).when(cloudServiceService).findAll(any());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .getCloudServices(new ListParameters(pageable, null)));

        var mvcResult = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var dtoElements = ObjectMapperUtils.mapResponseToList(
                mvcResult.getResponse().getContentAsString(),
                "cloudServices",
                CloudServiceDto.class
        );
        assertThat(dtoElements.size()).isEqualTo(inputList.size());
        // Ensure every element in the input array also exists in the output array.
        inputList.forEach(e -> {
            assertThat(dtoElements.stream().filter(dtoElem -> e.getId().equals(dtoElem.getId())).count()).isEqualTo(1);
        });
    }

    @Test
    void deleteCloudService_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(cloudServiceService).delete(any());
        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .deleteCloudService(UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCloudService_returnNoContent() throws Exception {
        doNothing().when(cloudServiceService).delete(any());
        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .deleteCloudService(UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void listComputeResourcesOfCloudService_empty() throws Exception {
        doReturn(Page.empty()).when(cloudServiceService).findLinkedComputeResources(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .getComputeResourcesOfCloudService(UUID.randomUUID(), new ListParameters(pageable, null)));
        var mvcResult = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    void listComputeResourcesOfCloudService_notEmpty() throws Exception {
        var inputList = new ArrayList<ComputeResource>();
        for (int i = 0; i < 50; i++) {
            var element = new ComputeResource();
            element.setName("Test Element " + i);
            element.setId(UUID.randomUUID());
            inputList.add(element);
        }
        doReturn(new PageImpl<>(inputList)).when(cloudServiceService).findLinkedComputeResources(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .getComputeResourcesOfCloudService(UUID.randomUUID(), new ListParameters(pageable, null)));

        var mvcResult = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

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
    void linkCloudServiceToComputeResource_returnNoContent() throws Exception {
        ComputeResourceDto computeResourceDto = new ComputeResourceDto();
        computeResourceDto.setId(UUID.randomUUID());
        computeResourceDto.setName("computeResource");

        doNothing().when(linkingService).linkCloudServiceAndComputeResource(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .linkCloudServiceAndComputeResource(UUID.randomUUID(), null));
        mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(computeResourceDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
    }

    @Test
    void linkCloudServiceToComputeResource_returnNotFound() throws Exception {
        ComputeResourceDto computeResourceDto = new ComputeResourceDto();
        computeResourceDto.setId(UUID.randomUUID());
        computeResourceDto.setName("computeResource");

        doThrow(new NoSuchElementException()).when(linkingService).linkCloudServiceAndComputeResource(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .linkCloudServiceAndComputeResource(UUID.randomUUID(), null));
        mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(computeResourceDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void unlinkCloudServiceAndComputeResource_returnNoContent() throws Exception {
        doNothing().when(linkingService).unlinkCloudServiceAndComputeResource(any(), any());
        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .unlinkCloudServiceAndComputeResource(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
    }

    @Test
    void unlinkCloudServiceToComputeResource_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkCloudServiceAndComputeResource(any(), any());
        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .unlinkCloudServiceAndComputeResource(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void unlinkCloudServiceToComputeResource_returnBadRequest() throws Exception {
        doThrow(new EntityReferenceConstraintViolationException("")).when(linkingService).unlinkCloudServiceAndComputeResource(any(), any());
        var url = linkBuilderService.urlStringTo(methodOn(CloudServiceController.class)
                .unlinkCloudServiceAndComputeResource(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}
