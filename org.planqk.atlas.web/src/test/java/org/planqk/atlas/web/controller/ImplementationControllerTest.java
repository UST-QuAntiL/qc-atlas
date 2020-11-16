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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.DiscussionComment;
import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.FileImplementationPackage;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.model.Status;
import org.planqk.atlas.core.model.TOSCAImplementationPackage;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.DiscussionCommentService;
import org.planqk.atlas.core.services.DiscussionTopicService;
import org.planqk.atlas.core.services.FileService;
import org.planqk.atlas.core.services.ImplementationPackageService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyTypeDto;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.dtos.DiscussionTopicDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.dtos.TagDto;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@WebMvcTest(controllers = {ImplementationController.class, DiscussionCommentController.class, DiscussionTopicController.class})
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class ImplementationControllerTest {

    private final int page = 0;

    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @MockBean
    private ImplementationService implementationService;

    @MockBean
    private ImplementationPackageService implementationPackageService;


    @MockBean
    private ComputeResourcePropertyService computeResourcePropertyService;

    @MockBean
    private PublicationService publicationService;

    @MockBean
    private SoftwarePlatformService softwarePlatformService;

    @MockBean
    private DiscussionTopicService discussionTopicService;

    @MockBean
    private DiscussionCommentService discussionCommentService;

    @MockBean
    private LinkingService linkingService;

    @MockBean
    private TagService tagService;

    @MockBean
    private FileService fileService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkBuilderService linkBuilderService;

    private DiscussionTopic discussionTopic1;

    private DiscussionTopicDto discussionTopic1Dto;

    private DiscussionComment discussionComment1;

    private DiscussionCommentDto discussionComment1Dto;

    private DiscussionTopic discussionTopic2;

    private DiscussionTopicDto discussionTopic2Dto;

    private DiscussionComment discussionComment2;

    private DiscussionCommentDto discussionComment2Dto;

    private Set<DiscussionTopic> discussionTopics;

    private Implementation implementation1;

    private Implementation implementation2;

    private Algorithm algorithm1;

    private Algorithm algorithm2;

    private void initializeDiscussions() {
        algorithm1 = new ClassicAlgorithm();
        algorithm1.setId(UUID.randomUUID());
        algorithm1.setName("alg1");
        algorithm1.setComputationModel(ComputationModel.CLASSIC);

        algorithm2 = new ClassicAlgorithm();
        algorithm2.setId(UUID.randomUUID());
        algorithm2.setName("alg2");
        algorithm2.setComputationModel(ComputationModel.CLASSIC);

        implementation1 = new Implementation();
        implementation1.setId(UUID.randomUUID());
        implementation1.setName("Impl1");
        implementation1.setImplementedAlgorithm(algorithm1);

        implementation2 = new Implementation();
        implementation2.setId(UUID.randomUUID());
        implementation2.setName("Impl2");
        implementation2.setImplementedAlgorithm(algorithm2);

        discussionTopic1 = new DiscussionTopic();
        discussionTopic1.setId(UUID.randomUUID());
        discussionTopic1.setTitle("DiscussionTopic1");
        discussionTopic1.setDate(OffsetDateTime.now());
        discussionTopic1.setDescription("Description1");
        discussionTopic1.setStatus(Status.OPEN);
        discussionTopic1.setKnowledgeArtifact(implementation1);
        discussionTopic1Dto = ModelMapperUtils.convert(discussionTopic1, DiscussionTopicDto.class);
        discussionTopics = new HashSet<>();
        discussionTopics.add(discussionTopic1);
        implementation1.setDiscussionTopics(discussionTopics);

        discussionComment1 = new DiscussionComment();
        discussionComment1.setId(UUID.randomUUID());
        discussionComment1.setText("This is a comment");
        discussionComment1.setDiscussionTopic(discussionTopic1);
        discussionComment1.setDate(OffsetDateTime.now());
        discussionComment1Dto = ModelMapperUtils.convert(discussionComment1, DiscussionCommentDto.class);

        discussionTopic2 = new DiscussionTopic();
        discussionTopic2.setId(UUID.randomUUID());
        discussionTopic2.setTitle("DiscussionTopic2");
        discussionTopic2.setDate(OffsetDateTime.now());
        discussionTopic2.setDescription("Description2");
        discussionTopic2.setStatus(Status.OPEN);
        discussionTopic2.setKnowledgeArtifact(implementation2);
        discussionTopic2Dto = ModelMapperUtils.convert(discussionTopic2, DiscussionTopicDto.class);

        discussionComment2 = new DiscussionComment();
        discussionComment2.setId(UUID.randomUUID());
        discussionComment2.setText("This is another comment");
        discussionComment2.setDiscussionTopic(discussionTopic2);
        discussionComment2.setDate(OffsetDateTime.now());
        discussionComment2Dto = ModelMapperUtils.convert(discussionComment2, DiscussionCommentDto.class);

        when(implementationService.findById(implementation1.getId())).thenReturn(implementation1);
        when(implementationService.findById(implementation2.getId())).thenReturn(implementation2);
    }

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

    @Test
    @SneakyThrows
    void getDiscussionTopics() {
        initializeDiscussions();

        List<DiscussionTopic> discussionTopicList = new ArrayList<>();
        discussionTopicList.add(discussionTopic1);
        final Page<DiscussionTopic> discussionTopics = new PageImpl<DiscussionTopic>(discussionTopicList);
        when(discussionTopicService.findByKnowledgeArtifactId(implementation1.getId(), pageable)).thenReturn(discussionTopics);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getDiscussionTopicsOfImplementation(algorithm1.getId(), implementation1.getId(), new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(get(path)).andExpect(status().isOk()).andReturn();

        // test
        Mockito.verify(discussionTopicService, times(1)).findByKnowledgeArtifactId(implementation1.getId(), pageable);

        JSONObject rootObject = new JSONObject(result.getResponse().getContentAsString());
        var embeddedJSONObjects = rootObject.getJSONObject("_embedded").getJSONArray("discussionTopics");
        var resultObject = mapper.readValue(embeddedJSONObjects.getJSONObject(0).toString(), DiscussionTopicDto.class);

        assertEquals(1, embeddedJSONObjects.length());
        assertEquals(resultObject.getTitle(), discussionTopic1.getTitle());
        assertEquals(resultObject.getId(), discussionTopic1.getId());
        assertEquals(resultObject.getDate(), discussionTopic1.getDate());
    }

    @Test
    @SneakyThrows
    void getDiscussionTopic() {
        initializeDiscussions();

        when(discussionTopicService.findById(discussionTopic1.getId())).thenReturn(discussionTopic1);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(),
                        new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(get(path)).andExpect(status().isOk()).andReturn();

        // test
        Mockito.verify(discussionTopicService, times(1)).findById(discussionTopic1.getId());

        EntityModel<DiscussionTopicDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(response.getContent().getDate(), discussionTopic1.getDate());
        assertEquals(response.getContent().getTitle(), discussionTopic1.getTitle());
        assertEquals(response.getContent().getStatus(), discussionTopic1.getStatus());
    }

    @Test
    @SneakyThrows
    void getDiscussionTopicAndFail() {
        initializeDiscussions();

        doThrow(new NoSuchElementException()).when(discussionTopicService)
                .checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(discussionTopic2.getId(), implementation1.getId());

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic2.getId(),
                        new ListParameters(pageable, null)));

        // call
        mockMvc.perform(get(path)).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createDiscussionTopic() {
        initializeDiscussions();

        when(discussionTopicService.create(any())).thenReturn(discussionTopic2);

        discussionTopic2Dto.setId(null);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .createDiscussionTopicOfImplementation(algorithm2.getId(), implementation2.getId(), discussionTopic2Dto,
                        new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(post(path).content(mapper.writeValueAsString(discussionTopic2Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        // test
        Mockito.verify(discussionTopicService, times(1)).create(any());

        EntityModel<DiscussionTopicDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(response.getContent().getDate(), discussionTopic2.getDate());
        assertEquals(response.getContent().getTitle(), discussionTopic2.getTitle());
        assertEquals(response.getContent().getStatus(), discussionTopic2.getStatus());
    }

    @Test
    @SneakyThrows
    void createDiscussionTopicAndFail() {
        initializeDiscussions();

        discussionTopic2Dto.setDate(null);
        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .createDiscussionTopicOfImplementation(algorithm2.getId(), implementation2.getId(), discussionTopic2Dto,
                        new ListParameters(pageable, null)));

        // call
        mockMvc.perform(post(path).content(mapper.writeValueAsString(discussionTopic2Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateDiscussionTopic() {
        initializeDiscussions();

        discussionTopic1.setDescription("Test123");
        when(discussionTopicService.update(any())).thenReturn(discussionTopic1);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .updateDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(), discussionTopic1Dto,
                        new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(put(path).content(mapper.writeValueAsString(discussionTopic1Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        // test
        Mockito.verify(discussionTopicService, times(1)).update(any());

        EntityModel<DiscussionTopicDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(discussionTopic1Dto.getId(), response.getContent().getId());
        assertEquals(discussionTopic1Dto.getTitle(), response.getContent().getTitle());
        assertEquals(discussionTopic1Dto.getDate(), response.getContent().getDate());
        assertEquals(discussionTopic1Dto.getStatus(), response.getContent().getStatus());
    }

    @Test
    @SneakyThrows
    void updateDiscussionTopicAndFail() {
        initializeDiscussions();

        discussionTopic1Dto.setDate(null);
        discussionTopic1.setDate(null);
        when(discussionTopicService.update(any())).thenReturn(discussionTopic1);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .updateDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(), discussionTopic1Dto,
                        new ListParameters(pageable, null)));

        // call
        mockMvc.perform(put(path).content(mapper.writeValueAsString(discussionTopic1Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void deleteDiscussionTopic() {
        initializeDiscussions();

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .deleteDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(),
                        new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(delete(path)).andExpect(status().isOk()).andReturn();
    }

    @Test
    @SneakyThrows
    void deleteDiscussionTopicAndFail() {
        initializeDiscussions();

        doThrow(new NoSuchElementException()).when(discussionTopicService)
                .checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(discussionTopic2.getId(), implementation1.getId());

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .deleteDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic2.getId(),
                        new ListParameters(pageable, null)));

        // call
        mockMvc.perform(delete(path)).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getDiscussionComments() {
        initializeDiscussions();

        List<DiscussionComment> discussionCommentList = new ArrayList<>();
        discussionCommentList.add(discussionComment1);
        Page<DiscussionComment> discussionCommentPage = new PageImpl<>(discussionCommentList);

        when(discussionCommentService.findAllByTopic(discussionTopic1.getId(), pageable)).thenReturn(discussionCommentPage);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getDiscussionCommentsOfDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(),
                        new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(get(path)).andExpect(status().isOk()).andReturn();

        // test
        Mockito.verify(discussionCommentService, times(1)).findAllByTopic(discussionTopic1.getId(), pageable);


        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "discussionComments", DiscussionCommentDto.class);

        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getText(), discussionComment1Dto.getText());
        assertEquals(resultList.get(0).getId(), discussionComment1Dto.getId());
    }

    @Test
    @SneakyThrows
    void getDiscussionComment() {
        initializeDiscussions();

        when(discussionCommentService.findById(discussionComment1.getId())).thenReturn(discussionComment1);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getDiscussionCommentOfDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(),
                        discussionComment1.getId(), new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(get(path)).andExpect(status().isOk()).andReturn();

        // test
        Mockito.verify(discussionCommentService, times(1)).findById(discussionComment1.getId());

        EntityModel<DiscussionCommentDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(response.getContent().getId(), discussionComment1Dto.getId());
        assertEquals(response.getContent().getText(), discussionComment1Dto.getText());
        assertEquals(response.getContent().getDate(), discussionComment1Dto.getDate());
    }

    @Test
    @SneakyThrows
    void getDiscussionCommentAndFail() {
        initializeDiscussions();

        doThrow(new NoSuchElementException()).when(discussionCommentService)
                .checkIfDiscussionCommentIsInDiscussionTopic(discussionComment2.getId(), discussionTopic1.getId());

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getDiscussionCommentOfDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(),
                        discussionComment2.getId(), new ListParameters(pageable, null)));

        // call
        mockMvc.perform(get(path)).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createDiscussionComment() {
        initializeDiscussions();

        discussionComment2Dto.setId(null);
        when(discussionCommentService.create(any())).thenReturn(discussionComment2);
        when(discussionTopicService.findById(discussionTopic1.getId())).thenReturn(discussionTopic1);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .createDiscussionCommentOfDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(),
                        discussionComment2Dto, new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(post(path).content(mapper.writeValueAsString(discussionComment2Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andReturn();

        // test
        Mockito.verify(discussionCommentService, times(1)).create(any());
        Mockito.verify(discussionTopicService, times(1)).findById(discussionTopic1.getId());

        EntityModel<DiscussionCommentDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(response.getContent().getText(), discussionComment2Dto.getText());
    }

    @Test
    @SneakyThrows
    void createDiscussionCommentAndFail() {
        initializeDiscussions();

        discussionComment2Dto.setDate(null);
        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .createDiscussionCommentOfDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(),
                        discussionComment2Dto, new ListParameters(pageable, null)));

        // call
        mockMvc.perform(post(path).content(mapper.writeValueAsString(discussionComment2Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateDiscussionComment() {
        initializeDiscussions();

        discussionComment1Dto.setText("Test123");
        when(discussionCommentService.update(any())).thenReturn(discussionComment1);
        when(discussionCommentService.findById(discussionComment1.getId())).thenReturn(discussionComment1);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .updateDiscussionCommentOfDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(),
                        discussionComment1.getId(), discussionComment1Dto, new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(put(path).content(mapper.writeValueAsString(discussionComment1Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        // test
        Mockito.verify(discussionCommentService, times(1)).update(any());
        Mockito.verify(discussionCommentService, times(1)).findById(discussionComment1.getId());

        EntityModel<DiscussionCommentDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(response.getContent().getText(), discussionComment1Dto.getText());
        assertEquals(response.getContent().getId(), discussionComment1Dto.getId());
    }

    @Test
    @SneakyThrows
    void updateDiscussionCommentAndFail() {
        initializeDiscussions();

        discussionComment1Dto.setDate(null);
        discussionComment1Dto.setText(null);
        discussionComment1.setDate(null);
        when(discussionCommentService.update(any())).thenReturn(discussionComment1);
        when(discussionCommentService.findById(discussionComment1.getId())).thenReturn(discussionComment1);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .updateDiscussionCommentOfDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(),
                        discussionComment1.getId(), discussionComment1Dto, new ListParameters(pageable, null)));

        // call
        mockMvc.perform(put(path).content(mapper.writeValueAsString(discussionComment1Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void deleteDiscussionComment() {
        initializeDiscussions();

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .deleteDiscussionCommentOfDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic1.getId(),
                        discussionComment1.getId(), new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(delete(path)).andExpect(status().isOk()).andReturn();
    }

    @Test
    @SneakyThrows
    void deleteDiscussionCommentAndFail() {
        initializeDiscussions();

        doThrow(new NoSuchElementException()).when(discussionCommentService)
                .checkIfDiscussionCommentIsInDiscussionTopic(discussionComment1.getId(), discussionTopic2.getId());

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .deleteDiscussionCommentOfDiscussionTopicOfImplementation(algorithm1.getId(), implementation1.getId(), discussionTopic2.getId(),
                        discussionComment1.getId(), new ListParameters(pageable, null)));

        // call
        mockMvc.perform(delete(path)).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void testCreateFileForImplementationPackage_returnOk() {
        // Given
        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        var implementationPackage = new FileImplementationPackage();
        implementationPackage.setName("implementation for Shor");
        implementationPackage.setId(UUID.randomUUID());

        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());

        byte[] testFile = new byte[20];
        final MockMultipartFile file = new MockMultipartFile("file", testFile);
        doReturn(new File()).when(implementationPackageService).addFileToImplementationPackage(implementationPackage.getId(), file);

        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .createFileForImplementationPackage(algo.getId(), impl.getId(), implementationPackage.getId(), file));

        // When
        ResultActions resultActions = mockMvc.perform(multipart(path).file(file));

        // Then
        resultActions.andExpect(status().isCreated());
        Mockito.verify(implementationPackageService, times(1)).addFileToImplementationPackage(implementationPackage.getId(), file);
    }

    @Test
    @SneakyThrows
    public void testGetFileOfImplementationPackage_response_OK() {
        // Given
        var implementationPackage = new FileImplementationPackage();
        implementationPackage.setName("implementation for Shor");
        implementationPackage.setId(UUID.randomUUID());

        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());

        when(implementationPackageService.findLinkedFile(implementationPackage.getId())).thenReturn(new File());

        // When
        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getFileOfImplementationPackage(algo.getId(), impl.getId(), implementationPackage.getId()));
        ResultActions result = mockMvc.perform(get(path).accept(MediaType.APPLICATION_JSON));


        // Then
        result.andExpect(status().isOk());
        Mockito.verify(implementationPackageService, times(1)).findLinkedFile(implementationPackage.getId());
    }

    @Test
    @SneakyThrows
    public void testGetFileOfImplementation_response_OK() {
        // Given
        var implementationPackage = new FileImplementationPackage();
        implementationPackage.setName("implementation for Shor");
        implementationPackage.setId(UUID.randomUUID());


        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());

        var file = new File();
        file.setId(UUID.randomUUID());
        file.setMimeType("img/png");
        implementationPackage.setFile(file);


        when(implementationPackageService.findLinkedFile(implementationPackage.getId())).thenReturn(file);

        // When
        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .getFileOfImplementationPackage(algo.getId(), impl.getId(), implementationPackage.getId()));

        ResultActions result = mockMvc.perform(get(path).accept(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.andReturn().getResponse().getContentAsString(),
                "file", File.class);
        assertEquals(0, resultList.size());

        Mockito.verify(implementationPackageService, times(1)).findLinkedFile(implementationPackage.getId());
    }

    @Test
    @SneakyThrows
    public void testDownloadFileContent_response_OK() {
        // Given
        var implementationPackage = new FileImplementationPackage();
        implementationPackage.setName("implementation for Shor");
        implementationPackage.setId(UUID.randomUUID());

        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());

        var file = new File();
        file.setId(UUID.randomUUID());
        file.setMimeType("img/png");

        when(implementationPackageService.findLinkedFile(implementationPackage.getId())).thenReturn(file);

        // When
        final String path = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .downloadFileContent(algo.getId(), impl.getId(), implementationPackage.getId()));

        ResultActions result = mockMvc.perform(get(path).accept(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk()).andReturn();
        Mockito.verify(implementationPackageService, times(1)).findLinkedFile(implementationPackage.getId());
    }

    @Test
    @SneakyThrows
    public void testDeleteFile_response_file_not_found() {
        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());

        var implementationPackage = new TOSCAImplementationPackage();
        implementationPackage.setId(UUID.randomUUID());

        var file = new File();
        file.setId(UUID.randomUUID());
        file.setMimeType("img/png");

        doNothing().when(fileService).delete(file.getId());

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .deleteFileOfImplementation(algo.getId(), impl.getId(), implementationPackage.getId()));
        mockMvc.perform(delete(url))
                .andExpect(status().isNotFound()).andReturn();

    }

    @Test
    @SneakyThrows
    public void testDeleteFile_response_no_content() {
        var impl = new Implementation();
        impl.setName("implementation for Shor");
        impl.setId(UUID.randomUUID());

        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());

        var implementationPackage = new TOSCAImplementationPackage();
        implementationPackage.setId(UUID.randomUUID());

        var file = new File();
        file.setId(UUID.randomUUID());
        file.setMimeType("img/png");

        doNothing().when(fileService).delete(file.getId());
        when(implementationPackageService.findLinkedFile(implementationPackage.getId())).thenReturn(file);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationController.class)
                .deleteFileOfImplementation(algo.getId(), impl.getId(), implementationPackage.getId()));
        mockMvc.perform(delete(url))
                .andExpect(status().isNoContent()).andReturn();

    }

}
