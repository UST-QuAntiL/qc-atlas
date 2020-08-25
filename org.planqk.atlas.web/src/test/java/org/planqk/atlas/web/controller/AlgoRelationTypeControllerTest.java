package org.planqk.atlas.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.services.AlgoRelationTypeService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;
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

@WebMvcTest(AlgoRelationTypeController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
@Slf4j
public class AlgoRelationTypeControllerTest {

    @MockBean
    private AlgoRelationTypeService algoRelationTypeService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();
    private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/");

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);

    private AlgoRelationType algoRelationType1;
    private AlgoRelationType algoRelationType2;
    private AlgoRelationTypeDto algoRelationType1Dto;

    @BeforeEach
    public void initialize() {
        algoRelationType1 = new AlgoRelationType();
        algoRelationType1.setId(UUID.randomUUID());
        algoRelationType1.setName("relationType1");
        algoRelationType2 = new AlgoRelationType();
        algoRelationType2.setId(UUID.randomUUID());
        algoRelationType2.setName("relationType2");

        algoRelationType1Dto = ModelMapperUtils.convert(algoRelationType1, AlgoRelationTypeDto.class);
    }

    @Test
    public void createAlgoRelationType_returnBadRequest() throws Exception {
        AlgoRelationTypeDto algoRelationTypeDto = new AlgoRelationTypeDto();
        algoRelationTypeDto.setId(UUID.randomUUID());

        var url = fromMethodCall(uriBuilder, on(AlgoRelationTypeController.class)
                .createAlgorithmRelationType(null)).toUriString();
        mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(algoRelationTypeDto))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAlgoRelationType_returnCreate() throws Exception {
        when(algoRelationTypeService.create(any())).thenReturn(algoRelationType1);

        var url = fromMethodCall(uriBuilder, on(AlgoRelationTypeController.class)
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
        AlgoRelationTypeDto algoRelationTypeDto = new AlgoRelationTypeDto();
        algoRelationTypeDto.setId(UUID.randomUUID());

        var url = fromMethodCall(uriBuilder, on(AlgoRelationTypeController.class)
                .updateAlgorithmRelationType(null)).toUriString();
        mockMvc.perform(put(url)
                .content(mapper.writeValueAsString(algoRelationTypeDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void updateAlgoRelationType_returnOk() throws Exception {
        when(algoRelationTypeService.update(any())).thenReturn(algoRelationType1);

        var url = fromMethodCall(uriBuilder, on(AlgoRelationTypeController.class)
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
        when(algoRelationTypeService.findAll(any())).thenReturn(Page.empty());

        var url = fromMethodCall(uriBuilder, on(AlgoRelationTypeController.class)
                .getAlgorithmRelationTypes(ListParameters.getDefault())).toUriString();
        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var providers = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algoRelationTypes", AlgorithmRelationDto.class);
        assertEquals(providers.size(), 0);
    }

    @Test
    public void getAlgoRelationTypes_withTwoAlgoRelationTypeList() throws Exception {
        List<AlgoRelationType> algoRelationList = new ArrayList<>();
        algoRelationList.add(algoRelationType1);
        algoRelationList.add(algoRelationType2);

        Page<AlgoRelationType> algoRelationPage = new PageImpl<>(algoRelationList);
        Page<AlgoRelationTypeDto> algoRelationDtoPage = ModelMapperUtils.convertPage(algoRelationPage,
                AlgoRelationTypeDto.class);

        when(algoRelationTypeService.findAll(any())).thenReturn(algoRelationPage);

        var url = fromMethodCall(uriBuilder, on(AlgoRelationTypeController.class)
                .getAlgorithmRelationTypes(ListParameters.getDefault())).toUriString();

        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var providers = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "algoRelationTypes", AlgorithmRelationDto.class);
        assertEquals(providers.size(), 2);
    }

    @Test
    public void getAlgoRelationTypeById_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(algoRelationTypeService).findById(any());

        var url = fromMethodCall(uriBuilder, on(AlgoRelationTypeController.class)
                .getAlgorithmRelationType(algoRelationType1.getId())).toUriString();
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAlgoRelationTypeById_returnAlgoRelationType() throws Exception {
        when(algoRelationTypeService.findById(any())).thenReturn(algoRelationType1);

        var url = fromMethodCall(uriBuilder, on(AlgoRelationTypeController.class)
                .getAlgorithmRelationType(algoRelationType1.getId())).toUriString();

        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        log.info(result.getResponse().getContentAsString());
        EntityModel<AlgorithmRelationDto> algoRelationTypeDto = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(algoRelationTypeDto.getContent().getId(), algoRelationType1.getId());
    }

    @Test
    public void deleteAlgoRelationType_returnNotFound() throws Exception {
        doThrow(NoSuchElementException.class).when(algoRelationTypeService).delete(any());

        var url = fromMethodCall(uriBuilder, on(AlgoRelationTypeController.class)
                .deleteAlgorithmRelationType(algoRelationType1.getId())).toUriString();

        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteAlgoRelationType_returnOk() throws Exception {
        doNothing().when(algoRelationTypeService).delete(any());

        var url = fromMethodCall(uriBuilder, on(AlgoRelationTypeController.class)
                .deleteAlgorithmRelationType(algoRelationType1.getId())).toUriString();
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
