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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.services.AlgoRelationService;
import org.planqk.atlas.core.services.AlgoRelationTypeService;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.PatternRelationTypeService;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.ComputeResourcePropertyMixin;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyTypeDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@WebMvcTest(value = AlgorithmController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ComputeResourcePropertyMixin.class})
})
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class AlgorithmControllerTest {

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);

    @MockBean
    private ApplicationAreaService applicationAreaService;
    @MockBean
    private AlgorithmService algorithmService;
    @MockBean
    private ComputeResourcePropertyService computeResourcePropertyService;
    @MockBean
    private PatternRelationService patternRelationService;
    @MockBean
    private PatternRelationTypeService patternRelationTypeService;
    @MockBean
    private ProblemTypeService problemTypeService;
    @MockBean
    private PublicationService publicationService;
    @MockBean
    private AlgoRelationService algoRelationService;
    @MockBean
    private AlgoRelationTypeService algoRelationTypeService;
    @MockBean
    private ImplementationService implementationService;
    @MockBean
    private LinkingService linkingService;
    @MockBean
    private TagService tagService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();
    private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/");

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

        AlgoRelationType relType1 = new AlgoRelationType();
        relType1.setName("RelationType1");

        algorithm1 = new ClassicAlgorithm();
        algorithm1.setId(UUID.randomUUID());
        algorithm1.setName("alg1");
        algorithm1.setComputationModel(ComputationModel.CLASSIC);

        algorithm2 = new ClassicAlgorithm();
        algorithm2.setId(UUID.randomUUID());
        algorithm2.setName("alg2");
        algorithm2.setComputationModel(ComputationModel.CLASSIC);
        algorithm2.setAlgorithmRelations(new HashSet<>());

        algorithmRelation1 = new AlgorithmRelation();
        algorithmRelation1.setId(UUID.randomUUID());
        algorithmRelation1.setSourceAlgorithm(algorithm1);
        algorithmRelation1.setTargetAlgorithm(algorithm2);
        algorithmRelation1.setAlgoRelationType(relType1);
        AlgorithmRelation algorithmRelation2 = new AlgorithmRelation();
        algorithmRelation2.setId(UUID.randomUUID());
        algorithmRelation2.setSourceAlgorithm(algorithm1);
        algorithmRelation2.setTargetAlgorithm(algorithm2);
        algorithmRelation2.setAlgoRelationType(relType1);
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

        algorithm1.setAlgorithmRelations(algorithmRelations);
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
    public void equality() throws Exception {
        initializeAlgorithms();
        assertEquals(algorithm1, algorithm1);
        assertEquals(algorithm1, ModelMapperUtils.convert(mapper.readValue(
                mapper.writeValueAsString(algorithm1Dto), AlgorithmDto.class), Algorithm.class));
    }

    @Test
    public void getAlgorithms_withEmptyAlgorithmList() throws Exception {
        initializeAlgorithms();
        when(algorithmService.findAll(pageable, null)).thenReturn(Page.empty());

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .getAlgorithms(null)).toUriString();
        MvcResult result = mockMvc.perform(get(url)
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algorithms", AlgorithmDto.class);
        assertEquals(0, resultList.size());
    }

    @Test
    public void getAlgorithms_withTwoAlgorithmList() throws Exception {
        initializeAlgorithms();
        List<Algorithm> algorithmList = new ArrayList<>();
        algorithmList.add(algorithm1);
        algorithmList.add(algorithm2);

        Page<Algorithm> pageAlg = new PageImpl<>(algorithmList);
        Page<AlgorithmDto> pageAlgDto = ModelMapperUtils.convertPage(pageAlg, AlgorithmDto.class);

        when(algorithmService.findAll(pageable, null)).thenReturn(pageAlg);

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .getAlgorithms(null)).toUriString();
        MvcResult result = mockMvc.perform(get(url)
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algorithms", AlgorithmDto.class);
        assertEquals(2, resultList.size());
    }

    @Test
    public void getAlgorithm_returnNotFound() throws Exception {
        initializeAlgorithms();
        when(algorithmService.findById(any())).thenThrow(new NoSuchElementException());

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .getAlgorithm(UUID.randomUUID())).toUriString();

        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAlgorithm_returnAlgorithm() throws Exception {
        initializeAlgorithms();
        when(algorithmService.findById(any())).thenReturn(algorithm1);

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .getAlgorithm(UUID.randomUUID())).toUriString();
        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(response.getContent().getId(), algorithm1Dto.getId());
    }

    @Test
    public void createAlgorithm_returnBadRequest() throws Exception {
        AlgorithmDto algoDto = new AlgorithmDto();
        algoDto.setId(UUID.randomUUID());

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .createAlgorithm(null)).toUriString();

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
    public void createAlgorithm_returnAlgorithm() throws Exception {
        initializeAlgorithms();
        when(algorithmService.save(any())).thenReturn(algorithm1);

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .createAlgorithm(null)).toUriString();

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
    public void updateAlgorithm_returnBadRequest() throws Exception {
        initializeAlgorithms();
        AlgorithmDto algoDto = new AlgorithmDto();
        algoDto.setId(UUID.randomUUID());

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .updateAlgorithm(UUID.randomUUID(), null)).toUriString();

        mockMvc.perform(put(url).content(mapper.writeValueAsString(algoDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateAlgorithm_returnAlgorithm() throws Exception {
        initializeAlgorithms();

        doReturn(algorithm1).when(algorithmService).update(any(), any());

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .updateAlgorithm(algorithm1.getId(), null)).toUriString();

        MvcResult result = mockMvc.perform(put(url)
                .content(mapper.writeValueAsString(algorithm1Dto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<AlgorithmDto>>() {
                });
        assertEquals(response.getContent().getName(), algorithm1Dto.getName());
    }

    @Test
    public void deleteAlgorithm_notFound() throws Exception {
        doThrow(new NoSuchElementException()).when(algorithmService).delete(any());

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .deleteAlgorithm(UUID.randomUUID())).toUriString();

        mockMvc.perform(delete(url))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteAlgorithm_returnOk() throws Exception {
        doNothing().when(algorithmService).delete(any());

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .deleteAlgorithm(UUID.randomUUID())).toUriString();
        mockMvc.perform(delete(url))
                .andExpect(status().isOk()).andReturn();
    }

//    @Test
//    public void getAlgorithmRelations_returnNotFound() throws Exception {
//        doThrow(new NoSuchElementException()).when(algorithmService).getAlgorithmRelations(any());
//
//        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
//                .getAlgorithmRelationsForAlgorithm(UUID.randomUUID())).toUriString();
//        mockMvc.perform(get(url))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void getAlgorithmRelations_withEmptyAlgorithmRelationList() throws Exception {
//        initializeAlgorithms();
//        when(algorithmService.getAlgorithmRelations(any())).thenReturn(new HashSet<>());
//
//        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
//                .getAlgorithmRelationsForAlgorithm(UUID.randomUUID())).toUriString();
//
//        MvcResult result = mockMvc
//                .perform(get(url))
//                .andExpect(status().isOk()).andReturn();
//
//        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
//                "algorithmRelationDtoes", AlgorithmRelationDto.class);
//        assertEquals(0, resultList.size());
//    }

//    @Test
//    public void getAlgorithmRelations_withTwoAlgorithmRelationList() throws Exception {
//        initializeAlgorithms();
//        when(algorithmService.getAlgorithmRelations(any())).thenReturn(algorithm1.getAlgorithmRelations());
//
//        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
//                .getAlgorithmRelationsForAlgorithm(UUID.randomUUID())).toUriString();
//
//        MvcResult result = mockMvc
//                .perform(get(url).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
//
//        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
//                "algorithmRelations", AlgorithmRelationDto.class);
//        assertEquals(2, resultList.size());
//    }
//
//    @Test
//    public void updateAlgorithmRelation_returnNotFound() throws Exception {
//        initializeAlgorithms();
//        when(algoRelationService.findById(any())).thenThrow(new NoSuchElementException());
//
//        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
//                .updateAlgorithmRelation(UUID.randomUUID(), UUID.randomUUID(), null)).toUriString();
//
//        mockMvc.perform(put(url).content(mapper.writeValueAsString(algorithmRelation1Dto))
//                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void addAlgorithmRelation_returnBadRequest() throws Exception {
//        initializeAlgorithms();
//
//        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
//                .addAlgorithmRelationReferenceToAlgorithm(UUID.randomUUID(), null)).toUriString();
//
//        mockMvc.perform(post(url).content(mapper.writeValueAsString(algorithmRelation1Dto))
//                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void updateAlgorithmRelation_returnAlgorithmRelation() throws Exception {
//        initializeAlgorithms();
//        when(algoRelationService.findById(any(UUID.class))).thenReturn(algorithmRelation1);
//        when(algoRelationService.save(any(AlgorithmRelation.class))).thenReturn(algorithmRelation1);
//
//        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
//                .updateAlgorithmRelation(algorithmRelation1.getSourceAlgorithm().getId(), algorithmRelation1.getId(), null)).toUriString();
//
//        MvcResult result = mockMvc
//                .perform(put(url).content(mapper.writeValueAsString(algorithmRelation1Dto))
//                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andReturn();
//
//        EntityModel<AlgorithmRelationDto> response = mapper.readValue(result.getResponse().getContentAsString(),
//                new TypeReference<EntityModel<AlgorithmRelationDto>>() {
//                });
//        assertEquals(algorithmRelation1.getSourceAlgorithm().getId(), response.getContent().getSourceAlgorithm().getId());
//    }

//    @Test
//    public void deleteAlgorithmRelation_notModified() throws Exception {
//
//    	when(algorithmService.deleteAlgorithmRelation(any(UUID.class),any(UUID.class))).thenReturn(false);
//
//    	mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS +
//    			"/{algorithmRelation_id}", UUID.randomUUID(), this.algorithmRelation1.getId()))
//    			.andExpect(status().isNotModified());
//    }
//
//    @Test
//    public void deleteAlgorithmRelation_returnOk() throws Exception {
//        initializeAlgorithms();
//        doNothing().when(algorithmService).deleteAlgorithmRelation(algorithm1.getId(), algorithmRelation1.getId());
//
//        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
//                .deleteAlgorithmRelationReferenceFromAlgorithm(UUID.randomUUID(), UUID.randomUUID())).toUriString();
//
//        mockMvc.perform(delete(url)).andExpect(status().isOk());
//    }

    @Test
    public void getPatternRelations_returnTwo() throws Exception {
        initializeAlgorithms();
        when(algorithmService.findPatternRelations(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>(algorithm1.getRelatedPatterns())));

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .getPatternRelationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault())).toUriString();

        MvcResult result = mockMvc.perform(
                get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "patternRelations", PatternRelationDto.class);
        assertEquals(2, resultList.size());
    }

    @Test
    public void getPatternRelations_returnNotFound() throws Exception {
        initializeAlgorithms();
        when(algorithmService.findPatternRelations(any(), any()))
                .thenThrow(NoSuchElementException.class);

        var url = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .getPatternRelationsOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault())).toUriString();

        mockMvc.perform(
                get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListComputingResources_ClassicAlgorithm() throws Exception {
        when(algorithmService.findComputeResourceProperties(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<ComputeResourceProperty>()));
        when(computeResourcePropertyService.findAllComputeResourcesPropertyByAlgorithmId(any(), any())).thenReturn(Page.empty());
        var path = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .getComputeResourcePropertiesOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault())).toUriString();

        mockMvc.perform(get(path)).andExpect(status().isOk());
    }

    @Test
    void testListComputingResources_ValidAlgo_NoResources() throws Exception {
        var algo = new QuantumAlgorithm();
        algo.setRequiredComputeResourceProperties(new HashSet<>());
        when(algorithmService.findComputeResourceProperties(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>(algo.getRequiredComputeResourceProperties())));
        when(computeResourcePropertyService.findAllComputeResourcesPropertyByAlgorithmId(any(), any())).thenReturn(Page.empty());
        var path = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .getComputeResourcePropertiesOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault())).toUriString();
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
            algo.addComputeResourceProperty(resource);
        }

        when(algorithmService.findComputeResourceProperties(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>(algo.getRequiredComputeResourceProperties())));
        when(computeResourcePropertyService.findAllComputeResourcesPropertyByAlgorithmId(any(), any())).thenReturn(new PageImpl<>(resources));
        var path = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .getComputeResourcePropertiesOfAlgorithm(UUID.randomUUID(), ListParameters.getDefault())).toUriString();
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
        when(algorithmService.createComputeResourceProperty(any(), any()))
                .thenThrow(new NoSuchElementException());
        var path = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm(UUID.randomUUID(), null)).toUriString();
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(getValidResourceInput())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddComputeResourceProperty_ClassicAlgo() throws Exception {
        var algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("alg1");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        var resReq = getValidResourceInput();
        var type = new ComputeResourcePropertyType();
        type.setDatatype(resReq.getType().getDatatype());
        type.setDescription(resReq.getType().getDescription());
        type.setName(resReq.getType().getName());
        type.setId(resReq.getType().getId());

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(type);
        resource.setValue(resReq.getValue());
        resource.setId(resReq.getId());

        when(algorithmService.createComputeResourceProperty(any(), any())).thenReturn(resource);

        var path = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm(UUID.randomUUID(), null)).toUriString();
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(resReq)))
                .andExpect(status().isOk());
    }

    @Test
    void testAddComputeResourceProperty_InvalidValue() throws Exception {

        var algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("alg1");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        var resReq = getValidResourceInput();
        // Should cause a fail, since the type s FLOAT
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

        when(algorithmService.findById(any())).thenReturn(algorithm);
        when(computeResourcePropertyService.findComputeResourcePropertyTypeById(any())).thenReturn(type);
        when(computeResourcePropertyService.addComputeResourcePropertyToAlgorithm(any(Algorithm.class), any(ComputeResourceProperty.class))).thenReturn(resource);
        var path = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm(UUID.randomUUID(), null)).toUriString();
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(resReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddComputeResourceProperty_InvalidInput_NoType() throws Exception {
        var resource = new ComputeResourcePropertyDto();
        resource.setId(UUID.randomUUID());

        when(algorithmService.findById(any())).thenReturn(new ClassicAlgorithm());
        var path = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm(UUID.randomUUID(), null)).toUriString();

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

    private ComputeResourcePropertyDto getValidResourceInput() {
        var type = new ComputeResourcePropertyTypeDto();
        type.setDatatype(ComputeResourcePropertyDataType.FLOAT);
        type.setName("test-type");
        type.setId(UUID.randomUUID());
        var resource = new ComputeResourcePropertyDto();
        resource.setType(type);
        resource.setId(UUID.randomUUID());
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

        AlgoRelationType relType1 = new AlgoRelationType();
        relType1.setName("RelationType1");

        var algorithm1 = new QuantumAlgorithm();
        algorithm1.setId(UUID.randomUUID());
        algorithm1.setName("alg1");
        algorithm1.setComputationModel(ComputationModel.CLASSIC);

        var algorithm2 = new QuantumAlgorithm();
        algorithm2.setId(UUID.randomUUID());
        algorithm2.setName("alg2");
        algorithm2.setComputationModel(ComputationModel.CLASSIC);
        algorithm2.setAlgorithmRelations(new HashSet<>());

        var algorithmRelation1 = new AlgorithmRelation();
        algorithmRelation1.setId(UUID.randomUUID());
        algorithmRelation1.setSourceAlgorithm(algorithm1);
        algorithmRelation1.setTargetAlgorithm(algorithm2);
        algorithmRelation1.setAlgoRelationType(relType1);
        AlgorithmRelation algorithmRelation2 = new AlgorithmRelation();
        algorithmRelation2.setId(UUID.randomUUID());
        algorithmRelation2.setSourceAlgorithm(algorithm1);
        algorithmRelation2.setTargetAlgorithm(algorithm2);
        algorithmRelation2.setAlgoRelationType(relType1);

        var resReq = getValidResourceInput();
        var type = new ComputeResourcePropertyType();
        type.setDatatype(resReq.getType().getDatatype());
        type.setDescription(resReq.getType().getDescription());
        type.setName(resReq.getType().getName());
        type.setId(resReq.getType().getId());

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(type);
        resource.setValue(resReq.getValue());
        resource.setId(resReq.getId());

        when(algorithmService.createComputeResourceProperty(any(), any())).thenReturn(resource);

        var path = fromMethodCall(uriBuilder, on(AlgorithmController.class)
                .createComputeResourcePropertyForAlgorithm(UUID.randomUUID(), null)).toUriString();
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(resReq)))
                .andExpect(status().isOk());
    }
}
