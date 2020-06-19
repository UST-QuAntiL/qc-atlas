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
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputingResource;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ComputingResourceService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ComputingResourceDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.ComputingResourceAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.TagAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
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
    private final TagAssembler tagAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/")
    public HttpEntity<CollectionModel<EntityModel<ImplementationDto>>> getImplementations(@PathVariable UUID algoId) {
        LOG.debug("Get to retrieve all implementations of algorithm with Id {} received.", algoId);
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

    @Operation(responses = {@ApiResponse(responseCode = "200")})
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

    @Operation(responses = {@ApiResponse(responseCode = "201")})
    @PostMapping("/")
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

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/{implId}/" + Constants.TAGS)
    public HttpEntity<CollectionModel<EntityModel<TagDto>>> getTags(@PathVariable UUID algoId, @PathVariable UUID implId) {
        // Get Implementation
        Implementation implementation = implementationService.findById(implId);
        // Get Tags of Implementation
        Set<Tag> tags = implementation.getTags();
        // Translate Entity to DTO
        Set<TagDto> dtoTags = ModelMapperUtils.convertSet(tags, TagDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<TagDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTags);
        // Fill EntityModel Links
        tagAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        implementationAssembler.addTagLink(resultCollection, algoId, implId);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

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
    })
    @GetMapping("/{implId}/" + Constants.COMPUTING_RESOURCES)
    public ResponseEntity<PagedModel<EntityModel<ComputingResourceDto>>> getComputingResources(
            @PathVariable UUID algoId, @PathVariable UUID implId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        var resources = computingResourceService.findAllResourcesByImplementationId(implId, RestUtils.getPageableFromRequestParams(page, size));
        var typeDtoes = ModelMapperUtils.convertPage(resources, ComputingResourceDto.class);
        var pagedModel = computingResourcePaginationAssembler.toModel(typeDtoes);
        computingResourceAssembler.addLinks(pagedModel);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @PostMapping("/{implId}/" + Constants.COMPUTING_RESOURCES)
    public ResponseEntity<EntityModel<ImplementationDto>> addComputingResource(
            @PathVariable UUID algoId, @PathVariable UUID implId,
            @Valid @RequestBody ComputingResourceDto resourceDto
    ) {
        var implementation = implementationService.findById(implId);
        var resource = ModelMapperUtils.convert(resourceDto, ComputingResource.class);
        var updatedImplementation = computingResourceService.addComputingResourceToImplementation(
                implementation,
                resource
        );
        EntityModel<ImplementationDto> implDto = HateoasUtils.generateEntityModel(
                ModelMapperUtils.convert(updatedImplementation, ImplementationDto.class));
        implementationAssembler.addLinks(implDto);
        return ResponseEntity.ok(implDto);
    }
}
