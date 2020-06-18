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
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.linkassembler.DiscussionCommentAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = "discussion-comment")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.DISCUSSION_COMMENTS)
@ApiVersion("v1")
@AllArgsConstructor
@Slf4j
public class DiscussionCommentController {

    private DiscussionCommentService discussionCommentService;
    private PagedResourcesAssembler<DiscussionCommentDto> pagedResourcesAssembler;
    private DiscussionCommentAssembler discussionCommentAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<DiscussionCommentDto>>> getDiscussionComments(@RequestParam(required = false) Integer page,
                                                                                           @RequestParam(required = false) Integer size) {
        log.debug("Received request to retrieve all DiscussionComments");

        Pageable pageable = RestUtils.getPageableFromRequestParams(page, size);
        Page<DiscussionCommentDto> discussionCommentDto = ModelMapperUtils.convertPage(discussionCommentService.findAll(pageable), DiscussionCommentDto.class);
        PagedModel<EntityModel<DiscussionCommentDto>> pagedModel = pagedResourcesAssembler.toModel(discussionCommentDto);
        discussionCommentAssembler.addLinks(pagedModel);
        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "400")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<DiscussionCommentDto>> getDiscussionComment(@PathVariable UUID id) {
        log.debug("Received request to retrieve DiscussionTopic with id: {}", id);

        DiscussionComment discussionComment = discussionCommentService.findById(id);
        EntityModel<DiscussionCommentDto> discussionCommentDtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(discussionComment, DiscussionCommentDto.class));
        discussionCommentAssembler.addLinks(discussionCommentDtoEntityModel);
        return new ResponseEntity<>(discussionCommentDtoEntityModel, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")})
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteDiscussionComment(@PathVariable UUID id) {
        discussionCommentService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")})
    @PostMapping("/")
    public HttpEntity<EntityModel<DiscussionCommentDto>> createDiscussionComment(
            @Valid @RequestBody DiscussionCommentDto discussionCommentDto) {

        DiscussionComment discussionComment = discussionCommentService.save(ModelMapperUtils.convert(discussionCommentDto, DiscussionComment.class));
        EntityModel<DiscussionCommentDto> discussionCommentDtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(discussionComment, DiscussionCommentDto.class));
        discussionCommentAssembler.addLinks(discussionCommentDtoEntityModel);
        return new ResponseEntity<>(discussionCommentDtoEntityModel, HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")})
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<DiscussionCommentDto>> updateDiscussionComment(@PathVariable UUID id,
                                                                                 @Valid @RequestBody DiscussionCommentDto discussionCommentDto) {

        DiscussionComment discussionComment = discussionCommentService.update(id, ModelMapperUtils.convert(discussionCommentDto, DiscussionComment.class));
        EntityModel<DiscussionCommentDto> discussionCommentDtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(discussionComment, DiscussionCommentDto.class));
        discussionCommentAssembler.addLinks(discussionCommentDtoEntityModel);
        return new ResponseEntity<>(discussionCommentDtoEntityModel, HttpStatus.OK);
    }
}
