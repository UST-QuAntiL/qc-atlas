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

import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.DiscussionComment;
import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.repository.DiscussionCommentRepository;
import org.planqk.atlas.core.services.DiscussionCommentService;
import org.planqk.atlas.core.services.DiscussionTopicService;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.linkassembler.DiscussionCommentAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@Tag(name = "discussion-topic")
@RestController("discussion-comment")
@CrossOrigin(allowedHeaders = "*", origins = "*")
@AllArgsConstructor
@Slf4j
public class DiscussionCommentController {

    private DiscussionCommentService discussionCommentService;
    private DiscussionTopicService discussionTopicService;
    private PagedResourcesAssembler<DiscussionCommentDto> pagedResourcesAssembler;
    private DiscussionCommentAssembler discussionCommentAssembler;
    private DiscussionCommentRepository discussionCommentRepository;

    public HttpEntity<PagedModel<EntityModel<DiscussionCommentDto>>> getDiscussionComments(@PathVariable("topicId") UUID topicId,
                                                                                           @RequestParam(required = false) Integer page,
                                                                                           @RequestParam(required = false) Integer size) {
        log.debug("Received request to retrieve all DiscussionComments");

        Pageable pageable = RestUtils.getPageableFromRequestParams(page, size);
        Page<DiscussionCommentDto> discussionCommentDto = ModelMapperUtils.convertPage(discussionCommentService.findAllByTopic(topicId, pageable), DiscussionCommentDto.class);
        PagedModel<EntityModel<DiscussionCommentDto>> pagedModel = pagedResourcesAssembler.toModel(discussionCommentDto);
        discussionCommentAssembler.addLinks(pagedModel);
        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    public HttpEntity<EntityModel<DiscussionCommentDto>> getDiscussionComment(@PathVariable UUID commentId) {
        log.debug("Received request to retrieve DiscussionTopic with id: {}", commentId);

        DiscussionComment discussionComment = discussionCommentService.findById(commentId);
        EntityModel<DiscussionCommentDto> discussionCommentDtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(discussionComment, DiscussionCommentDto.class));
        discussionCommentAssembler.addLinks(discussionCommentDtoEntityModel);
        return new ResponseEntity<>(discussionCommentDtoEntityModel, HttpStatus.OK);
    }

    public HttpEntity<?> deleteDiscussionComment(@PathVariable UUID commentId) {
        discussionCommentService.deleteById(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public HttpEntity<EntityModel<DiscussionCommentDto>> createDiscussionComment(
            @Valid @RequestBody DiscussionCommentDto discussionCommentDto) {

        DiscussionComment discussionComment = discussionCommentService.save(ModelMapperUtils.convert(discussionCommentDto, DiscussionComment.class));
        EntityModel<DiscussionCommentDto> discussionCommentDtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(discussionComment, DiscussionCommentDto.class));
        discussionCommentAssembler.addLinks(discussionCommentDtoEntityModel);
        return new ResponseEntity<>(discussionCommentDtoEntityModel, HttpStatus.CREATED);
    }

    public HttpEntity<EntityModel<DiscussionCommentDto>> updateDiscussionComment(@PathVariable UUID commentId,
                                                                                 @Valid @RequestBody DiscussionCommentDto discussionCommentDto) {

        DiscussionComment discussionCommentObject = discussionCommentService.findById(commentId);
        DiscussionComment discussionComment = ModelMapperUtils.convert(discussionCommentDto, DiscussionComment.class);
        discussionComment.setDiscussionTopic(discussionCommentObject.getDiscussionTopic());
        DiscussionTopic discussionTopic = discussionCommentObject.getDiscussionTopic();
        discussionTopic.getDiscussionComments().add(discussionComment);
        discussionTopicService.update(discussionTopic.getId(), discussionTopic);
        EntityModel<DiscussionCommentDto> discussionCommentDtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(discussionComment, DiscussionCommentDto.class));
        discussionCommentAssembler.addLinks(discussionCommentDtoEntityModel);
        return new ResponseEntity<>(discussionCommentDtoEntityModel, HttpStatus.OK);
    }
}
