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

import org.planqk.atlas.core.model.DiscussionComment;
import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.model.Status;
import org.planqk.atlas.core.services.DiscussionCommentService;
import org.planqk.atlas.core.services.DiscussionTopicService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith( {MockitoExtension.class})
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class DiscussionCommentControllerTest {
    @MockBean
    private PagedResourcesAssembler<DiscussionCommentDto> paginationAssembler;

    @MockBean
    private DiscussionCommentService discussionCommentService;

    @MockBean
    private DiscussionTopicService discussionTopicService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    private final int page = 0;
    private final int size = 1;
    private final Pageable pageable = PageRequest.of(page, size);

    private DiscussionCommentDto discussionCommentDto;
    private DiscussionComment discussionComment;
    private DiscussionTopic discussionTopic;

    List<DiscussionComment> discussionCommentList;
    Page<DiscussionComment> discussionCommentPage;
    Page<DiscussionCommentDto> discussionCommentPageDto;

    @BeforeEach
    public void init() {
        mapper = ObjectMapperUtils.newTestMapper();
        discussionTopic = new DiscussionTopic();
        discussionTopic.setDescription("Description");
        discussionTopic.setTitle("Topic");
        discussionTopic.setId(UUID.randomUUID());
        discussionTopic.setStatus(Status.CLOSED);
        discussionTopic.setDate(OffsetDateTime.now());
        discussionComment = new DiscussionComment();
        discussionComment.setId(UUID.randomUUID());
        discussionComment.setText("This is a comment");
        discussionComment.setDiscussionTopic(discussionTopic);
        discussionComment.setDate(OffsetDateTime.now());
        discussionCommentDto = ModelMapperUtils.convert(discussionComment, DiscussionCommentDto.class);
        discussionCommentList = new ArrayList<>();
        discussionCommentList.add(discussionComment);
        discussionCommentPage = new PageImpl<>(discussionCommentList);
        discussionCommentPageDto = ModelMapperUtils.convertPage(discussionCommentPage, DiscussionCommentDto.class);
    }

    @Test
    public void createDiscussionComment_returnDiscussionComment() throws Exception {
        when(discussionCommentService.save(any())).thenReturn(discussionComment);
        when(discussionTopicService.findById(discussionTopic.getId())).thenReturn(discussionTopic);

        MvcResult result = mockMvc
                .perform(post("/" + Constants.DISCUSSION_TOPICS + "/" + discussionTopic.getId() + "/" + Constants.DISCUSSION_COMMENTS).content(mapper.writeValueAsString(discussionCommentDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<DiscussionCommentDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<DiscussionCommentDto>>() {
                });

        assertEquals(response.getContent().getText(), discussionCommentDto.getText());
        assertEquals(response.getContent().getId(), discussionCommentDto.getId());
    }

    @Test
    public void createDiscussionComment_returnBadRequest() throws Exception {
        when(discussionTopicService.findById(discussionTopic.getId())).thenReturn(discussionTopic);

        // Missing required attribute
        discussionCommentDto.setDate(null);
        mockMvc.perform(post("/" + Constants.DISCUSSION_TOPICS + "/" + discussionTopic.getId() + "/" + Constants.DISCUSSION_COMMENTS + "/")
                .content(mapper.writeValueAsString(discussionCommentDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void getDiscussionComments_returnDiscussionComments() throws Exception {
        when(discussionCommentService.findAllByTopic(discussionTopic.getId(), pageable)).thenReturn(discussionCommentPage);
        when(paginationAssembler.toModel(ArgumentMatchers.any()))
                .thenReturn(HateoasUtils.generatePagedModel(discussionCommentPageDto));
        when(discussionTopicService.findById(discussionTopic.getId())).thenReturn(discussionTopic);

        MvcResult result = mockMvc
                .perform(get("/" + Constants.DISCUSSION_TOPICS + "/" + discussionTopic.getId() + "/" + Constants.DISCUSSION_COMMENTS + "/").queryParam(Constants.PAGE, Integer.toString(page))
                        .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(),
                "discussionCommentDtoes", DiscussionCommentDto.class);

        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getText(), discussionCommentDto.getText());
        assertEquals(resultList.get(0).getId(), discussionCommentDto.getId());
    }

    @Test
    public void deleteDiscussionComment_returnNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(discussionCommentService.findById(id)).thenReturn(discussionComment);

        doThrow(new NoSuchElementException()).when(discussionCommentService).deleteById(id);
        mockMvc.perform(delete("/" + Constants.DISCUSSION_TOPICS + "/" + discussionTopic.getId() + "/" + Constants.DISCUSSION_COMMENTS + "/" + id).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void getDiscussionComment_returnDiscussionComment() throws Exception {
        when(discussionCommentService.findById(discussionComment.getId())).thenReturn(discussionComment);

        MvcResult result = mockMvc.perform(
                get("/" + Constants.DISCUSSION_TOPICS + "/" + discussionTopic.getId() + "/" + Constants.DISCUSSION_COMMENTS + "/" + discussionComment.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<DiscussionCommentDto> response = mapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<EntityModel<DiscussionCommentDto>>() {
                });

        assertEquals(response.getContent().getId(), discussionCommentDto.getId());
        assertEquals(response.getContent().getText(), discussionCommentDto.getText());
        assertEquals(response.getContent().getDate(), discussionCommentDto.getDate());
    }

    @Test
    public void getDiscussionComment_returnNotFound() throws Exception {
        when(discussionCommentService.findById(any(UUID.class))).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/" + Constants.DISCUSSION_TOPICS + "/" + discussionTopic.getId() + "/" + Constants.DISCUSSION_COMMENTS + "/" + UUID.randomUUID()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteDiscussionComment_returnOK() throws Exception {
        when(discussionCommentService.findById(discussionComment.getId())).thenReturn(discussionComment);
        mockMvc.perform(delete("/" + Constants.DISCUSSION_TOPICS + "/" + discussionTopic.getId() + "/" + Constants.DISCUSSION_COMMENTS + "/{id}", this.discussionComment.getId()))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void updateDiscussionComment_returnDiscussionComment() throws Exception {
        when(discussionCommentService.findById(discussionComment.getId())).thenReturn(discussionComment);
        when(discussionCommentService.update(discussionComment.getId(), discussionComment)).thenReturn(discussionComment);

        MvcResult result = mockMvc.perform(put("/" + Constants.DISCUSSION_TOPICS + "/" + discussionTopic.getId() + "/" + Constants.DISCUSSION_COMMENTS + "/" + discussionComment.getId())
                .content(mapper.writeValueAsString(discussionCommentDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        EntityModel<DiscussionCommentDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<DiscussionCommentDto>>() {
                });

        assertEquals(response.getContent().getText(), discussionCommentDto.getText());
        assertEquals(response.getContent().getId(), discussionCommentDto.getId());
    }

    @Test
    public void updateDiscussionComment_returnBadRequest() throws Exception {
        when(discussionCommentService.findById(discussionComment.getId())).thenReturn(discussionComment);

        // Missing required attribute
        discussionComment.setDate(null);
        when(discussionCommentService.update(discussionComment.getId(), discussionComment)).thenReturn(discussionComment);

        mockMvc.perform(put("/" + Constants.DISCUSSION_TOPICS + "/" + discussionTopic.getId() + "/" + Constants.DISCUSSION_COMMENTS + "/" + discussionComment.getId())
                .content(mapper.writeValueAsString(discussionComment)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}
