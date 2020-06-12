package org.planqk.atlas.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.services.PatternRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.PatternRelationTypeDto;
import org.planqk.atlas.web.linkassembler.PatternRelationTypeAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PatternRelationTypeController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class PatternRelationTypeControllerTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public PatternRelationTypeAssembler patternRelationTypeAssembler() {
            return new PatternRelationTypeAssembler();
        }
    }

    @MockBean
    private PatternRelationTypeService patternRelationTypeService;
    @MockBean
    private PagedResourcesAssembler<PatternRelationTypeDto> paginationAssembler;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);

    PatternRelationType type1;
    PatternRelationType type2;
    PatternRelationType type1Updated;

    PatternRelationTypeDto type1Dto;
    PatternRelationTypeDto type2Dto;
    PatternRelationTypeDto noReqParamDto;
    PatternRelationTypeDto type1DtoUpdated;

    List<PatternRelationType> typeList;
    Page<PatternRelationType> typePage;

    Page<PatternRelationTypeDto> typePageDto;

    @BeforeEach
    public void initialize() {
        // Init Object-Mapper
        mapper = ObjectMapperUtils.newTestMapper();

        // Generate UUIDs
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        // Fill Type-Objects
        type1 = new PatternRelationType();
        type1.setId(id1);
        type1.setName("PatternType1");
        type2 = new PatternRelationType();
        type2.setId(id2);
        type2.setName("PatternType2");
        type1Updated = new PatternRelationType();
        type1Updated.setId(id1);
        type1Updated.setName("PatternType1Updated");

        // Init Type that misses required parameters
        noReqParamDto = new PatternRelationTypeDto();

        // Generate DTOs from Entities
        type1Dto = ModelMapperUtils.convert(type1, PatternRelationTypeDto.class);
        type2Dto = ModelMapperUtils.convert(type2, PatternRelationTypeDto.class);
        type1DtoUpdated = ModelMapperUtils.convert(type1Updated, PatternRelationTypeDto.class);

        // Fill Type-list
        typeList = new ArrayList<>();
        typeList.add(type1);
        typeList.add(type2);

        // Generate Page
        typePage = new PageImpl<>(typeList);
        typePageDto = ModelMapperUtils.convertPage(typePage, PatternRelationTypeDto.class);
    }

    @Test
    public void createType_returnType() throws Exception {
        when(patternRelationTypeService.save(type1)).thenReturn(type1);

        MvcResult result = mockMvc
                .perform(post("/" + Constants.PATTERN_RELATION_TYPES + "/").content(mapper.writeValueAsString(type1Dto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<PatternRelationTypeDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<PatternRelationTypeDto>>() {
                });

        assertEquals(response.getContent().getName(), type1Dto.getName());
    }

    @Test
    public void createType_returnBadRequest() throws Exception {
        when(patternRelationTypeService.save(type1)).thenReturn(type1);

        mockMvc.perform(
                post("/" + Constants.PATTERN_RELATION_TYPES + "/").content(mapper.writeValueAsString(noReqParamDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getTypesPaged_returnTypesPaged() throws Exception {
        when(patternRelationTypeService.findAll(pageable)).thenReturn(typePage);
        when(paginationAssembler.toModel(ArgumentMatchers.any()))
                .thenReturn(HateoasUtils.generatePagedModel(typePageDto));

        MvcResult result = mockMvc
                .perform(get("/" + Constants.PATTERN_RELATION_TYPES + "/")
                        .queryParam(Constants.PAGE, Integer.toString(page))
                        .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "patternRelationTypeDtoes", PatternRelationTypeDto.class);

        assertEquals(resultList.size(), 2);
        assertTrue(resultList.contains(type1Dto));
        assertTrue(resultList.contains(type2Dto));
    }

    @Test
    public void getType_returnType() throws Exception {
        when(patternRelationTypeService.findById(type1.getId())).thenReturn(type1);

        MvcResult result = mockMvc.perform(
                get("/" + Constants.PATTERN_RELATION_TYPES + "/" + type1.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<PatternRelationTypeDto> response = new ObjectMapper().readValue(
                result.getResponse().getContentAsString(), new TypeReference<EntityModel<PatternRelationTypeDto>>() {
                });
        assertEquals(response.getContent(), type1Dto);
    }

    @Test
    public void getType_returnNotFound() throws Exception {
        when(patternRelationTypeService.findById(any())).thenThrow(NoSuchElementException.class);

        mockMvc.perform(
                get("/" + Constants.PATTERN_RELATION_TYPES + "/" + type1.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateType_returnType() throws Exception {
        when(patternRelationTypeService.update(type1.getId(), type1)).thenReturn(type1Updated);

        MvcResult result = mockMvc.perform(put("/" + Constants.PATTERN_RELATION_TYPES + "/{id}", type1.getId())
                .content(mapper.writeValueAsString(type1Dto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        EntityModel<PatternRelationTypeDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<PatternRelationTypeDto>>() {
                });

        assertEquals(response.getContent(), type1DtoUpdated);
    }

    @Test
    public void updateType_returnBadRequest() throws Exception {
        when(patternRelationTypeService.update(type1.getId(), type1)).thenReturn(type1Updated);

        mockMvc.perform(put("/" + Constants.PATTERN_RELATION_TYPES + "/{id}", type1.getId())
                .content(mapper.writeValueAsString(noReqParamDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void updateType_returnNotFound() throws Exception {
        when(patternRelationTypeService.update(any(), any())).thenThrow(NoSuchElementException.class);

        mockMvc.perform(put("/" + Constants.PATTERN_RELATION_TYPES + "/{id}", UUID.randomUUID())
                .content(mapper.writeValueAsString(type1Dto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void deleteType_returnOk() throws Exception {
        doNothing().when(patternRelationTypeService).deleteById(type1.getId());

        mockMvc.perform(delete("/" + Constants.PATTERN_RELATION_TYPES + "/{id}", type1.getId())
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void deleteType_returnNoContent() throws Exception {
        doThrow(EmptyResultDataAccessException.class).when(patternRelationTypeService).deleteById(any());

        mockMvc.perform(delete("/" + Constants.PATTERN_RELATION_TYPES + "/{id}", UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
    }
}
