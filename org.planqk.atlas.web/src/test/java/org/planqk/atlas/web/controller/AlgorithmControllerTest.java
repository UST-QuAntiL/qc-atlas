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

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.Image;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.Sketch;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ComputeResourcePropertyTypeService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.SketchService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyTypeDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.PatternRelationTypeDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.SketchDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AlgorithmController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class AlgorithmControllerTest {

    private final int page = 0;

    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AlgorithmService algorithmService;

    @MockBean
    private ComputeResourcePropertyService computeResourcePropertyService;

    @MockBean
    private ComputeResourcePropertyTypeService computeResourcePropertyTypeService;

    @MockBean
    private ImplementationService implementationService;

    @MockBean
    private TagService tagService;

    @MockBean
    private LinkingService linkingService;

    @MockBean
    private PatternRelationService patternRelationService;

    @MockBean
    private ProblemTypeService problemTypeService;

    @MockBean
    private ApplicationAreaService applicationAreaService;

    @MockBean
    private SketchService sketchService;

    @MockBean
    private PublicationService publicationService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private Algorithm algorithm1;

    private Algorithm algorithm2;

    private AlgorithmRelation algorithmRelation1;

    private AlgorithmRelationDto algorithmRelation1Dto;

    private AlgorithmDto algorithm1Dto;

    private AlgorithmDto algorithm2Dto;

    private Set<AlgorithmRelation> algorithmRelations;

    private Set<PatternRelation> patternRelations;

    private void initializeAlgorithms() {
        Set<ProblemType> problemTypes = new HashSet<>();

        ProblemType type1 = new ProblemType();
        type1.setId(UUID.randomUUID());
        type1.setName("ProblemType1");
        problemTypes.add(type1);

        AlgorithmRelationType relType1 = new AlgorithmRelationType();
        relType1.setName("RelationType1");

        algorithm1 = new ClassicAlgorithm();
        algorithm1.setId(UUID.randomUUID());
        algorithm1.setName("alg1");
        algorithm1.setComputationModel(ComputationModel.CLASSIC);

        algorithm2 = new ClassicAlgorithm();
        algorithm2.setId(UUID.randomUUID());
        algorithm2.setName("alg2");
        algorithm2.setComputationModel(ComputationModel.CLASSIC);

        algorithmRelation1 = new AlgorithmRelation();
        algorithmRelation1.setId(UUID.randomUUID());
        algorithmRelation1.setSourceAlgorithm(algorithm1);
        algorithmRelation1.setTargetAlgorithm(algorithm2);
        algorithmRelation1.setAlgorithmRelationType(relType1);
        AlgorithmRelation algorithmRelation2 = new AlgorithmRelation();
        algorithmRelation2.setId(UUID.randomUUID());
        algorithmRelation2.setSourceAlgorithm(algorithm1);
        algorithmRelation2.setTargetAlgorithm(algorithm2);
        algorithmRelation2.setAlgorithmRelationType(relType1);
        algorithmRelations = new HashSet<>();
        algorithmRelations.add(algorithmRelation1);
        algorithmRelations.add(algorithmRelation2);

        PatternRelationType patternType1 = new PatternRelationType();
        patternType1.setId(UUID.randomUUID());
        patternType1.setName("Type1");
        PatternRelationType patternType2 = new PatternRelationType();
        patternType2.setId(UUID.randomUUID());
        patternType2.setName("Type2");

        PatternRelation patternRelation1 = new PatternRelation();
        patternRelation1.setId(UUID.randomUUID());
        patternRelation1.setDescription("Description1");
        patternRelation1.setAlgorithm(algorithm1);
        patternRelation1.setPatternRelationType(patternType1);
        PatternRelation patternRelation2 = new PatternRelation();
        patternRelation2.setId(UUID.randomUUID());
        patternRelation2.setDescription("Description2");
        patternRelation2.setAlgorithm(algorithm1);
        patternRelation2.setPatternRelationType(patternType2);
        patternRelations = new HashSet<>();
        patternRelations.add(patternRelation1);
        patternRelations.add(patternRelation2);
        algorithm1.setRelatedPatterns(patternRelations);

        algorithmRelations.forEach(algorithmRelation -> algorithm1.addAlgorithmRelation(algorithmRelation));
        algorithm1.setProblemTypes(problemTypes);

        algorithm2.setProblemTypes(problemTypes);

        // Generate DTOs from above Entities
        algorithm1Dto = ModelMapperUtils.convert(algorithm1, AlgorithmDto.class);
        algorithm2Dto = ModelMapperUtils.convert(algorithm2, AlgorithmDto.class);

        algorithmRelation1Dto = ModelMapperUtils.convert(algorithmRelation1, AlgorithmRelationDto.class);

//    	when(algorithmService.findById(any(UUID.class))).thenReturn(Optional.empty());
        when(algorithmService.findById(algorithm1.getId())).thenReturn(algorithm1);
        when(algorithmService.findById(algorithm2.getId())).thenReturn(algorithm2);
    }

    @Test
    @SneakyThrows
    void equality() {
        initializeAlgorithms();
        assertEquals(algorithm1, algorithm1);
        assertEquals(algorithm1, ModelMapperUtils.convert(mapper.readValue(
                mapper.writeValueAsString(algorithm1Dto), AlgorithmDto.class), Algorithm.class));
    }

    @Test
    @SneakyThrows
    void getAlgorithms_EmptyList_returnOk() {
        initializeAlgorithms();

        doReturn(Page.empty()).when(algorithmService).findAll(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getAlgorithms(new ListParameters(pageable, null)));

        MvcResult result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algorithms", AlgorithmDto.class);
        assertEquals(0, resultList.size());
    }

    @Test
    @SneakyThrows
    void getAlgorithms_TwoElements_returnOk() {
        initializeAlgorithms();
        List<Algorithm> algorithmList = new ArrayList<>();
        algorithmList.add(algorithm1);
        algorithmList.add(algorithm2);

        Page<Algorithm> pageAlg = new PageImpl<>(algorithmList);
        Page<AlgorithmDto> pageAlgDto = ModelMapperUtils.convertPage(pageAlg, AlgorithmDto.class);

        doReturn(pageAlg).when(algorithmService).findAll(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getAlgorithms(new ListParameters(pageable, null)));

        MvcResult result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algorithms", AlgorithmDto.class);
        assertEquals(2, resultList.size());
    }

    @Test
    @SneakyThrows
    void createAlgorithm_returnBadRequest() {
        AlgorithmDto algoDto = new AlgorithmDto();
        algoDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createAlgorithm(null));

        mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(algoDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        algoDto.setName("algoDto");

        mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(algoDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createAlgorithm_returnCreated() {
        initializeAlgorithms();
        algorithm1Dto.setId(null);

        doReturn(algorithm1).when(algorithmService).create(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createAlgorithm(null));

        MvcResult result = mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(algorithm1Dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        EntityModel<AlgorithmDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(response.getContent().getName(), this.algorithm1Dto.getName());
    }

    @Test
    @SneakyThrows
    void updateAlgorithm_returnBadRequest() {
        initializeAlgorithms();
        AlgorithmDto algoDto = new AlgorithmDto();
        algoDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .updateAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(put(url).content(mapper.writeValueAsString(algoDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateAlgorithm_returnOk() {
        initializeAlgorithms();

        doReturn(algorithm1).when(algorithmService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .updateAlgorithm(UUID.randomUUID(), null));

        MvcResult result = mockMvc.perform(put(url)
                .content(mapper.writeValueAsString(algorithm1Dto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<AlgorithmDto>>() {
                });
        assertEquals(response.getContent().getName(), algorithm1Dto.getName());
    }

    @Test
    @SneakyThrows
    void deleteAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(algorithmService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .deleteAlgorithm(UUID.randomUUID()));

        mockMvc.perform(delete(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteAlgorithm_returnNoContent() {
        doNothing().when(algorithmService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .deleteAlgorithm(UUID.randomUUID()));
        mockMvc.perform(delete(url))
                .andExpect(status().isNoContent()).andReturn();
    }

    @Test
    @SneakyThrows
    void getAlgorithm_returnNotFound() {
        initializeAlgorithms();

        doThrow(NoSuchElementException.class).when(algorithmService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getAlgorithm(UUID.randomUUID()));

        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getAlgorithm_returnOk() {
        initializeAlgorithms();

        doReturn(algorithm1).when(algorithmService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getAlgorithm(UUID.randomUUID()));
        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(response.getContent().getId(), algorithm1Dto.getId());
    }

    @Test
    @SneakyThrows
    void getTagsOfAlgorithm_returnOk() {
        Algorithm algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("Algorithm");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        var inputList = new HashSet<Tag>();
        for (int i = 0; i < 10; i++) {
            var element = new Tag();
            element.setValue("Test Element " + i);
            element.setCategory("category");
            inputList.add(element);
        }

        algorithm.setTags(inputList);

        doReturn(algorithm).when(algorithmService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .addTagToAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void addTagToAlgorithm_returnNoContent() {
        Tag tag = new Tag();
        tag.setCategory("category");
        tag.setValue("value");
        TagDto tagDto = ModelMapperUtils.convert(tag, TagDto.class);

        doNothing().when(tagService).addTagToAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .addTagToAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(post(url).content(mapper.writeValueAsString(tagDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void addTagToAlgorithm_returnBadRequest() {
        TagDto tagDto = new TagDto();

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .addTagToAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(post(url).content(mapper.writeValueAsString(tagDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void addTagToAlgorithm_returnNotFound() {
        Tag tag = new Tag();
        tag.setCategory("category");
        tag.setValue("value");
        TagDto tagDto = ModelMapperUtils.convert(tag, TagDto.class);

        doThrow(NoSuchElementException.class).when(tagService).addTagToAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .addTagToAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(post(url).content(mapper.writeValueAsString(tagDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void removeTagFromAlgorithm_returnNoContent() {
        Tag tag = new Tag();
        tag.setCategory("category");
        tag.setValue("value");
        TagDto tagDto = ModelMapperUtils.convert(tag, TagDto.class);

        doNothing().when(tagService).removeTagFromAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .removeTagFromAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(delete(url).content(mapper.writeValueAsString(tagDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void removeTagFromAlgorithm_returnBadRequest() {
        TagDto tagDto = new TagDto();

        doNothing().when(tagService).removeTagFromAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .removeTagFromAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(delete(url).content(mapper.writeValueAsString(tagDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void removeTagFromAlgorithm_returnNotFound() {
        TagDto tagDto = new TagDto();
        tagDto.setCategory("category");
        tagDto.setValue("value");

        doThrow(NoSuchElementException.class).when(tagService).removeTagFromAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .removeTagFromAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(delete(url).content(mapper.writeValueAsString(tagDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }


    @Test
    @SneakyThrows
    void getPublicationsOfAlgorithm_EmptyList_returnOk() {
        doReturn(new PageImpl<>(List.of())).when(algorithmService).findLinkedPublications(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getPublicationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.publications").doesNotExist())
                ;
    }

    @Test
    @SneakyThrows
    void getPublicationsOfAlgorithm_SingleElement_returnOk() {
        var pub = new Publication();
        pub.setTitle("test");
        pub.setAuthors(List.of("test"));
        pub.setId(UUID.randomUUID());

        doReturn(new PageImpl<>(List.of(pub))).when(algorithmService).findLinkedPublications(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getPublicationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.publications").isArray())
                .andExpect(jsonPath("$._embedded.publications[0].id").value(pub.getId().toString()))
                ;
    }

    @Test
    @SneakyThrows
    void getPublicationsOfAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(algorithmService).findLinkedPublications(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getPublicationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getPublicationOfAlgorithm_returnOk() {
        var pub = new Publication();
        pub.setTitle("test");
        pub.setAuthors(List.of("test"));
        pub.setId(UUID.randomUUID());

        doReturn(pub).when(publicationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getPublicationOfAlgorithm(UUID.randomUUID(), pub.getId()));
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
    void getPublicationOfAlgorithm_returnNotFound() {
        doNothing().when(algorithmService).checkIfPublicationIsLinkedToAlgorithm(any(), any());
        doThrow(NoSuchElementException.class).when(publicationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getPublicationOfAlgorithm(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndPublication_returnNoContent() {
        doNothing().when(linkingService).linkAlgorithmAndPublication(any(), any());

        var pubDto = new PublicationDto();
        pubDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndPublication(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(pubDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndPublication_returnBadRequest() {
        var pubDto = new PublicationDto();
        pubDto.setId(null);

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndPublication(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(pubDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndPublication_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).linkAlgorithmAndPublication(any(), any());

        var pubDto = new PublicationDto();
        pubDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndPublication(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(pubDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndPublication_UnknownPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).linkAlgorithmAndPublication(any(), any());

        var pubDto = new PublicationDto();
        pubDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndPublication(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(pubDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndPublication_returnNoContent() {
        doNothing().when(linkingService).unlinkAlgorithmAndPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndPublication(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndPublication_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndPublication(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndPublication_UnknownPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndPublication(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getProblemTypesOfAlgorithm_EmptyList_returnOk() {
        doReturn(new PageImpl<>(List.of())).when(algorithmService).findLinkedProblemTypes(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getProblemTypesOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.ProblemTypes").doesNotExist())
                ;
    }

    @Test
    @SneakyThrows
    void getProblemTypesOfAlgorithm_SingleElement_returnOk() {
        var problemType = new ProblemType();
        problemType.setName("test");
        problemType.setId(UUID.randomUUID());

        doReturn(new PageImpl<>(List.of(problemType))).when(algorithmService).findLinkedProblemTypes(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getProblemTypesOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.problemTypes").isArray())
                .andExpect(jsonPath("$._embedded.problemTypes[0].id").value(problemType.getId().toString()))
                ;
    }

    @Test
    @SneakyThrows
    void getProblemTypesOfAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(algorithmService).findLinkedProblemTypes(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getProblemTypesOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getProblemTypeOfAlgorithm_returnOk() {
        var problemType = new ProblemType();
        problemType.setName("test");
        problemType.setId(UUID.randomUUID());

        doReturn(problemType).when(problemTypeService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getProblemTypeOfAlgorithm(UUID.randomUUID(), problemType.getId()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(problemType.getName()))
                .andExpect(jsonPath("$.id").value(problemType.getId().toString()))
                ;
    }

    @Test
    @SneakyThrows
    void getProblemTypeOfAlgorithm_returnNotFound() {
        doNothing().when(algorithmService).checkIfProblemTypeIsLinkedToAlgorithm(any(), any());
        doThrow(NoSuchElementException.class).when(problemTypeService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getProblemTypeOfAlgorithm(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndProblemType_returnNoContent() {
        doNothing().when(linkingService).linkAlgorithmAndProblemType(any(), any());

        var problemTypeDto = new ProblemTypeDto();
        problemTypeDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndProblemType(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(problemTypeDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndProblemType_returnBadRequest() {
        var problemTypeDto = new ProblemTypeDto();
        problemTypeDto.setId(null);

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndProblemType(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(problemTypeDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndProblemType_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).linkAlgorithmAndProblemType(any(), any());

        var problemTypeDto = new ProblemTypeDto();
        problemTypeDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndProblemType(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(problemTypeDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndProblemType_UnknownProblemType_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).linkAlgorithmAndProblemType(any(), any());

        var problemTypeDto = new ProblemTypeDto();
        problemTypeDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndProblemType(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(problemTypeDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndProblemType_returnNoContent() {
        doNothing().when(linkingService).unlinkAlgorithmAndProblemType(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndProblemType(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndProblemType_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndProblemType(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndProblemType(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndProblemType_UnknownProblemType_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndProblemType(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndProblemType(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getApplicationAreasOfAlgorithm_EmptyList_returnOk() {
        doReturn(new PageImpl<>(List.of())).when(algorithmService).findLinkedApplicationAreas(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getApplicationAreasOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.applicationAreas").doesNotExist())
                ;
    }

    @Test
    @SneakyThrows
    void getApplicationAreasOfAlgorithm_SingleElement_returnOk() {
        var applicationArea = new ApplicationArea();
        applicationArea.setName("test");
        applicationArea.setId(UUID.randomUUID());

        doReturn(new PageImpl<>(List.of(applicationArea))).when(algorithmService).findLinkedApplicationAreas(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getApplicationAreasOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.applicationAreas").isArray())
                .andExpect(jsonPath("$._embedded.applicationAreas[0].id").value(applicationArea.getId().toString()))
                ;
    }

    @Test
    @SneakyThrows
    void getApplicationAreasOfAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(algorithmService).findLinkedApplicationAreas(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getApplicationAreasOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getApplicationAreaOfAlgorithm_returnOk() {
        var applicationArea = new ApplicationArea();
        applicationArea.setName("test");
        applicationArea.setId(UUID.randomUUID());

        doReturn(applicationArea).when(applicationAreaService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getApplicationAreaOfAlgorithm(UUID.randomUUID(), applicationArea.getId()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(applicationArea.getName()))
                .andExpect(jsonPath("$.id").value(applicationArea.getId().toString()))
                ;
    }

    @Test
    @SneakyThrows
    void getApplicationAreaOfAlgorithm_returnNotFound() {
        doNothing().when(algorithmService).checkIfApplicationAreaIsLinkedToAlgorithm(any(), any());
        doThrow(NoSuchElementException.class).when(applicationAreaService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getApplicationAreaOfAlgorithm(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndApplicationArea_returnNoContent() {
        doNothing().when(linkingService).linkAlgorithmAndApplicationArea(any(), any());

        var applicationAreaDto = new ApplicationAreaDto();
        applicationAreaDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndApplicationArea(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(applicationAreaDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndApplicationArea_returnBadRequest() {
        var applicationAreaDto = new ApplicationAreaDto();
        applicationAreaDto.setId(null);

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndApplicationArea(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(applicationAreaDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndApplicationArea_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).linkAlgorithmAndApplicationArea(any(), any());

        var applicationAreaDto = new ApplicationAreaDto();
        applicationAreaDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndApplicationArea(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(applicationAreaDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkAlgorithmAndApplicationArea_UnknownApplicationArea_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).linkAlgorithmAndApplicationArea(any(), any());

        var applicationAreaDto = new ApplicationAreaDto();
        applicationAreaDto.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .linkAlgorithmAndApplicationArea(UUID.randomUUID(), null));
        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(applicationAreaDto)).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndApplicationArea_returnNoContent() {
        doNothing().when(linkingService).unlinkAlgorithmAndApplicationArea(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndApplicationArea(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndApplicationArea_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndApplicationArea(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndApplicationArea(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndApplicationArea_UnknownApplicationArea_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndApplicationArea(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndApplicationArea(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getImplementationsOfAlgorithm_EmptyList_returnOk() {
        doReturn(Page.empty()).when(implementationService).findByImplementedAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getImplementationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getImplementationsOfAlgorithm_WithElements_returnOk() {
        var inputList = new ArrayList<Implementation>();
        for (int i = 0; i < 10; i++) {
            var element = new Implementation();
            element.setName("Test Element " + i);
            element.setId(UUID.randomUUID());
            inputList.add(element);
        }

        doReturn(new PageImpl<>(inputList)).when(implementationService).findByImplementedAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getImplementationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));

        var mvcResult = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var dtoElements = ObjectMapperUtils.mapResponseToList(
                mvcResult.getResponse().getContentAsString(),
                "implementations",
                Implementation.class
        );
        assertThat(dtoElements.size()).isEqualTo(inputList.size());
        // Ensure every element in the input array also exists in the output array.
        inputList.forEach(e -> {
            assertThat(dtoElements.stream().filter(dtoElem -> e.getId().equals(dtoElem.getId())).count()).isEqualTo(1);
        });
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertiesOfAlgorithm_EmptyList_returnOk() {
        doReturn(new PageImpl<>(List.of())).when(computeResourcePropertyService)
                .findComputeResourcePropertiesOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getComputeResourcePropertiesOfAlgorithm( UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.computeResourceProperties").doesNotExist())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertiesOfAlgorithm_SingleElement_returnOk() {
        var type = new ComputeResourcePropertyType();
        type.setName("test");
        type.setId(UUID.randomUUID());
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var res = new ComputeResourceProperty();
        res.setComputeResourcePropertyType(type);
        res.setValue("1.3");
        res.setId(UUID.randomUUID());

        doReturn(new PageImpl<>(List.of(res))).when(computeResourcePropertyService)
                .findComputeResourcePropertiesOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getComputeResourcePropertiesOfAlgorithm( UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.computeResourceProperties[0].id").value(res.getId().toString()))
                .andExpect(jsonPath("$._embedded.computeResourceProperties[0].type.id").value(type.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertiesOfAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService)
                .findComputeResourcePropertiesOfAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getComputeResourcePropertiesOfAlgorithm( UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getComputeResourcePropertyOfAlgorithm_SingleElement_returnOk() {
        var type = new ComputeResourcePropertyType();
        type.setName("test");
        type.setId(UUID.randomUUID());
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        var res = new ComputeResourceProperty();
        res.setComputeResourcePropertyType(type);
        res.setValue("1.3");
        res.setId(UUID.randomUUID());

        doReturn(res).when(computeResourcePropertyService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getComputeResourcePropertyOfAlgorithm( UUID.randomUUID(), res.getId()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(res.getId().toString()))
                .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
                .andExpect(status().isOk());
    }



    @Test
    @SneakyThrows
    void getComputeResourcePropertyOfAlgorithm_UnknownProperty_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getComputeResourcePropertyOfAlgorithm( UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteComputeResourcePropertyOfAlgorithm_returnNoContent() {
        doNothing().when(computeResourcePropertyService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .deleteComputeResourcePropertyOfAlgorithm( UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deleteComputeResourcePropertyOfAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .deleteComputeResourcePropertyOfAlgorithm( UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createComputeResourcePropertyForAlgorithm_returnCreated() {
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

        doReturn(res).when(computeResourcePropertyService).addComputeResourcePropertyToAlgorithm(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm( UUID.randomUUID(), null));
        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(res.getId().toString()))
                .andExpect(jsonPath("$.type.id").value(type.getId().toString()));
    }

    @Test
    @SneakyThrows
    void createComputeResourcePropertyForAlgorithm_returnBadRequest() {
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("test");
        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm( UUID.randomUUID(), null));
        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createComputeResourcePropertyForAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService)
                .addComputeResourcePropertyToAlgorithm(any(), any());

        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setId(UUID.randomUUID());
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("123");
        resDto.setType(typeDto);

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm( UUID.randomUUID(), null));
        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void updateComputeResourcePropertyOfAlgorithm_returnOk() {
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

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .updateComputeResourcePropertyOfAlgorithm( UUID.randomUUID(), res.getId(), null));
        mockMvc.perform(
                put(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(res.getId().toString()))
                .andExpect(jsonPath("$.type.id").value(type.getId().toString()));
    }

    @Test
    @SneakyThrows
    void updateComputeResourcePropertyOfAlgorithm_returnBadRequest() {
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("123");

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .updateComputeResourcePropertyOfAlgorithm( UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(
                put(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isBadRequest());
    }



    @Test
    @SneakyThrows
    void updateComputeResourcePropertyOfAlgorithm_UnknownProperty_returnNotFound() {
        doThrow(new NoSuchElementException()).when(computeResourcePropertyService).update(any());

        var typeDto = new ComputeResourcePropertyTypeDto();
        typeDto.setId(UUID.randomUUID());
        var resDto = new ComputeResourcePropertyDto();
        resDto.setValue("123");
        resDto.setType(typeDto);

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .updateComputeResourcePropertyOfAlgorithm(UUID.randomUUID(), UUID.randomUUID(), null));
        mockMvc.perform(
                put(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(resDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getPatternRelationsOfAlgorithm_TwoElements_returnOk() {
        initializeAlgorithms();

        doReturn(new PageImpl<>(new ArrayList<>(algorithm1.getRelatedPatterns())))
                .when(algorithmService).findLinkedPatternRelations(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getPatternRelationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));

        MvcResult result = mockMvc.perform(
                get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "patternRelations", PatternRelationDto.class);
        assertEquals(2, resultList.size());
    }

    @Test
    @SneakyThrows
    void getPatternRelationsOfAlgorithm_returnNotFound() {
        initializeAlgorithms();

        doThrow(NoSuchElementException.class).when(algorithmService).findLinkedPatternRelations(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getPatternRelationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(
                get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createPatternRelationForAlgorithm_returnCreated() {
        Algorithm algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("Algorithm");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        PatternRelationType type = new PatternRelationType();
        type.setName("PatternRelationType");
        type.setId(UUID.randomUUID());

        PatternRelation patternRelation = new PatternRelation();
        patternRelation.setPatternRelationType(type);
        patternRelation.setAlgorithm(algorithm);
        patternRelation.setPattern(new URI("http://www.example.com"));
        patternRelation.setDescription("description");

        PatternRelationDto patternRelationDto = ModelMapperUtils.convert(patternRelation, PatternRelationDto.class);

        doReturn(patternRelation).when(patternRelationService).create(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createPatternRelationForAlgorithm(algorithm.getId(), null));

        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(patternRelationDto))
        ).andExpect(jsonPath("$.id").isEmpty())
                .andExpect(jsonPath("$.algorithmId").value(algorithm.getId().toString()))
                .andExpect(jsonPath("$.patternRelationType.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.description").value(patternRelation.getDescription()))
                .andExpect(jsonPath("$.pattern").value(patternRelation.getPattern().toString()))
                .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    void createPatternRelationForAlgorithm_returnBadRequest() {
        Algorithm algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("Algorithm");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        PatternRelationType type = new PatternRelationType();
        type.setName("PatternRelationType");
        type.setId(UUID.randomUUID());

        PatternRelation patternRelation = new PatternRelation();
        patternRelation.setPatternRelationType(type);
        patternRelation.setAlgorithm(algorithm);
        patternRelation.setPattern(new URI("http://www.example.com"));
        patternRelation.setDescription("description");

        PatternRelationDto patternRelationDto = ModelMapperUtils.convert(patternRelation, PatternRelationDto.class);

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createPatternRelationForAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(patternRelationDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createPatternRelationForAlgorithm_returnNotFound() {
        UUID algorithmId = UUID.randomUUID();

        PatternRelationTypeDto type = new PatternRelationTypeDto();
        type.setName("PatternRelationType");
        type.setId(UUID.randomUUID());

        PatternRelationDto patternRelationDto = new PatternRelationDto();
        patternRelationDto.setPatternRelationType(type);
        patternRelationDto.setAlgorithmId(algorithmId);
        patternRelationDto.setPattern(new URI("http://www.example.com"));
        patternRelationDto.setDescription("description");

        doThrow(NoSuchElementException.class).when(patternRelationService).create(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createPatternRelationForAlgorithm(algorithmId, null));

        mockMvc.perform(post(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(patternRelationDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void updatePatternRelationOfAlgorithm_returnOk() {
        Algorithm algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("Algorithm");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        PatternRelationType type = new PatternRelationType();
        type.setName("PatternRelationType");
        type.setId(UUID.randomUUID());

        PatternRelation patternRelation = new PatternRelation();
        patternRelation.setId(UUID.randomUUID());
        patternRelation.setPatternRelationType(type);
        patternRelation.setAlgorithm(algorithm);
        patternRelation.setPattern(new URI("http://www.example.com"));
        patternRelation.setDescription("description");

        PatternRelationDto patternRelationDto = ModelMapperUtils.convert(patternRelation, PatternRelationDto.class);

        doReturn(patternRelation).when(patternRelationService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .updatePatternRelationOfAlgorithm(algorithm.getId(), patternRelation.getId(), null));

        mockMvc.perform(put(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(patternRelationDto))
        ).andExpect(jsonPath("$.id").value(patternRelation.getId().toString()))
                .andExpect(jsonPath("$.algorithmId").value(algorithm.getId().toString()))
                .andExpect(jsonPath("$.patternRelationType.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.description").value(patternRelation.getDescription()))
                .andExpect(jsonPath("$.pattern").value(patternRelation.getPattern().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void updatePatternRelationOfAlgorithm_returnBadRequest() {
        Algorithm algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("Algorithm");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        PatternRelationType type = new PatternRelationType();
        type.setName("PatternRelationType");
        type.setId(UUID.randomUUID());

        PatternRelation patternRelation = new PatternRelation();
        patternRelation.setId(UUID.randomUUID());
        patternRelation.setPatternRelationType(type);
        patternRelation.setAlgorithm(algorithm);
        patternRelation.setPattern(new URI("http://www.example.com"));
        patternRelation.setDescription("description");

        PatternRelationDto patternRelationDto = ModelMapperUtils.convert(patternRelation, PatternRelationDto.class);

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .updatePatternRelationOfAlgorithm(UUID.randomUUID(), patternRelation.getId(), null));

        mockMvc.perform(put(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(patternRelationDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updatePatternRelationOfAlgorithm_returnNotFound() {
        UUID algorithmId = UUID.randomUUID();

        PatternRelationTypeDto type = new PatternRelationTypeDto();
        type.setName("PatternRelationType");
        type.setId(UUID.randomUUID());

        PatternRelationDto patternRelationDto = new PatternRelationDto();
        patternRelationDto.setId(UUID.randomUUID());
        patternRelationDto.setPatternRelationType(type);
        patternRelationDto.setAlgorithmId(algorithmId);
        patternRelationDto.setPattern(new URI("http://www.example.com"));
        patternRelationDto.setDescription("description");

        doThrow(NoSuchElementException.class).when(patternRelationService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .updatePatternRelationOfAlgorithm(algorithmId, patternRelationDto.getId(), null));

        mockMvc.perform(put(url).accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(patternRelationDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deletePatternRelationOfAlgorithm_returnNoContent() {
        doNothing().when(patternRelationService).checkIfAlgorithmIsInPatternRelation(any(), any());
        doNothing().when(patternRelationService).delete(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .deletePatternRelationOfAlgorithm(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deletePatternRelationOfAlgorithm_returnNotFound() {
        doThrow(NoSuchElementException.class).when(patternRelationService).checkIfAlgorithmIsInPatternRelation(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .deletePatternRelationOfAlgorithm(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getPatternRelationOfAlgorithm_returnOk() {
        Algorithm algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("Algorithm");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        PatternRelationType type = new PatternRelationType();
        type.setName("PatternRelationType");
        type.setId(UUID.randomUUID());

        PatternRelation patternRelation = new PatternRelation();
        patternRelation.setId(UUID.randomUUID());
        patternRelation.setPatternRelationType(type);
        patternRelation.setAlgorithm(algorithm);
        patternRelation.setPattern(new URI("http://www.example.com"));
        patternRelation.setDescription("description");

        doNothing().when(patternRelationService).checkIfAlgorithmIsInPatternRelation(any(), any());
        doReturn(patternRelation).when(patternRelationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getPatternRelationOfAlgorithm(algorithm.getId(), patternRelation.getId()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(patternRelation.getId().toString()))
                .andExpect(jsonPath("$.algorithmId").value(algorithm.getId().toString()))
                .andExpect(jsonPath("$.patternRelationType.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.description").value(patternRelation.getDescription()))
                .andExpect(jsonPath("$.pattern").value(patternRelation.getPattern().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getPatternRelationOfAlgorithm_returnNotFound() {
        Algorithm algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("Algorithm");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        PatternRelationType type = new PatternRelationType();
        type.setName("PatternRelationType");
        type.setId(UUID.randomUUID());

        PatternRelation patternRelation = new PatternRelation();
        patternRelation.setId(UUID.randomUUID());
        patternRelation.setPatternRelationType(type);
        patternRelation.setAlgorithm(algorithm);
        patternRelation.setPattern(new URI("http://www.example.com"));
        patternRelation.setDescription("description");

        doNothing().when(patternRelationService).checkIfAlgorithmIsInPatternRelation(any(), any());
        doThrow(NoSuchElementException.class).when(patternRelationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getPatternRelationOfAlgorithm(algorithm.getId(), patternRelation.getId()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    //////////////////////////////////// Fixed up to this point /////////////////////////////////////////////////////

    @Test
    @SneakyThrows
    void uploadSketch() {

        // mock
        final UUID algorithmId = UUID.randomUUID();
        final UUID resultId = UUID.randomUUID();
        final String description = "test description";
        final String baseURL = "baseURL";
        byte[] testFile = new byte[20];
        final MockMultipartFile file = new MockMultipartFile("file", testFile);
        final Sketch sketch = new Sketch();
        sketch.setId(resultId);

        when(sketchService.addSketchToAlgorithm(algorithmId, file, description, baseURL)).thenReturn(sketch);

        final String path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .uploadSketch(algorithmId, null, description, baseURL));

        // call
        ResultActions resultActions = mockMvc.perform(multipart(path).file(file)).andExpect(status().isOk());

        // test
        Mockito.verify(sketchService, times(1)).addSketchToAlgorithm(algorithmId, file, description, baseURL);

        final String json = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(json).contains(sketch.getId().toString());
    }

    @Test
    @SneakyThrows
    void getSketches() {

        // mock
        final UUID algorithmId = UUID.randomUUID();

        final List<Sketch> sketches = new ArrayList<>();
        when(sketchService.findByAlgorithm(algorithmId)).thenReturn(sketches);

        final String path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getSketches(algorithmId));

        // call
        final ResultActions resultActions = mockMvc.perform(get(path)).andExpect(status().isOk());

        // test
        Mockito.verify(sketchService, times(1)).findByAlgorithm(algorithmId);

        final String json = resultActions.andReturn().getResponse().getContentAsString();
        final List<Sketch> sketchResult = mapper.readValue(json, List.class);
        assertEquals(0, sketchResult.size());
    }

    @Test
    @SneakyThrows
    void deleteSketch() {

        // mock
        final UUID algorithmId = UUID.randomUUID();
        final UUID sketchId = UUID.randomUUID();

        final String path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .deleteSketch(algorithmId, sketchId));

        // call
        mockMvc.perform(delete(path)).andExpect(status().isNoContent());

        // test
        Mockito.verify(sketchService, times(1)).delete(sketchId);
    }

    @Test
    @SneakyThrows
    void getSketch() {

        // mock
        final UUID algorithmId = UUID.randomUUID();
        final UUID sketchId = UUID.randomUUID();

        final Sketch sketch = new Sketch();
        sketch.setId(sketchId);
        when(sketchService.findById(sketchId)).thenReturn(sketch);

        final String url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getSketch(algorithmId, sketchId));

        // call
        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        // test
        Mockito.verify(sketchService, times(1)).findById(sketchId);

        EntityModel<SketchDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(response.getContent().getId(), sketch.getId());
    }

    @Test
    @SneakyThrows
    void updateSketch() {
        final UUID algorithmId = UUID.randomUUID();
        final UUID sketchId = UUID.randomUUID();

        final Sketch sketch = new Sketch();
        sketch.setId(sketchId);
        SketchDto sketchDto = ModelMapperUtils.convert(sketch, SketchDto.class);

        doReturn(sketch).when(sketchService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .updateSketch(algorithmId, sketchId, null));

        mockMvc.perform(put(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sketchDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sketch.getId().toString()));
    }

    @Test
    @SneakyThrows
    void getSketchImage() {

        // mock
        final UUID algorithmId = UUID.randomUUID();
        final UUID sketchId = UUID.randomUUID();

        final Sketch sketch = new Sketch();
        sketch.setId(sketchId);
        sketch.setImageURL(new URL("http://test/image/url"));

        Image image = new Image();
        image.setImage(new byte[20]);
        image.setMimeType("img/png");

        doReturn(image).when(sketchService).getImageBySketch(sketchId);

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getSketchImage(algorithmId, sketchId));

        // call
        var resultActions = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        byte[] responseImage = resultActions.getResponse().getContentAsByteArray();

        assertThat(image.getImage()).isEqualTo(responseImage);
    }
}
