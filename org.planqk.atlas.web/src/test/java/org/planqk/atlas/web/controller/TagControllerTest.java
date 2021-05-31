/*******************************************************************************
 * Copyright (c) 2020-2021 the qc-atlas contributors.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TagController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class TagControllerTest {

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    private final int page = 0;

    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    @MockBean
    private TagService tagService;

    @MockBean
    private AlgorithmService algorithmService;

    @MockBean
    private ImplementationService implementationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkBuilderService linkBuilderService;

    private Tag getTestTag() {
        Tag tag1 = new Tag();
        tag1.setValue(UUID.randomUUID().toString());
        tag1.setCategory(UUID.randomUUID().toString());
        return tag1;
    }

    @Test
    public void getAllTags_SingleElement_returnOk() throws Exception {
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = getTestTag();
        tags.add(tag1);
        tags.add(new Tag());
        Pageable pageable = PageRequest.of(0, 2);

        Page<Tag> p = new PageImpl<>(tags);

        when(tagService.findAllByContent(null, pageable)).thenReturn(p);

        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .getTags(new ListParameters(pageable, null)));
        MvcResult result = mockMvc
                .perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                TagDto.class);
        assertEquals(2, resultList.size());
    }

    @Test
    public void getTags_EmptyList_returnOk() throws Exception {
        Pageable pageable = PageRequest.of(0, 2);
        when(tagService.findAllByContent(null, pageable)).thenReturn(Page.empty());

        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .getTags(new ListParameters(pageable, null)));
        MvcResult result = mockMvc
                .perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(), "tags",
                TagDto.class);
        assertEquals(0, resultList.size());
    }

    @Test
    public void getTagByName_returnOk() throws Exception {
        Tag tag1 = getTestTag();
        when(tagService.findByValue(tag1.getValue())).thenReturn(tag1);

        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .getTag(tag1.getValue()));
        MvcResult mvcResult = mockMvc
                .perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<TagDto> response = mapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(response.getContent().getValue(), tag1.getValue());
        assertEquals(response.getContent().getCategory(), tag1.getCategory());
    }

    @Test
    public void getTagByName_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(tagService).findByValue(any());
        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .getTag("test"));
        mockMvc
                .perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createTag_returnCreated() throws Exception {
        Tag tag1 = getTestTag();
        TagDto tagDto = ModelMapperUtils.convert(tag1, TagDto.class);
        when(tagService.create(tag1)).thenReturn(tag1);

        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .createTag(tagDto));
        MvcResult result = mockMvc
                .perform(post(url).content(mapper.writeValueAsString(tagDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<TagDto> createdTag = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(createdTag.getContent().getCategory(), tag1.getCategory());
        assertEquals(createdTag.getContent().getValue(), tag1.getValue());
    }

    @Test
    public void createTag_returnBadRequest() throws Exception {
        Tag tag = getTestTag();
        tag.setValue(null);
        TagDto tagDto = ModelMapperUtils.convert(tag, TagDto.class);

        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .createTag(tagDto));
        mockMvc
                .perform(post(url).content(mapper.writeValueAsString(tagDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getImplementations_EmptyList_returnOk() throws Exception {
        var tag = getTestTag();

        doReturn(tag).when(tagService).findByValue(any());
        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .getImplementationsOfTag("test"));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void getImplementations_SingleElement_returnOk() throws Exception {
        var tag = getTestTag();
        var impl = new Implementation();
        impl.setId(UUID.randomUUID());
        impl.setName("test-impl");
        tag.setImplementations(Set.of(impl));

        doReturn(tag).when(tagService).findByValue(any());
        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .getImplementationsOfTag("test"));
        MvcResult mvcResult = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        assertEquals(ObjectMapperUtils.mapResponseToList(mvcResult, ImplementationDto.class).get(0).getId(), impl.getId());
    }

    @Test
    public void getImplementations_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(tagService).findByValue(any());
        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .getImplementationsOfTag("test"));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAlgorithms_SingleElement_returnOk() throws Exception {
        var tag = getTestTag();
        var algo = new Algorithm();
        algo.setId(UUID.randomUUID());
        algo.setName("test-algo");
        algo.setComputationModel(ComputationModel.CLASSIC);
        algo.setTags(Set.of(tag));
        tag.setAlgorithms(Set.of(algo));

        doReturn(tag).when(tagService).findByValue(any());
        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .getAlgorithmsOfTag("test"));
        MvcResult mvcResult = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        assertEquals(ObjectMapperUtils.mapResponseToList(mvcResult, AlgorithmDto.class).get(0).getId(), algo.getId());
    }

    @Test
    public void getAlgorithms_EmptyList_returnOk() throws Exception {
        var tag = getTestTag();

        doReturn(tag).when(tagService).findByValue(any());
        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .getAlgorithmsOfTag("test"));
        MvcResult mvcResult = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        Assert.assertEquals(ObjectMapperUtils.mapResponseToList(mvcResult, AlgorithmDto.class).size(), 0);
    }

    @Test
    public void getAlgorithms_returnNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(tagService).findByValue(any());
        var url = linkBuilderService.urlStringTo(methodOn(TagController.class)
                .getAlgorithmsOfTag("test"));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
