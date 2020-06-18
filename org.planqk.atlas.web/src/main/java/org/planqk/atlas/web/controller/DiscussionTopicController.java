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

import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.services.DiscussionTopicService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.dtos.DiscussionTopicDto;
import org.planqk.atlas.web.linkassembler.DiscussionCommentAssembler;
import org.planqk.atlas.web.linkassembler.DiscussionTopicAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "discussion_topic")
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.DISCUSSION_TOPICS)
@Slf4j
@AllArgsConstructor
@RestController
public class DiscussionTopicController {

    private DiscussionTopicService discussionTopicService;
    private PagedResourcesAssembler<DiscussionTopicDto> pagedResourcesAssembler;
    private DiscussionTopicAssembler discussionTopicAssembler;
    private DiscussionCommentAssembler discussionCommentAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<DiscussionTopicDto>>> getDiscussionTopics(@RequestParam(required = false) Integer page,
                                                                                       @RequestParam(required = false) Integer size) {
        log.debug("Received request to retrieve all DiscussionTopics");

        Pageable pageable = RestUtils.getPageableFromRequestParams(page, size);
        Page<DiscussionTopicDto> discussionTopicDto = ModelMapperUtils.convertPage(discussionTopicService.findAll(pageable), DiscussionTopicDto.class);
        PagedModel<EntityModel<DiscussionTopicDto>> pagedModel = pagedResourcesAssembler.toModel(discussionTopicDto);
        discussionTopicAssembler.addLinks(pagedModel);
        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<DiscussionTopicDto>> getDiscussionTopic(@PathVariable UUID id) {
        log.debug("Received request to retrieve DiscussionTopic with id: {}", id);

        DiscussionTopic discussionTopic = discussionTopicService.findById(id);
        EntityModel<DiscussionTopicDto> discussionTopicDtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(discussionTopic, DiscussionTopicDto.class));
        discussionTopicAssembler.addLinks(discussionTopicDtoEntityModel);
        return new ResponseEntity<>(discussionTopicDtoEntityModel, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @DeleteMapping("/{id}")
    public HttpEntity<DiscussionTopicDto> deleteDiscussionTopic(@PathVariable UUID id) {
        discussionTopicService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "404")})
    @PostMapping("/")
    public HttpEntity<EntityModel<DiscussionTopicDto>> createDiscussionTopic(
            @Valid @RequestBody DiscussionTopicDto discussionTopicDto) {

        DiscussionTopic discussionTopic = discussionTopicService.save(ModelMapperUtils.convert(discussionTopicDto, DiscussionTopic.class));
        EntityModel<DiscussionTopicDto> discussionTopicDtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(discussionTopic, DiscussionTopicDto.class));
        discussionTopicAssembler.addLinks(discussionTopicDtoEntityModel);
        return new ResponseEntity<>(discussionTopicDtoEntityModel, HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}/" + Constants.DISCUSSION_COMMENTS)
    public HttpEntity<CollectionModel<EntityModel<DiscussionCommentDto>>> getDiscussionCommentsOfTopic(@PathVariable UUID id) {
        DiscussionTopic discussionTopic = discussionTopicService.findById(id);
        Set<DiscussionCommentDto> discussionCommentDtos = ModelMapperUtils.convertSet(discussionTopic.getDiscussionComments(), DiscussionCommentDto.class);

        CollectionModel<EntityModel<DiscussionCommentDto>> result = HateoasUtils.generateCollectionModel(discussionCommentDtos);
        discussionCommentAssembler.addLinks(result);
        discussionTopicAssembler.addDiscussionCommentLink(result, id);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<DiscussionTopicDto>> updateDiscussionTopic(@PathVariable UUID id,
                                                                             @Valid @RequestBody DiscussionTopicDto discussionTopicDto) {

        DiscussionTopic discussionTopic = discussionTopicService.update(id, ModelMapperUtils.convert(discussionTopicDto, DiscussionTopic.class));
        EntityModel<DiscussionTopicDto> discussionTopicDtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(discussionTopic, DiscussionTopicDto.class));
        discussionTopicAssembler.addLinks(discussionTopicDtoEntityModel);
        return new ResponseEntity<>(discussionTopicDtoEntityModel, HttpStatus.OK);
    }
}
