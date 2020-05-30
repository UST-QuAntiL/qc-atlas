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
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.planqk.atlas.core.model.Provider;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.services.ProviderService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ProviderDto;
import org.planqk.atlas.web.linkassembler.ProviderAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ProviderControllerTest {

	@Mock
	private ProviderService providerService;
	@Mock
	private PagedResourcesAssembler<ProviderDto> paginationAssembler;
	@Mock
	private ProviderAssembler providerAssembler;

	@InjectMocks
	private ProviderController providerController;

	private MockMvc mockMvc;

	private int page = 0;
	private int size = 2;
	private Pageable pageable = PageRequest.of(page, size);

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(providerController).setControllerAdvice(new RestErrorHandler())
				.build();
	}

	@Test
	public void setupTest() {
		assertNotNull(mockMvc);
	}

	@Test
	public void getProviders_withoutPagination() throws Exception {
		when(providerService.findAll(Pageable.unpaged())).thenReturn(Page.empty());
		when(paginationAssembler.toModel(ArgumentMatchers.<Page<ProviderDto>>any()))
				.thenReturn(HateoasUtils.generatePagedModel(Page.empty()));
		doNothing().when(providerAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<ProviderDto>>>any());

		mockMvc.perform(get("/" + Constants.PROVIDERS + "/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void getProviders_withEmptyProviderList() throws Exception {
		when(providerService.findAll(pageable)).thenReturn(Page.empty());
		when(paginationAssembler.toModel(ArgumentMatchers.<Page<ProviderDto>>any()))
				.thenReturn(HateoasUtils.generatePagedModel(Page.empty()));
		doNothing().when(providerAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<ProviderDto>>>any());

		MvcResult result = mockMvc
				.perform(get("/" + Constants.PROVIDERS + "/").queryParam(Constants.PAGE, Integer.toString(page))
						.queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		PagedModel<EntityModel<ProviderDto>> providerListDto = new ObjectMapper().readValue(
				result.getResponse().getContentAsString(), new TypeReference<PagedModel<EntityModel<ProviderDto>>>() {
				});
		assertEquals(providerListDto.getContent().size(), 0);
	}

	@Test
	public void getProviders_withOneProvider() throws Exception {
		List<Provider> providerList = new ArrayList<>();

		UUID provId = UUID.randomUUID();

		Provider provider = new Provider();
		provider.setId(provId);
		providerList.add(provider);

		Page<Provider> pageEntity = new PageImpl<Provider>(providerList, pageable, providerList.size());
		Page<ProviderDto> pageDto = ModelMapperUtils.convertPage(pageEntity, ProviderDto.class);

		when(providerService.findAll(pageable)).thenReturn(pageEntity);
		when(paginationAssembler.toModel(ArgumentMatchers.<Page<ProviderDto>>any()))
				.thenReturn(HateoasUtils.generatePagedModel(pageDto));
		doNothing().when(providerAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<ProviderDto>>>any());

		MvcResult result = mockMvc
				.perform(get("/" + Constants.PROVIDERS + "/").queryParam(Constants.PAGE, Integer.toString(page))
						.queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		PagedModel<EntityModel<ProviderDto>> providerListDto = new ObjectMapper().readValue(
				result.getResponse().getContentAsString(), new TypeReference<PagedModel<EntityModel<ProviderDto>>>() {
				});
		List<EntityModel<ProviderDto>> resultList = new ArrayList<>(providerListDto.getContent());

		assertEquals(resultList.size(), 1);
		assertEquals(resultList.get(0).getContent().getId(), provId);
	}

	@Test
	public void getProvider_returnNotFound() throws Exception {
		when(providerService.findById(any(UUID.class))).thenThrow(new NotFoundException());
		mockMvc.perform(get("/" + Constants.PROVIDERS + "/" + UUID.randomUUID()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getProvider_returnProvider() throws Exception {
		UUID provId = UUID.randomUUID();
		Provider provider = new Provider();
		provider.setId(provId);

		when(providerService.findById(provId)).thenReturn(provider);
		doNothing().when(providerAssembler).addLinks(ArgumentMatchers.<EntityModel<ProviderDto>>any());

		MvcResult result = mockMvc
				.perform(get("/" + Constants.PROVIDERS + "/" + provId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		EntityModel<ProviderDto> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(),
				new TypeReference<EntityModel<ProviderDto>>() {
				});
		assertEquals(response.getContent().getId(), provId);
	}

	@Test
	public void createProvider_returnBadRequest() throws Exception {
		ProviderDto providerDto = new ProviderDto();
		providerDto.setName("IBM");

		mockMvc.perform(
				post("/" + Constants.PROVIDERS + "/").content(new ObjectMapper().writeValueAsString(providerDto))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createProvider_returnProvider() throws Exception {
		ProviderDto providerDto = new ProviderDto();
		providerDto.setName("IBM");
		providerDto.setAccessKey("123");
		providerDto.setSecretKey("456");
		Provider provider = ModelMapperUtils.convert(providerDto, Provider.class);

		when(providerService.save(provider)).thenReturn(provider);
		doNothing().when(providerAssembler).addLinks(ArgumentMatchers.<EntityModel<ProviderDto>>any());

		MvcResult result = mockMvc
				.perform(post("/" + Constants.PROVIDERS + "/")
						.content(new ObjectMapper().writeValueAsString(providerDto))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();

		EntityModel<ProviderDto> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(),
				new TypeReference<EntityModel<ProviderDto>>() {
				});
		assertEquals(response.getContent().getName(), providerDto.getName());
	}
}
