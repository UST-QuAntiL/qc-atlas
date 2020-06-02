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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.controller.util.TestControllerUtils;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.dtos.TagListDto;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TagController.class})
@ExtendWith({MockitoExtension.class})
@AutoConfigureMockMvc
public class TagControllerTest {

    @MockBean
    private TagService tagService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @BeforeEach
    public void init() {
        mapper = ObjectMapperUtils.newTestMapper();
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
        when(tagService.findAll(any(Pageable.class))).thenReturn(page);

        MvcResult mvcResult = mockMvc.perform(get("/" + Constants.TAGS + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        TagListDto tagList = mapper.readValue(mvcResult.getResponse().getContentAsString(), TagListDto.class);
        assertEquals(tagList.getTagsDtos().size(), 2);
    }

    @Test
    public void getTags_withEmptyTagList() throws Exception {
        when(tagService.findAll(any(Pageable.class))).thenReturn(Page.empty());
        MvcResult result = mockMvc.perform(get("/" + Constants.TAGS + "/")
                .queryParam(Constants.PAGE, Integer.toString(0))
                .queryParam(Constants.SIZE, Integer.toString(4))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        TagListDto tagListDto = mapper.readValue(result.getResponse().getContentAsString(), TagListDto.class);
        assertEquals(tagListDto.getTagsDtos().size(), 0);
    }

    @Test
    public void testGetId() throws Exception {
        Tag tag1 = getTestTag();
        when(tagService.getTagById(any(UUID.class))).thenReturn(java.util.Optional.of(tag1));

        MvcResult mvcResult = mockMvc.perform(get("/" + Constants.TAGS + "/" + tag1.getId() + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        TagDto createdTag = mapper.readValue(mvcResult.getResponse().getContentAsString(), TagDto.class);
        assertEquals(createdTag.getKey(), tag1.getKey());
        assertEquals(createdTag.getValue(), tag1.getValue());
    }

    @Test
    public void testPostTag() throws Exception {
        Tag tag1 = getTestTag();

        when(tagService.save(tag1)).thenReturn(tag1);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/" + Constants.TAGS + "/")
                .content(TestControllerUtils.asJsonString(tag1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        TagDto createdTag = mapper.readValue(result.getResponse().getContentAsString(), TagDto.class);
        assertEquals(createdTag.getKey(), tag1.getKey());
        assertEquals(createdTag.getValue(), tag1.getValue());
    }
}
