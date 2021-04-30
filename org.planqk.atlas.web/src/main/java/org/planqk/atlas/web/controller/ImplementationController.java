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
import java.util.UUID;

import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationPackage;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.FileService;
import org.planqk.atlas.core.services.ImplementationPackageService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.dtos.DiscussionTopicDto;
import org.planqk.atlas.web.dtos.FileDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.ImplementationPackageDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.ComputeResourcePropertyAssembler;
import org.planqk.atlas.web.linkassembler.FileAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationPackageAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.linkassembler.SoftwarePlatformAssembler;
import org.planqk.atlas.web.linkassembler.TagAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
 * Controller to access and manipulate implementations of quantum algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_ALGORITHM)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.IMPLEMENTATIONS)
@AllArgsConstructor
@Slf4j
public class ImplementationController {

    private final ImplementationService implementationService;

    private final ImplementationAssembler implementationAssembler;

    private final TagService tagService;

    private final TagAssembler tagAssembler;

    private final ComputeResourcePropertyService computeResourcePropertyService;

    private final ComputeResourcePropertyAssembler computeResourcePropertyAssembler;

    private final PublicationService publicationService;

    private final PublicationAssembler publicationAssembler;

    private final SoftwarePlatformService softwarePlatformService;

    private final SoftwarePlatformAssembler softwarePlatformAssembler;

    private final LinkingService linkingService;

    private final DiscussionTopicController discussionTopicController;

    private final ImplementationPackageService implementationPackageService;

    private final FileService fileService;

    private final FileAssembler fileAssembler;

    private final ImplementationPackageAssembler implementationPackageAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm with given ID doesn't exist")
    }, description = "Define the basic properties of an implementation for an algorithm. " +
            "References to sub-objects (e.g. a software platform) can be added via sub-routes " +
            "(e.g. POST on /" + Constants.SOFTWARE_PLATFORMS + ").")
    @PostMapping
    public ResponseEntity<ImplementationDto> createImplementation(
            @PathVariable UUID algorithmId,
            @Validated(ValidationGroups.Create.class) @RequestBody ImplementationDto implementationDto) {
        final Implementation savedImplementation = implementationService.create(
                ModelMapperUtils.convert(implementationDto, Implementation.class), algorithmId);
        return new ResponseEntity<>(ModelMapperUtils.convert(savedImplementation, ImplementationDto.class), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or implementation with given IDs don't exist.")
    }, description = "Update the basic properties of an implementation (e.g. name). " +
            "References to sub-objects (e.g. a software platform) are not updated via this operation - " +
            "use the corresponding sub-route for updating them (e.g. PUT on /" + Constants.SOFTWARE_PLATFORMS + "/{softwarePlatformId}).\n")
    @PutMapping("/{implementationId}")
    public ResponseEntity<ImplementationDto> updateImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated(ValidationGroups.Update.class) @RequestBody ImplementationDto implementationDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        implementationDto.setId(implementationId);
        implementationDto.setImplementedAlgorithmId(algorithmId);
        final Implementation updatedImplementation = implementationService.update(
                ModelMapperUtils.convert(implementationDto, Implementation.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(updatedImplementation, ImplementationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or implementation with given IDs don't exist.")
    }, description = "Delete an implementation. " +
            "This also removes all references to other entities (e.g. software platforms).")
    @DeleteMapping("/{implementationId}")
    public ResponseEntity<Void> deleteImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        implementationService.delete(implementationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or implementation with given IDs don't exist.")
    }, description = "Retrieve a specific implementation and its basic properties of an algorithm.")
    @GetMapping("/{implementationId}")
    public ResponseEntity<ImplementationDto> getImplementationOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        final var implementation = implementationService.findById(implementationId);
        return ResponseEntity.ok(ModelMapperUtils.convert(implementation, ImplementationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm or implementation with given IDs don't exist.")
    }, description = "Retrieve all tags associated with a specific implementation.")
    @GetMapping("/{implementationId}/" + Constants.TAGS)
    public ResponseEntity<Collection<TagDto>> getTagsOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        final Implementation implementation = implementationService.findById(implementationId);
        return ResponseEntity.ok(ModelMapperUtils.convertCollection(implementation.getTags(), TagDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or implementation with given IDs don't exist.")
    }, description = "Add a tag to an implementation. The tag does not have to exist before adding it.")
    @PostMapping("/{implementationId}/" + Constants.TAGS)
    public ResponseEntity<Void> addTagToImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated(ValidationGroups.Create.class) @RequestBody TagDto tagDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        tagService.addTagToImplementation(implementationId, ModelMapperUtils.convert(tagDto, Tag.class));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or implementation with given IDs or Tag don't exist.")
    }, description = "Remove a tag from an implementation.")
    @DeleteMapping("/{implementationId}/" + Constants.TAGS)
    public ResponseEntity<Void> removeTagFromImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated(ValidationGroups.IDOnly.class) @RequestBody TagDto tagDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        tagService.removeTagFromImplementation(implementationId, ModelMapperUtils.convert(tagDto, Tag.class));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or implementation with given IDs don't exist.")
    }, description = "Retrieve referenced publications of an implementation. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.PUBLICATIONS)
    public ResponseEntity<Page<PublicationDto>> getPublicationsOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        final var publications = implementationService.findLinkedPublications(implementationId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(publications, PublicationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, implementation or publication with given IDs don't exist or " +
                            "reference was already added.")
    }, description = "Add a reference to an existing publication " +
            "(that was previously created via a POST on e.g. /" + Constants.PUBLICATIONS + "). " +
            "Only the ID is required in the request body, other attributes will be ignored and not changed.")
    @PostMapping("/{implementationId}/" + Constants.PUBLICATIONS)
    public ResponseEntity<Void> linkImplementationAndPublication(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated({ValidationGroups.IDOnly.class}) @RequestBody PublicationDto publicationDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        linkingService.linkImplementationAndPublication(implementationId, publicationDto.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, implementation or publication with given IDs don't exist or " +
                            "no reference exists."),
    }, description = "Delete a reference to a publication of an implementation. " +
            "The reference has to be previously created via a POST on /" + Constants.ALGORITHMS + "/{algorithmId}/" +
            Constants.IMPLEMENTATIONS + "/{implementationId}/" + Constants.PUBLICATIONS + ").")
    @DeleteMapping("/{implementationId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<Void> unlinkImplementationAndPublication(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID publicationId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        linkingService.unlinkImplementationAndPublication(implementationId, publicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, implementation or publication with given IDs don't exist.")
    }, description = "Retrieve a specific publication of an implementation")
    @GetMapping("/{implementationId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<PublicationDto> getPublicationOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID publicationId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        final Publication publication = publicationService.findById(publicationId);
        return new ResponseEntity<>(ModelMapperUtils.convert(publication, PublicationDto.class), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or implementation with given IDs don't exist.")
    }, description = "Retrieve referenced software platform for an implementation. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.SOFTWARE_PLATFORMS)
    public ResponseEntity<Page<SoftwarePlatformDto>> getSoftwarePlatformsOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        final var softwarePlatforms = implementationService.findLinkedSoftwarePlatforms(implementationId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(softwarePlatforms, SoftwarePlatformDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, implementation or software platform with given IDs don't exist or " +
                            "reference was already added.")
    }, description = "Add a reference to an existing software platform " +
            "(that was previously created via a POST on e.g. /" + Constants.SOFTWARE_PLATFORMS + "). " +
            "Only the ID is required in the request body, other attributes will be ignored and not changed.")
    @PostMapping("/{implementationId}/" + Constants.SOFTWARE_PLATFORMS)
    public ResponseEntity<Void> linkImplementationAndSoftwarePlatform(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated({ValidationGroups.IDOnly.class}) @RequestBody SoftwarePlatformDto softwarePlatformDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        linkingService.linkImplementationAndSoftwarePlatform(implementationId, softwarePlatformDto.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, implementation or software platform with given IDs don't exist or " +
                            "no reference exists.")
    }, description = "Delete a reference to a software platform of an implementation. " +
            "The reference has to be previously created via a POST on /" + Constants.ALGORITHMS + "/{algorithmId}/" +
            Constants.IMPLEMENTATIONS + "/{implementationId}/" + Constants.SOFTWARE_PLATFORMS + ").")
    @DeleteMapping("/{implementationId}/" + Constants.SOFTWARE_PLATFORMS + "/{softwarePlatformId}")
    public ResponseEntity<Void> unlinkImplementationAndSoftwarePlatform(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID softwarePlatformId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        linkingService.unlinkImplementationAndSoftwarePlatform(implementationId, softwarePlatformId);
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, implementation or software platform with given IDs don't exist."),
    }, description = "Retrieve a specific software platform and its basic properties of an implementation.")
    @GetMapping("/{implementationId}/" + Constants.SOFTWARE_PLATFORMS + "/{softwarePlatformId}")
    public ResponseEntity<SoftwarePlatformDto> getSoftwarePlatformOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID softwarePlatformId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        final var softwarePlatform = softwarePlatformService.findById(softwarePlatformId);
        return ResponseEntity.ok(ModelMapperUtils.convert(softwarePlatform, SoftwarePlatformDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or implementation with given IDs don't exist.")
    }, description = "Retrieve referenced compute resource properties of an implementation. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
    public ResponseEntity<Page<ComputeResourcePropertyDto>> getComputeResourcePropertiesOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        final var resources = computeResourcePropertyService.findComputeResourcePropertiesOfImplementation(
                implementationId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(resources, ComputeResourcePropertyDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, implementation or compute resource property type with given IDs don't exist.")
    }, description = "Add a compute resource property (e.g. a certain number of qubits) that is required by an implementation. " +
            "The compute resource property type has to be already created (e.g. via POST on /" + Constants.COMPUTE_RESOURCE_PROPERTY_TYPES + "). " +
            "As a result only the ID is required for the compute resource property type, other attributes will be ignored not changed.")
    @PostMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
    public ResponseEntity<ComputeResourcePropertyDto> createComputeResourcePropertyForImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        final var computeResourceProperty = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);

        final var createdComputeResourceProperty = computeResourcePropertyService
                .addComputeResourcePropertyToImplementation(implementationId, computeResourceProperty);
        return ResponseEntity.ok(ModelMapperUtils.convert(createdComputeResourceProperty, ComputeResourcePropertyDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. " +
                            "Algorithm, implementation, compute resource property or compute resource type with given IDs don't exist.")
    }, description = "Update a Compute resource property of an implementation. " +
            "For the compute resource property type only the ID is required, " +
            "other compute resource property type attributes will be ignored and not changed.")
    @PutMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public ResponseEntity<ComputeResourcePropertyDto> updateComputeResourcePropertyOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID computeResourcePropertyId,
            @Validated(ValidationGroups.Update.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfImplementation(implementationId, computeResourcePropertyId);

        computeResourcePropertyDto.setId(computeResourcePropertyId);
        final var resource = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);
        final var updatedResource = computeResourcePropertyService.update(resource);
        return ResponseEntity.ok(ModelMapperUtils.convert(updatedResource, ComputeResourcePropertyDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, implementation or compute resource property with given IDs don't exist."),
    }, description = "Delete a Compute resource property of an implementation. " +
            "The compute resource property type is not affected by this.")
    @DeleteMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public HttpEntity<Void> deleteComputeResourcePropertyOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID computeResourcePropertyId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfImplementation(implementationId, computeResourcePropertyId);

        computeResourcePropertyService.delete(computeResourcePropertyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body or algorithm resource is not implemented algorithm of implementation."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, implementation or compute resource property with given IDs don't exist."),
    }, description = "Retrieve a specific compute resource property of an implementation.")
    @GetMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public HttpEntity<ComputeResourcePropertyDto> getComputeResourcePropertyOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID computeResourcePropertyId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfImplementation(implementationId, computeResourcePropertyId);

        final var resource = computeResourcePropertyService.findById(computeResourcePropertyId);
        return ResponseEntity.ok(ModelMapperUtils.convert(resource, ComputeResourcePropertyDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation with given ID doesn't exist.")
    }, description = "Retrieve discussion topics of an implementation of an algorithm. If none are found an empty list is returned."
    )
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.DISCUSSION_TOPICS)
    public HttpEntity<PagedModel<EntityModel<DiscussionTopicDto>>> getDiscussionTopicsOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        return discussionTopicController.getDiscussionTopics(implementationId, listParameters);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation or discussion topic with given ID doesn't exist.")
    }, description = "Retrieve discussion topic of an implementation of an algorithm."
    )
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}")
    public HttpEntity<EntityModel<DiscussionTopicDto>> getDiscussionTopicOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID topicId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        return discussionTopicController.getDiscussionTopic(implementationId, topicId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation or discussion topic with given ID doesn't exist.")
    }, description = "Delete discussion topic of an implementation of an algorithm."
    )
    @ListParametersDoc
    @DeleteMapping("/{implementationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}")
    public HttpEntity<Void> deleteDiscussionTopicOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID topicId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        return discussionTopicController.deleteDiscussionTopic(implementationId, topicId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation or discussion topic with given ID doesn't exist.")
    }, description = "Create a discussion topic of an implementation of an algorithm."
    )
    @ListParametersDoc
    @PostMapping("/{implementationId}/" + Constants.DISCUSSION_TOPICS)
    public HttpEntity<EntityModel<DiscussionTopicDto>> createDiscussionTopicOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated(ValidationGroups.Create.class) @RequestBody DiscussionTopicDto discussionTopicDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        final var implementation = implementationService.findById(implementationId);
        return discussionTopicController.createDiscussionTopic(implementation, discussionTopicDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation or discussion topic with given ID doesn't exist.")
    }, description = "Update discussion topic of an implementation of an algorithm."
    )
    @ListParametersDoc
    @PutMapping("/{implementationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}")
    public HttpEntity<EntityModel<DiscussionTopicDto>> updateDiscussionTopicOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID topicId,
            @Validated(ValidationGroups.Update.class) @RequestBody DiscussionTopicDto discussionTopicDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        final var implementation = implementationService.findById(implementationId);
        return discussionTopicController.updateDiscussionTopic(implementation, topicId, discussionTopicDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation or discussion topic with given ID doesn't exist.")
    }, description = "Retrieve discussion comments of a discussion topic of an implementation of an algorithm." +
            " If none are found an empty list is returned."
    )
    @ListParametersDoc
    @GetMapping("/{implementationId}/" +
            Constants.DISCUSSION_TOPICS + "/{topicId}/" + Constants.DISCUSSION_COMMENTS)
    public HttpEntity<PagedModel<EntityModel<DiscussionCommentDto>>> getDiscussionCommentsOfDiscussionTopicOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID topicId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        return discussionTopicController.getDiscussionComments(implementationId, topicId, listParameters);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation, discussion topic or discussion comment with given ID doesn't exist.")
    }, description = "Retrieve discussion comment of a discussion topic of an implementation of an algorithm."
    )
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" +
            Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public HttpEntity<EntityModel<DiscussionCommentDto>> getDiscussionCommentOfDiscussionTopicOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID topicId,
            @PathVariable UUID commentId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        return discussionTopicController.getDiscussionComment(implementationId, topicId, commentId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation, discussion topic or discussion comment with given ID doesn't exist.")
    }, description = "Delete discussion comment of a discussion topic of an implementation of an algorithm."
    )
    @ListParametersDoc
    @DeleteMapping("/{implementationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" +
            Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public HttpEntity<Void> deleteDiscussionCommentOfDiscussionTopicOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID topicId,
            @PathVariable UUID commentId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        return discussionTopicController.deleteDiscussionComment(implementationId, topicId, commentId);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation or discussion topic with given ID doesn't exist.")
    }, description = "Create discussion comment of a discussion topic of an implementation of an algorithm."
    )
    @ListParametersDoc
    @PostMapping("/{implementationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" +
            Constants.DISCUSSION_COMMENTS)
    public HttpEntity<EntityModel<DiscussionCommentDto>> createDiscussionCommentOfDiscussionTopicOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID topicId,
            @Validated(ValidationGroups.Create.class) @RequestBody DiscussionCommentDto discussionCommentDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        return discussionTopicController.createDiscussionComment(implementationId, topicId, discussionCommentDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation or discussion topic with given ID doesn't exist.")
    }, description = "Update discussion comment of a discussion topic of an implementation of an algorithm."
    )
    @ListParametersDoc
    @PutMapping("/{implementationId}/" + Constants.DISCUSSION_TOPICS + "/{topicId}/" +
            Constants.DISCUSSION_COMMENTS + "/{commentId}")
    public HttpEntity<EntityModel<DiscussionCommentDto>> updateDiscussionCommentOfDiscussionTopicOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID topicId,
            @PathVariable UUID commentId,
            @Validated(ValidationGroups.Update.class) @RequestBody DiscussionCommentDto discussionCommentDto,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        return discussionTopicController.updateDiscussionComment(implementationId, topicId, commentId, discussionCommentDto);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation with given ID doesn't exist.")
    }, description = "Retrieve discussion topics of an implementation of an algorithm. If none are found an empty list is returned."
    )
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.IMPLEMENTATION_PACKAGES)
    public ResponseEntity<PagedModel<EntityModel<ImplementationPackageDto>>> getImplementationPackagesOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        final var packages =
                implementationPackageService.findImplementationPackagesByImplementationId(implementationId, listParameters.getPageable());
        return ResponseEntity.ok(implementationPackageAssembler.toModel(packages));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation with given ID doesn't exist.")
    }, description = "Retrieve implementation package of an implementation of an algorithm."
    )
    @GetMapping("/{implementationId}/" + Constants.IMPLEMENTATION_PACKAGES + "/{implementationPackageId}")
    public ResponseEntity<EntityModel<ImplementationPackageDto>> getImplementationPackageOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID implementationPackageId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        implementationPackageService.checkIfImplementationPackageIsLinkedToImplementation(implementationPackageId, implementationId);
        final var implementationPackage =
                implementationPackageService.findById(implementationPackageId);
        return ResponseEntity.ok(implementationPackageAssembler.toModel(implementationPackage));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation or implementation package with given ID doesn't exist.")
    }, description = "Create a implementation package of an implementation of an algorithm."
    )
    @PostMapping("/{implementationId}/" + Constants.IMPLEMENTATION_PACKAGES)
    public HttpEntity<EntityModel<ImplementationPackageDto>> createImplementationPackageOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated(ValidationGroups.Create.class) @RequestBody ImplementationPackageDto implementationPackageDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        final ImplementationPackage implementationP = ModelMapperUtils.convert(implementationPackageDto, ImplementationPackage.class);
        final ImplementationPackage implementationPackageToSave =
                implementationPackageService.create(implementationP, implementationId);
        return new ResponseEntity<>(implementationPackageAssembler.toModel(implementationPackageToSave), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
    }, description = "Uploads and adds a file to a given implementation")
    @PostMapping(value = "/{implementationId}/" + Constants.IMPLEMENTATION_PACKAGES + "/{implementationPackageId}/" +
            Constants.FILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDto> createFileForImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID implementationPackageId,
            @RequestParam("file") MultipartFile multipartFile) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        implementationPackageService.checkIfImplementationPackageIsLinkedToImplementation(implementationPackageId, implementationId);
        final File file = implementationPackageService.addFileToImplementationPackage(implementationPackageId, multipartFile);
        return ResponseEntity.ok(ModelMapperUtils.convert(file, FileDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404"),
    }, description = "Retrieve all files of an implementation")
    @GetMapping("/{implementationId}/" + Constants.FILES)
    public ResponseEntity<Page<FileDto>> getAllFilesOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        final Page<File> files =
                implementationService.findLinkedFiles(implementationId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(files, FileDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
    }, description = "Retrieve the file of an implementation package")
    @GetMapping("/{implementationId}/" + Constants.IMPLEMENTATION_PACKAGES + "/{implementationPackageId}/" + Constants.FILE)
    public ResponseEntity<FileDto> getFileOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID implementationPackageId
    ) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        implementationPackageService.checkIfImplementationPackageIsLinkedToImplementation(implementationPackageId, implementationId);
        final File file =
                implementationPackageService.findLinkedFile(implementationPackageId);
        return ResponseEntity.ok(ModelMapperUtils.convert(file, FileDto.class));
    }


    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",
                    description = "File of Implementation with given ID doesn't exist")
    }, description = "Downloads a specific file content of an Implementation")
    @GetMapping("/{implementationId}/" + Constants.IMPLEMENTATION_PACKAGES + "/{implementationPackageId}/" + Constants.FILE + "/content")
    public ResponseEntity<byte[]> downloadFileContentFromImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID implementationPackageId
    ) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        implementationPackageService.checkIfImplementationPackageIsLinkedToImplementation(implementationPackageId, implementationId);
        final File file =
                implementationPackageService.findLinkedFile(implementationPackageId);
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .body(fileService.getFileContent(file.getId()));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Implementation or File with given IDs don't exist")
    }, description = "Delete a file of an implementation.")
    @DeleteMapping("/{implementationId}/" + Constants.IMPLEMENTATION_PACKAGES + "/{implementationPackageId}/" + Constants.FILE)
    public ResponseEntity<Void> deleteFileOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID implementationPackageId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        implementationPackageService.checkIfImplementationPackageIsLinkedToImplementation(implementationPackageId, implementationId);
        final File file =
                implementationPackageService.findLinkedFile(implementationPackageId);
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        final ImplementationPackage implementationPackage = implementationPackageService.findById(implementationPackageId);
        implementationPackage.setFile(null);
        fileService.delete(file.getId());
        implementationPackageService.update(implementationPackage);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation or implementation package with given ID doesn't exist.")
    }, description = "Update implementation package of an implementation of an algorithm."
    )
    @PutMapping("/{implementationId}/" + Constants.IMPLEMENTATION_PACKAGES + "/{implementationPackageId}")
    public HttpEntity<EntityModel<ImplementationPackageDto>> updateImplementationPackageOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID implementationPackageId,
            @Validated(ValidationGroups.Update.class) @RequestBody ImplementationPackageDto implementationPackageDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        implementationPackageService.checkIfImplementationPackageIsLinkedToImplementation(implementationPackageId, implementationId);
        final ImplementationPackage implementationP = ModelMapperUtils.convert(implementationPackageDto, ImplementationPackage.class);
        final ImplementationPackage implementationPackageToUpdate =
                implementationPackageService.update(implementationP);
        return new ResponseEntity<>(implementationPackageAssembler.toModel(implementationPackageToUpdate), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. implementation, implementation package with given ID doesn't exist.")
    }, description = "Delete implementation package of an implementation of an algorithm."
    )
    @DeleteMapping("/{implementationId}/" + Constants.IMPLEMENTATION_PACKAGES + "/{implementationPackageId}")
    public HttpEntity<Void> deleteImplementationPackageOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID implementationPackageId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        implementationPackageService.checkIfImplementationPackageIsLinkedToImplementation(implementationPackageId, implementationId);
        implementationPackageService.delete(implementationPackageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
