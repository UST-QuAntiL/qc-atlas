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
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ComputingResource;
import org.planqk.atlas.core.model.ComputingResourceDataType;
import org.planqk.atlas.core.model.ComputingResourceType;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.core.services.ComputingResourceService;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ComputingResourceDto;
import org.planqk.atlas.web.dtos.ComputingResourceTypeDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlgorithmController.class)
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
    private ComputingResourceService computingResourceService;
    @MockBean
    private PatternRelationService patternRelationService;
    @MockBean
    private ProblemTypeService problemTypeService;
    @MockBean
    private PublicationService publicationService;
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private Algorithm algorithm1;
    private Algorithm algorithm2;
    private AlgorithmRelation algorithmRelation1;
    private AlgorithmRelationDto algorithmRelation1Dto;
    private AlgorithmDto algorithm1Dto;
    private AlgorithmDto algorithm2Dto;
    private Set<AlgorithmRelation> algorithmRelations;
    private Set<PatternRelation> patternRelations;

    @BeforeEach
    public void initialize() {
        mapper = ObjectMapperUtils.newTestMapper();
    }

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
    public void getAlgorithms_withoutPagination() throws Exception {
        initializeAlgorithms();
        when(algorithmService.findAll(Pageable.unpaged())).thenReturn(Page.empty());

        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAlgorithms_withEmptyAlgorithmList() throws Exception {
        initializeAlgorithms();
        when(algorithmService.findAll(pageable)).thenReturn(Page.empty());

        MvcResult result = mockMvc
                .perform(get("/" + Constants.ALGORITHMS + "/").queryParam(Constants.PAGE, Integer.toString(page))
                        .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algorithmDtoes", AlgorithmDto.class);
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

        when(algorithmService.findAll(pageable)).thenReturn(pageAlg);

        MvcResult result = mockMvc
                .perform(get("/" + Constants.ALGORITHMS + "/").queryParam(Constants.PAGE, Integer.toString(page))
                        .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "classicAlgorithmDtoes", AlgorithmDto.class);
        assertEquals(2, resultList.size());
    }

    @Test
    public void getAlgorithm_returnNotFound() throws Exception {
        initializeAlgorithms();
        when(algorithmService.findById(any(UUID.class))).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/" + UUID.randomUUID()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAlgorithm_returnAlgorithm() throws Exception {
        initializeAlgorithms();
        when(algorithmService.findById(any(UUID.class))).thenReturn(algorithm1);

        MvcResult result = mockMvc
                .perform(get("/" + Constants.ALGORITHMS + "/" + algorithm1.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<AlgorithmDto>>() {
                });
        assertEquals(response.getContent().getId(), algorithm1Dto.getId());
    }

    @Test
    public void createAlgorithm_returnBadRequest() throws Exception {
        initializeAlgorithms();
        AlgorithmDto algoDto = new AlgorithmDto();
        algoDto.setId(UUID.randomUUID());
        mockMvc.perform(post("/" + Constants.ALGORITHMS + "/").content(mapper.writeValueAsString(algoDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        algoDto.setName("algoDto");
        mockMvc.perform(post("/" + Constants.ALGORITHMS + "/").content(mapper.writeValueAsString(algoDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAlgorithm_returnAlgorithm() throws Exception {
        initializeAlgorithms();
        when(algorithmService.save(algorithm1)).thenReturn(algorithm1);

        MvcResult result = mockMvc
                .perform(post("/" + Constants.ALGORITHMS + "/").content(mapper.writeValueAsString(algorithm1Dto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<AlgorithmDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<AlgorithmDto>>() {
                });
        assertEquals(response.getContent().getName(), this.algorithm1Dto.getName());
    }

    @Test
    public void updateAlgorithm_returnBadRequest() throws Exception {
        initializeAlgorithms();
        AlgorithmDto algoDto = new AlgorithmDto();
        algoDto.setId(UUID.randomUUID());

        mockMvc.perform(
                put("/" + Constants.ALGORITHMS + "/{id}", algoDto.getId()).content(mapper.writeValueAsString(algoDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        algoDto.setName("algoDto");

        mockMvc.perform(post("/" + Constants.ALGORITHMS + "/").content(mapper.writeValueAsString(algoDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateAlgorithm_returnAlgorithm() throws Exception {
        initializeAlgorithms();
        when(algorithmService.update(algorithm1.getId(), algorithm1)).thenReturn(algorithm1);

        MvcResult result = mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{id}", algorithm1.getId())
                .content(mapper.writeValueAsString(algorithm1Dto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<AlgorithmDto>>() {
                });
        assertEquals(response.getContent().getName(), algorithm1Dto.getName());
    }

    @Test
    public void deleteAlgorithm_notFound() throws Exception {
        initializeAlgorithms();
        doThrow(new NoSuchElementException()).when(algorithmService).delete(any(UUID.class));

        mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteAlgorithm_returnOk() throws Exception {
        initializeAlgorithms();
        mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{id}", this.algorithm1.getId()))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void getAlgorithmRelations_returnNotFound() throws Exception {
        initializeAlgorithms();
        when(algorithmService.getAlgorithmRelations(any(UUID.class))).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/" + Constants.ALGORITHMS + "/{id}/" + Constants.ALGORITHM_RELATIONS, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAlgorithmRelations_withEmptyAlgorithmRelationList() throws Exception {
        initializeAlgorithms();
        when(algorithmService.getAlgorithmRelations(any(UUID.class))).thenReturn(new HashSet<>());

        MvcResult result = mockMvc
                .perform(get("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                        algorithm2.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algorithmRelationDtoes", AlgorithmRelationDto.class);
        assertEquals(0, resultList.size());
    }

    @Test
    public void getAlgorithmRelations_withTwoAlgorithmRelationList() throws Exception {
        initializeAlgorithms();
        when(algorithmService.getAlgorithmRelations(any(UUID.class))).thenReturn(algorithm1.getAlgorithmRelations());

        MvcResult result = mockMvc
                .perform(get("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                        algorithm1.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algorithmRelationDtoes", AlgorithmRelationDto.class);
        assertEquals(2, resultList.size());
    }

    @Test
    public void updateAlgorithmRelation_returnBadRequest() throws Exception {
        initializeAlgorithms();
        Algorithm algo = new Algorithm();
        mockMvc.perform(
                put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS, algo.getId())
                        .content(mapper.writeValueAsString(this.algorithmRelation1Dto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        AlgorithmRelationDto algoRelationDto = new AlgorithmRelationDto();
        mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                algorithm1.getId()).content(mapper.writeValueAsString(algoRelationDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        algoRelationDto.setSourceAlgorithm(algorithm1Dto);
        mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                algorithm1.getId()).content(mapper.writeValueAsString(algoRelationDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        algoRelationDto.setTargetAlgorithm(algorithm2Dto);
        mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                algorithm1.getId()).content(mapper.writeValueAsString(algoRelationDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateAlgorithmRelation_returnNotFound() throws Exception {
        initializeAlgorithms();
        when(algorithmService.addOrUpdateAlgorithmRelation(any(UUID.class), any(AlgorithmRelation.class)))
                .thenThrow(new NoSuchElementException());

        mockMvc.perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                UUID.randomUUID()).content(mapper.writeValueAsString(algorithmRelation1Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateAlgorithmRelation_returnAlgorithmRelation() throws Exception {
        initializeAlgorithms();
        when(algorithmService.addOrUpdateAlgorithmRelation(any(UUID.class), any(AlgorithmRelation.class)))
                .thenReturn(algorithmRelation1);

        MvcResult result = mockMvc
                .perform(put("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS,
                        algorithm1.getId()).content(mapper.writeValueAsString(algorithmRelation1Dto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmRelationDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<AlgorithmRelationDto>>() {
                });
        assertEquals(response.getContent().getSourceAlgorithm().getName(), algorithm1Dto.getName());
    }

//    @Test
//    public void deleteAlgorithmRelation_notModified() throws Exception {
//
//    	when(algorithmService.deleteAlgorithmRelation(any(UUID.class),any(UUID.class))).thenReturn(false);
//
//    	mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS +
//    			"/{algorithmRelation_id}", UUID.randomUUID(), this.algorithmRelation1.getId()))
//    			.andExpect(status().isNotModified());
//    }

    @Test
    public void deleteAlgorithmRelation_returnOk() throws Exception {
        initializeAlgorithms();
        doNothing().when(algorithmService).deleteAlgorithmRelation(algorithm1.getId(), algorithmRelation1.getId());

        mockMvc.perform(delete("/" + Constants.ALGORITHMS + "/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS
                + "/{relation_id}", algorithm1.getId(), algorithmRelation1.getId())).andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getPatternRelations_returnTwo() throws Exception {
        initializeAlgorithms();
        when(algorithmService.findById(any())).thenReturn(algorithm1);

        MvcResult result = mockMvc.perform(
                get("/" + Constants.ALGORITHMS + "/{id}/" + Constants.PATTERN_RELATIONS, UUID.randomUUID()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "patternRelationDtoes", PatternRelationDto.class);
        assertEquals(2, resultList.size());
    }

    @Test
    public void getPatternRelations_returnNotFound() throws Exception {
        initializeAlgorithms();
        when(algorithmService.findById(any())).thenThrow(NoSuchElementException.class);

        mockMvc.perform(
                get("/" + Constants.ALGORITHMS + "/{id}/" + Constants.PATTERN_RELATIONS, algorithm1.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListComputingResources_ClassicAlgorithm() throws Exception {
        when(algorithmService.findById(any())).thenReturn(new QuantumAlgorithm());
        when(computingResourceService.findAllResourcesByAlgorithmId(any(), any())).thenReturn(Page.empty());
        var path = "/" + Constants.ALGORITHMS + "/" + UUID.randomUUID().toString() + "/" + Constants.COMPUTING_RESOURCES + "/";
        mockMvc.perform(get(path)).andExpect(status().isOk());
    }

    @Test
    void testListComputingResources_ValidAlgo_NoResources() throws Exception {
        var algo = new QuantumAlgorithm();
        algo.setRequiredComputingResources(new HashSet<>());
        when(algorithmService.findById(any())).thenReturn(algo);
        when(computingResourceService.findAllResourcesByAlgorithmId(any(), any())).thenReturn(Page.empty());
        var path = "/" + Constants.ALGORITHMS + "/" + UUID.randomUUID().toString() + "/" + Constants.COMPUTING_RESOURCES + "/";
        var result = mockMvc.perform(get(path)).andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(
                result.getResponse().getContentAsString(),
                "computingResourceDtoes",
                ComputingResourceDto.class
        );
        assertThat(resultList.size()).isEqualTo(0);
    }

    @Test
    void testListComputingResources_ValidAlgo_ResourcesIncluded() throws Exception {
        var type = new ComputingResourceType();
        type.setDatatype(ComputingResourceDataType.FLOAT);
        type.setName("test-type");
        type.setId(UUID.randomUUID());

        var algo = new QuantumAlgorithm();
        algo.setRequiredComputingResources(new HashSet<>());
        algo.setId(UUID.randomUUID());
        var resources = new ArrayList<ComputingResource>();

        for (int i = 0; i < 10; i++) {
            var resource = new ComputingResource();
            resource.setComputingResourceType(type);
            resource.setId(UUID.randomUUID());
            resources.add(resource);
            algo.addComputingResource(resource);
        }

        when(algorithmService.findById(any())).thenReturn(algo);
        when(computingResourceService.findAllResourcesByAlgorithmId(any(), any())).thenReturn(new PageImpl<>(resources));
        var path = "/" + Constants.ALGORITHMS + "/" + UUID.randomUUID().toString() + "/" + Constants.COMPUTING_RESOURCES + "/";
        var result = mockMvc.perform(get(path)).andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(
                result.getResponse().getContentAsString(),
                "computingResourceDtoes",
                ComputingResourceDto.class
        );
        assertThat(resultList.size()).isEqualTo(10);

        var presentCount = resultList.stream().filter(e -> resources.stream().anyMatch(b -> b.getId().equals(e.getId()))).count();
        assertThat(presentCount).isEqualTo(10);
    }

    @Test
    void testAddQuantumResource_AlgoNotFound() throws Exception {
        when(algorithmService.findById(any())).thenThrow(new NoSuchElementException());
        var path = "/" + Constants.ALGORITHMS + "/" + UUID.randomUUID().toString() + "/" + Constants.COMPUTING_RESOURCES + "/";
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(getValidResourceInput())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddQuantumResource_ClassicAlgo() throws Exception {

        var algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setName("alg1");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        when(algorithmService.findById(any())).thenReturn(algorithm);
        when(computingResourceService.addComputingResourceToAlgorithm(any(Algorithm.class), any(ComputingResource.class))).thenReturn(new ComputingResource());
        var path = "/" + Constants.ALGORITHMS + "/" + UUID.randomUUID().toString() + "/" + Constants.COMPUTING_RESOURCES + "/";
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(getValidResourceInput())))
                .andExpect(status().isOk());
    }

    @Test
    void testAddQuantumResource_InvalidInput_NoType() throws Exception {
        var resource = new ComputingResourceDto();
        resource.setId(UUID.randomUUID());

        when(algorithmService.findById(any())).thenReturn(new ClassicAlgorithm());
        var path = "/" + Constants.ALGORITHMS + "/" + UUID.randomUUID().toString() + "/" + Constants.COMPUTING_RESOURCES + "/";
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(resource)))
                .andExpect(status().isBadRequest());
    }

    // TODO: We want to test this case
    private ComputingResourceDto getInvalidInputResource() {
        var type = new ComputingResourceTypeDto();
        type.setId(UUID.randomUUID());
        var resource = new ComputingResourceDto();
        resource.setType(type);
        resource.setId(UUID.randomUUID());
        return resource;
    }

    private ComputingResourceDto getValidResourceInput() {
        var type = new ComputingResourceTypeDto();
        type.setDatatype(ComputingResourceDataType.FLOAT);
        type.setName("test-type");
        type.setId(UUID.randomUUID());
        var resource = new ComputingResourceDto();
        resource.setType(type);
        resource.setId(UUID.randomUUID());
        return resource;
    }

    @Test
    void testAddQuantumResource() throws Exception {

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

        when(algorithmService.findById(any())).thenReturn(algorithm1);
        when(computingResourceService.addComputingResourceToAlgorithm(any(QuantumAlgorithm.class), any(ComputingResource.class))).thenReturn(new ComputingResource());
        var path = "/" + Constants.ALGORITHMS + "/" + UUID.randomUUID().toString() + "/" + Constants.COMPUTING_RESOURCES + "/";
        mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(getValidResourceInput())))
                .andExpect(status().isOk());
    }
}
