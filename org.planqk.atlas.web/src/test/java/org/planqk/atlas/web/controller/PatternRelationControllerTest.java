package org.planqk.atlas.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.PatternRelationTypeDto;
import org.planqk.atlas.web.linkassembler.PatternRelationAssembler;
import org.planqk.atlas.web.linkassembler.PatternRelationTypeAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PatternRelationController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class PatternRelationControllerTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public PatternRelationAssembler patternRelationAssembler() {
            return new PatternRelationAssembler();
        }
    }

    @MockBean
    private PatternRelationService patternRelationService;
    @MockBean
    private PagedResourcesAssembler<PatternRelationDto> paginationAssembler;
    @MockBean
    private PatternRelationTypeAssembler patternRelationAssembler;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);

    private PatternRelation relation1;
    private PatternRelation relation2;
    private PatternRelation missingReqParamRelation;
    private PatternRelationDto relation1Dto;
    private PatternRelationDto relation2Dto;
    private PatternRelationDto missingReqParamRelationDto;

    private PatternRelationType type1;
    private PatternRelationType type2;
    private PatternRelationTypeDto type1Dto;
    private PatternRelationTypeDto type2Dto;

    private Algorithm algorithm1;
    private Algorithm algorithm2;
    private AlgorithmDto algorithm1Dto;
    private AlgorithmDto algorithm2Dto;

    List<PatternRelation> relationList;
    Page<PatternRelation> relationPage;
    Page<PatternRelationDto> relationPageDto;

    @BeforeEach
    public void initialize() {
        // Init Object-Mapper
        mapper = ObjectMapperUtils.newTestMapper();

        // Generate UUIDs
        UUID relationId1 = UUID.randomUUID();
        UUID relationId2 = UUID.randomUUID();
        UUID algoId1 = UUID.randomUUID();
        UUID algoId2 = UUID.randomUUID();
        UUID typeId1 = UUID.randomUUID();
        UUID typeId2 = UUID.randomUUID();

        // Init Algorithms and DTOs
        algorithm1 = new Algorithm();
        algorithm1.setId(algoId1);
        algorithm1.setName("Algorithm1");
        algorithm1.setComputationModel(ComputationModel.CLASSIC);
        algorithm2 = new Algorithm();
        algorithm2.setId(algoId2);
        algorithm2.setName("Algorithm2");
        algorithm2.setComputationModel(ComputationModel.CLASSIC);

        algorithm1Dto = ModelMapperUtils.convert(algorithm1, AlgorithmDto.class);
        algorithm2Dto = ModelMapperUtils.convert(algorithm2, AlgorithmDto.class);

        // Init Types and DTOs
        type1 = new PatternRelationType();
        type1.setId(typeId1);
        type1.setName("PatternType1");
        type2 = new PatternRelationType();
        type2.setId(typeId2);
        type2.setName("PatternType2");
        
        type1Dto = ModelMapperUtils.convert(type1, PatternRelationTypeDto.class);
        type2Dto = ModelMapperUtils.convert(type2, PatternRelationTypeDto.class);
        
        // Init Relations and DTOs and Pages
        relation1 = new PatternRelation();
        relation1.setId(relationId1);
        relation1.setAlgorithm(algorithm1);
        relation1.setPattern(URI.create("http://www.pattern1.de"));
        relation1.setDescription("Description1");
        relation1.setPatternRelationType(type1);
        relation2 = new PatternRelation();
        relation2.setId(relationId2);
        relation2.setAlgorithm(algorithm2);
        relation2.setPattern(URI.create("http://www.pattern2.de"));
        relation2.setDescription("Description2");
        relation2.setPatternRelationType(type2);
        missingReqParamRelation = new PatternRelation();
        
        relation1Dto = ModelMapperUtils.convert(relation1, PatternRelationDto.class);
        relation1Dto.setAlgorithm(algorithm1Dto);
        relation1Dto.setPatternRelationType(type1Dto);
        relation2Dto = ModelMapperUtils.convert(relation2, PatternRelationDto.class);
        relation2Dto.setAlgorithm(algorithm2Dto);
        relation2Dto.setPatternRelationType(type2Dto);
        missingReqParamRelationDto = ModelMapperUtils.convert(missingReqParamRelation, PatternRelationDto.class);
        
        try {
            System.err.println(mapper.writeValueAsString(relation1Dto));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        relationList = new ArrayList<>();
        relationList.add(relation1);
        relationList.add(relation2);
        
        relationPage = new PageImpl<>(relationList);
        relationPageDto = ModelMapperUtils.convertPage(relationPage, PatternRelationDto.class);
    }
    
//    @Test
//    public void createType_returnType() throws Exception {
//        when(patternRelationService.save(relation1)).thenReturn(relation1);
//
//        MvcResult result = mockMvc
//                .perform(post("/" + Constants.PATTERN_RELATIONS + "/").content(mapper.writeValueAsString(relation1Dto))
//                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated()).andReturn();
//
//        EntityModel<PatternRelationDto> response = mapper.readValue(result.getResponse().getContentAsString(),
//                new TypeReference<EntityModel<PatternRelationDto>>() {
//                });
//
//        assertEquals(response.getContent().getId(), relation1Dto.getPatternRelationType().getId());
//    }

    @Test
    public void createType_returnBadRequest() throws Exception {
        mockMvc.perform(
                post("/" + Constants.PATTERN_RELATIONS + "/").content(mapper.writeValueAsString(missingReqParamRelationDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
