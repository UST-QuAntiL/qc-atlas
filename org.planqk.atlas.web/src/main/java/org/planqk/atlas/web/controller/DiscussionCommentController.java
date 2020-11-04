/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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
import org.planqk.atlas.core.model.KnowledgeArtifact;
import org.planqk.atlas.core.services.DiscussionCommentService;
import org.planqk.atlas.core.services.DiscussionTopicService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.linkassembler.DiscussionCommentAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = Constants.TAG_DISCUSSION_TOPIC)
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RestController("discussion-comment")
@ApiVersion("v1")
@AllArgsConstructor
@Slf4j
public class DiscussionCommentController {

    private final DiscussionCommentService discussionCommentService;

    private final DiscussionTopicService discussionTopicService;

    private final DiscussionCommentAssembler discussionCommentAssembler;

    @Operation(responses = {
        @ApiResponse(responseCode = "200")
    }, description = "")
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<DiscussionCommentDto>>> getDiscussionCommentsOfTopic(
        @PathVariable("topicId") UUID topicId,
        @Parameter(hidden = true) ListParameters listParameters) {
        final var result = discussionCommentService.findAllByTopic(topicId, listParameters.getPageable());
        return ResponseEntity.ok(discussionCommentAssembler.toModel(result));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "201")
    }, description = "")
    public ResponseEntity<EntityModel<DiscussionCommentDto>> createDiscussionComment(
        @Valid @RequestBody DiscussionCommentDto discussionCommentDto) {
        final var comment = discussionCommentService.create(ModelMapperUtils.convert(discussionCommentDto, DiscussionComment.class));
        return new ResponseEntity<>(discussionCommentAssembler.toModel(comment), HttpStatus.CREATED);
    }

    public ResponseEntity<EntityModel<DiscussionCommentDto>> createDiscussionComment(
        @Valid @RequestBody DiscussionCommentDto discussionCommentDto,
        KnowledgeArtifact knowledgeArtifact) {
        final DiscussionComment convertedDiscussionComment = ModelMapperUtils.convert(discussionCommentDto, DiscussionComment.class);
        convertedDiscussionComment.getDiscussionTopic().setKnowledgeArtifact(knowledgeArtifact);
        final var comment = discussionCommentService.create(convertedDiscussionComment);
        return new ResponseEntity<>(discussionCommentAssembler.toModel(comment), HttpStatus.CREATED);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200")
    }, description = "")
    public ResponseEntity<EntityModel<DiscussionCommentDto>> updateDiscussionComment(
        @PathVariable UUID commentId,
        @Valid @RequestBody DiscussionCommentDto discussionCommentDto) {
        final var discussionCommentObject = discussionCommentService.findById(commentId);
        final var discussionComment = ModelMapperUtils.convert(discussionCommentDto, DiscussionComment.class);
        final var discussionTopic = discussionCommentObject.getDiscussionTopic();
        discussionComment.setDiscussionTopic(discussionTopic);
        discussionCommentService.update(discussionComment);
        return ResponseEntity.ok(discussionCommentAssembler.toModel(discussionComment));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404", description = "Discussion comment with given id doesn't exist")
    })
    public ResponseEntity<Void> deleteDiscussionComment(@PathVariable UUID commentId) {
        discussionCommentService.findById(commentId);
        discussionCommentService.delete(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200")
    }, description = "")
    public ResponseEntity<EntityModel<DiscussionCommentDto>> getDiscussionComment(@PathVariable UUID commentId) {
        final var discussionComment = discussionCommentService.findById(commentId);
        return ResponseEntity.ok(discussionCommentAssembler.toModel(discussionComment));
    }
}
