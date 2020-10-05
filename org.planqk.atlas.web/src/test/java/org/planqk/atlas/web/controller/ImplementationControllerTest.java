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

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ImplementationController.class)
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
    @MockBean
    private LinkingService linkingService;
    @MockBean
    private TagService tagService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @Test
    @SneakyThrows
    void createImplementation_returnOk() {
        var implDto = new ImplementationDto();
        implDto.setName("test-impl");

        var impl = new Implementation();
        impl.setId(UUID.randomUUID());
        impl.setName(implDto.getName());
        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());
        algo.setName("test-algo");
        impl.setImplementedAlgorithm(algo);
        implDto.setImplementedAlgorithmId(algo.getId());

        doReturn(impl).when(implementationService).create(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .createImplementation(algo.getId(), null));
        mockMvc.perform(post(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(implDto))
        ).andExpect(jsonPath("$.id").value(impl.getId().toString()))
                .andExpect(jsonPath("$.name").value(impl.getName()))
                .andExpect(jsonPath("$.implementedAlgorithmId").value(algo.getId().toString()))
                .andExpect(status().isCreated()).andDo(print());
    }

    @Test
    @SneakyThrows
    void createImplementation_returnBadRequest() {
        var implDto = new ImplementationDto();
        implDto.setName(null);
        implDto.setId(UUID.randomUUID());
        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .createImplementation(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    @SneakyThrows
    void createImplementation_returnNotFound() {
        var implDto = new ImplementationDto();
        implDto.setName("test-impl");

        doThrow(new NoSuchElementException()).when(implementationService).create(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .createImplementation(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void updateImplementation_returnOk() {
        var implDto = new ImplementationDto();
        implDto.setName("test-impl");

        var impl = new Implementation();
        impl.setId(UUID.randomUUID());
        impl.setName(implDto.getName());
        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());
        algo.setName("test-algo");
        impl.setImplementedAlgorithm(algo);
        implDto.setImplementedAlgorithmId(algo.getId());

        doReturn(impl).when(implementationService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .updateImplementation(algo.getId(), UUID.randomUUID(), null));
        mockMvc.perform(put(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(implDto))
        ).andExpect(jsonPath("$.id").value(impl.getId().toString()))
                .andExpect(jsonPath("$.name").value(impl.getName()))
                .andExpect(jsonPath("$.implementedAlgorithmId").value(algo.getId().toString()))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @SneakyThrows
    void updateImplementation_returnBadRequest() {
        var implDto = new ImplementationDto();
        implDto.setName(null);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .updateImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(put(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    @SneakyThrows
    void updateImplementation_returnNotFound() {
        var implDto = new ImplementationDto();
        implDto.setName("test-impl");

        doThrow(new NoSuchElementException()).when(implementationService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .updateImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(put(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void deleteImplementation_returnNoContent() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doNothing().when(implementationService).delete(UUID.randomUUID());
        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .deleteImplementation(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    @SneakyThrows
    void deleteImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .deleteImplementation(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void getTagsOfImplementation_EmptyList_returnOk() {
        var impl = new Implementation();
        impl.setId(UUID.randomUUID());
        impl.setName("test-impl");
        impl.setTags(Set.of());
        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());
        algo.setName("test-algo");
        impl.setImplementedAlgorithm(algo);

        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doReturn(impl).when(implementationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getTagsOfImplementation(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @SneakyThrows
    void getTagsOfImplementation_SingleElementList_returnOk() {
        var impl = new Implementation();
        impl.setId(UUID.randomUUID());
        impl.setName("test-impl");
        var tag = new Tag();
        tag.setValue("test");
        tag.setCategory("test-c");
        impl.setTags(Set.of(tag));
        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());
        algo.setName("test-algo");
        impl.setImplementedAlgorithm(algo);

        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doReturn(impl).when(implementationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getTagsOfImplementation(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.tags").isArray())
                .andExpect(jsonPath("$._embedded.tags[0].value").value(tag.getValue()))
                .andExpect(jsonPath("$._embedded.tags[0].category").value(tag.getCategory()))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @SneakyThrows
    void getTagsOfImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getTagsOfImplementation(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void addTagToImplementation_returnCreated() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doNothing().when(tagService).addTagToImplementation(any(), any());

        var tagDto = new TagDto();
        tagDto.setCategory("test-c");
        tagDto.setValue("test-v");

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .addTagToImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(tagDto)))
                .andExpect(status().isCreated()).andDo(print());
    }

    @Test
    @SneakyThrows
    void addTagToImplementation_returnBadRequest() {
        var tagDto = new TagDto();
        tagDto.setCategory("test-c");
        tagDto.setValue(null);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .addTagToImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(tagDto)))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    @SneakyThrows
    void addTagToImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var tagDto = new TagDto();
        tagDto.setCategory("test-c");
        tagDto.setValue("test-v");

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .addTagToImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(tagDto)))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void removeTagFromImplementation_returnOk() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doNothing().when(tagService).removeTagFromImplementation(any(), any());

        var tagDto = new TagDto();
        tagDto.setCategory("test-c");
        tagDto.setValue("test-v");

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .removeTagFromImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(tagDto)))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @SneakyThrows
    void removeTagFromImplementation_returnBadRequest() {
        var tagDto = new TagDto();
        tagDto.setCategory("test-c");
        tagDto.setValue(null);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .removeTagFromImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(tagDto)))
                .andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    @SneakyThrows
    void removeTagFromImplementation_NoImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var tagDto = new TagDto();
        tagDto.setCategory("test-c");
        tagDto.setValue("test-v");

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .removeTagFromImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(tagDto)))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void removeTagFromImplementation_NoTag_returnNotFound() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doThrow(new NoSuchElementException()).when(tagService).removeTagFromImplementation(any(), any());

        var tagDto = new TagDto();
        tagDto.setCategory("test-c");
        tagDto.setValue("test-v");

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .removeTagFromImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(tagDto)))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void getImplementation_returnOk() {
        var impl = new Implementation();
        impl.setId(UUID.randomUUID());
        impl.setName("test-impl");
        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());
        algo.setName("test-algo");
        impl.setImplementedAlgorithm(algo);

        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doReturn(impl).when(implementationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getImplementation(algo.getId(), impl.getId()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(impl.getId().toString()))
                .andExpect(jsonPath("$.name").value(impl.getName()))
                .andExpect(jsonPath("$.implementedAlgorithmId").value(algo.getId().toString()))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    @SneakyThrows
    void getImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getImplementation(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }
}
