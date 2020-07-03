package org.planqk.atlas.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
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
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {SoftwarePlatformController.class})
@ExtendWith( {MockitoExtension.class})
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class SoftwarePlatformControllerTest {
    private final int page = 0;
    private final int size = 10;
    private final Pageable pageable = PageRequest.of(page, size);
    private final String softwarePlatformDtoJSONName = "softwarePlatforms";
    @MockBean
    private SoftwarePlatformService softwarePlatformService;
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    public void init() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void addSoftwarePlatform_returnBadRequest() throws Exception {
        SoftwarePlatformDto softwarePlatform = new SoftwarePlatformDto();
        softwarePlatform.setId(UUID.randomUUID());

        mockMvc.perform(
                post("/" + Constants.API_VERSION + "/" + Constants.SOFTWARE_PLATFORMS + "/").content(mapper.writeValueAsString(softwarePlatform))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addSoftwarePlatform_returnCreate() throws Exception {
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setId(UUID.randomUUID());
        softwarePlatform.setName("test platform");
        SoftwarePlatformDto softwarePlatformDto = ModelMapperUtils.convert(softwarePlatform, SoftwarePlatformDto.class);

        when(softwarePlatformService.save(any(SoftwarePlatform.class))).thenReturn(softwarePlatform);

        MvcResult result = mockMvc.perform(
                post("/" + Constants.API_VERSION + "/" + Constants.SOFTWARE_PLATFORMS + "/").content(mapper.writeValueAsString(softwarePlatformDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<SoftwarePlatformDto> resultDtoEntity = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(softwarePlatformDto.getId(), resultDtoEntity.getContent().getId());
        assertEquals(softwarePlatformDto.getName(), resultDtoEntity.getContent().getName());
    }

    @Test
    public void getSoftwarePlatforms_withEmptySet() throws Exception {
        when(softwarePlatformService.findAll(pageable)).thenReturn(Page.empty());

        MvcResult result = mockMvc
                .perform(
                        get("/" + Constants.API_VERSION + "/" + Constants.SOFTWARE_PLATFORMS + "/").queryParam(Constants.PAGE, Integer.toString(page))
                                .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        PagedModel<EntityModel<SoftwarePlatformDto>> pagedDtoEntities = mapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(0, pagedDtoEntities.getContent().size());
    }

    @Test
    public void getSoftwarePlatforms_withOneElement() throws Exception {
        List<SoftwarePlatform> softwarePlatforms = new ArrayList<>();
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setId(UUID.randomUUID());
        softwarePlatform.setName("test software platform");
        softwarePlatforms.add(softwarePlatform);

        Page<SoftwarePlatform> softwarePlatformPage = new PageImpl<>(softwarePlatforms);

        when(softwarePlatformService.findAll(pageable)).thenReturn(softwarePlatformPage);

        MvcResult result = mockMvc
                .perform(
                        get("/" + Constants.API_VERSION + "/" + Constants.SOFTWARE_PLATFORMS + "/").queryParam(Constants.PAGE, Integer.toString(page))
                                .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONObject rootObject = new JSONObject(result.getResponse().getContentAsString());
        var embeddedResources = rootObject.getJSONObject("_embedded").getJSONArray(softwarePlatformDtoJSONName);

        var responseDto = mapper.readValue(embeddedResources.getJSONObject(0).toString(), SoftwarePlatformDto.class);

        assertEquals(1, embeddedResources.length());
        assertEquals(responseDto.getId(), softwarePlatform.getId());
        assertEquals(responseDto.getName(), softwarePlatform.getName());
    }

    @Test
    public void getSoftwarePlatforms_withMultipleElements() throws Exception {
        List<SoftwarePlatform> softwarePlatforms = new ArrayList<>();
        SoftwarePlatform softwarePlatform;
        for (int i = 0; i < size; i++) {
            softwarePlatform = new SoftwarePlatform();
            softwarePlatform.setId(UUID.randomUUID());
            softwarePlatform.setName("test software platform " + i);
            softwarePlatforms.add(softwarePlatform);
        }

        Page<SoftwarePlatform> softwarePlatformPage = new PageImpl<>(softwarePlatforms);

        when(softwarePlatformService.findAll(pageable)).thenReturn(softwarePlatformPage);

        MvcResult result = mockMvc
                .perform(
                        get("/" + Constants.API_VERSION + "/" + Constants.SOFTWARE_PLATFORMS + "/").queryParam(Constants.PAGE, Integer.toString(page))
                                .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONObject rootObject = new JSONObject(result.getResponse().getContentAsString());
        var embeddedResources = rootObject.getJSONObject("_embedded").getJSONArray(softwarePlatformDtoJSONName);

//        for (int i = 0; i < embeddedResources.length(); i++) {
//            var responseDto = mapper.readValue(embeddedResources.getJSONObject(i).toString(), SoftwarePlatformDto.class);
//        }

        assertEquals(embeddedResources.length(), size);
    }

    @Test
    public void getSoftwarePlatform_returnNotFound() throws Exception {
        UUID testId = UUID.randomUUID();
        when(softwarePlatformService.findById(testId)).thenThrow(NoSuchElementException.class);

        mockMvc.perform(
                get("/" + Constants.API_VERSION + "/" + Constants.SOFTWARE_PLATFORMS + "/" + testId.toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getSoftwarePlatform_returnElement() throws Exception {
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setId(UUID.randomUUID());
        softwarePlatform.setName("test software platform");
        when(softwarePlatformService.findById(softwarePlatform.getId())).thenReturn(softwarePlatform);

        MvcResult result = mockMvc.perform(get("/" + Constants.API_VERSION + "/" + Constants.SOFTWARE_PLATFORMS + "/" + softwarePlatform.getId())
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        EntityModel<SoftwarePlatformDto> softwarePlatformDtoEntity = mapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(softwarePlatformDtoEntity.getContent().getId(), softwarePlatform.getId());
        assertEquals(softwarePlatformDtoEntity.getContent().getName(), softwarePlatform.getName());
    }

    @Test
    public void deleteSoftwarePlatform_returnNotFound() throws Exception {
        UUID testId = UUID.randomUUID();
        doThrow(new NoSuchElementException()).when(softwarePlatformService).findById(testId);

        mockMvc.perform(delete("/" + Constants.API_VERSION + "/" + Constants.SOFTWARE_PLATFORMS + "/" + testId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteSoftwarePlatform_returnOk() throws Exception {
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setId(UUID.randomUUID());
        softwarePlatform.setName("test software platform");
        when(softwarePlatformService.findById(softwarePlatform.getId())).thenReturn(softwarePlatform);

        mockMvc.perform(delete("/" + Constants.API_VERSION + "/" + Constants.SOFTWARE_PLATFORMS + "/" + softwarePlatform.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
