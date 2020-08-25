package org.planqk.atlas.web.controller;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.services.CloudServiceService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@WebMvcTest(controllers = {CloudServiceController.class})
@ExtendWith({MockitoExtension.class})
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class CloudServiceControllerTest {

    @MockBean
    private CloudServiceService cloudServiceService;
    @MockBean
    private LinkingService linkingService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();
    private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/");

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);

    @Test
    public void addCloudService_returnBadRequest() throws Exception {
        var resource = new CloudServiceDto();
        resource.setId(UUID.randomUUID());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .createCloudService(null)).toUriString();

        mockMvc.perform(post(url).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void addCloudService_returnCreated() throws Exception {
        var service = new CloudServiceDto();
        service.setId(UUID.randomUUID());
        service.setName("Hello World");

        var returnedService = new CloudService();
        returnedService.setName(service.getName());
        returnedService.setId(service.getId());

        doReturn(returnedService).when(cloudServiceService).create(any());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .createCloudService(null)).toUriString();

        mockMvc.perform(post(url).content(mapper.writeValueAsString(service))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(service.getId().toString()))
                .andExpect(jsonPath("$.name").value(service.getName()));
    }

    @Test
    public void updateCloudService_returnNotFound() throws Exception {
        var resource = new CloudServiceDto();
        resource.setId(UUID.randomUUID());
        resource.setName("Hello World");

        doThrow(new NoSuchElementException()).when(cloudServiceService).update(any());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .updateCloudService(null)).toUriString();

        mockMvc.perform(put(url).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void updateCloudService_returnBadRequest() throws Exception {
        var resource = new CloudServiceDto();
        resource.setId(UUID.randomUUID());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .updateCloudService(null)).toUriString();

        mockMvc.perform(put(url).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateCloudService_returnOk() throws Exception {
        var resource = new CloudServiceDto();
        resource.setId(UUID.randomUUID());
        resource.setName("Hello World");

        var returnedResource = new CloudService();
        returnedResource.setName(resource.getName());
        returnedResource.setId(resource.getId());

        doReturn(returnedResource).when(cloudServiceService).update(any());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .updateCloudService(null)).toUriString();

        mockMvc.perform(put(url).content(mapper.writeValueAsString(resource))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(resource.getId().toString()))
                .andExpect(jsonPath("$.name").value(resource.getName()));
    }

    @Test
    void getCloudService_returnOk() throws Exception {
        var resource = new CloudService();
        resource.setId(UUID.randomUUID());
        resource.setName("Test");

        doReturn(resource).when(cloudServiceService).findById(any());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .getCloudService(resource.getId())).toUriString();

        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resource.getId().toString()))
                .andExpect(jsonPath("$.name").value(resource.getName()));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void listCloudServices_empty() throws Exception {
        doReturn(Page.empty()).when(cloudServiceService).findAll(any());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .getCloudServices(null)).toUriString();

        var mvcResult = mockMvc.perform(get(url).queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void searchCloudServices_empty() throws Exception {
        doReturn(Page.empty()).when(cloudServiceService).searchAllByName(any(), any());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .getCloudServices(null)).toUriString();

        var mvcResult = mockMvc.perform(get(url).queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .queryParam(Constants.SEARCH, "hello")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void listCloudService_notEmpty() throws Exception {
        var inputList = new ArrayList<CloudService>();
        for (int i = 0; i < 50; i++) {
            var element = new CloudService();
            element.setName("Test Element " + i);
            element.setId(UUID.randomUUID());
            inputList.add(element);
        }
        doReturn(new PageImpl<>(inputList)).when(cloudServiceService).findAll(any());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .getCloudServices(null)).toUriString();

        var mvcResult = mockMvc.perform(get(url).queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        var dtoElements = ObjectMapperUtils.mapResponseToList(
                mvcResult.getResponse().getContentAsString(),
                "cloudServices",
                CloudServiceDto.class
        );
        assertThat(dtoElements.size()).isEqualTo(inputList.size());
        // Ensure every element in the input array also exists in the output array.
        inputList.forEach(e -> {
            assertThat(dtoElements.stream().filter(dtoElem -> e.getId().equals(dtoElem.getId())).count()).isEqualTo(1);
        });
    }

    @Test
    void deleteCloudService_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(cloudServiceService).delete(any());
        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .deleteCloudService(UUID.randomUUID())).toUriString();
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCloudService_returnOk() throws Exception {
        doNothing().when(cloudServiceService).delete(any());
        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .deleteCloudService(UUID.randomUUID())).toUriString();
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void listComputeResourcesOfCloudService_empty() throws Exception {
        doReturn(Page.empty()).when(cloudServiceService).findComputeResources(any(), any());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .getComputeResourcesOfCloudService(UUID.randomUUID(), null)).toUriString();
        var mvcResult = mockMvc.perform(get(url).queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var page = ObjectMapperUtils.getPageInfo(mvcResult.getResponse().getContentAsString());

        assertThat(page.getSize()).isEqualTo(0);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void listComputeResourcesOfCloudService_notEmpty() throws Exception {
        var inputList = new ArrayList<ComputeResource>();
        for (int i = 0; i < 50; i++) {
            var element = new ComputeResource();
            element.setName("Test Element " + i);
            element.setId(UUID.randomUUID());
            inputList.add(element);
        }
        doReturn(new PageImpl<>(inputList)).when(cloudServiceService).findComputeResources(any(), any());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .getComputeResourcesOfCloudService(UUID.randomUUID(), null)).toUriString();

        var mvcResult = mockMvc.perform(get(url).queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var dtoElements = ObjectMapperUtils.mapResponseToList(
                mvcResult.getResponse().getContentAsString(),
                "computeResources",
                ComputeResourceDto.class
        );
        assertThat(dtoElements.size()).isEqualTo(inputList.size());
        // Ensure every element in the input array also exists in the output array.
        inputList.forEach(e -> {
            assertThat(dtoElements.stream().filter(dtoElem -> e.getId().equals(dtoElem.getId())).count()).isEqualTo(1);
        });
    }

    @Test
    void linkCloudServiceToComputeResource_returnOk() throws Exception {
        doNothing().when(linkingService).linkCloudServiceAndComputeResource(any(), any());
        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .linkCloudServiceAndComputeResource(UUID.randomUUID(), UUID.randomUUID())).toUriString();
        mockMvc.perform(post(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void linkCloudServiceToComputeResource_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(linkingService).linkCloudServiceAndComputeResource(any(), any());

        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .linkCloudServiceAndComputeResource(UUID.randomUUID(), UUID.randomUUID())).toUriString();
        mockMvc.perform(post(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void unlinkCloudServiceToComputeResource_returnOk() throws Exception {
        doNothing().when(linkingService).unlinkCloudServiceAndComputeResource(any(), any());
        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .unlinkCloudServiceAndComputeResource(UUID.randomUUID(), UUID.randomUUID())).toUriString();
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void unlinkCloudServiceToComputeResource_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkCloudServiceAndComputeResource(any(), any());
        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .unlinkCloudServiceAndComputeResource(UUID.randomUUID(), UUID.randomUUID())).toUriString();
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void unlinkCloudServiceToComputeResource_returnBadRequest() throws Exception {
        doThrow(new ConsistencyException()).when(linkingService).unlinkCloudServiceAndComputeResource(any(), any());
        var url = fromMethodCall(uriBuilder, on(CloudServiceController.class)
                .unlinkCloudServiceAndComputeResource(UUID.randomUUID(), UUID.randomUUID())).toUriString();
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}
