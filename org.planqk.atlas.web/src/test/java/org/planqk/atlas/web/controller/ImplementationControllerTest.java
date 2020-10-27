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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyTypeDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@WebMvcTest(controllers = ImplementationController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class ImplementationControllerTest {

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

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
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(implDto))
        ).andExpect(jsonPath("$.id").value(impl.getId().toString()))
            .andExpect(jsonPath("$.name").value(impl.getName()))
            .andExpect(jsonPath("$.implementedAlgorithmId").value(algo.getId().toString()))
            .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    void createImplementation_returnBadRequest() {
        var implDto = new ImplementationDto();
        implDto.setName(null);
        implDto.setId(UUID.randomUUID());
        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .createImplementation(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createImplementation_returnNotFound() {
        var implDto = new ImplementationDto();
        implDto.setName("test-impl");

        doThrow(new NoSuchElementException()).when(implementationService).create(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .createImplementation(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isNotFound());
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
        mockMvc.perform(put(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(implDto))
        ).andExpect(jsonPath("$.id").value(impl.getId().toString()))
            .andExpect(jsonPath("$.name").value(impl.getName()))
            .andExpect(jsonPath("$.implementedAlgorithmId").value(algo.getId().toString()))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void updateImplementation_returnBadRequest() {
        var implDto = new ImplementationDto();
        implDto.setName(null);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .updateImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(put(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateImplementation_returnNotFound() {
        var implDto = new ImplementationDto();
        implDto.setName("test-impl");

        doThrow(new NoSuchElementException()).when(implementationService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .updateImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(put(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(implDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteImplementation_returnNoContent() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doNothing().when(implementationService).delete(UUID.randomUUID());
        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .deleteImplementation(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deleteImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .deleteImplementation(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
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
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(jsonPath("$").isEmpty())
            .andExpect(status().isOk());
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
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(jsonPath("$._embedded.tags").isArray())
            .andExpect(jsonPath("$._embedded.tags[0].value").value(tag.getValue()))
            .andExpect(jsonPath("$._embedded.tags[0].category").value(tag.getCategory()))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getTagsOfImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getTagsOfImplementation(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
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
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(tagDto)))
            .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    void addTagToImplementation_returnBadRequest() {
        var tagDto = new TagDto();
        tagDto.setCategory("test-c");
        tagDto.setValue(null);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .addTagToImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(tagDto)))
            .andExpect(status().isBadRequest());
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
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(tagDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void removeTagFromImplementation_returnNoContent() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doNothing().when(tagService).removeTagFromImplementation(any(), any());

        var tagDto = new TagDto();
        tagDto.setCategory("test-c");
        tagDto.setValue("test-v");

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .removeTagFromImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(tagDto)))
            .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void removeTagFromImplementation_returnBadRequest() {
        var tagDto = new TagDto();
        tagDto.setCategory("test-c");
        tagDto.setValue(null);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .removeTagFromImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(tagDto)))
            .andExpect(status().isBadRequest());
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
        mockMvc.perform(delete(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(tagDto)))
            .andExpect(status().isNotFound());
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
        mockMvc.perform(delete(url).accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(tagDto)))
            .andExpect(status().isNotFound());
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
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(impl.getId().toString()))
            .andExpect(jsonPath("$.name").value(impl.getName()))
            .andExpect(jsonPath("$.implementedAlgorithmId").value(algo.getId().toString()))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getImplementation(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getPublicationsOfImplementation_EmptyList_returnOk() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doReturn(new PageImpl<>(List.of())).when(implementationService).findLinkedPublications(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getPublicationsOfImplementation(UUID.randomUUID(), UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.publications").doesNotExist())
        ;
    }

    @Test
    @SneakyThrows
    void getPublicationsOfImplementation_SingleElement_returnOk() {
        var pub = new Publication();
        pub.setTitle("test");
        pub.setAuthors(List.of("test"));
        pub.setId(UUID.randomUUID());

        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doReturn(new PageImpl<>(List.of(pub))).when(implementationService).findLinkedPublications(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getPublicationsOfImplementation(UUID.randomUUID(), UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.publications").isArray())
            .andExpect(jsonPath("$._embedded.publications[0].id").value(pub.getId().toString()))
        ;
    }

    @Test
    @SneakyThrows
    void getPublicationsOfImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getPublicationsOfImplementation(UUID.randomUUID(), UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getPublicationOfImplementation_returnOk() {
        var pub = new Publication();
        pub.setTitle("test");
        pub.setAuthors(List.of("test"));
        pub.setId(UUID.randomUUID());

        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doReturn(pub).when(publicationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getPublicationOfImplementation(UUID.randomUUID(), UUID.randomUUID(), pub.getId()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(pub.getTitle()))
            .andExpect(jsonPath("$.id").value(pub.getId().toString()))
            .andExpect(jsonPath("$.authors").isArray())
            .andExpect(jsonPath("$.authors[0]").value(pub.getAuthors().get(0)))
        ;
    }

    @Test
    @SneakyThrows
    void getPublicationOfImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getPublicationOfImplementation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkImplementationAndPublication_returnNoContent() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doNothing().when(linkingService).linkImplementationAndPublication(any(), any());

        var pubDto = new PublicationDto();
        pubDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .linkImplementationAndPublication(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(pubDto)).contentType(APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void linkImplementationAndPublication_returnBadRequest() {
        var pubDto = new PublicationDto();
        pubDto.setId(null);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .linkImplementationAndPublication(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(pubDto)).contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void linkImplementationAndPublication_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var pubDto = new PublicationDto();
        pubDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .linkImplementationAndPublication(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(pubDto)).contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkImplementationAndPublication_UnknownPublication_returnNotFound() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doThrow(new NoSuchElementException()).when(linkingService).linkImplementationAndPublication(any(), any());

        var pubDto = new PublicationDto();
        pubDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .linkImplementationAndPublication(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(pubDto)).contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkImplementationAndPublication_returnNoContent() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doNothing().when(linkingService).unlinkImplementationAndPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .unlinkImplementationAndPublication(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void unlinkImplementationAndPublication_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .unlinkImplementationAndPublication(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkImplementationAndPublication_UnknownPublication_returnNotFound() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doThrow(new NoSuchElementException()).when(linkingService).unlinkImplementationAndPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .unlinkImplementationAndPublication(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getSoftwarePlatformsOfImplementation_EmptyList_returnOk() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doReturn(new PageImpl<>(List.of())).when(implementationService).findLinkedSoftwarePlatforms(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getSoftwarePlatformsOfImplementation(UUID.randomUUID(), UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.SoftwarePlatforms").doesNotExist())
        ;
    }

    @Test
    @SneakyThrows
    void getSoftwarePlatformsOfImplementation_SingleElement_returnOk() {
        var softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setName("test");
        softwarePlatform.setId(UUID.randomUUID());

        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doReturn(new PageImpl<>(List.of(softwarePlatform))).when(implementationService).findLinkedSoftwarePlatforms(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getSoftwarePlatformsOfImplementation(UUID.randomUUID(), UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.softwarePlatforms").isArray())
            .andExpect(jsonPath("$._embedded.softwarePlatforms[0].id").value(softwarePlatform.getId().toString()))
        ;
    }

    @Test
    @SneakyThrows
    void getSoftwarePlatformsOfImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getSoftwarePlatformsOfImplementation(UUID.randomUUID(), UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getSoftwarePlatformOfImplementation_returnOk() {
        var softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setName("test");
        softwarePlatform.setId(UUID.randomUUID());

        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doReturn(softwarePlatform).when(softwarePlatformService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getSoftwarePlatformOfImplementation(UUID.randomUUID(), UUID.randomUUID(), softwarePlatform.getId()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(softwarePlatform.getName()))
            .andExpect(jsonPath("$.id").value(softwarePlatform.getId().toString()))
        ;
    }

    @Test
    @SneakyThrows
    void getSoftwarePlatformOfImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getSoftwarePlatformOfImplementation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkImplementationAndSoftwarePlatform_returnNoContent() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doNothing().when(linkingService).linkImplementationAndSoftwarePlatform(any(), any());

        var softwarePlatformDto = new SoftwarePlatformDto();
        softwarePlatformDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .linkImplementationAndSoftwarePlatform(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(softwarePlatformDto)).contentType(APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void linkImplementationAndSoftwarePlatform_returnBadRequest() {
        var softwarePlatformDto = new SoftwarePlatformDto();
        softwarePlatformDto.setId(null);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .linkImplementationAndSoftwarePlatform(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(softwarePlatformDto)).contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void linkImplementationAndSoftwarePlatform_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var softwarePlatformDto = new SoftwarePlatformDto();
        softwarePlatformDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .linkImplementationAndSoftwarePlatform(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(softwarePlatformDto)).contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkImplementationAndSoftwarePlatform_UnknownSoftwarePlatform_returnNotFound() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doThrow(new NoSuchElementException()).when(linkingService).linkImplementationAndSoftwarePlatform(any(), any());

        var softwarePlatformDto = new SoftwarePlatformDto();
        softwarePlatformDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .linkImplementationAndSoftwarePlatform(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
            .content(mapper.writeValueAsString(softwarePlatformDto)).contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkImplementationAndSoftwarePlatform_returnNoContent() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doNothing().when(linkingService).unlinkImplementationAndSoftwarePlatform(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .unlinkImplementationAndSoftwarePlatform(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void unlinkImplementationAndSoftwarePlatform_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .unlinkImplementationAndSoftwarePlatform(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkImplementationAndSoftwarePlatform_UnknownSoftwarePlatform_returnNotFound() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doThrow(new NoSuchElementException()).when(linkingService).unlinkImplementationAndSoftwarePlatform(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .unlinkImplementationAndSoftwarePlatform(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertiesOfImplementation_EmptyList_returnOk() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doReturn(new PageImpl<>(List.of())).when(computeResourcePropertyService)
            .findComputeResourcePropertiesOfImplementation(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getComputeResourcePropertiesOfImplementation(UUID.randomUUID(), UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(jsonPath("$._embedded.computeResourceProperties").doesNotExist())
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertiesOfImplementation_SingleElement_returnOk() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var type = new ComputeResourcePropertyType();
        type.setName("test");
        type.setId(UUID.randomUUID());
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var res = new ComputeResourceProperty();
        res.setComputeResourcePropertyType(type);
        res.setValue("1.3");
        res.setId(UUID.randomUUID());

        doReturn(new PageImpl<>(List.of(res))).when(computeResourcePropertyService)
            .findComputeResourcePropertiesOfImplementation(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getComputeResourcePropertiesOfImplementation(UUID.randomUUID(), UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(jsonPath("$._embedded.computeResourceProperties[0].id").value(res.getId().toString()))
            .andExpect(jsonPath("$._embedded.computeResourceProperties[0].type.id").value(type.getId().toString()))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertiesOfImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getComputeResourcePropertiesOfImplementation(UUID.randomUUID(), UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertyOfImplementation_SingleElement_returnOk() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var type = new ComputeResourcePropertyType();
        type.setName("test");
        type.setId(UUID.randomUUID());
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var res = new ComputeResourceProperty();
        res.setComputeResourcePropertyType(type);
        res.setValue("1.3");
        res.setId(UUID.randomUUID());

        doReturn(res).when(computeResourcePropertyService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getComputeResourcePropertyOfImplementation(UUID.randomUUID(), UUID.randomUUID(), res.getId()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(res.getId().toString()))
            .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertyOfImplementation_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getComputeResourcePropertyOfImplementation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertyOfImplementation_UnknownProperty_returnNotFound() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .getComputeResourcePropertyOfImplementation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteComputeResourcePropertyOfImplementation_returnNoContent() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doNothing().when(computeResourcePropertyService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .deleteComputeResourcePropertyOfImplementation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deleteComputeResourcePropertyOfImplementation_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .deleteComputeResourcePropertyOfImplementation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteComputeResourcePropertyOfImplementation_UnknownProperty_returnNotFound() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .deleteComputeResourcePropertyOfImplementation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createComputeResourcePropertyForImplementation_returnCreated() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

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

        doReturn(res).when(computeResourcePropertyService).addComputeResourcePropertyToImplementation(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .createComputeResourcePropertyForImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
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
    void createComputeResourcePropertyForImplementation_returnBadRequest() {
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("test");
        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .createComputeResourcePropertyForImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
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
    void createComputeResourcePropertyForImplementation_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setId(UUID.randomUUID());
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("123");
        resDto.setType(typeDto);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .createComputeResourcePropertyForImplementation(UUID.randomUUID(), UUID.randomUUID(), null));
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
    void updateComputeResourcePropertyOfImplementation_returnOk() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

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

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .updateComputeResourcePropertyOfImplementation(UUID.randomUUID(), UUID.randomUUID(), res.getId(), null));
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
    void updateComputeResourcePropertyOfImplementation_returnBadRequest() {
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("123");

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .updateComputeResourcePropertyOfImplementation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null));
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
    void updateComputeResourcePropertyOfImplementation_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());

        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setId(UUID.randomUUID());
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("123");
        resDto.setType(typeDto);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .updateComputeResourcePropertyOfImplementation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(
            put(url)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isNotFound())
        ;
    }

    @Test
    @SneakyThrows
    void updateComputeResourcePropertyOfImplementation_UnknownProperty_returnNotFound() {
        doNothing().when(implementationService).checkIfImplementationIsOfAlgorithm(any(), any());
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService).update(any());

        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setId(UUID.randomUUID());
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("123");
        resDto.setType(typeDto);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
            .updateComputeResourcePropertyOfImplementation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(
            put(url)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isNotFound())
        ;
    }
}
