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

import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.ComputeResourcePropertyMixin;
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
import org.planqk.atlas.web.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.IMPLEMENTATIONS)
@AllArgsConstructor
@Slf4j
public class ImplementationController {

    private final ImplementationService implementationService;
    private final ImplementationAssembler implementationAssembler;

    private final TagService tagService;
    private final TagAssembler tagAssembler;

    private final ComputeResourcePropertyService computeResourcePropertyService;
    private final ComputeResourcePropertyAssembler computeResourcePropertyAssembler;

    private final PublicationAssembler publicationAssembler;

    private final SoftwarePlatformAssembler softwarePlatformAssembler;

    private final LinkingService linkingService;

    private final ComputeResourcePropertyMixin computeResourcePropertyMixin;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Implementation doesn't exist")
    }, description = "Custom ID will be ignored.")
    @PutMapping()
    public ResponseEntity<EntityModel<ImplementationDto>> updateImplementation(
            @Validated(ValidationGroups.Update.class) @RequestBody ImplementationDto implementationDto) {
        Implementation updatedImplementation = implementationService.update(
                implementationDto.getId(), ModelMapperUtils.convert(implementationDto, Implementation.class));
        return ResponseEntity.ok(implementationAssembler.toModel(updatedImplementation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Implementation doesn't exist")
    }, description = "")
    @DeleteMapping("/{implementationId}/")
    public ResponseEntity<Void> deleteImplementation(
            @PathVariable UUID implementationId) {
        implementationService.delete(implementationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Implementation doesn't exist")
    }, description = "Retrieve a specific implementation of the algorithm.")
    @GetMapping("/{implementationId}/")
    public ResponseEntity<EntityModel<ImplementationDto>> getImplementation(
            @PathVariable UUID implementationId) {
        var implementation = implementationService.findById(implementationId);
        return ResponseEntity.ok(implementationAssembler.toModel(implementation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @GetMapping("/{implementationId}/" + Constants.TAGS)
    public HttpEntity<CollectionModel<EntityModel<TagDto>>> getTagsOfImplementation(
            @PathVariable UUID implementationId) {
        Implementation implementation = implementationService.findById(implementationId);
        return ResponseEntity.ok(tagAssembler.toModel(implementation.getTags()));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @PutMapping("/{implementationId}/" + Constants.TAGS)
    public HttpEntity<Void> addTagToImplementation(
            @PathVariable UUID implementationId,
            @Validated @RequestBody TagDto tagDto) {
        tagService.addTagToImplementation(implementationId, ModelMapperUtils.convert(tagDto, Tag.class));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @DeleteMapping("/{implementationId}/" + Constants.TAGS)
    public HttpEntity<Void> removeTagFromImplementation(
            @PathVariable UUID implementationId,
            @Validated @RequestBody TagDto tagDto) {
        tagService.removeTagFromImplementation(implementationId, ModelMapperUtils.convert(tagDto, Tag.class));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm or implementation doesn't exist")
    }, description = "Retrieve the required computing resources of an implementation")
    @GetMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES)
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<ComputeResourcePropertyDto>>> getComputeResourcePropertiesOfImplementation(
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var resources = computeResourcePropertyService.findAllComputeResourcesPropertiesByImplementationId(
                implementationId, listParameters.getPageable());
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(resources));
    }

    // TODO refactor
    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Id of the passed computing resource type is null"),
            @ApiResponse(responseCode = "404", description = "Computing resource type, " +
                    "implementation or algorithm can not be found with the given Ids")
    }, description = "Add a computing resource (e.g. a certain number of qubits) " +
            "that is requiered by an implementation. Custom ID will be ignored. For computing " +
            "resource type only ID is required, other computing resource type attributes will not change")
    @PostMapping("/{implementationId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES)
    public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> createComputeResourcePropertyForImplementation(
            @PathVariable UUID implementationId,
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        var implementation = implementationService.findById(implementationId);
        var resource = computeResourcePropertyMixin.fromDto(computeResourcePropertyDto);
        ValidationUtils.validateComputingResourceProperty(resource);
        resource = computeResourcePropertyService.addComputeResourcePropertyToImplementation(implementation, resource);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(resource));
    }

//    @Operation(operationId = "getComputingResourceByImplementation", responses = {
//            @ApiResponse(responseCode = "200"),
//            @ApiResponse(responseCode = "400", description = "Resource doesn't belong to this implementation"),
//            @ApiResponse(responseCode = "404")
//    }, description = "")
//    @GetMapping("/{implId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES + "/{resourceId}")
//    public HttpEntity<EntityModel<ComputeResourcePropertyDto>> getComputingResource(
//            @PathVariable UUID algoId,
//            @PathVariable UUID implId,
//            @PathVariable UUID resourceId) {
//        var computingResourceProperty = computeResourcePropertyService.findComputeResourcePropertyById(resourceId);
//        if (Objects.isNull(computingResourceProperty.getImplementation()) || !computingResourceProperty.getImplementation().getId().equals(implId)) {
//            log.debug("Implementation is not referenced from the computing resource to retrieve!");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(computingResourceProperty));
//    }
//
//    @Operation(operationId = "updateComputingResourceByImplementation", responses = {
//            @ApiResponse(responseCode = "200"),
//            @ApiResponse(responseCode = "400")
//    }, description = "Update a computing resource of the implementation. " +
//            "Custom ID will be ignored. For computing resource type only ID is required, " +
//            "other computing resource type attributes will not change")
//    @PutMapping("/{implId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES + "/{resourceId}")
//    public HttpEntity<EntityModel<ComputeResourcePropertyDto>> updateComputingResource(
//            @PathVariable UUID algoId,
//            @PathVariable UUID implId,
//            @PathVariable UUID resourceId,
//            @RequestBody ComputeResourcePropertyDto resourceDto) {
//        log.debug("Put received to update computing resource with id {}.", resourceId);
//        ComputeResourceProperty computeResourceProperty = computeResourcePropertyService.findComputeResourcePropertyById(resourceId);
//        Implementation implementation = implementationService.findById(implId);
//        if (Objects.isNull(computeResourceProperty.getImplementation()) || !computeResourceProperty.getImplementation().getId().equals(implId)) {
//            log.debug("Implementation is not referenced from the computing resource to update!");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        ValidationUtils.validateComputingResourceProperty(resourceDto);
//        var resource = computeResourcePropertyMixin.fromDto(resourceDto);
//        resource.setId(resourceId);
//        resource = computeResourcePropertyService.addComputeResourcePropertyToImplementation(implementation, resource);
//        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(resource));
//    }
//
//    @Operation(operationId = "deleteComputingResourceByImplementation", responses = {
//            @ApiResponse(responseCode = "200"),
//            @ApiResponse(responseCode = "400"),
//            @ApiResponse(responseCode = "404",
//                    description = "Algorithm, Implementation or computing resource with given id doesn't exist")
//    }, description = "Delete a computing resource of the implementation.")
//    @DeleteMapping("/{implId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES + "/{resourceId}")
//    public HttpEntity<Void> deleteComputingResource(
//            @PathVariable UUID algoId,
//            @PathVariable UUID implId,
//            @PathVariable UUID resourceId) {
//        algorithmService.findById(algoId);
//        implementationService.findById(implId);
//        ComputeResourceProperty computeResourceProperty = computeResourcePropertyService.findComputeResourcePropertyById(resourceId);
//        if (Objects.isNull(computeResourceProperty.getImplementation()) || !computeResourceProperty.getImplementation().getId().equals(implId)) {
//            log.debug("Implementation is not referenced from the computing resource to delete!");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        computeResourcePropertyService.deleteComputeResourceProperty(resourceId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Implementation doesn't exist")
    }, description = "Get referenced publications for an implementation")
    @GetMapping("/{implementationId}/" + Constants.PUBLICATIONS)
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<PublicationDto>>> getPublicationsOfImplementation(
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var publications = implementationService.findLinkedPublications(implementationId, listParameters.getPageable());
        return ResponseEntity.ok(publicationAssembler.toModel(publications));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content,
                    description = "Implementation or publication does not exist.")
    }, description = "Add a reference to an existing publication " +
            "(that was previously created via a POST on /publications/). Custom ID will be ignored. " +
            "For publication only ID is required, other publication attributes will not change. " +
            "If the publication doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{implementationId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<Void> linkImplementationAndPublication(
            @PathVariable UUID implementationId,
            @PathVariable UUID publicationId) {
        linkingService.linkImplementationAndPublication(implementationId, publicationId);
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
    }, description = "Delete a reference to a publication of the implementation.")
    @DeleteMapping("/{implementationId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<Void> unlinkImplementationAndPublication(
            @PathVariable UUID implementationId,
            @PathVariable UUID publicationId) {
        linkingService.unlinkImplementationAndPublication(implementationId, publicationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Implementation doesn't exist")
    }, description = "Get referenced software platform for an implementation")
    @GetMapping("/{implementationId}/" + Constants.SOFTWARE_PLATFORMS)
    @ListParametersDoc
    public HttpEntity<CollectionModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatformsOfImplementation(
            @PathVariable UUID implementationId,
            @Parameter(hidden = true) ListParameters listParameters
    ) {
        var softwarePlatforms = implementationService.findLinkedSoftwarePlatforms(implementationId, listParameters.getPageable());
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatforms));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content,
                    description = "Software platform or publication does not exist")
    }, description = "Add a reference to an existing software platform" +
            "(that was previously created via a POST on /software-platforms/)." +
            "Custom ID will be ignored. For software platform only ID is required," +
            "other software platform attributes will not change." +
            "If the software platform doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{implementationId}/" + Constants.SOFTWARE_PLATFORMS + "/{softwarePlatformId}")
    public HttpEntity<CollectionModel<EntityModel<SoftwarePlatformDto>>> linkImplementationAndSoftwarePlatform(
            @PathVariable UUID implementationId,
            @PathVariable UUID softwarePlatformId) {
        linkingService.linkImplementationAndSoftwarePlatform(implementationId, softwarePlatformId);
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content,
                    description = "Software platform or publication does not exist")
    }, description = "Delete a reference to a software platform of the implementation")
    @DeleteMapping("/{implementationId}/" + Constants.SOFTWARE_PLATFORMS + "/{softwarePlatformId}")
    public HttpEntity<Void> unlinkImplementationAndSoftwarePlatform(
            @PathVariable UUID implementationId,
            @PathVariable UUID softwarePlatformId) {
        linkingService.unlinkImplementationAndSoftwarePlatform(implementationId, softwarePlatformId);
        return ResponseEntity.noContent().build();
    }
}
