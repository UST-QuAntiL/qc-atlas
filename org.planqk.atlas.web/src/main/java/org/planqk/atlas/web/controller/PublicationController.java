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

import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.dtos.DiscussionTopicDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller to access and manipulate publication algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_PUBLICATION)
@Slf4j
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@AllArgsConstructor
@RequestMapping("/" + Constants.PUBLICATIONS)
public class PublicationController {

    private final PublicationService publicationService;

    private final AlgorithmService algorithmService;

    private final DiscussionTopicController discussionTopicController;

    private final ImplementationService implementationService;

    private final LinkingService linkingService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all publications.")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<Page<PublicationDto>> getPublications(
            @Parameter(hidden = true) ListParameters listParameters) {
        final var entities = publicationService.findAll(listParameters.getPageable(), listParameters.getSearch());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(entities, PublicationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body.")
    }, description = "Define the basic properties of an publication.")
    @PostMapping
    public ResponseEntity<PublicationDto> createPublication(
            @Validated(ValidationGroups.Create.class) @RequestBody PublicationDto publicationDto) {
        final Publication publication = publicationService.create(ModelMapperUtils.convert(publicationDto, Publication.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(publication, PublicationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication with given ID doesn't exist.")
    }, description = "Update the basic properties of an publication (e.g. title).")
    @PutMapping("/{publicationId}")
    public ResponseEntity<PublicationDto> updatePublication(
            @PathVariable UUID publicationId,
            @Validated(ValidationGroups.Update.class) @RequestBody PublicationDto publicationDto) {
        publicationDto.setId(publicationId);
        final Publication publication = publicationService.update(
                ModelMapperUtils.convert(publicationDto, Publication.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(publication, PublicationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication with given ID doesn't exist.")
    }, description = "Retrieve a specific publication and its basic properties.")
    @GetMapping("/{publicationId}")
    public ResponseEntity<PublicationDto> getPublication(@PathVariable UUID publicationId) {
        final Publication publication = publicationService.findById(publicationId);
        return ResponseEntity.ok(ModelMapperUtils.convert(publication, PublicationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication with given ID doesn't exist.")
    }, description = "Delete an publication. This also removes all references to other entities (e.g. algorithm).")
    @DeleteMapping("/{publicationId}")
    public ResponseEntity<Void> deletePublication(@PathVariable UUID publicationId) {
        publicationService.delete(publicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication with given ID doesn't exist.")
    }, description = "Retrieve referenced algorithms of an publication. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{publicationId}/" + Constants.ALGORITHMS)
    public ResponseEntity<Page<AlgorithmDto>> getAlgorithmsOfPublication(
            @PathVariable UUID publicationId,
            @Parameter(hidden = true) ListParameters params) {
        final var publications = publicationService.findLinkedAlgorithms(publicationId, params.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(publications, AlgorithmDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or publication with given IDs don't exist or " +
                            "reference was already added.")
    }, description = "Add a reference to an existing algorithm " +
            "(that was previously created via a POST on e.g. /" + Constants.ALGORITHMS + "). " +
            "Only the ID is required in the request body, other attributes will be ignored and not changed.")
    @PostMapping("/{publicationId}/" + Constants.ALGORITHMS)
    public ResponseEntity<Void> linkPublicationAndAlgorithm(
            @PathVariable UUID publicationId,
            @Validated({ValidationGroups.IDOnly.class}) @RequestBody AlgorithmDto algorithmDto) {
        linkingService.linkAlgorithmAndPublication(algorithmDto.getId(), publicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or publication with given IDs don't exist or " +
                            "no reference exists.")
    }, description = "Delete a reference to a publication of an algorithm. The reference has to be previously created " +
            "via a POST on /" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.PUBLICATIONS + "/{publicationId}).")
    @DeleteMapping("/{publicationId}/" + Constants.ALGORITHMS + "/{algorithmId}")
    public ResponseEntity<Void> unlinkPublicationAndAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID publicationId) {
        linkingService.unlinkAlgorithmAndPublication(algorithmId, publicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or publication with given IDs don't exist.")
    }, description = "Retrieve a specific algorithm of a publication.")
    @GetMapping("/{publicationId}/" + Constants.ALGORITHMS + "/{algorithmId}")
    public ResponseEntity<AlgorithmDto> getAlgorithmOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID algorithmId) {
        publicationService.checkIfAlgorithmIsLinkedToPublication(publicationId, algorithmId);

        final var algorithm = algorithmService.findById(algorithmId);
        return ResponseEntity.ok(ModelMapperUtils.convert(algorithm, AlgorithmDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication with given ID doesn't exist.")
    }, description = "Retrieve discussion topics of a publication. If none are found an empty list is returned."
    )
    @ListParametersDoc
    @GetMapping("/{publicationId}/" + Constants.DISCUSSION_TOPICS)
    public HttpEntity<PagedModel<EntityModel<DiscussionTopicDto>>> getDiscussionTopicsOfPublication(
            @PathVariable UUID publicationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.getDiscussionTopics(publicationId, listParameters);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication or discussion topic with given ID doesn't exist.")
    }, description = "Retrieve discussion topic of a publication."
    )
    @ListParametersDoc
    @GetMapping("/{publicationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}")
    public HttpEntity<EntityModel<DiscussionTopicDto>> getDiscussionTopicOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID topicId,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.getDiscussionTopic(publicationId, topicId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication or discussion topic with given ID doesn't exist.")
    }, description = "Delete discussion topic of a publication."
    )
    @ListParametersDoc
    @DeleteMapping("/{publicationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}")
    public HttpEntity<Void> deleteDiscussionTopicOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID topicId,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.deleteDiscussionTopic(publicationId, topicId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication or discussion topic with given ID doesn't exist.")
    }, description = "Create a discussion topic of a publication."
    )
    @ListParametersDoc
    @PostMapping("/{publicationId}/" + Constants.DISCUSSION_TOPICS)
    public HttpEntity<EntityModel<DiscussionTopicDto>> createDiscussionTopicOfPublication(
            @PathVariable UUID publicationId,
            @Validated(ValidationGroups.Create.class) @RequestBody DiscussionTopicDto discussionTopicDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var publication = publicationService.findById(publicationId);
        return discussionTopicController.createDiscussionTopic(publication, discussionTopicDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication or discussion topic with given ID doesn't exist.")
    }, description = "Update discussion topic of a publication."
    )
    @ListParametersDoc
    @PutMapping("/{publicationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}")
    public HttpEntity<EntityModel<DiscussionTopicDto>> updateDiscussionTopicOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID topicId,
            @Validated(ValidationGroups.Update.class) @RequestBody DiscussionTopicDto discussionTopicDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var publication = publicationService.findById(publicationId);
        return discussionTopicController.updateDiscussionTopic(publication, topicId, discussionTopicDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication or discussion topic with given ID doesn't exist.")
    }, description = "Retrieve discussion comments of a discussion topic of a publication. If none are found an empty list is returned."
    )
    @ListParametersDoc
    @GetMapping("/{publicationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" + Constants.DISCUSSION_COMMENTS)
    public HttpEntity<PagedModel<EntityModel<DiscussionCommentDto>>> getDiscussionCommentsOfDiscussionTopicOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID topicId,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.getDiscussionComments(publicationId, topicId, listParameters);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication, discussion topic or discussion comment with given ID doesn't exist.")
    }, description = "Retrieve discussion comment of a discussion topic of a publication."
    )
    @ListParametersDoc
    @GetMapping("/{publicationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" + Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public HttpEntity<EntityModel<DiscussionCommentDto>> getDiscussionCommentOfDiscussionTopicOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID topicId,
            @PathVariable UUID commentId,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.getDiscussionComment(publicationId, topicId, commentId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication, discussion topic or discussion comment with given ID doesn't exist.")
    }, description = "Delete discussion comment of a discussion topic of a publication."
    )
    @ListParametersDoc
    @DeleteMapping("/{publicationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" + Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public HttpEntity<Void> deleteDiscussionCommentOfDiscussionTopicOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID topicId,
            @PathVariable UUID commentId,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.deleteDiscussionComment(publicationId, topicId, commentId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication or discussion topic with given ID doesn't exist.")
    }, description = "Create discussion comment of a discussion topic of a publication."
    )
    @ListParametersDoc
    @PostMapping("/{publicationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" + Constants.DISCUSSION_COMMENTS)
    public HttpEntity<EntityModel<DiscussionCommentDto>> createDiscussionCommentOfDiscussionTopicOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID topicId,
            @Validated(ValidationGroups.Create.class) @RequestBody DiscussionCommentDto discussionCommentDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.createDiscussionComment(publicationId, topicId, discussionCommentDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Publication or discussion topic with given ID doesn't exist.")
    }, description = "Update discussion comment of a discussion topic of a publication."
    )
    @ListParametersDoc
    @PutMapping("/{publicationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" + Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public HttpEntity<EntityModel<DiscussionCommentDto>> updateDiscussionCommentOfDiscussionTopicOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID topicId,
            @PathVariable UUID commentId,
            @Validated(ValidationGroups.Update.class) @RequestBody DiscussionCommentDto discussionCommentDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.updateDiscussionComment(publicationId, topicId, commentId, discussionCommentDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Implementation or publication with given IDs don't exist.")
    }, description = "Retrieve referenced implementations of an publication. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{publicationId}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<Page<ImplementationDto>> getImplementationsOfPublication(
            @PathVariable UUID publicationId,
            @Parameter(hidden = true) ListParameters params) {
        final var implementations = publicationService.findLinkedImplementations(publicationId, params.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(implementations, ImplementationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Implementation or publication with given IDs don't exist.")
    }, description = "Retrieve a specific implementation of a publication.")
    @GetMapping("/{publicationId}/" + Constants.IMPLEMENTATIONS + "/{implementationId}")
    public ResponseEntity<ImplementationDto> getImplementationOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID implementationId) {
        publicationService.checkIfImplementationIsLinkedToPublication(publicationId, implementationId);

        final var implementation = implementationService.findById(implementationId);
        return ResponseEntity.ok(ModelMapperUtils.convert(implementation, ImplementationDto.class));
    }
}





