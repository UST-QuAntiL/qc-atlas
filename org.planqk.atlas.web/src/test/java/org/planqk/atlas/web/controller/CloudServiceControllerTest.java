package org.planqk.atlas.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.services.CloudServiceService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.planqk.atlas.web.linkassembler.CloudServiceAssembler;
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
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CloudServiceController.class})
@ExtendWith({MockitoExtension.class})
@AutoConfigureMockMvc
public class CloudServiceControllerTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public CloudServiceAssembler getCloudServiceAssembler() {
            return new CloudServiceAssembler();
        }

        @Bean
        public PagedResourcesAssembler<CloudServiceDto> getPagedResourcesAssembler() {
            return new PagedResourcesAssembler<>(null, null);
        }
    }

    @MockBean
    private CloudServiceService cloudServiceService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    private final int page = 0;
    private final int size = 10;
    private final Pageable pageable = PageRequest.of(page, size);

    private final String cloudServiceDtoJSONName = "cloudServiceDtoes";

    @BeforeEach
    public void init() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void addCloudService_returnBadRequest() throws Exception {
        CloudServiceDto cloudServiceDto = new CloudServiceDto();
        cloudServiceDto.setId(UUID.randomUUID());

        mockMvc.perform(put("/" + Constants.CLOUD_SERVICES + "/")
                .content(mapper.writeValueAsString(cloudServiceDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addCloudService_returnCreate() throws Exception {
        CloudService cloudService = new CloudService();
        cloudService.setId(UUID.randomUUID());
        cloudService.setName("test cloud service");
        CloudServiceDto cloudServiceDto = ModelMapperUtils.convert(cloudService, CloudServiceDto.class);

        when(cloudServiceService.save(any(CloudService.class))).thenReturn(cloudService);

        MvcResult result = mockMvc.perform(put("/" + Constants.CLOUD_SERVICES + "/")
                .content(mapper.writeValueAsString(cloudServiceDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<CloudServiceDto> resultDtoEntity = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(cloudServiceDto.getId(), resultDtoEntity.getContent().getId());
        assertEquals(cloudServiceDto.getName(), resultDtoEntity.getContent().getName());
    }

    @Test
    public void getCloudServices_withEmptySet() throws Exception {
        when(cloudServiceService.findAll(pageable)).thenReturn(Page.empty());

        MvcResult result = mockMvc.perform(get("/" + Constants.CLOUD_SERVICES + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        PagedModel<EntityModel<CloudServiceDto>> pagedDtoEntities = mapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(0, pagedDtoEntities.getContent().size());
    }

    @Test
    public void getCloudServices_withOneElement() throws Exception {
        List<CloudService> cloudServices = new ArrayList<>();
        CloudService cloudService = new CloudService();
        cloudService.setId(UUID.randomUUID());
        cloudService.setName("test software platform");
        cloudServices.add(cloudService);

        Page<CloudService> cloudServicePage = new PageImpl<>(cloudServices);

        when(cloudServiceService.findAll(pageable)).thenReturn(cloudServicePage);

        MvcResult result = mockMvc.perform(get("/" + Constants.CLOUD_SERVICES + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONObject rootObject = new JSONObject(result.getResponse().getContentAsString());
        var embeddedResources = rootObject.getJSONObject("_embedded").getJSONArray(cloudServiceDtoJSONName);

        var responseDto = mapper.readValue(embeddedResources.getJSONObject(0).toString(), CloudServiceDto.class);

        assertEquals(1, embeddedResources.length());
        assertEquals(responseDto.getId(), cloudService.getId());
        assertEquals(responseDto.getName(), cloudService.getName());
    }

    @Test
    public void getCloudServices_withMultipleElements() throws Exception {
        List<CloudService> cloudServices = new ArrayList<>();
        CloudService cloudService;
        for (int i = 0; i < size; i++) {
            cloudService = new CloudService();
            cloudService.setId(UUID.randomUUID());
            cloudService.setName("test cloud service " + i);
            cloudServices.add(cloudService);
        }

        Page<CloudService> cloudServicePage = new PageImpl<>(cloudServices);

        when(cloudServiceService.findAll(pageable)).thenReturn(cloudServicePage);

        MvcResult result = mockMvc.perform(get("/" + Constants.CLOUD_SERVICES + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONObject rootObject = new JSONObject(result.getResponse().getContentAsString());
        var embeddedResources = rootObject.getJSONObject("_embedded").getJSONArray(cloudServiceDtoJSONName);

        assertEquals(embeddedResources.length(), size);
    }

    @Test
    public void getCloudService_returnNotFound() throws Exception {
        UUID testId = UUID.randomUUID();
        when(cloudServiceService.findById(testId)).thenThrow(NoSuchElementException.class);

        mockMvc.perform(get("/" + Constants.CLOUD_SERVICES + "/" + testId.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCloudService_returnElement() throws Exception {
        CloudService cloudService = new CloudService();
        cloudService.setId(UUID.randomUUID());
        cloudService.setName("test software platform");
        when(cloudServiceService.findById(cloudService.getId())).thenReturn(cloudService);

        MvcResult result = mockMvc.perform(get("/" + Constants.CLOUD_SERVICES + "/" + cloudService.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<CloudServiceDto> cloudServiceDtoEntity = mapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(cloudServiceDtoEntity.getContent().getId(), cloudService.getId());
        assertEquals(cloudServiceDtoEntity.getContent().getName(), cloudService.getName());
    }

    @Test
    public void deleteCloudService_returnNotFound() throws Exception {
        UUID testId = UUID.randomUUID();
        doThrow(new NoSuchElementException()).when(cloudServiceService).delete(testId);

        mockMvc.perform(delete("/" + Constants.CLOUD_SERVICES + "/" + testId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteCloudService_returnOk() throws Exception {
        UUID testId = UUID.randomUUID();
        doNothing().when(cloudServiceService).delete(testId);

        mockMvc.perform(delete("/" + Constants.CLOUD_SERVICES + "/" + testId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
