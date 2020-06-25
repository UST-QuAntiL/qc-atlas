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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputingResource;
import org.planqk.atlas.core.model.ComputingResourceType;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ComputingResourceService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ComputingResourceDto;
import org.planqk.atlas.web.dtos.ComputingResourceTypeDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.linkassembler.ComputingResourceAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//import org.planqk.atlas.web.linkassembler.TagAssembler;

/**
 * Controller to access and manipulate implementations of quantum algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "algorithm")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHMS + "/" + "{algoId}" + "/" + Constants.IMPLEMENTATIONS)
@AllArgsConstructor
public class ImplementationController {

    final private static Logger LOG = LoggerFactory.getLogger(ImplementationController.class);
    private final ComputingResourceService computingResourceService;
    private final ComputingResourceAssembler computingResourceAssembler;
    private final PagedResourcesAssembler<ComputingResourceDto> computingResourcePaginationAssembler;
    private final ImplementationService implementationService;
    private final AlgorithmService algorithmService;
    private final ImplementationAssembler implementationAssembler;
    private final PublicationAssembler publicationAssembler;
    private final PublicationService publicationService;
    private final SoftwarePlatformService softwarePlatformService;
//    private final TagAssembler tagAssembler;

//    private TagAssembler tagAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")}, description = "Retrieve all implementations for the algorithm")
    @GetMapping()
    public HttpEntity<CollectionModel<EntityModel<ImplementationDto>>> getImplementations(@PathVariable UUID algoId) {
        LOG.debug("Get to retrieve all implementations of algorithm with Id {} received.", algoId);
        algorithmService.findById(algoId);
        Set<ImplementationDto> dtoList = new HashSet<ImplementationDto>();
        // Add all available implementations to the response
        for (Implementation impl : implementationService.findAll(RestUtils.getAllPageable())) {
            if (impl.getImplementedAlgorithm().getId().equals(algoId)) {
                dtoList.add(ModelMapperUtils.convert(impl, ImplementationDto.class));
            }
        }
        // Generate CollectionModel
        CollectionModel<EntityModel<ImplementationDto>> dtoOutput = HateoasUtils.generateCollectionModel(dtoList);
        // Add EntityLinks
        implementationAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Retrieve a specific implemention of the algorithm")
    @GetMapping("/{implId}")
    public HttpEntity<EntityModel<ImplementationDto>> getImplementation(@PathVariable UUID algoId, @PathVariable UUID implId) {
        LOG.debug("Get to retrieve implementation with id: {}.", implId);
        // Get Implementation
        Implementation implementation = implementationService.findById(implId);
        // Generate EntityModel
        EntityModel<ImplementationDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(implementation, ImplementationDto.class));
        // Fill Links
        implementationAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201")}, description = "Create a new implementation for the algorithm")
    @PostMapping()
    public HttpEntity<EntityModel<ImplementationDto>> createImplementation(@PathVariable UUID algoId, @Valid @RequestBody ImplementationDto impl) {
        LOG.debug("Post to create new implementation received.");
        // Get Algorithm
        Algorithm algorithm = algorithmService.findById(algoId);
        // Store and return implementation
        Implementation input = ModelMapperUtils.convert(impl, Implementation.class);
        input.setImplementedAlgorithm(algorithm);
        // Generate EntityModel
        EntityModel<ImplementationDto> dtoOutput = HateoasUtils.generateEntityModel(
                ModelMapperUtils.convert(implementationService.save(input), ImplementationDto.class));
        // Add Links
        implementationAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }

//    @Operation(responses = { @ApiResponse(responseCode = "200") })
//    @GetMapping("/{implId}/" + Constants.TAGS)
//    public HttpEntity<CollectionModel<EntityModel<TagDto>>> getTags(@PathVariable UUID implId) {
//        // Get Implementation
//        Implementation implementation = implementationService.findById(implId);
////        // Get Tags of Implementation
////        Set<Tag> tags = implementation.getTags();
//        // Translate Entity to DTO
////        Set<TagDto> dtoTags = ModelMapperUtils.convertSet(tags, TagDto.class);
//        // Create CollectionModel
////        CollectionModel<EntityModel<TagDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTags);
//        // Fill EntityModel Links
////        tagAssembler.addLinks(resultCollection);
//        // Fill Collection-Links
//        implementationAssembler.addTagLink(resultCollection, implId);
//        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
//    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @DeleteMapping("/{implId}")
    public HttpEntity<?> deleteImplementation(@PathVariable UUID algoId, @PathVariable UUID implId) {
        LOG.debug("Delete to remove implementation with id: {}.", implId);
        implementationService.delete(implId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @PutMapping("/{implId}")
    public HttpEntity<EntityModel<ImplementationDto>> updateImplementation(@PathVariable UUID algoId, @PathVariable UUID implId, @Valid @RequestBody ImplementationDto dto) {
        var impl = ModelMapperUtils.convert(dto, Implementation.class);
        impl = implementationService.update(implId, impl);
        return ResponseEntity.ok(HateoasUtils.generateEntityModel(ModelMapperUtils.convert(impl, ImplementationDto.class)));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Retrieve the required computing resources of an implementation")
    @GetMapping("/{implId}/" + Constants.COMPUTING_RESOURCES)
    public ResponseEntity<PagedModel<EntityModel<ComputingResourceDto>>> getComputingResources(
            @PathVariable UUID implId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        implementationService.findById(implId);
        var resources = computingResourceService.findAllResourcesByImplementationId(implId, RestUtils.getPageableFromRequestParams(page, size));
        var typeDtoes = ModelMapperUtils.convertPage(resources, ComputingResourceDto.class);
        var pagedModel = computingResourcePaginationAssembler.toModel(typeDtoes);
        computingResourceAssembler.addLinks(pagedModel);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Id of the passed computing resource type is null"),
            @ApiResponse(responseCode = "404", description = "Computing resource type or implementation can not be found with the given Ids")
    }, description = "Add a computing resource (e.g. a certain number of qubits) that is requiered by an implementation")
    @PostMapping("/{implId}/" + Constants.COMPUTING_RESOURCES)
    public ResponseEntity<EntityModel<ComputingResourceDto>> addComputingResource(
            @PathVariable UUID implId,
            @Valid @RequestBody ComputingResourceDto resourceDto
    ) {
        var implementation = implementationService.findById(implId);

        if (Objects.isNull(resourceDto.getType().getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ComputingResourceType type = computingResourceService.findResourceTypeById(resourceDto.getType().getId());
        resourceDto.setType(ModelMapperUtils.convert(type, ComputingResourceTypeDto.class));
        ComputingResource updatedComputeResource = computingResourceService.addComputingResourceToImplementation(
                implementation,
                ModelMapperUtils.convert(resourceDto, ComputingResource.class)
        );
        EntityModel<ComputingResourceDto> dto = HateoasUtils.generateEntityModel(
                ModelMapperUtils.convert(updatedComputeResource, ComputingResourceDto.class));
        return ResponseEntity.ok(dto);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400", description = "Resource doesn't belong to this implementation"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{implId}/" + Constants.COMPUTING_RESOURCES + "/{resourceId}")
    public HttpEntity<EntityModel<ComputingResourceDto>> getComputingResource(
            @PathVariable UUID implId, @PathVariable UUID resourceId) {
        LOG.debug("Get received to retrieve computing resource with id {}.", resourceId);
        ComputingResource computingResource = computingResourceService.findResourceById(resourceId);
        if (Objects.isNull(computingResource.getImplementation()) || !computingResource.getImplementation().getId().equals(implId)) {
            LOG.debug("Implementation is not referenced from the computing resource to retrieve!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        EntityModel<ComputingResourceDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(computingResource, ComputingResourceDto.class));
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400")}, description = "Update a computing resource of the implementation")
    @PutMapping("/{implId}/" + Constants.COMPUTING_RESOURCES + "/{resourceId}")
    public HttpEntity<EntityModel<ComputingResourceDto>> updateComputingResource(@PathVariable UUID implId,
                                                                                 @PathVariable UUID resourceId, @RequestBody ComputingResourceDto resourceDto) {
        LOG.debug("Put received to update computing resource with id {}.", resourceId);
        ComputingResource computingResource = computingResourceService.findResourceById(resourceId);
        Implementation implementation = implementationService.findById(implId);
        if (Objects.isNull(computingResource.getImplementation()) || !computingResource.getImplementation().getId().equals(implId)) {
            LOG.debug("Implementation is not referenced from the computing resource to update!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ComputingResourceType type = computingResourceService.findResourceTypeById(resourceDto.getType().getId());
        resourceDto.setType(ModelMapperUtils.convert(type, ComputingResourceTypeDto.class));
        resourceDto.setId(resourceId);
        ComputingResource updatedComputeResource = computingResourceService.addComputingResourceToImplementation(
                implementation,
                ModelMapperUtils.convert(resourceDto, ComputingResource.class)
        );
        EntityModel<ComputingResourceDto> dto = HateoasUtils.generateEntityModel(
                ModelMapperUtils.convert(updatedComputeResource, ComputingResourceDto.class));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400")}, description = "Delete a computing resource of the implementation")
    @DeleteMapping("/{implId}/" + Constants.COMPUTING_RESOURCES + "/{resourceId}")
    public HttpEntity<ComputingResourceDto> deleteComputingResource(@PathVariable UUID implId,
                                                                    @PathVariable UUID resourceId) {
        LOG.debug("Delete received to remove computing resource with id {}.", resourceId);
        ComputingResource computingResource = computingResourceService.findResourceById(resourceId);
        if (Objects.isNull(computingResource.getImplementation()) || !computingResource.getImplementation().getId().equals(implId)) {
            LOG.debug("Implementation is not referenced from the computing resource to delete!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        computingResourceService.deleteComputingResource(resourceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Implementation doesn't exist")},
            description = "Get referenced publications for an implementation")
    @GetMapping("/{implId}/" + Constants.PUBLICATIONS)
    public HttpEntity<CollectionModel<EntityModel<PublicationDto>>> getPublications(@PathVariable UUID implId) {
        Implementation implementation = implementationService.findById(implId);
        Set<Publication> publications = implementation.getPublications();
        Set<PublicationDto> dtoPublications = ModelMapperUtils.convertSet(publications, PublicationDto.class);
        CollectionModel<EntityModel<PublicationDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoPublications);
        publicationAssembler.addLinks(resultCollection);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404", content = @Content,
            description = "Implementation or publication does not exist")},
            description = "Add a reference to an existing publication (that was previously created via a POST on /publications/). If the publication doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{implId}/" + Constants.PUBLICATIONS)
    public HttpEntity<CollectionModel<EntityModel<PublicationDto>>> addPublication(@PathVariable UUID implId, @RequestBody PublicationDto publicationDto) {
        Implementation implementation = implementationService.findById(implId);
        Publication publication = publicationService.findById(publicationDto.getId());

        // update publication reference list of implementation
        Set<Publication> publications = implementation.getPublications();
        publications.add(publication);
        implementation.setPublications(publications);

        Set<Publication> updatedPublications = implementationService.save(implementation).getPublications();
        Set<PublicationDto> dtoPublications = ModelMapperUtils.convertSet(updatedPublications, PublicationDto.class);
        CollectionModel<EntityModel<PublicationDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoPublications);

        publicationAssembler.addLinks(resultCollection);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Get a specific referenced publication of an implementation")
    @GetMapping("/{implId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public HttpEntity<EntityModel<PublicationDto>> getPublication(@PathVariable UUID implId, @PathVariable UUID publicationId) {
        LOG.debug("Get to retrieve referenced publication with Id {} from implementation with Id {}", publicationId, implId);
        Publication publication = publicationService.findById(publicationId);
        Set<Publication> publications = implementationService.findById(implId).getPublications();
        if (!publications.contains(publication)) {
            LOG.info("Trying to get Publication that is not referenced by the implementation");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Convert To EntityModel
        EntityModel<PublicationDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(publication, PublicationDto.class));
        // Fill EntityModel with links
        publicationAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Delete a reference to a publication of the implementation")
    @DeleteMapping("/{implId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public HttpEntity<EntityModel<ProblemTypeDto>> deleteReferenceToPublication(@PathVariable UUID implId, @PathVariable UUID publicationId) {
        Implementation implementation = implementationService.findById(implId);
        Set<Publication> publications = implementation.getPublications();
        publications.removeIf(publication -> publication.getId().equals(publicationId));
        implementation.setPublications(publications);
        implementationService.save(implementation);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Implementation doesn't exist")},
            description = "Get referenced software platform for an implementation")
    @GetMapping("/{implId}/" + Constants.SOFTWARE_PLATFORMS)
    public HttpEntity<CollectionModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatforms(@PathVariable UUID implId) {
        Implementation implementation = implementationService.findById(implId);
        Set<SoftwarePlatform> softwarePlatforms = implementation.getSoftwarePlatforms();
        Set<SoftwarePlatformDto> softwarePlatformDtos = ModelMapperUtils.convertSet(softwarePlatforms, SoftwarePlatformDto.class);
        CollectionModel<EntityModel<SoftwarePlatformDto>> resultCollection = HateoasUtils.generateCollectionModel(softwarePlatformDtos);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404", content = @Content,
            description = "Software platform or publication does not exist")},
            description = "Add a reference to an existing software platform (that was previously created via a POST on /software-platforms/). If the software platform doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{implId}/" + Constants.SOFTWARE_PLATFORMS)
    public HttpEntity<CollectionModel<EntityModel<SoftwarePlatformDto>>> addSoftwarePlatform(@PathVariable UUID implId, @RequestBody SoftwarePlatformDto softwarePlatformDto) {
        Implementation implementation = implementationService.findById(implId);
        SoftwarePlatform softwarePlatform = softwarePlatformService.findById(softwarePlatformDto.getId());

        // update software platform reference list of implementation
        Set<SoftwarePlatform> softwarePlatforms = implementation.getSoftwarePlatforms();
        softwarePlatforms.add(softwarePlatform);
        implementation.setSoftwarePlatforms(softwarePlatforms);

        Set<SoftwarePlatform> updatedSoftwarePlatforms = implementationService.save(implementation).getSoftwarePlatforms();
        Set<SoftwarePlatformDto> dtoSoftwarePlatforms = ModelMapperUtils.convertSet(updatedSoftwarePlatforms, SoftwarePlatformDto.class);
        CollectionModel<EntityModel<SoftwarePlatformDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoSoftwarePlatforms);

        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Get a specific referenced software platform of an implementation")
    @GetMapping("/{implId}/" + Constants.SOFTWARE_PLATFORMS + "/{platformId}")
    public HttpEntity<EntityModel<SoftwarePlatformDto>> getSoftwarePlatform(@PathVariable UUID implId, @PathVariable UUID platformId) {
        LOG.debug("Get to retrieve referenced software platform with Id {} from implementation with Id {}", platformId, implId);
        SoftwarePlatform platform = softwarePlatformService.findById(platformId);
        Set<SoftwarePlatform> softwarePlatforms = implementationService.findById(implId).getSoftwarePlatforms();
        if (!softwarePlatforms.contains(platform)) {
            LOG.info("Trying to get software platform that is not referenced by the implementation");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        EntityModel<SoftwarePlatformDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(platform, SoftwarePlatformDto.class));
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Delete a reference to a software platform of the implementation")
    @DeleteMapping("/{implId}/" + Constants.SOFTWARE_PLATFORMS + "/{platformId}")
    public HttpEntity<EntityModel<SoftwarePlatformDto>> deleteReferenceToSoftwarePlatform(@PathVariable UUID implId, @PathVariable UUID platformId) {
        Implementation implementation = implementationService.findById(implId);
        Set<SoftwarePlatform> softwarePlatforms = implementation.getSoftwarePlatforms();
        softwarePlatforms.removeIf(platform -> platform.getId().equals(platformId));
        implementation.setSoftwarePlatforms(softwarePlatforms);
        implementationService.save(implementation);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
