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
import org.planqk.atlas.core.services.DiscussionCommentService;
import org.planqk.atlas.core.services.DiscussionTopicService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.linkassembler.DiscussionCommentAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
@Tag(name = Constants.TAG_DISCUSSION_TOPIC)
@RestController("discussion-comment")
@CrossOrigin(allowedHeaders = "*", origins = "*")
@AllArgsConstructor
@Slf4j
public class DiscussionCommentController {

    private final DiscussionCommentService discussionCommentService;
    private final DiscussionTopicService discussionTopicService;
    private final DiscussionCommentAssembler discussionCommentAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "")
    public HttpEntity<PagedModel<EntityModel<DiscussionCommentDto>>> getDiscussionComments(
            @PathVariable("topicId") UUID topicId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Pageable pageable = RestUtils.getPageableFromRequestParams(page, size);
        var result = discussionCommentService.findAllByTopic(topicId, pageable);
        return ResponseEntity.ok(discussionCommentAssembler.toModel(result));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "")
    public HttpEntity<EntityModel<DiscussionCommentDto>> getDiscussionComment(@PathVariable UUID commentId) {
        var discussionComment = discussionCommentService.findById(commentId);
        return ResponseEntity.ok(discussionCommentAssembler.toModel(discussionComment));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Discussion comment with given id doesn't exist")
    })
    public HttpEntity<Void> deleteDiscussionComment(@PathVariable UUID commentId) {
        discussionCommentService.findById(commentId);
        discussionCommentService.delete(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201")
    }, description = "")
    public HttpEntity<EntityModel<DiscussionCommentDto>> createDiscussionComment(
            @Valid @RequestBody DiscussionCommentDto discussionCommentDto) {
        var comment = discussionCommentService.create(ModelMapperUtils.convert(discussionCommentDto, DiscussionComment.class));
        return new ResponseEntity<>(discussionCommentAssembler.toModel(comment), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "")
    public HttpEntity<EntityModel<DiscussionCommentDto>> updateDiscussionComment(
            @PathVariable UUID commentId,
            @Valid @RequestBody DiscussionCommentDto discussionCommentDto) {
        var discussionCommentObject = discussionCommentService.findById(commentId);
        var discussionComment = ModelMapperUtils.convert(discussionCommentDto, DiscussionComment.class);
        discussionComment.setDiscussionTopic(discussionCommentObject.getDiscussionTopic());
        var discussionTopic = discussionCommentObject.getDiscussionTopic();
        discussionTopic.getDiscussionComments().add(discussionComment);
        discussionTopicService.update(discussionTopic);
        return ResponseEntity.ok(discussionCommentAssembler.toModel(discussionComment));
    }
}
