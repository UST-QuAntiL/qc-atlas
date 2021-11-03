/*******************************************************************************
 * Copyright (c) 2020-2021 the qc-atlas contributors.
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

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ClassicImplementation;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.DiscussionComment;
import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.Status;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.DiscussionCommentService;
import org.planqk.atlas.core.services.DiscussionTopicService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ClassicAlgorithmDto;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.dtos.DiscussionTopicDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
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

import lombok.SneakyThrows;

@WebMvcTest(controllers = {PublicationController.class, DiscussionCommentController.class, DiscussionTopicController.class})
@ExtendWith({MockitoExtension.class})
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class PublicationControllerTest {

    private final int page = 0;

    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @MockBean
    private PublicationService publicationService;

    @MockBean
    private AlgorithmService algorithmService;

    @MockBean
    private ImplementationService implementationService;

    @MockBean
    private LinkingService linkingService;

    @MockBean
    private DiscussionTopicService discussionTopicService;

    @MockBean
    private DiscussionCommentService discussionCommentService;

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

    private Publication publication1;

    private Publication publication2;

    private void initializeDiscussions() {
        publication1 = new Publication();
        publication1.setId(UUID.randomUUID());
        publication1.setTitle("Pub1");
        publication1.setAuthors(List.of("A1"));
        publication1.setDoi("DOI1");

        publication2 = new Publication();
        publication2.setId(UUID.randomUUID());
        publication1.setTitle("Pub2");
        publication1.setAuthors(List.of("A2"));
        publication1.setDoi("DOI2");

        discussionTopic1 = new DiscussionTopic();
        discussionTopic1.setId(UUID.randomUUID());
        discussionTopic1.setTitle("DiscussionTopic1");
        discussionTopic1.setDate(OffsetDateTime.now());
        discussionTopic1.setDescription("Description1");
        discussionTopic1.setStatus(Status.OPEN);
        discussionTopic1.setKnowledgeArtifact(publication1);
        discussionTopic1Dto = ModelMapperUtils.convert(discussionTopic1, DiscussionTopicDto.class);
        discussionTopics = new HashSet<>();
        discussionTopics.add(discussionTopic1);
        publication1.setDiscussionTopics(discussionTopics);

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
        discussionTopic2.setKnowledgeArtifact(publication2);
        discussionTopic2Dto = ModelMapperUtils.convert(discussionTopic2, DiscussionTopicDto.class);

        discussionComment2 = new DiscussionComment();
        discussionComment2.setId(UUID.randomUUID());
        discussionComment2.setText("This is another comment");
        discussionComment2.setDiscussionTopic(discussionTopic2);
        discussionComment2.setDate(OffsetDateTime.now());
        discussionComment2Dto = ModelMapperUtils.convert(discussionComment2, DiscussionCommentDto.class);

        when(publicationService.findById(publication1.getId())).thenReturn(publication1);
        when(publicationService.findById(publication2.getId())).thenReturn(publication2);
    }

    @Test
    @SneakyThrows
    void getPublications_EmptyList_returnOk() {
        doReturn(new PageImpl<Publication>(List.of())).when(publicationService).findAll(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getPublications(ListParameters.getDefault()));
        MvcResult mvcResult = mockMvc
                .perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        Assert.assertEquals(ObjectMapperUtils.mapResponseToList(mvcResult, ProblemTypeDto.class).size(), 0);
    }

    @Test
    @SneakyThrows
    void getPublications_SingleElement_returnOk() {
        var publ = new Publication();
        publ.setId(UUID.randomUUID());
        publ.setAuthors(List.of("test", "test-2"));
        publ.setTitle("test");

        doReturn(new PageImpl<Publication>(List.of(publ))).when(publicationService).findAll(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getPublications(ListParameters.getDefault()));
        MvcResult mvcResult = mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        PublicationDto publicationDto = ObjectMapperUtils.mapResponseToList(mvcResult, PublicationDto.class).get(0);
        assertEquals(publicationDto.getId(), publ.getId());
        assertEquals(publicationDto.getTitle(), publ.getTitle());
        assertEquals(publicationDto.getAuthors(), publ.getAuthors());
    }

    @Test
    @SneakyThrows
    void getPublication_returnOk() {
        var publ = new Publication();
        publ.setId(UUID.randomUUID());
        publ.setAuthors(List.of("test", "test-2"));
        publ.setTitle("test");

        doReturn(publ).when(publicationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getPublication(publ.getId()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(publ.getId().toString()))
                .andExpect(jsonPath("$.title").value(publ.getTitle()))
                .andExpect(jsonPath("$.authors").isArray());
    }

    @Test
    @SneakyThrows
    void getPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getPublication(UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createPublication_returnCreated() {
        var publ = new Publication();
        publ.setAuthors(List.of("test", "test-2"));
        publ.setTitle("test");
        var publDto = ModelMapperUtils.convert(publ, PublicationDto.class);
        publ.setId(UUID.randomUUID());
        doReturn(publ).when(publicationService).create(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .createPublication(null));
        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(publDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(publ.getId().toString()));
    }

    @Test
    @SneakyThrows
    void createPublication_returnBadRequest() {
        var publ = new Publication();
        publ.setAuthors(List.of());
        publ.setTitle("test");
        var publDto = ModelMapperUtils.convert(publ, PublicationDto.class);
        publ.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .createPublication(null));
        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(publDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updatePublication_returnOk() {
        var publ = new Publication();
        publ.setAuthors(List.of("test", "test-2"));
        publ.setTitle("test");
        publ.setId(UUID.randomUUID());
        var publDto = ModelMapperUtils.convert(publ, PublicationDto.class);
        doReturn(publ).when(publicationService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .updatePublication(publ.getId(), null));
        mockMvc.perform(
                put(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(publDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(publ.getId().toString()));
    }

    @Test
    @SneakyThrows
    void updatePublication_returnBadRequest() {
        var publ = new Publication();
        publ.setAuthors(List.of());
        publ.setTitle("test");
        publ.setId(UUID.randomUUID());
        var publDto = ModelMapperUtils.convert(publ, PublicationDto.class);

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .updatePublication(publ.getId(), null));
        mockMvc.perform(
                put(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(publDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updatePublication_returnNotFound() {
        var publ = new Publication();
        publ.setAuthors(List.of("test", "test-2"));
        publ.setTitle("test");
        publ.setId(UUID.randomUUID());
        var publDto = ModelMapperUtils.convert(publ, PublicationDto.class);
        doThrow(new NoSuchElementException()).when(publicationService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .updatePublication(publ.getId(), null));
        mockMvc.perform(
                put(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(publDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deletePublication_returnNoContent() {
        doNothing().when(publicationService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .deletePublication(UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deletePublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .deletePublication(UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getAlgorithmsOfPublication_SingleElement_returnOk() {
        var algo = new ClassicAlgorithm();
        algo.setName("algo");
        algo.setId(UUID.randomUUID());
        algo.setComputationModel(ComputationModel.CLASSIC);

        doReturn(new PageImpl<>(List.of(algo))).when(publicationService).findLinkedAlgorithms(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getAlgorithmsOfPublication(UUID.randomUUID(), ListParameters.getDefault()));

        MvcResult mvcResult = mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        assertEquals(ObjectMapperUtils.mapResponseToList(mvcResult, ClassicAlgorithmDto.class).get(0).getId(), algo.getId());
    }

    @Test
    @SneakyThrows
    void getAlgorithmsOfPublication_EmptyList_returnOk() {
        doReturn(new PageImpl<>(List.of())).when(publicationService).findLinkedAlgorithms(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getAlgorithmsOfPublication(UUID.randomUUID(), ListParameters.getDefault()));

        MvcResult mvcResult = mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        Assert.assertEquals(ObjectMapperUtils.mapResponseToList(mvcResult, PublicationDto.class).size(), 0);
    }

    @Test
    @SneakyThrows
    void getAlgorithmsOfPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).checkIfAlgorithmIsLinkedToPublication(any(), any());
        doThrow(new NoSuchElementException()).when(publicationService).findLinkedAlgorithms(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getAlgorithmsOfPublication(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getAlgorithmOfPublication_returnOk() {
        Algorithm algo = new ClassicAlgorithm();
        algo.setName("algo");
        algo.setId(UUID.randomUUID());

        doNothing().when(publicationService).checkIfAlgorithmIsLinkedToPublication(any(), any());
        doReturn(algo).when(algorithmService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getAlgorithmOfPublication(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(algo.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getAlgorithmOfPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).checkIfAlgorithmIsLinkedToPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getAlgorithmOfPublication(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkPublicationAndAlgorithm_returnNoContent() {
        doNothing().when(linkingService).linkAlgorithmAndPublication(any(), any());
        AlgorithmDto algoDto = new ClassicAlgorithmDto();
        algoDto.setId(UUID.randomUUID());
        algoDto.setComputationModel(ComputationModel.QUANTUM);

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .linkPublicationAndAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(algoDto))
        ).andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void linkPublicationAndAlgorithm_returnBadRequest() {
        AlgorithmDto algoDto = new ClassicAlgorithmDto();
        algoDto.setId(null);
        algoDto.setComputationModel(ComputationModel.QUANTUM);

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .linkPublicationAndAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(algoDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void linkPublicationAndAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).linkAlgorithmAndPublication(any(), any());
        AlgorithmDto algoDto = new ClassicAlgorithmDto();
        algoDto.setId(UUID.randomUUID());
        algoDto.setComputationModel(ComputationModel.QUANTUM);

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .linkPublicationAndAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(algoDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkPublicationAndAlgorithm_returnNoContent() {
        doNothing().when(linkingService).unlinkImplementationAndPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .unlinkPublicationAndAlgorithm(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void unlinkPublicationAndAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .unlinkPublicationAndAlgorithm(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getImplementationsOfPublication_EmptyList_returnOk() {
        doReturn(new PageImpl<>(List.of())).when(publicationService).findLinkedImplementations(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getImplementationsOfPublication(UUID.randomUUID(), ListParameters.getDefault()));

        MvcResult mvcResult = mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        Assert.assertEquals(ObjectMapperUtils.mapResponseToList(mvcResult, PublicationDto.class).size(), 0);
    }

    @Test
    @SneakyThrows
    void getImplementationsOfPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).checkIfImplementationIsLinkedToPublication(any(), any());
        doThrow(new NoSuchElementException()).when(publicationService).findLinkedImplementations(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getImplementationsOfPublication(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getImplementationOfPublication_returnOk() {
        Implementation algo = new ClassicImplementation();
        algo.setName("algo");
        algo.setId(UUID.randomUUID());

        doNothing().when(publicationService).checkIfImplementationIsLinkedToPublication(any(), any());
        doReturn(algo).when(implementationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getImplementationOfPublication(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(algo.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getImplementationOfPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).checkIfImplementationIsLinkedToPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getImplementationOfPublication(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getDiscussionTopics() {
        initializeDiscussions();

        List<DiscussionTopic> discussionTopicList = new ArrayList<>();
        discussionTopicList.add(discussionTopic1);
        final Page<DiscussionTopic> discussionTopics = new PageImpl<DiscussionTopic>(discussionTopicList);
        when(discussionTopicService.findByKnowledgeArtifactId(publication1.getId(), pageable)).thenReturn(discussionTopics);

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getDiscussionTopicsOfPublication(publication1.getId(), new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(get(path)).andExpect(status().isOk()).andReturn();

        // test
        Mockito.verify(discussionTopicService, times(1)).findByKnowledgeArtifactId(publication1.getId(), pageable);

        DiscussionTopicDto discussionTopicDto =
                ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(), DiscussionTopicDto.class).get(0);

        assertEquals(discussionTopicDto.getTitle(), discussionTopic1.getTitle());
        assertEquals(discussionTopicDto.getId(), discussionTopic1.getId());
        assertEquals(discussionTopicDto.getDate(), discussionTopic1.getDate());
    }

    @Test
    @SneakyThrows
    void getDiscussionTopic() {
        initializeDiscussions();

        when(discussionTopicService.findById(discussionTopic1.getId())).thenReturn(discussionTopic1);

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(), new ListParameters(pageable, null)));

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
                .checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(discussionTopic2.getId(), publication1.getId());

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getDiscussionTopicOfPublication(publication1.getId(), discussionTopic2.getId(), new ListParameters(pageable, null)));

        // call
        mockMvc.perform(get(path)).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createDiscussionTopic() {
        initializeDiscussions();

        when(discussionTopicService.create(any())).thenReturn(discussionTopic2);

        discussionTopic2Dto.setId(null);

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .createDiscussionTopicOfPublication(publication2.getId(), discussionTopic2Dto, new ListParameters(pageable, null)));

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
        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .createDiscussionTopicOfPublication(publication2.getId(), discussionTopic2Dto, new ListParameters(pageable, null)));

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

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .updateDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(), discussionTopic1Dto,
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

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .updateDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(), discussionTopic1Dto,
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

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .deleteDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(), new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(delete(path)).andExpect(status().isOk()).andReturn();
    }

    @Test
    @SneakyThrows
    void deleteDiscussionTopicAndFail() {
        initializeDiscussions();

        doThrow(new NoSuchElementException()).when(discussionTopicService)
                .checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(discussionTopic2.getId(), publication1.getId());

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .deleteDiscussionTopicOfPublication(publication1.getId(), discussionTopic2.getId(), new ListParameters(pageable, null)));

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

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getDiscussionCommentsOfDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(),
                        new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(get(path)).andExpect(status().isOk()).andReturn();

        // test
        Mockito.verify(discussionCommentService, times(1)).findAllByTopic(discussionTopic1.getId(), pageable);

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                DiscussionCommentDto.class);

        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getText(), discussionComment1Dto.getText());
        assertEquals(resultList.get(0).getId(), discussionComment1Dto.getId());
    }

    @Test
    @SneakyThrows
    void getDiscussionComment() {
        initializeDiscussions();

        when(discussionCommentService.findById(discussionComment1.getId())).thenReturn(discussionComment1);

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getDiscussionCommentOfDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(), discussionComment1.getId(),
                        new ListParameters(pageable, null)));

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

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getDiscussionCommentOfDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(), discussionComment2.getId(),
                        new ListParameters(pageable, null)));

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

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .createDiscussionCommentOfDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(), discussionComment2Dto,
                        new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(post(path).content(mapper.writeValueAsString(discussionComment2Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isCreated()).andReturn();

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
        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .createDiscussionCommentOfDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(), discussionComment2Dto,
                        new ListParameters(pageable, null)));

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

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .updateDiscussionCommentOfDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(), discussionComment1.getId(),
                        discussionComment1Dto, new ListParameters(pageable, null)));

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

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .updateDiscussionCommentOfDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(), discussionComment1.getId(),
                        discussionComment1Dto, new ListParameters(pageable, null)));

        // call
        mockMvc.perform(put(path).content(mapper.writeValueAsString(discussionComment1Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void deleteDiscussionComment() {
        initializeDiscussions();

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .deleteDiscussionCommentOfDiscussionTopicOfPublication(publication1.getId(), discussionTopic1.getId(), discussionComment1.getId(),
                        new ListParameters(pageable, null)));

        // call
        final MvcResult result = mockMvc.perform(delete(path)).andExpect(status().isOk()).andReturn();
    }

    @Test
    @SneakyThrows
    void deleteDiscussionCommentAndFail() {
        initializeDiscussions();

        doThrow(new NoSuchElementException()).when(discussionCommentService)
                .checkIfDiscussionCommentIsInDiscussionTopic(discussionComment1.getId(), discussionTopic2.getId());

        final String path = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .deleteDiscussionCommentOfDiscussionTopicOfPublication(publication1.getId(), discussionTopic2.getId(), discussionComment1.getId(),
                        new ListParameters(pageable, null)));

        // call
        mockMvc.perform(delete(path)).andExpect(status().isNotFound());
    }
}
