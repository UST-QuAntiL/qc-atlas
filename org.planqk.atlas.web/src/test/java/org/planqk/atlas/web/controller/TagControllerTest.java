/*******************************************************************************
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

import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.controller.util.TestControllerUtils;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.TagAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class TagControllerTest {
    @MockBean
    private TagService tagService;
    @MockBean
    private TagAssembler tagAssembler;
    @MockBean
    private PagedResourcesAssembler<TagDto> paginationAssembler;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @BeforeEach
    public void before() {
        this.mapper = ObjectMapperUtils.newTestMapper();
    }

    private Tag getTestTag() {
        UUID tagId = UUID.randomUUID();
        Tag tag1 = new Tag();
        tag1.setId(tagId);
        tag1.setKey("testkey");
        tag1.setValue("testvalue");
        return tag1;
    }

    @Test
    public void testGetAllTags() throws Exception {
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = getTestTag();
        tags.add(tag1);
        tags.add(new Tag());
        Pageable pageable = PageRequest.of(0, 2);

        Page<Tag> page = new PageImpl<Tag>(tags, pageable, tags.size());
        Page<TagDto> pageDto = ModelMapperUtils.convertPage(page, TagDto.class);

        when(tagService.findAll(any(Pageable.class))).thenReturn(page);
        when(paginationAssembler.toModel(ArgumentMatchers.any())).thenReturn(HateoasUtils.generatePagedModel(pageDto));
        doNothing().when(tagAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<TagDto>>>any());

        MvcResult result = mockMvc.perform(get("/" + Constants.TAGS + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(), "tagDtoes",
                TagDto.class);
        assertEquals(2, resultList.size());
    }

    @Test
    public void getTags_withEmptyTagList() throws Exception {
        when(tagService.findAll(any(Pageable.class))).thenReturn(Page.empty());
        when(paginationAssembler.toModel(ArgumentMatchers.any()))
                .thenReturn(HateoasUtils.generatePagedModel(Page.empty()));
        doNothing().when(tagAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<TagDto>>>any());

        MvcResult result = mockMvc
                .perform(get("/" + Constants.TAGS + "/").queryParam(Constants.PAGE, Integer.toString(0))
                        .queryParam(Constants.SIZE, Integer.toString(4)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(), "tagDtoes",
                TagDto.class);
        assertEquals(0, resultList.size());
    }

    @Test
    public void testGetId() throws Exception {
        Tag tag1 = getTestTag();
        when(tagService.getTagById(any(UUID.class))).thenReturn(tag1);
        doNothing().when(tagAssembler).addLinks(ArgumentMatchers.<EntityModel<TagDto>>any());

        MvcResult mvcResult = mockMvc
                .perform(get("/" + Constants.TAGS + "/" + tag1.getId() + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<TagDto> createdTag = mapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<EntityModel<TagDto>>() {
                });
        assertEquals(createdTag.getContent().getKey(), tag1.getKey());
        assertEquals(createdTag.getContent().getValue(), tag1.getValue());
    }

    @Test
    public void testPostTag() throws Exception {
        Tag tag1 = getTestTag();

        when(tagService.save(tag1)).thenReturn(tag1);
        doNothing().when(tagAssembler).addLinks(ArgumentMatchers.<EntityModel<TagDto>>any());

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/" + Constants.TAGS + "/").content(TestControllerUtils.asJsonString(tag1))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<TagDto> createdTag = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<TagDto>>() {
                });
        assertEquals(createdTag.getContent().getKey(), tag1.getKey());
        assertEquals(createdTag.getContent().getValue(), tag1.getValue());
    }
}
