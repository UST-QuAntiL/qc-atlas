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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.model.Status;
import org.planqk.atlas.core.services.DiscussionCommentService;
import org.planqk.atlas.core.services.DiscussionTopicService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.DiscussionTopicDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest( {DiscussionCommentController.class, DiscussionTopicController.class})
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class DiscussionTopicControllerTest {

    @MockBean
    private DiscussionTopicService discussionTopicService;
    @MockBean
    private DiscussionCommentService discussionCommentService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    private final int page = 0;
    private final int size = 1;
    private final Pageable pageable = PageRequest.of(page, size);

    private DiscussionTopicDto discussionTopicDto;
    private DiscussionTopic discussionTopic;

    private List<DiscussionTopic> discussionTopicList;
    private Page<DiscussionTopic> discussionTopicPage;
    private Page<DiscussionTopicDto> discussionTopicPageDto;

    @BeforeEach
    public void init() {
        discussionTopic = new DiscussionTopic();
        discussionTopic.setDescription("Description");
        discussionTopic.setTitle("Topic");
        discussionTopic.setDate(OffsetDateTime.now());
        discussionTopic.setId(UUID.randomUUID());
        discussionTopic.setStatus(Status.OPEN);
        discussionTopicDto = ModelMapperUtils.convert(discussionTopic, DiscussionTopicDto.class);
        discussionTopicList = new ArrayList<>();
        discussionTopicList.add(discussionTopic);
        discussionTopicPage = new PageImpl<>(discussionTopicList);
        discussionTopicPageDto = ModelMapperUtils.convertPage(discussionTopicPage, DiscussionTopicDto.class);
    }

    @Test
    public void createDiscussionTopic_returnDiscussionTopic() throws Exception {
        when(discussionTopicService.create(any())).thenReturn(discussionTopic);
        discussionTopicDto.setId(null);

        var url = linkBuilderService.urlStringTo(methodOn(DiscussionTopicController.class)
                .createDiscussionTopic(discussionTopicDto));
        MvcResult result = mockMvc
                .perform(post(url)
                        .content(mapper.writeValueAsString(discussionTopicDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<DiscussionTopicDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(response.getContent().getDate(), discussionTopicDto.getDate());
        assertEquals(response.getContent().getTitle(), discussionTopicDto.getTitle());
        assertEquals(response.getContent().getStatus(), discussionTopicDto.getStatus());
    }

    @Test
    public void createDiscussionTopic_returnBadRequest() throws Exception {
        // Missing required attribute
        discussionTopicDto.setDate(null);
        var url = linkBuilderService.urlStringTo(methodOn(DiscussionTopicController.class)
                .createDiscussionTopic(discussionTopicDto));
        mockMvc.perform(post(url)
                .content(mapper.writeValueAsString(discussionTopicDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void getDiscussionTopics_returnDiscussionTopics() throws Exception {
        when(discussionTopicService.findAll(pageable)).thenReturn(discussionTopicPage);
        var url = linkBuilderService.urlStringTo(methodOn(DiscussionTopicController.class)
                .getDiscussionTopics(new ListParameters(pageable, null)));
        MvcResult result = mockMvc
                .perform(get(url).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JSONObject rootObject = new JSONObject(result.getResponse().getContentAsString());
        var embeddedJSONObjects = rootObject.getJSONObject("_embedded").getJSONArray("discussionTopics");
        var resultObject = mapper.readValue(embeddedJSONObjects.getJSONObject(0).toString(), DiscussionTopicDto.class);

        assertEquals(1, embeddedJSONObjects.length());
        assertEquals(resultObject.getTitle(), discussionTopicDto.getTitle());
        assertEquals(resultObject.getId(), discussionTopicDto.getId());
        assertEquals(resultObject.getDate(), discussionTopicDto.getDate());
    }

    @Test
    public void getDiscussionTopic_returnDiscussionTopic() throws Exception {
        when(discussionTopicService.findById(discussionTopic.getId())).thenReturn(discussionTopic);

        var url = linkBuilderService.urlStringTo(methodOn(DiscussionTopicController.class)
                .getDiscussionTopic(discussionTopic.getId()));
        MvcResult result = mockMvc.perform(
                get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<DiscussionTopicDto> response = mapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(response.getContent().getId(), discussionTopicDto.getId());
        assertEquals(response.getContent().getTitle(), discussionTopicDto.getTitle());
        assertEquals(response.getContent().getDate(), discussionTopicDto.getDate());
        assertEquals(response.getContent().getStatus(), discussionTopicDto.getStatus());
    }

    @Test
    public void getDiscussionTopic_returnNotFound() throws Exception {
        when(discussionTopicService.findById(any(UUID.class))).thenThrow(new NoSuchElementException());

        var url = linkBuilderService.urlStringTo(methodOn(DiscussionTopicController.class)
                .getDiscussionTopic(discussionTopic.getId()));
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteDiscussionTopic_returnOK() throws Exception {
        var url = linkBuilderService.urlStringTo(methodOn(DiscussionTopicController.class)
                .deleteDiscussionTopic(discussionTopic.getId()));
        mockMvc.perform(delete(url))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void deleteDiscussionTopic_returnNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new NoSuchElementException()).when(discussionTopicService).delete(id);
        var url = linkBuilderService.urlStringTo(methodOn(DiscussionTopicController.class)
                .deleteDiscussionTopic(id));
        mockMvc.perform(delete(url).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void updateDiscussionTopic_returnDiscussionTopic() throws Exception {
        when(discussionTopicService.update(discussionTopic)).thenReturn(discussionTopic);
        var url = linkBuilderService.urlStringTo(methodOn(DiscussionTopicController.class)
                .updateDiscussionTopic(discussionTopic.getId(), discussionTopicDto));
        MvcResult result = mockMvc.perform(put(url)
                .content(mapper.writeValueAsString(discussionTopicDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        EntityModel<DiscussionTopicDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(response.getContent().getId(), discussionTopicDto.getId());
        assertEquals(response.getContent().getTitle(), discussionTopicDto.getTitle());
        assertEquals(response.getContent().getDate(), discussionTopicDto.getDate());
        assertEquals(response.getContent().getStatus(), discussionTopicDto.getStatus());
    }

    @Test
    public void updateDiscussionTopic_returnBadRequest() throws Exception {

        // Missing required attribute
        discussionTopic.setDate(null);
        when(discussionTopicService.update(discussionTopic)).thenReturn(discussionTopic);

        var url = linkBuilderService.urlStringTo(methodOn(DiscussionTopicController.class)
                .updateDiscussionTopic(discussionTopic.getId(), discussionTopicDto));
        mockMvc.perform(put(url)
                .content(mapper.writeValueAsString(discussionTopic)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}
