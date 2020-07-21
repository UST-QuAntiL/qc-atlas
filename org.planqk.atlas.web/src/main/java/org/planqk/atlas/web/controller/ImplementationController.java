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

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputingResourceProperty;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ComputingResourcePropertyService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.ComputingResourceMixin;
import org.planqk.atlas.web.controller.mixin.PublicationMixin;
import org.planqk.atlas.web.dtos.ComputingResourcePropertyDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.linkassembler.ComputingResourcePropertyAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.linkassembler.SoftwarePlatformAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;
import org.planqk.atlas.web.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//import org.planqk.atlas.web.linkassembler.TagAssembler;

/**
 * Controller to access and manipulate implementations of quantum algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "algorithm")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.ALGORITHMS + "/" + "{algoId}" + "/" + Constants.IMPLEMENTATIONS)
@AllArgsConstructor
@Slf4j
public class ImplementationController {

    private final ComputingResourcePropertyService computingResourcePropertyService;
    private final ImplementationService implementationService;
    private final AlgorithmService algorithmService;
    private final PublicationService publicationService;
    private final SoftwarePlatformService softwarePlatformService;

    private final ImplementationAssembler implementationAssembler;
    private final PublicationAssembler publicationAssembler;
    private final ComputingResourcePropertyAssembler computingResourcePropertyAssembler;
    private final SoftwarePlatformAssembler softwarePlatformAssembler;

    private final PublicationMixin publicationMixin;
    private final ComputingResourceMixin computingResourceMixin;

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")}, description = "Retrieve all implementations for the algorithm")
    @GetMapping()
    public HttpEntity<CollectionModel<EntityModel<ImplementationDto>>> getImplementations(@PathVariable UUID algoId) {
        log.debug("Get to retrieve all implementations of algorithm with Id {} received.", algoId);
        algorithmService.findById(algoId);
        var implementations = implementationService.findByImplementedAlgorithm(algoId, RestUtils.getAllPageable());
        return ResponseEntity.ok(implementationAssembler.toModel(implementations));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", description = "Algorithm or implementation doesn't exist")}, description = "Retrieve a specific implemention of the algorithm.")
    @GetMapping("/{implId}")
    public HttpEntity<EntityModel<ImplementationDto>> getImplementation(@PathVariable UUID algoId, @PathVariable UUID implId) {
        log.debug("Get to retrieve implementation with id: {}.", implId);
        algorithmService.findById(algoId);
        var implementation = implementationService.findById(implId);
        return ResponseEntity.ok(implementationAssembler.toModel(implementation));
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")}, description = "Create a new implementation for the algorithm. Custom ID will be ignored.")
    @PostMapping()
    public HttpEntity<EntityModel<ImplementationDto>> createImplementation(@PathVariable UUID algoId, @Valid @RequestBody ImplementationDto impl) {
        log.debug("Post to create new implementation received.");
        Algorithm algorithm = algorithmService.findById(algoId);
        // Store and return implementation
        Implementation input = ModelMapperUtils.convert(impl, Implementation.class);
        input.setImplementedAlgorithm(algorithm);
        input = implementationService.save(input);
        return new ResponseEntity<>(implementationAssembler.toModel(input), HttpStatus.CREATED);
    }

//    @Operation(responses = { @ApiResponse(responseCode = "200") })
//    @GetMapping("/{implId}/" + Constants.TAGS)
//    public HttpEntity<CollectionModel<EntityModel<TagDto>>> getTags(@PathVariable UUID algoId, @PathVariable UUID implId) {
//        Implementation implementation = implementationService.findById(implId);
//         Set<Tag> tags = implementation.getTags();
//        return ResponseEntity.ok(tagAssembler.toModel(tags));
//    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")})
    @DeleteMapping("/{implId}")
    public HttpEntity<Void> deleteImplementation(@PathVariable UUID algoId, @PathVariable UUID implId) {
        log.debug("Delete to remove implementation with id: {}.", implId);
        algorithmService.findById(algoId);
        implementationService.delete(implId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")}, description = "Custom ID will be ignored.")
    @PutMapping("/{implId}")
    public HttpEntity<EntityModel<ImplementationDto>> updateImplementation(@PathVariable UUID algoId, @PathVariable UUID implId, @Valid @RequestBody ImplementationDto dto) {
        log.debug("Put to update implementation with id: {}.", implId);
        algorithmService.findById(algoId);
        var impl = ModelMapperUtils.convert(dto, Implementation.class);
        impl = implementationService.update(implId, impl);
        return ResponseEntity.ok(implementationAssembler.toModel(impl));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm or implementation doesn't exist")
    }, description = "Retrieve the required computing resources of an implementation")
    @GetMapping("/{implId}/" + Constants.COMPUTING_RESOURCES_PROPERTIES)
    public HttpEntity<PagedModel<EntityModel<ComputingResourcePropertyDto>>> getComputingResources(
            @PathVariable UUID algoId, @PathVariable UUID implId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        log.debug("Received Get to retrieve all computing resources of implementation with id: {}.", implId);
        algorithmService.findById(algoId);
        implementationService.findById(implId);
        var resources = computingResourcePropertyService.findAllComputingResourcesPropertiesByImplementationId(implId, RestUtils.getPageableFromRequestParams(page, size));
        return ResponseEntity.ok(computingResourcePropertyAssembler.toModel(resources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Id of the passed computing resource type is null"),
            @ApiResponse(responseCode = "404", description = "Computing resource type, implementation or algorithm can not be found with the given Ids")
    }, description = "Add a computing resource (e.g. a certain number of qubits) that is requiered by an implementation. Custom ID will be ignored. For computing resource type only ID is required, other computing resource type attributes will not change")
    @PostMapping("/{implId}/" + Constants.COMPUTING_RESOURCES_PROPERTIES)
    public HttpEntity<EntityModel<ComputingResourcePropertyDto>> addComputingResource(
            @PathVariable UUID algoId, @PathVariable UUID implId,
            @Valid @RequestBody ComputingResourcePropertyDto resourceDto
    ) {
        algorithmService.findById(algoId);
        var implementation = implementationService.findById(implId);
        ValidationUtils.validateComputingResourceProperty(resourceDto);
        var resource = computingResourceMixin.fromDto(resourceDto);
        resource = computingResourcePropertyService.addComputingResourcePropertyToImplementation(implementation, resource);
        return ResponseEntity.ok(computingResourcePropertyAssembler.toModel(resource));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400", description = "Resource doesn't belong to this implementation"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{implId}/" + Constants.COMPUTING_RESOURCES_PROPERTIES + "/{resourceId}")
    public HttpEntity<EntityModel<ComputingResourcePropertyDto>> getComputingResource(
            @PathVariable UUID algoId, @PathVariable UUID implId, @PathVariable UUID resourceId) {
        log.debug("Get received to retrieve computing resource with id {}.", resourceId);
        var computingResourceProperty = computingResourcePropertyService.findComputingResourcePropertyById(resourceId);
        if (Objects.isNull(computingResourceProperty.getImplementation()) || !computingResourceProperty.getImplementation().getId().equals(implId)) {
            log.debug("Implementation is not referenced from the computing resource to retrieve!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(computingResourcePropertyAssembler.toModel(computingResourceProperty));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400")}, description = "Update a computing resource of the implementation. Custom ID will be ignored. For computing resource type only ID is required, other computing resource type attributes will not change")
    @PutMapping("/{implId}/" + Constants.COMPUTING_RESOURCES_PROPERTIES + "/{resourceId}")
    public HttpEntity<EntityModel<ComputingResourcePropertyDto>> updateComputingResource(@PathVariable UUID algoId,
                                                                                         @PathVariable UUID implId,
                                                                                         @PathVariable UUID resourceId,
                                                                                         @RequestBody ComputingResourcePropertyDto resourceDto) {
        log.debug("Put received to update computing resource with id {}.", resourceId);
        ComputingResourceProperty computingResourceProperty = computingResourcePropertyService.findComputingResourcePropertyById(resourceId);
        Implementation implementation = implementationService.findById(implId);
        if (Objects.isNull(computingResourceProperty.getImplementation()) || !computingResourceProperty.getImplementation().getId().equals(implId)) {
            log.debug("Implementation is not referenced from the computing resource to update!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ValidationUtils.validateComputingResourceProperty(resourceDto);
        var resource = computingResourceMixin.fromDto(resourceDto);
        resource.setId(resourceId);
        resource = computingResourcePropertyService.addComputingResourcePropertyToImplementation(implementation, resource);
        return ResponseEntity.ok(computingResourcePropertyAssembler.toModel(resource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm, Implementation or computing resource with given id doesn't exist")
    }, description = "Delete a computing resource of the implementation.")
    @DeleteMapping("/{implId}/" + Constants.COMPUTING_RESOURCES_PROPERTIES + "/{resourceId}")
    public HttpEntity<Void> deleteComputingResource(@PathVariable UUID algoId, @PathVariable UUID implId,
                                                    @PathVariable UUID resourceId) {
        log.debug("Delete received to remove computing resource with id {}.", resourceId);
        algorithmService.findById(algoId);
        implementationService.findById(implId);
        ComputingResourceProperty computingResourceProperty = computingResourcePropertyService.findComputingResourcePropertyById(resourceId);
        if (Objects.isNull(computingResourceProperty.getImplementation()) || !computingResourceProperty.getImplementation().getId().equals(implId)) {
            log.debug("Implementation is not referenced from the computing resource to delete!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        computingResourcePropertyService.deleteComputingResourceProperty(resourceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Implementation doesn't exist")},
            description = "Get referenced publications for an implementation")
    @GetMapping("/{implId}/" + Constants.PUBLICATIONS)
    public HttpEntity<CollectionModel<EntityModel<PublicationDto>>> getPublications(@PathVariable UUID algoId,
                                                                                    @PathVariable UUID implId) {
        Implementation implementation = implementationService.findById(implId);
        Set<Publication> publications = implementation.getPublications();
        return ResponseEntity.ok(publicationAssembler.toModel(publications));
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404", content = @Content,
            description = "Implementation or publication does not exist.")},
            description = "Add a reference to an existing publication (that was previously created via a POST on /publications/). Custom ID will be ignored. For publication only ID is required, other publication attributes will not change. If the publication doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{implId}/" + Constants.PUBLICATIONS)
    public HttpEntity<CollectionModel<EntityModel<PublicationDto>>> addPublication(@PathVariable UUID algoId,
                                                                                   @PathVariable UUID implId,
                                                                                   @RequestBody PublicationDto publicationDto) {
        Implementation implementation = implementationService.findById(implId);
        publicationMixin.addPublication(implementation, publicationDto);
        implementation = implementationService.save(implementation);
        return ResponseEntity.ok(publicationAssembler.toModel(implementation.getPublications()));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Get a specific referenced publication of an implementation.")
    @GetMapping("/{implId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public HttpEntity<EntityModel<PublicationDto>> getPublication(@PathVariable UUID algoId,
                                                                  @PathVariable UUID implId,
                                                                  @PathVariable UUID publicationId) {
        log.debug("Get to retrieve referenced publication with Id {} from implementation with Id {}", publicationId, implId);
        Publication publication = publicationService.findById(publicationId);
        Set<Publication> publications = implementationService.findById(implId).getPublications();
        if (!publications.contains(publication)) {
            log.info("Trying to get Publication that is not referenced by the implementation");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(publicationAssembler.toModel(publication));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Delete a reference to a publication of the implementation.")
    @DeleteMapping("/{implId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public HttpEntity<Void> deleteReferenceToPublication(@PathVariable UUID algoId,
                                                         @PathVariable UUID implId,
                                                         @PathVariable UUID publicationId) {
        Implementation implementation = implementationService.findById(implId);
        publicationMixin.unlinkPublication(implementation, publicationId);
        implementationService.save(implementation);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Implementation doesn't exist")},
            description = "Get referenced software platform for an implementation")
    @GetMapping("/{implId}/" + Constants.SOFTWARE_PLATFORMS)
    public HttpEntity<CollectionModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatforms(@PathVariable UUID algoId,
                                                                                              @PathVariable UUID implId) {
        Implementation implementation = implementationService.findById(implId);
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(implementation.getSoftwarePlatforms()));
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404", content = @Content,
            description = "Software platform or publication does not exist")},
            description = "Add a reference to an existing software platform (that was previously created via a POST on /software-platforms/). Custom ID will be ignored. For software platform only ID is required, other software platform attributes will not change. If the software platform doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{implId}/" + Constants.SOFTWARE_PLATFORMS)
    public HttpEntity<CollectionModel<EntityModel<SoftwarePlatformDto>>> addSoftwarePlatform(@PathVariable UUID algoId,
                                                                                             @PathVariable UUID implId,
                                                                                             @RequestBody SoftwarePlatformDto softwarePlatformDto) {
        Implementation implementation = implementationService.findById(implId);
        SoftwarePlatform softwarePlatform = softwarePlatformService.findById(softwarePlatformDto.getId());

        // update software platform reference list of implementation
        var softwarePlatforms = implementation.getSoftwarePlatforms();
        softwarePlatforms.add(softwarePlatform);
        implementation.setSoftwarePlatforms(softwarePlatforms);

        var updatedSoftwarePlatforms = implementationService.save(implementation).getSoftwarePlatforms();
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(updatedSoftwarePlatforms));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Get a specific referenced software platform of an implementation")
    @GetMapping("/{implId}/" + Constants.SOFTWARE_PLATFORMS + "/{platformId}")
    public HttpEntity<EntityModel<SoftwarePlatformDto>> getSoftwarePlatform(@PathVariable UUID algoId,
                                                                            @PathVariable UUID implId,
                                                                            @PathVariable UUID platformId) {
        log.debug("Get to retrieve referenced software platform with Id {} from implementation with Id {}", platformId, implId);
        SoftwarePlatform platform = softwarePlatformService.findById(platformId);
        var softwarePlatforms = implementationService.findById(implId).getSoftwarePlatforms();
        if (!softwarePlatforms.contains(platform)) {
            log.info("Trying to get software platform that is not referenced by the implementation");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(platform));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Delete a reference to a software platform of the implementation")
    @DeleteMapping("/{implId}/" + Constants.SOFTWARE_PLATFORMS + "/{platformId}")
    public HttpEntity<Void> deleteReferenceToSoftwarePlatform(@PathVariable UUID algoId,
                                                              @PathVariable UUID implId,
                                                              @PathVariable UUID platformId) {
        Implementation implementation = implementationService.findById(implId);
        Set<SoftwarePlatform> softwarePlatforms = implementation.getSoftwarePlatforms();
        softwarePlatforms.removeIf(platform -> platform.getId().equals(platformId));
        implementation.setSoftwarePlatforms(softwarePlatforms);
        implementationService.save(implementation);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
