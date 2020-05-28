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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.planqk.atlas.core.model.Provider;
import org.planqk.atlas.core.services.ProviderService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ProviderDto;
import org.planqk.atlas.web.dtos.ProviderListDto;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ProviderControllerTest {

    @Mock
    private ProviderService providerService;

    private ProviderController providerController;

    private MockMvc mockMvc;

    private int page = 0;
    private int size = 2;
    private Pageable pageable = PageRequest.of(page, size);

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        this.providerController = new ProviderController(providerService);
        mockMvc = MockMvcBuilders.standaloneSetup(providerController).build();
    }

    @Test
    public void setupTest() {
        assertNotNull(mockMvc);
    }

    @Test
    public void getProviders_withoutPagination() throws Exception {
        when(providerService.findAll(Pageable.unpaged())).thenReturn(Page.empty());
        mockMvc.perform(get("/" + Constants.PROVIDERS + "/")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void getProviders_withEmptyProviderList() throws Exception {
        when(providerService.findAll(pageable)).thenReturn(Page.empty());
        MvcResult result = mockMvc.perform(get("/" + Constants.PROVIDERS + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        ProviderListDto providerListDto = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ProviderListDto.class);
        assertEquals(providerListDto.getProviderDtoList().size(), 0);
    }

    @Test
    public void getProviders_withOneProvider() throws Exception {
        List<Provider> providerList = new ArrayList<>();

        UUID provId = UUID.randomUUID();

        Provider provider = new Provider();
        provider.setId(provId);
        providerList.add(provider);

        when(providerService.findAll(pageable)).thenReturn(new PageImpl<>(providerList));

        MvcResult result = mockMvc.perform(get("/" + Constants.PROVIDERS + "/")
                .queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        ProviderListDto providerListDto = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ProviderListDto.class);
        assertEquals(providerListDto.getProviderDtoList().size(), 1);
        assertEquals(providerListDto.getProviderDtoList().get(0).getId(), provId);
    }

    @Test
    public void getProvider_returnNotFound() throws Exception {
        mockMvc.perform(get("/" + Constants.PROVIDERS + "/" + UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void getProvider_returnProvider() throws Exception {
        UUID provId = UUID.randomUUID();
        Provider provider = new Provider();
        provider.setId(provId);
        when(providerService.findById(provId)).thenReturn(Optional.of(provider));

        MvcResult result = mockMvc.perform(get("/" + Constants.PROVIDERS + "/" + provId)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        ProviderDto response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ProviderDto.class);
        assertEquals(response.getId(), provId);
    }

    @Test
    public void createProvider_returnBadRequest() throws Exception {
        ProviderDto providerDto = new ProviderDto();
        providerDto.setName("IBM");

        mockMvc.perform(post("/" + Constants.PROVIDERS + "/")
                .content(new ObjectMapper().writeValueAsString(providerDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void createProvider_returnProvider() throws Exception {
        ProviderDto providerDto = new ProviderDto();
        providerDto.setName("IBM");
        providerDto.setAccessKey("123");
        providerDto.setSecretKey("456");
        Provider provider = ProviderDto.Converter.convert(providerDto);
        when(providerService.save(provider)).thenReturn(provider);

        MvcResult result = mockMvc.perform(post("/" + Constants.PROVIDERS + "/")
                .content(new ObjectMapper().writeValueAsString(providerDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        ProviderDto response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ProviderDto.class);
        assertEquals(response.getName(), providerDto.getName());
    }
}
