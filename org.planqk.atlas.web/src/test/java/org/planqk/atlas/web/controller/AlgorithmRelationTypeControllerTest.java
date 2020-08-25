package org.planqk.atlas.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.planqk.atlas.core.services.AlgorithmRelationTypeService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmRelationTypeDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@WebMvcTest(AlgorithmRelationTypeController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
@Slf4j
public class AlgorithmRelationTypeControllerTest {

    @MockBean
    private AlgorithmRelationTypeService algorithmRelationTypeService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();
    private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/");

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);

    private AlgorithmRelationType algorithmRelationType1;
    private AlgorithmRelationType algorithmRelationType2;
    private AlgorithmRelationTypeDto algoRelationType1Dto;

    @BeforeEach
    public void initialize() {
        algorithmRelationType1 = new AlgorithmRelationType();
        algorithmRelationType1.setId(UUID.randomUUID());
        algorithmRelationType1.setName("relationType1");
        algorithmRelationType2 = new AlgorithmRelationType();
        algorithmRelationType2.setId(UUID.randomUUID());
        algorithmRelationType2.setName("relationType2");

        algoRelationType1Dto = ModelMapperUtils.convert(algorithmRelationType1, AlgorithmRelationTypeDto.class);
    }

    @Test
    public void createAlgoRelationType_returnBadRequest() throws Exception {
        AlgorithmRelationTypeDto algorithmRelationTypeDto = new AlgorithmRelationTypeDto();
        algorithmRelationTypeDto.setId(UUID.randomUUID());

        var url = fromMethodCall(uriBuilder, on(AlgorithmRelationTypeController.class)
                .createAlgorithmRelationType(null)).toUriString();
        mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(algorithmRelationTypeDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAlgoRelationType_returnCreate() throws Exception {
        when(algorithmRelationTypeService.create(any())).thenReturn(algorithmRelationType1);

        var url = fromMethodCall(uriBuilder, on(AlgorithmRelationTypeController.class)
                .createAlgorithmRelationType(null)).toUriString();
        MvcResult result = mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(algoRelationType1Dto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<AlgorithmRelationDto> type = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(type.getContent().getId(), algoRelationType1Dto.getId());
    }

    @Test
    public void updateAlgoRelationType_returnBadRequest() throws Exception {
        AlgorithmRelationTypeDto algorithmRelationTypeDto = new AlgorithmRelationTypeDto();
        algorithmRelationTypeDto.setId(UUID.randomUUID());

        var url = fromMethodCall(uriBuilder, on(AlgorithmRelationTypeController.class)
                .updateAlgorithmRelationType(null)).toUriString();
        mockMvc.perform(put(url)
                .content(mapper.writeValueAsString(algorithmRelationTypeDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void updateAlgoRelationType_returnOk() throws Exception {
        when(algorithmRelationTypeService.update(any())).thenReturn(algorithmRelationType1);

        var url = fromMethodCall(uriBuilder, on(AlgorithmRelationTypeController.class)
                .updateAlgorithmRelationType(null)).toUriString();
        MvcResult result = mockMvc
                .perform(put(url)
                        .content(mapper.writeValueAsString(algoRelationType1Dto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<AlgorithmRelationDto> type = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(type.getContent().getId(), algoRelationType1Dto.getId());
    }

    @Test
    public void getAlgoRelationTypes_withEmptyAlgoRelationTypeList() throws Exception {
        when(algorithmRelationTypeService.findAll(any())).thenReturn(Page.empty());

        var url = fromMethodCall(uriBuilder, on(AlgorithmRelationTypeController.class)
                .getAlgorithmRelationTypes(ListParameters.getDefault())).toUriString();
        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var providers = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algoRelationTypes", AlgorithmRelationDto.class);
        assertEquals(providers.size(), 0);
    }

    @Test
    public void getAlgoRelationTypes_withTwoAlgoRelationTypeList() throws Exception {
        List<AlgorithmRelationType> algoRelationList = new ArrayList<>();
        algoRelationList.add(algorithmRelationType1);
        algoRelationList.add(algorithmRelationType2);

        Page<AlgorithmRelationType> algoRelationPage = new PageImpl<>(algoRelationList);
        Page<AlgorithmRelationTypeDto> algoRelationDtoPage = ModelMapperUtils.convertPage(algoRelationPage,
                AlgorithmRelationTypeDto.class);

        when(algorithmRelationTypeService.findAll(any())).thenReturn(algoRelationPage);

        var url = fromMethodCall(uriBuilder, on(AlgorithmRelationTypeController.class)
                .getAlgorithmRelationTypes(ListParameters.getDefault())).toUriString();

        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var providers = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algoRelationTypes", AlgorithmRelationDto.class);
        assertEquals(providers.size(), 2);
    }

    @Test
    public void getAlgoRelationTypeById_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(algorithmRelationTypeService).findById(any());

        var url = fromMethodCall(uriBuilder, on(AlgorithmRelationTypeController.class)
                .getAlgorithmRelationType(algorithmRelationType1.getId())).toUriString();
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAlgoRelationTypeById_returnAlgoRelationType() throws Exception {
        when(algorithmRelationTypeService.findById(any())).thenReturn(algorithmRelationType1);

        var url = fromMethodCall(uriBuilder, on(AlgorithmRelationTypeController.class)
                .getAlgorithmRelationType(algorithmRelationType1.getId())).toUriString();

        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        log.info(result.getResponse().getContentAsString());
        EntityModel<AlgorithmRelationDto> algoRelationTypeDto = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(algoRelationTypeDto.getContent().getId(), algorithmRelationType1.getId());
    }

    @Test
    public void deleteAlgoRelationType_returnNotFound() throws Exception {
        doThrow(NoSuchElementException.class).when(algorithmRelationTypeService).delete(any());

        var url = fromMethodCall(uriBuilder, on(AlgorithmRelationTypeController.class)
                .deleteAlgorithmRelationType(algorithmRelationType1.getId())).toUriString();

        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteAlgoRelationType_returnOk() throws Exception {
        doNothing().when(algorithmRelationTypeService).delete(any());

        var url = fromMethodCall(uriBuilder, on(AlgorithmRelationTypeController.class)
                .deleteAlgorithmRelationType(algorithmRelationType1.getId())).toUriString();
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
