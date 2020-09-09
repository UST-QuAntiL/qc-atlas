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

import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.ComputeResourcePropertyAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.linkassembler.SoftwarePlatformAssembler;
import org.planqk.atlas.web.linkassembler.TagAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
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

/**
 * Controller to access and manipulate implementations of quantum algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_ALGORITHM)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.IMPLEMENTATIONS)
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

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
    }, description = "Create a new implementation for the algorithm.")
    @PostMapping
    public ResponseEntity<EntityModel<ImplementationDto>> createImplementation(
            @PathVariable UUID algorithmId,
            @Validated(ValidationGroups.Create.class) @RequestBody ImplementationDto implementationDto) {
        Implementation savedImplementation = implementationService.create(
                ModelMapperUtils.convert(implementationDto, Implementation.class), algorithmId);
        return new ResponseEntity<>(implementationAssembler.toModel(savedImplementation), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Implementation doesn't exist")
    }, description = "Custom ID will be ignored.")
    @PutMapping("/{implementationId}")
    public ResponseEntity<EntityModel<ImplementationDto>> updateImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated(ValidationGroups.Update.class) @RequestBody ImplementationDto implementationDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        implementationDto.setId(implementationId);
        Implementation updatedImplementation = implementationService.update(
                ModelMapperUtils.convert(implementationDto, Implementation.class));
        return ResponseEntity.ok(implementationAssembler.toModel(updatedImplementation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Implementation doesn't exist")
    }, description = "")
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
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Implementation doesn't exist")
    }, description = "Retrieve a specific implementation of the algorithm.")
    @GetMapping("/{implementationId}")
    public ResponseEntity<EntityModel<ImplementationDto>> getImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        var implementation = implementationService.findById(implementationId);
        return ResponseEntity.ok(implementationAssembler.toModel(implementation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @GetMapping("/{implementationId}/" + Constants.TAGS)
    public ResponseEntity<CollectionModel<EntityModel<TagDto>>> getTagsOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        Implementation implementation = implementationService.findById(implementationId);
        return ResponseEntity.ok(tagAssembler.toModel(implementation.getTags()));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @PostMapping("/{implementationId}/" + Constants.TAGS)
    public ResponseEntity<Void> addTagToImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated @RequestBody TagDto tagDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        tagService.addTagToImplementation(implementationId, ModelMapperUtils.convert(tagDto, Tag.class));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @DeleteMapping("/{implementationId}/" + Constants.TAGS)
    public ResponseEntity<Void> removeTagFromImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated @RequestBody TagDto tagDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        tagService.removeTagFromImplementation(implementationId, ModelMapperUtils.convert(tagDto, Tag.class));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Implementation doesn't exist")
    }, description = "Get referenced publications for an implementation")
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.PUBLICATIONS)
    public ResponseEntity<PagedModel<EntityModel<PublicationDto>>> getPublicationsOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        var publications = implementationService.findLinkedPublications(implementationId, listParameters.getPageable());
        return ResponseEntity.ok(publicationAssembler.toModel(publications));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Implementation or publication does not exist.")
    }, description = "Add a reference to an existing publication " +
            "(that was previously created via a POST on /publications/). Custom ID will be ignored. " +
            "For publication only ID is required, other publication attributes will not change. " +
            "If the publication doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{implementationId}/" + Constants.PUBLICATIONS)
    public ResponseEntity<Void> linkImplementationAndPublication(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated({ValidationGroups.Update.class}) @RequestBody PublicationDto publicationDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        linkingService.linkImplementationAndPublication(implementationId, publicationDto.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
    }, description = "Delete a reference to a publication of the implementation.")
    @DeleteMapping("/{implementationId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<Void> unlinkImplementationAndPublication(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID publicationId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        linkingService.unlinkImplementationAndPublication(implementationId, publicationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Retrieve a publication of an implementation")
    @GetMapping("/{implementationId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<EntityModel<PublicationDto>> getPublicationOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID publicationId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        Publication publication = publicationService.findById(publicationId);
        return new ResponseEntity<>(publicationAssembler.toModel(publication), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Implementation doesn't exist")
    }, description = "Get referenced software platform for an implementation")
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.SOFTWARE_PLATFORMS)
    public ResponseEntity<CollectionModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatformsOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        var softwarePlatforms = implementationService.findLinkedSoftwarePlatforms(implementationId, listParameters.getPageable());
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatforms));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software platform or publication does not exist")
    }, description = "Add a reference to an existing software platform" +
            "(that was previously created via a POST on /software-platforms/)." +
            "Custom ID will be ignored. For software platform only ID is required," +
            "other software platform attributes will not change." +
            "If the software platform doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{implementationId}/" + Constants.SOFTWARE_PLATFORMS)
    public ResponseEntity<CollectionModel<EntityModel<SoftwarePlatformDto>>> linkImplementationAndSoftwarePlatform(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated({ValidationGroups.Update.class}) SoftwarePlatformDto softwarePlatformDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        linkingService.linkImplementationAndSoftwarePlatform(implementationId, softwarePlatformDto.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software platform or publication does not exist")
    }, description = "Delete a reference to a software platform of the implementation")
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
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform with given id does not exist"),
    }, description = "Retrieve a specific software platform and its basic properties.")
    @GetMapping("/{implementationId}/" + Constants.SOFTWARE_PLATFORMS + "/{softwarePlatformId}")
    public ResponseEntity<EntityModel<SoftwarePlatformDto>> getSoftwarePlatformOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID softwarePlatformId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        var softwarePlatform = softwarePlatformService.findById(softwarePlatformId);
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatform));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Implementation doesn't exist")
    }, description = "Retrieve the required computing resources of an implementation")
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
    public ResponseEntity<PagedModel<EntityModel<ComputeResourcePropertyDto>>> getComputeResourcePropertiesOfImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        var resources = computeResourcePropertyService.findComputeResourcePropertiesOfImplementation(
                implementationId, listParameters.getPageable());
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(resources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Id of the passed computing resource type is null"),
            @ApiResponse(responseCode = "404", description = "Computing resource type, " +
                    "implementation or algorithm can not be found with the given Ids")
    }, description = "Add a computing resource (e.g. a certain number of qubits) " +
            "that is required by an implementation. Custom ID will be ignored. For computing " +
            "resource type only ID is required, other computing resource type attributes will not change")
    @PostMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
    public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> createComputeResourcePropertyForImplementation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);

        var computeResourceProperty = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);

        var createdComputeResourceProperty = computeResourcePropertyService
                .addComputeResourcePropertyToImplementation(implementationId, computeResourceProperty);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(createdComputeResourceProperty));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")},
            description = "Update a Compute resource property of an implementation. " +
                    "For compute resource property type only ID is required, other compute resource property type " +
                    "attributes will not change.")
    @PutMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> updateComputeResourcePropertyOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID computeResourcePropertyId,
            @Validated(ValidationGroups.Update.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfImplementation(implementationId, computeResourcePropertyId);

        computeResourcePropertyDto.setId(computeResourcePropertyId);
        var resource = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);
        var updatedResource = computeResourcePropertyService.update(resource);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(updatedResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute resource property with given id doesn't exist"),
    }, description = "Delete a Compute resource property of an implementation")
    @DeleteMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public HttpEntity<Void> deleteComputeResourceProperty(
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
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
    }, description = "Retrieve a specific compute resource property of an implementation")
    @GetMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public HttpEntity<EntityModel<ComputeResourcePropertyDto>> getComputeResourceProperty(
            @PathVariable UUID algorithmId,
            @PathVariable UUID implementationId,
            @PathVariable UUID computeResourcePropertyId) {
        implementationService.checkIfImplementationIsOfAlgorithm(implementationId, algorithmId);
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfImplementation(implementationId, computeResourcePropertyId);

        var resource = computeResourcePropertyService.findById(computeResourcePropertyId);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(resource));
    }
}
