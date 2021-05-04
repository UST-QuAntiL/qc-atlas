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

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.Image;
import org.planqk.atlas.core.model.LearningMethod;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.Sketch;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.SketchService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.dtos.DiscussionTopicDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.LearningMethodDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.SketchDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.utils.ControllerValidationUtils;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller to access and manipulate classic, hybrid and quantum algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_ALGORITHM)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHMS)
@AllArgsConstructor
@Slf4j
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    private final SketchService sketchService;

    private final PatternRelationService patternRelationService;

    private final ImplementationService implementationService;

    private final DiscussionTopicController discussionTopicController;

    private final ProblemTypeService problemTypeService;

    private final ApplicationAreaService applicationAreaService;

    private final TagService tagService;

    private final PublicationService publicationService;

    private final ComputeResourcePropertyService computeResourcePropertyService;


    private final LinkingService linkingService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all algorithms (quantum, hybrid and classic).")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<Page<AlgorithmDto>> getAlgorithms(
            @Parameter(hidden = true) ListParameters listParameters) {
        return ResponseEntity.ok(ModelMapperUtils.convertPage(algorithmService.findAll(listParameters.getPageable(),
                listParameters.getSearch()), AlgorithmDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
    }, description = "Define the basic properties of an algorithm. " +
            "References to sub-objects (e.g. a ProblemType) can be added via " +
            "sub-routes (e.g. POST on /" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.PROBLEM_TYPES + ").")
    @PostMapping
    public ResponseEntity<AlgorithmDto> createAlgorithm(
            @Validated(ValidationGroups.Create.class) @RequestBody AlgorithmDto algorithmDto) {
        final Algorithm savedAlgorithm = algorithmService.create(ModelMapperUtils.convert(algorithmDto, Algorithm.class));
        return new ResponseEntity<>(ModelMapperUtils.convert(savedAlgorithm, AlgorithmDto.class), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Update the basic properties of an algorithm (e.g. name). " +
            "References to sub-objects (e.g. a ProblemType) are not updated via this operation " +
            "- use the corresponding sub-route for updating them (e.g. PUT on " +
            "/" + Constants.PROBLEM_TYPES + "/{problemTypeId})."
    )
    @PutMapping("/{algorithmId}")
    public ResponseEntity<AlgorithmDto> updateAlgorithm(
            @PathVariable UUID algorithmId,
            @Validated(ValidationGroups.Update.class) @RequestBody AlgorithmDto algorithmDto) {
        algorithmDto.setId(algorithmId);
        final Algorithm updatedAlgorithm = algorithmService.update(
                ModelMapperUtils.convert(algorithmDto, Algorithm.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(updatedAlgorithm, AlgorithmDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Delete an algorithm. This also deletes all entities that depend on it " +
            "(e.g. the algorithm's relations to other algorithms).")
    @DeleteMapping("/{algorithmId}")
    public ResponseEntity<Void> deleteAlgorithm(
            @PathVariable UUID algorithmId) {
        algorithmService.delete(algorithmId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Retrieve a specific algorithm and its basic properties.")
    @GetMapping("/{algorithmId}")
    public ResponseEntity<AlgorithmDto> getAlgorithm(
            @PathVariable UUID algorithmId) {
        final var algorithm = algorithmService.findById(algorithmId);
        return ResponseEntity.ok(ModelMapperUtils.convert(algorithm, AlgorithmDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Retrieve all tags associated with a specific algorithm.")
    @GetMapping("/{algorithmId}/" + Constants.TAGS)
    public ResponseEntity<Collection<TagDto>> getTagsOfAlgorithm(@PathVariable UUID algorithmId) {
        final Algorithm algorithm = algorithmService.findById(algorithmId);
        return ResponseEntity.ok(ModelMapperUtils.convertCollection(algorithm.getTags(), TagDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Add a tag to an algorithm. The tag does not have to exist before adding it.")
    @PostMapping("/{algorithmId}/" + Constants.TAGS)
    public ResponseEntity<Void> addTagToAlgorithm(
            @PathVariable UUID algorithmId,
            @Validated(ValidationGroups.Create.class) @RequestBody TagDto tagDto) {
        tagService.addTagToAlgorithm(algorithmId, ModelMapperUtils.convert(tagDto, Tag.class));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm with given ID or Tag doesn't exist.")
    }, description = "Remove a tag from an algorithm.")
    @DeleteMapping("/{algorithmId}/" + Constants.TAGS)
    public ResponseEntity<Void> removeTagFromAlgorithm(
            @PathVariable UUID algorithmId,
            @Validated(ValidationGroups.IDOnly.class) @RequestBody TagDto tagDto) {
        tagService.removeTagFromAlgorithm(algorithmId, ModelMapperUtils.convert(tagDto, Tag.class));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Retrieve referenced publications of an algorithm. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.PUBLICATIONS)
    public ResponseEntity<Page<PublicationDto>> getPublicationsOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final Page<Publication> publications = algorithmService.findLinkedPublications(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(publications, PublicationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm or publication with given IDs don't exist or " +
                    "reference was already added.")
    }, description = "Add a reference to an existing publication " +
            "(that was previously created via a POST on e.g. /" + Constants.PUBLICATIONS + "). " +
            "Only the ID is required in the request body, other attributes will be ignored and not changed.")
    @PostMapping("/{algorithmId}/" + Constants.PUBLICATIONS)
    public ResponseEntity<Void> linkAlgorithmAndPublication(
            @PathVariable UUID algorithmId,
            @Validated({ValidationGroups.IDOnly.class}) @RequestBody PublicationDto publicationDto) {
        linkingService.linkAlgorithmAndPublication(algorithmId, publicationDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm or publication with given IDs don't exist or " +
                    "no reference exists.")
    }, description = "Delete a reference to a publication of an algorithm. The reference has to be previously created " +
            "via a POST on /" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.PUBLICATIONS + ").")
    @DeleteMapping("/{algorithmId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<Void> unlinkAlgorithmAndPublication(
            @PathVariable UUID algorithmId,
            @PathVariable UUID publicationId) {
        linkingService.unlinkAlgorithmAndPublication(algorithmId, publicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm or publication with given IDs don't exist.")
    }, description = "Retrieve a specific publication and its basic properties of an algorithm.")
    @GetMapping("/{algorithmId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<PublicationDto> getPublicationOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID publicationId) {
        algorithmService.checkIfPublicationIsLinkedToAlgorithm(algorithmId, publicationId);

        final Publication publication = publicationService.findById(publicationId);
        return new ResponseEntity<>(ModelMapperUtils.convert(publication, PublicationDto.class), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Retrieve problem types of an algorithm. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.PROBLEM_TYPES)
    public ResponseEntity<Page<ProblemTypeDto>> getProblemTypesOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final Page<ProblemType> problemTypes = algorithmService.findLinkedProblemTypes(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(problemTypes, ProblemTypeDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or problem type with given IDs don't exist or " +
                            "reference was already added.")
    }, description = "Add a reference to an existing ProblemType " +
            "(that was previously created via a POST on /" + Constants.PROBLEM_TYPES + "). " +
            "Only the ID is required in the request body, other attributes will be ignored and not changed.")
    @PostMapping("/{algorithmId}/" + Constants.PROBLEM_TYPES)
    public ResponseEntity<Void> linkAlgorithmAndProblemType(
            @PathVariable UUID algorithmId,
            @Validated({ValidationGroups.IDOnly.class}) @RequestBody ProblemTypeDto problemTypeDto) {
        linkingService.linkAlgorithmAndProblemType(algorithmId, problemTypeDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or problem type with given IDs don't exist or " +
                            "no reference exists.")
    }, description = "Delete a reference to a problem types of an algorithm. The reference has to be previously created " +
            "via a POST on e.g. /" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.PROBLEM_TYPES + ").")
    @DeleteMapping("/{algorithmId}/" + Constants.PROBLEM_TYPES + "/{problemTypeId}")
    public ResponseEntity<Void> unlinkAlgorithmAndProblemType(
            @PathVariable UUID algorithmId,
            @PathVariable UUID problemTypeId) {
        linkingService.unlinkAlgorithmAndProblemType(algorithmId, problemTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or problem type with given IDs don't exist.")
    }, description = "Retrieve a specific problem type of an algorithm.")
    @GetMapping("/{algorithmId}/" + Constants.PROBLEM_TYPES + "/{problemTypeId}")
    public ResponseEntity<ProblemTypeDto> getProblemTypeOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID problemTypeId) {
        algorithmService.checkIfProblemTypeIsLinkedToAlgorithm(algorithmId, problemTypeId);

        final ProblemType problemType = problemTypeService.findById(problemTypeId);
        return ResponseEntity.ok(ModelMapperUtils.convert(problemType, ProblemTypeDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or application area with given IDs don't exist.")
    }, description = "Retrieve application areas of an algorithm. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.APPLICATION_AREAS)
    public ResponseEntity<Page<ApplicationAreaDto>> getApplicationAreasOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final Page<ApplicationArea> applicationAreas = algorithmService.findLinkedApplicationAreas(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(applicationAreas, ApplicationAreaDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or application area with given IDs don't exist or " +
                            "reference was already added.")
    }, description = "Add a reference to an existing application area " +
            "(that was previously created via a POST on e.g. /" + Constants.APPLICATION_AREAS + "). " +
            "Only the ID is required in the request body, other attributes will be ignored and not changed.")
    @PostMapping("/{algorithmId}/" + Constants.APPLICATION_AREAS)
    public ResponseEntity<Void> linkAlgorithmAndApplicationArea(
            @PathVariable UUID algorithmId,
            @Validated({ValidationGroups.IDOnly.class}) @RequestBody ApplicationAreaDto applicationAreaDto) {
        linkingService.linkAlgorithmAndApplicationArea(algorithmId, applicationAreaDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or application area with given IDs don't exist or " +
                            "no reference exists.")
    }, description = "Delete a reference to an application area of an algorithm. The reference has to be previously " +
            "created via a POST on /" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.APPLICATION_AREAS + ").")
    @DeleteMapping("/{algorithmId}/" + Constants.APPLICATION_AREAS + "/{applicationAreaId}")
    public ResponseEntity<Void> unlinkAlgorithmAndApplicationArea(
            @PathVariable UUID algorithmId,
            @PathVariable UUID applicationAreaId) {
        linkingService.unlinkAlgorithmAndApplicationArea(algorithmId, applicationAreaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or application area with given IDs don't exist.")
    }, description = "Retrieve a specific application area of an algorithm.")
    @GetMapping("/{algorithmId}/" + Constants.APPLICATION_AREAS + "/{applicationAreaId}")
    public ResponseEntity<ApplicationAreaDto> getApplicationAreaOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID applicationAreaId) {
        algorithmService.checkIfApplicationAreaIsLinkedToAlgorithm(algorithmId, applicationAreaId);

        final ApplicationArea applicationArea = applicationAreaService.findById(applicationAreaId);
        return ResponseEntity.ok(ModelMapperUtils.convert(applicationArea, ApplicationAreaDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Retrieve implementations of an algorithm. If none are found an empty list is returned."
    )
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<Page<ImplementationDto>> getImplementationsOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var implementations = implementationService.findByImplementedAlgorithm(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(implementations, ImplementationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Retrieve discussion topics of an algorithm. If none are found an empty list is returned."
    )
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.DISCUSSION_TOPICS)
    public ResponseEntity<Page<DiscussionTopicDto>> getDiscussionTopicsOfAlgorithm(@PathVariable UUID algorithmId,
                                                                                   @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.getDiscussionTopics(algorithmId, listParameters);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or discussion topic with given ID doesn't exist.")
    }, description = "Retrieve discussion topic of an algorithm."
    )
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}")
    public ResponseEntity<DiscussionTopicDto> getDiscussionTopicOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID topicId,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.getDiscussionTopic(algorithmId, topicId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or discussion topic with given ID doesn't exist.")
    }, description = "Delete discussion topic of an algorithm."
    )
    @ListParametersDoc
    @DeleteMapping("/{algorithmId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}")
    public HttpEntity<Void> deleteDiscussionTopicOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID topicId,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.deleteDiscussionTopic(algorithmId, topicId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or discussion topic with given ID doesn't exist.")
    }, description = "Create a discussion topic of an algorithm."
    )
    @ListParametersDoc
    @PostMapping("/{algorithmId}/" + Constants.DISCUSSION_TOPICS)
    public ResponseEntity<DiscussionTopicDto> createDiscussionTopicOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Validated(ValidationGroups.Create.class) @RequestBody DiscussionTopicDto discussionTopicDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var algorithm = algorithmService.findById(algorithmId);
        return discussionTopicController.createDiscussionTopic(algorithm, discussionTopicDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or discussion topic with given ID doesn't exist.")
    }, description = "Update discussion topic of an algorithm."
    )
    @ListParametersDoc
    @PutMapping("/{algorithmId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}")
    public ResponseEntity<DiscussionTopicDto> updateDiscussionTopicOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID topicId,
            @Validated(ValidationGroups.Update.class) @RequestBody DiscussionTopicDto discussionTopicDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var algorithm = algorithmService.findById(algorithmId);
        return discussionTopicController.updateDiscussionTopic(algorithm, topicId, discussionTopicDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or discussion topic with given ID doesn't exist.")
    }, description = "Retrieve discussion comments of a discussion topic of an algorithm. If none are found an empty list is returned."
    )
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" + Constants.DISCUSSION_COMMENTS)
    public ResponseEntity<Page<DiscussionCommentDto>> getDiscussionCommentsOfDiscussionTopicOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID topicId,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.getDiscussionComments(algorithmId, topicId, listParameters);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, discussion topic or discussion comment with given ID doesn't exist.")
    }, description = "Retrieve discussion comment of a discussion topic of an algorithm."
    )
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" + Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public ResponseEntity<DiscussionCommentDto> getDiscussionCommentOfDiscussionTopicOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID topicId,
            @PathVariable UUID commentId,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.getDiscussionComment(algorithmId, topicId, commentId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, discussion topic or discussion comment with given ID doesn't exist.")
    }, description = "Delete discussion comment of a discussion topic of an algorithm."
    )
    @ListParametersDoc
    @DeleteMapping("/{algorithmId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" + Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public HttpEntity<Void> deleteDiscussionCommentOfDiscussionTopicOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID topicId,
            @PathVariable UUID commentId,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.deleteDiscussionComment(algorithmId, topicId, commentId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or discussion topic with given ID doesn't exist.")
    }, description = "Create discussion comment of a discussion topic of an algorithm."
    )
    @ListParametersDoc
    @PostMapping("/{algorithmId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" + Constants.DISCUSSION_COMMENTS)
    public ResponseEntity<DiscussionCommentDto> createDiscussionCommentOfDiscussionTopicOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID topicId,
            @Validated(ValidationGroups.Create.class) @RequestBody DiscussionCommentDto discussionCommentDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.createDiscussionComment(algorithmId, topicId, discussionCommentDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or discussion topic with given ID doesn't exist.")
    }, description = "Update discussion comment of a discussion topic of an algorithm."
    )
    @ListParametersDoc
    @PutMapping("/{algorithmId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" + Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public ResponseEntity<DiscussionCommentDto> updateDiscussionCommentOfDiscussionTopicOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID topicId,
            @PathVariable UUID commentId,
            @Validated(ValidationGroups.Update.class) @RequestBody DiscussionCommentDto discussionCommentDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        return discussionTopicController.updateDiscussionComment(algorithmId, topicId, commentId, discussionCommentDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Retrieve the required compute resource properties of an algorithm. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
    public ResponseEntity<Page<ComputeResourcePropertyDto>> getComputeResourcePropertiesOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var resources = computeResourcePropertyService.findComputeResourcePropertiesOfAlgorithm(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(resources, ComputeResourcePropertyDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or compute resource property type with given IDs don't exist.")
    }, description = "Add a compute resource property (e.g. a certain number of qubits) that is required by an algorithm. " +
            "The compute resource property type has to be already created (e.g. via POST on /" + Constants.COMPUTE_RESOURCE_PROPERTY_TYPES + "). " +
            "As a result only the ID is required for the compute resource property type, other attributes will be ignored not changed.")
    @PostMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
    public ResponseEntity<ComputeResourcePropertyDto> createComputeResourcePropertyForAlgorithm(
            @PathVariable UUID algorithmId,
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        final var computeResourceProperty = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);
        final var createdComputeResourceProperty = computeResourcePropertyService
                .addComputeResourcePropertyToAlgorithm(algorithmId, computeResourceProperty);
        return new ResponseEntity<>(ModelMapperUtils.convert(createdComputeResourceProperty, ComputeResourcePropertyDto.class), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, compute resource property or compute resource type with given IDs don't exist.")
    }, description = "Update a Compute resource property of an algorithm. " +
            "For the compute resource property type only the ID is required, " +
            "other compute resource property type attributes will be ignored and not changed.")
    @PutMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public ResponseEntity<ComputeResourcePropertyDto> updateComputeResourcePropertyOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID computeResourcePropertyId,
            @Validated(ValidationGroups.Update.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfAlgorithm(algorithmId, computeResourcePropertyId);

        computeResourcePropertyDto.setId(computeResourcePropertyId);
        final var resource = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);
        final var updatedResource = computeResourcePropertyService.update(resource);
        return ResponseEntity.ok(ModelMapperUtils.convert(updatedResource, ComputeResourcePropertyDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or compute resource property with given IDs don't exist."),
    }, description = "Delete a Compute resource property of an algorithm. " +
            "The compute resource property type is not affected by this.")
    @DeleteMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public ResponseEntity<Void> deleteComputeResourcePropertyOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID computeResourcePropertyId) {
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfAlgorithm(algorithmId, computeResourcePropertyId);

        computeResourcePropertyService.delete(computeResourcePropertyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or compute resource property with given IDs don't exist."),
    }, description = "Retrieve a specific compute resource property of an algorithm.")
    @GetMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public ResponseEntity<ComputeResourcePropertyDto> getComputeResourcePropertyOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID computeResourcePropertyId) {
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfAlgorithm(algorithmId, computeResourcePropertyId);

        final var resource = computeResourcePropertyService.findById(computeResourcePropertyId);
        return ResponseEntity.ok(ModelMapperUtils.convert(resource, ComputeResourcePropertyDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Retrieve pattern relations of an algorithms. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS)
    public ResponseEntity<Page<PatternRelationDto>> getPatternRelationsOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final Page<PatternRelation> patternRelations = algorithmService.findLinkedPatternRelations(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(patternRelations, PatternRelationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or pattern relation type with given IDs don't exist.")
    }, description = "Create a relation between a pattern and an algorithm." +
            "The pattern relation type has to be already created (e.g. via POST on /" + Constants.PATTERN_RELATION_TYPES + "). " +
            "As a result only the ID is required for the pattern relation type, other attributes will be ignored not changed.")
    @PostMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS)
    public ResponseEntity<PatternRelationDto> createPatternRelationForAlgorithm(
            @PathVariable UUID algorithmId,
            @Validated({ValidationGroups.Create.class}) @RequestBody PatternRelationDto patternRelationDto) {
        ControllerValidationUtils.checkIfAlgorithmIsInPatternRelationDTO(algorithmId, patternRelationDto);

        final var savedPatternRelation = patternRelationService.create(
                ModelMapperUtils.convert(patternRelationDto, PatternRelation.class));
        return new ResponseEntity<>(ModelMapperUtils.convert(savedPatternRelation, PatternRelationDto.class), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or Pattern relation doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, pattern relation or pattern relation type with given IDs don't exist.")
    }, description = "Update a relation between a pattern and an algorithm. " +
            "For the pattern relation type only the ID is required," +
            "other pattern relation type attributes will be ignored and not changed.")
    @PutMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS + "/{patternRelationId}")
    public ResponseEntity<PatternRelationDto> updatePatternRelationOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID patternRelationId,
            @Validated({ValidationGroups.Update.class}) @RequestBody PatternRelationDto patternRelationDto) {
        ControllerValidationUtils.checkIfAlgorithmIsInPatternRelationDTO(algorithmId, patternRelationDto);
        patternRelationService.checkIfAlgorithmIsInPatternRelation(algorithmId, patternRelationId);

        patternRelationDto.setId(patternRelationId);
        final var savedPatternRelation = patternRelationService.update(
                ModelMapperUtils.convert(patternRelationDto, PatternRelation.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(savedPatternRelation, PatternRelationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or pattern relation with given IDs don't exist.")
    }, description = "Delete a specific relation between a pattern and an algorithm. " +
            "The pattern relation type is not affected by this.")
    @DeleteMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS + "/{patternRelationId}")
    public ResponseEntity<Void> deletePatternRelationOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID patternRelationId) {
        patternRelationService.checkIfAlgorithmIsInPatternRelation(algorithmId, patternRelationId);

        patternRelationService.delete(patternRelationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or pattern relation with given IDs don't exist.")
    }, description = "Retrieve a specific relation between a pattern and an algorithm.")
    @GetMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS + "/{patternRelationId}")
    public ResponseEntity<PatternRelationDto> getPatternRelationOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID patternRelationId) {
        patternRelationService.checkIfAlgorithmIsInPatternRelation(algorithmId, patternRelationId);

        final var patternRelation = patternRelationService.findById(patternRelationId);
        return ResponseEntity.ok(ModelMapperUtils.convert(patternRelation, PatternRelationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "417"),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm with the given ID doesn't exist")
    }, description = "Add a Sketch to the algorithm.")
    @PostMapping("/{algorithmId}/" + Constants.SKETCHES)
    public ResponseEntity<SketchDto> uploadSketch(
            @PathVariable UUID algorithmId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            @RequestParam("baseURL") String baseURL) {
        final Sketch sketch = sketchService.addSketchToAlgorithm(algorithmId, file, description, baseURL);
        return ResponseEntity.ok(ModelMapperUtils.convert(sketch, SketchDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all sketches for a specific algorithm.")
    @GetMapping("/{algorithmId}/" + Constants.SKETCHES)
    public ResponseEntity<Collection<SketchDto>> getSketches(@PathVariable UUID algorithmId) {
        final List<Sketch> sketches = sketchService.findByAlgorithm(algorithmId);

        return ResponseEntity.ok(ModelMapperUtils.convertCollection(sketches, SketchDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm or sketch with given IDs don't exist")
    }, description = "Delete a sketch of the algorithm.")
    @DeleteMapping("/{algorithmId}/" + Constants.SKETCHES + "/{sketchId}")
    public ResponseEntity<Void> deleteSketch(@PathVariable UUID algorithmId, @PathVariable UUID sketchId) {
        sketchService.delete(sketchId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Sketch with given ID doesn't exist")
    }, description = "Retrieve a specific Sketch and its basic properties.")
    @GetMapping("/{algorithmId}/" + Constants.SKETCHES + "/{sketchId}")
    public ResponseEntity<SketchDto> getSketch(@PathVariable UUID algorithmId, @PathVariable UUID sketchId) {
        final Sketch sketch = this.sketchService.findById(sketchId);
        return ResponseEntity.ok(ModelMapperUtils.convert(sketch, SketchDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Sketch with given ID doesn't exist")
    }, description = "Update the properties of a sketch.")
    @PutMapping("/{algorithmId}/" + Constants.SKETCHES + "/{sketchId}")
    public ResponseEntity<SketchDto> updateSketch(
            @PathVariable UUID algorithmId,
            @PathVariable UUID sketchId,
            @Validated @RequestBody SketchDto sketchDto) {
        sketchDto.setId(sketchId);

        final Sketch updatedSketch = sketchService.update(
                ModelMapperUtils.convert(sketchDto, Sketch.class));

        return ResponseEntity.ok(ModelMapperUtils.convert(updatedSketch, SketchDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Sketch with given ID doesn't exist")
    }, description = "Retrieve the image of specific Sketch.")
    @GetMapping(value = "/{algorithmId}/" + Constants.SKETCHES + "/{sketchId}" + "/image")
    public ResponseEntity<byte[]> getSketchImage(@PathVariable UUID algorithmId, @PathVariable UUID sketchId) {
        final Image image = this.sketchService.getImageBySketch(sketchId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(image.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(image.getImage());
    }


    @Operation(responses = {
            @ApiResponse(responseCode = "200", description = "The request has succeeded. " +
                    "The learning method has been fetched and is transmitted in the message body"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or learning method with given IDs don't exist.")
    }, description = "Retrieve learning method of an algorithm. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.LEARNING_METHODS)
    public ResponseEntity<Page<LearningMethodDto>> getLearningMethodsOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final Page<LearningMethod> learningMethods = algorithmService.findLinkedLearningMethods(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(learningMethods, LearningMethodDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200", description = "The request has succeeded. " +
                    "The learning method has been fetched and is transmitted in the message body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or learning method with given IDs don't exist.")
    }, description = "Retrieve a specific learning method of an algorithm.")
    @GetMapping("/{algorithmId}/" + Constants.LEARNING_METHODS + "/{learningMethodId}")
    public ResponseEntity<LearningMethodDto> getLearningMethodOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID learningMethodId) {
        final LearningMethod learningMethod = algorithmService.getLearningMethodOfAlgorithm(algorithmId, learningMethodId);
        return ResponseEntity.ok(ModelMapperUtils.convert(learningMethod, LearningMethodDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204", description = "There is no content to send for this request."),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or learning method with given IDs don't exist or " +
                            "reference was already added.")
    }, description = "Add a reference to an existing learning method " +
            "(that was previously created via a POST on e.g. /" + Constants.LEARNING_METHODS + "). " +
            "Only the ID is required in the request body, other attributes will be ignored and not changed.")
    @PostMapping("/{algorithmId}/" + Constants.LEARNING_METHODS)
    public ResponseEntity<Void> linkAlgorithmAndLearningMethod(
            @PathVariable UUID algorithmId,
            @Validated({ValidationGroups.IDOnly.class}) @RequestBody LearningMethodDto learningMethodDto) {
        linkingService.linkAlgorithmAndLearningMethod(algorithmId, learningMethodDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204", description = "There is no content to send for this request."),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or learning method with given IDs don't exist or " +
                            "no reference exists.")
    }, description = "Delete a reference to a learning method of an algorithm.")
    @DeleteMapping("/{algorithmId}/" + Constants.LEARNING_METHODS + "/{learningMethodId}")
    public ResponseEntity<Void> unlinkAlgorithmAndLearningMethod(
            @PathVariable UUID algorithmId,
            @PathVariable UUID learningMethodId) {
        linkingService.unlinkAlgorithmAndLearningMethod(algorithmId, learningMethodId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
