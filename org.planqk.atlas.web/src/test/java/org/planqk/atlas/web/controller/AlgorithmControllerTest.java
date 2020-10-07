/********************************************************************************
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

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.exceptions.InvalidResourceTypeValueException;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.QuantumAlgorithm;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
                .andExpect(status().isOk()).andDo(print());
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
        ).andExpect(status().isNoContent()).andDo(print());
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
        ).andExpect(status().isBadRequest()).andDo(print());
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
        ).andExpect(status().isNotFound()).andDo(print());
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
        ).andExpect(status().isNoContent()).andDo(print());
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
        ).andExpect(status().isBadRequest()).andDo(print());
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
        ).andExpect(status().isNotFound()).andDo(print());
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
                .andDo(print());
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
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getPublicationsOfAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(algorithmService).findLinkedPublications(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getPublicationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
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
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getPublicationOfAlgorithm_returnNotFound() {
        doNothing().when(algorithmService).checkIfPublicationIsLinkedToAlgorithm(any(), any());
        doThrow(NoSuchElementException.class).when(publicationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getPublicationOfAlgorithm(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
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
                .andExpect(status().isNoContent()).andDo(print());
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
                .andExpect(status().isBadRequest()).andDo(print());
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
                .andExpect(status().isNotFound()).andDo(print());
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
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndPublication_returnNoContent() {
        doNothing().when(linkingService).unlinkAlgorithmAndPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndPublication(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndPublication_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndPublication(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndPublication_UnknownPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndPublication(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
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
                .andDo(print());
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
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getProblemTypesOfAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(algorithmService).findLinkedProblemTypes(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getProblemTypesOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
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
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getProblemTypeOfAlgorithm_returnNotFound() {
        doNothing().when(algorithmService).checkIfProblemTypeIsLinkedToAlgorithm(any(), any());
        doThrow(NoSuchElementException.class).when(problemTypeService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getProblemTypeOfAlgorithm(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
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
                .andExpect(status().isNoContent()).andDo(print());
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
                .andExpect(status().isBadRequest()).andDo(print());
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
                .andExpect(status().isNotFound()).andDo(print());
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
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndProblemType_returnNoContent() {
        doNothing().when(linkingService).unlinkAlgorithmAndProblemType(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndProblemType(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndProblemType_UnknownAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndProblemType(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndProblemType(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void unlinkAlgorithmAndProblemType_UnknownProblemType_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndProblemType(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .unlinkAlgorithmAndProblemType(UUID.randomUUID(), UUID.randomUUID()));
        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                .andExpect(status().isCreated()).andDo(print());
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
        ).andExpect(status().isBadRequest()).andDo(print());
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
        ).andExpect(status().isNotFound()).andDo(print());
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
                .andExpect(status().isOk()).andDo(print());
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
        ).andExpect(status().isBadRequest()).andDo(print());
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
        ).andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @SneakyThrows
    void deletePatternRelationOfAlgorithm_returnNoContent() {
        doNothing().when(patternRelationService).checkIfAlgorithmIsInPatternRelation(any(), any());
        doNothing().when(patternRelationService).delete(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .deletePatternRelationOfAlgorithm(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    @SneakyThrows
    void deletePatternRelationOfAlgorithm_returnNotFound() {
        doThrow(NoSuchElementException.class).when(patternRelationService).checkIfAlgorithmIsInPatternRelation(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .deletePatternRelationOfAlgorithm(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound()).andDo(print());
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
                .andExpect(status().isOk()).andDo(print());
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

        mockMvc.perform(get(url).accept(APPLICATION_JSON)).andExpect(status().isNotFound()).andDo(print());
    }

    //////////////////////////////////// Fixed up to this point /////////////////////////////////////////////////////

    @Test
    void testListComputingResources_ClassicAlgorithm() throws Exception {
        when(computeResourcePropertyService.findComputeResourcePropertiesOfAlgorithm(any(), any())).thenReturn(Page.empty());
        var path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getComputeResourcePropertiesOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(path)).andExpect(status().isOk());
    }

    @Test
    void testListComputingResources_ValidAlgo_NoResources() throws Exception {
        var algo = new QuantumAlgorithm();
        algo.setRequiredComputeResourceProperties(new HashSet<>());
        when(computeResourcePropertyService.findComputeResourcePropertiesOfAlgorithm(any(), any())).thenReturn(Page.empty());
        var path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getComputeResourcePropertiesOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        var result = mockMvc.perform(get(path)).andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(
                result.getResponse().getContentAsString(),
                "computingResourceDtoes",
                ComputeResourcePropertyDto.class
        );
        assertThat(resultList.size()).isEqualTo(0);
    }

    @Test
    void testListComputingResources_ValidAlgo_ResourcesIncluded() throws Exception {
        var type = new ComputeResourcePropertyType();
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        type.setName("test-type");
        type.setId(UUID.randomUUID());

        var algo = new QuantumAlgorithm();
        algo.setRequiredComputeResourceProperties(new HashSet<>());
        algo.setId(UUID.randomUUID());
        var resources = new ArrayList<ComputeResourceProperty>();

        for (int i = 0; i < 10; i++) {
            var resource = new ComputeResourceProperty();
            resource.setComputeResourcePropertyType(type);
            resource.setId(UUID.randomUUID());
            resources.add(resource);
            algo.getRequiredComputeResourceProperties().add(resource);
        }

        when(computeResourcePropertyService.findComputeResourcePropertiesOfAlgorithm(any(), any())).thenReturn(new PageImpl<>(resources));
        var path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getComputeResourcePropertiesOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault()));
        var result = mockMvc.perform(get(path)).andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(
                result.getResponse().getContentAsString(),
                "computeResourceProperties",
                ComputeResourcePropertyDto.class
        );
        assertThat(resultList.size()).isEqualTo(10);

        var presentCount = resultList.stream().filter(e -> resources.stream().anyMatch(b -> b.getId().equals(e.getId()))).count();
        assertThat(presentCount).isEqualTo(10);
    }

    @Test
    void testAddComputeResourceProperty_AlgoNotFound() throws Exception {
        when(computeResourcePropertyService.addComputeResourcePropertyToAlgorithm(any(), any()))
                .thenThrow(new NoSuchElementException());

        mockComputeResourceTypeValidation(ComputeResourcePropertyDataType.FLOAT);
        var path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm(UUID.randomUUID(), null));
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(getValidResourceInput(false))))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddComputeResourceProperty_ClassicAlgo() throws Exception {
        var algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("alg1");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        var resReq = getValidResourceInput(false);
        var type = new ComputeResourcePropertyType();
        type.setDatatype(resReq.getType().getDatatype());
        type.setDescription(resReq.getType().getDescription());
        type.setName(resReq.getType().getName());
        type.setId(resReq.getType().getId());

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(type);
        resource.setValue(resReq.getValue());
        resource.setId(resReq.getId());

        mockComputeResourceTypeValidation(ComputeResourcePropertyDataType.FLOAT);

        when(computeResourcePropertyService.addComputeResourcePropertyToAlgorithm(any(), any())).thenReturn(resource);

        var path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm(UUID.randomUUID(), null));
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(resReq)))
                .andExpect(status().isCreated());
    }

    @Test
    void testAddComputeResourceProperty_InvalidValue() throws Exception {

        var algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("alg1");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        var resReq = getValidResourceInput(false);
        resReq.setValue("Hallo Welt");

        var type = new ComputeResourcePropertyType();
        type.setDatatype(resReq.getType().getDatatype());
        type.setDescription(resReq.getType().getDescription());
        type.setName(resReq.getType().getName());
        type.setId(resReq.getType().getId());

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(type);
        resource.setValue(resReq.getValue());
        resource.setId(resReq.getId());

        when(computeResourcePropertyService.addComputeResourcePropertyToAlgorithm(any(), any()))
                .thenThrow((new InvalidResourceTypeValueException("")));

        var path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm(UUID.randomUUID(), null));
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(resReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddComputeResourceProperty_InvalidInput_NoType() throws Exception {
        var resource = new ComputeResourcePropertyDto();
        resource.setId(UUID.randomUUID());

        when(algorithmService.findById(any())).thenReturn(new ClassicAlgorithm());
        var path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(resource)))
                .andExpect(status().isBadRequest());
    }

    // TODO: We want to test this case
    private ComputeResourcePropertyDto getInvalidInputResource() {
        var type = new ComputeResourcePropertyTypeDto();
        type.setId(UUID.randomUUID());
        var resource = new ComputeResourcePropertyDto();
        resource.setType(type);
        resource.setId(UUID.randomUUID());
        return resource;
    }

    private ComputeResourcePropertyDto getValidResourceInput(boolean addResourceId) {
        var type = new ComputeResourcePropertyTypeDto();
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        type.setName("test-type");
        type.setId(UUID.randomUUID());
        var resource = new ComputeResourcePropertyDto();
        resource.setType(type);
        if (addResourceId) {
            resource.setId(UUID.randomUUID());
        }
        resource.setValue("10.0");
        return resource;
    }

    @Test
    void testAddComputeResourceProperty() throws Exception {

        Set<ProblemType> problemTypes = new HashSet<>();

        ProblemType type1 = new ProblemType();
        type1.setId(UUID.randomUUID());
        type1.setName("ProblemType1");
        problemTypes.add(type1);

        AlgorithmRelationType relType1 = new AlgorithmRelationType();
        relType1.setName("RelationType1");

        var algorithm1 = new QuantumAlgorithm();
        algorithm1.setId(UUID.randomUUID());
        algorithm1.setName("alg1");
        algorithm1.setComputationModel(ComputationModel.CLASSIC);

        var algorithm2 = new QuantumAlgorithm();
        algorithm2.setId(UUID.randomUUID());
        algorithm2.setName("alg2");
        algorithm2.setComputationModel(ComputationModel.CLASSIC);

        var algorithmRelation1 = new AlgorithmRelation();
        algorithmRelation1.setId(UUID.randomUUID());
        algorithmRelation1.setSourceAlgorithm(algorithm1);
        algorithmRelation1.setTargetAlgorithm(algorithm2);
        algorithmRelation1.setAlgorithmRelationType(relType1);
        AlgorithmRelation algorithmRelation2 = new AlgorithmRelation();
        algorithmRelation2.setId(UUID.randomUUID());
        algorithmRelation2.setSourceAlgorithm(algorithm1);
        algorithmRelation2.setTargetAlgorithm(algorithm2);
        algorithmRelation2.setAlgorithmRelationType(relType1);

        var resReq = getValidResourceInput(false);
        var type = new ComputeResourcePropertyType();
        type.setDatatype(resReq.getType().getDatatype());
        type.setDescription(resReq.getType().getDescription());
        type.setName(resReq.getType().getName());
        type.setId(resReq.getType().getId());

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(type);
        resource.setValue(resReq.getValue());
        resource.setId(resReq.getId());

        mockComputeResourceTypeValidation(ComputeResourcePropertyDataType.FLOAT);

        when(computeResourcePropertyService.addComputeResourcePropertyToAlgorithm(any(), any())).thenReturn(resource);

        var path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm(UUID.randomUUID(), null));
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(resReq)))
                .andExpect(status().isCreated());
    }

//    @Test
//    void deleteResourceProperty_returnNoContent() throws Exception {
//        doNothing().when(resourceService).delete(any());
//        var url = fromMethodCall(uriBuilder, on(ComputeResourcePropertyController.class)
//                .deleteComputeResourceProperty(UUID.randomUUID()));
//        mockMvc.perform(delete(url)).andExpect(status().isNoContent());
//    }
//
//    @Test
//    void deleteResourceProperty_returnNotFound() throws Exception {
//        doThrow(new NoSuchElementException()).when(resourceService).delete(any());
//        var url = fromMethodCall(uriBuilder, on(ComputeResourcePropertyController.class)
//                .deleteComputeResourceProperty(UUID.randomUUID()));
//        mockMvc.perform(delete(url)).andExpect(status().isNotFound());
//    }
//
//    @Test
//    void getResource_returnNotFound() throws Exception {
//        when(resourceService.findById(any())).thenThrow(new NoSuchElementException());
//        var url = fromMethodCall(uriBuilder, on(ComputeResourcePropertyController.class)
//                .getComputeResourceProperty(UUID.randomUUID()));
//        mockMvc.perform(get(url)).andExpect(status().isNotFound());
//    }
//
//    @Test
//    void getResource_returnOk() throws Exception {
//        var sampleType = new ComputeResourcePropertyType();
//        sampleType.setId(UUID.randomUUID());
//        sampleType.setName("Hello World");
//        sampleType.setDatatype(ComputeResourcePropertyDataType.FLOAT);
//        sampleType.setDescription("Test");
//        var sampleResource = new ComputeResourceProperty();
//        sampleResource.setId(UUID.randomUUID());
//        sampleResource.setComputeResourcePropertyType(sampleType);
//
//        when(resourceService.findById(any())).thenReturn(sampleResource);
//        var url = fromMethodCall(uriBuilder, on(ComputeResourcePropertyController.class)
//                .deleteComputeResourceProperty(UUID.randomUUID()));
//        var result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
//
//        var dto = mapper.readValue(
//                result.getResponse().getContentAsString(),
//                new TypeReference<EntityModel<ComputeResourceProperty>>() {
//                }
//        ).getContent();
//
//        assertThat(dto.getId()).isEqualTo(sampleResource.getId());
//    }

    private void mockComputeResourceTypeValidation(ComputeResourcePropertyDataType type) {
        var propertyType = new ComputeResourcePropertyType();
        propertyType.setDatatype(type);
        propertyType.setName("test");
        propertyType.setId(UUID.randomUUID());

        when(computeResourcePropertyTypeService.findById(any())).thenReturn(propertyType);
    }

    @Test
    void testUploadSketch() throws Exception {

        // mock
        final UUID algorithmId = UUID.randomUUID();
        final UUID resultId = UUID.randomUUID();
        final String description = "test description";
        final String baseURL = "baseURL";
        byte[] testFile = hexStringToByteArray("e04fd020ea3a6910a2d808002b30309d");
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
    void testGetSketches() throws Exception {

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
    void testDeleteSketch() throws Exception {

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
    void testGetSketch() throws Exception {

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

    //@Test
    void testGetSketchImage() throws Exception {

        // mock
        final UUID algorithmId = UUID.randomUUID();
        final UUID sketchId = UUID.randomUUID();

        final Sketch sketch = new Sketch();
        sketch.setId(sketchId);
        sketch.setImageURL(new URL("test/image/url"));
        when(sketchService.getImageBySketch(sketchId).getImage()).thenReturn(this.hexStringToByteArray(sketch.getImageURL().toString()));

        final String path = linkBuilderService.urlStringTo(methodOn(AlgorithmController.class)
                .getSketchImage(algorithmId, sketchId));

        // call
        final ResultActions resultActions = mockMvc.perform(get(path)).andExpect(status().isOk());

        // test
        Mockito.verify(sketchService, times(1)).getImageBySketch(sketchId);

        byte[] json = resultActions.andReturn().getResponse().getContentAsByteArray();
        assertTrue(Arrays.equals(this.hexStringToByteArray(sketch.getImageURL().toString()), json));
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
