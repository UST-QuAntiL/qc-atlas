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

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.QuantumResource;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.QuantumResourceService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.QuantumResourceDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationAssembler;
import org.planqk.atlas.web.linkassembler.ProblemTypeAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.linkassembler.QuantumResourceAssembler;
import org.planqk.atlas.web.linkassembler.TagAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Controller to access and manipulate quantum algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "algorithm")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHMS)
@ApiVersion("v1")
@AllArgsConstructor
public class AlgorithmController {

    final private static Logger LOG = LoggerFactory.getLogger(AlgorithmController.class);

    private final AlgorithmService algorithmService;
    private final QuantumResourceService quantumResourceService;

    private final PagedResourcesAssembler<AlgorithmDto> algorithmPaginationAssembler;
    private final PagedResourcesAssembler<QuantumResourceDto> quantumResourcePaginationAssembler;
    private final ProblemTypeAssembler problemTypeAssembler;
    private final TagAssembler tagAssembler;
    private final AlgorithmAssembler algorithmAssembler;
    private final AlgorithmRelationAssembler algorithmRelationAssembler;
    private final PublicationAssembler publicationAssembler;
    private final QuantumResourceAssembler quantumResourceAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<AlgorithmDto>>> getAlgorithms(@RequestParam(required = false) Integer page,
                                                                           @RequestParam(required = false) Integer size) {
        LOG.debug("Get to retrieve all algorithms received.");
        // Generate Pageable
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        // Get Page of DTOs
        Page<AlgorithmDto> pageDto = ModelMapperUtils.convertPage(algorithmService.findAll(p), AlgorithmDto.class);
        // Generate PagedModel
        PagedModel<EntityModel<AlgorithmDto>> outputDto = algorithmPaginationAssembler.toModel(pageDto);
        algorithmAssembler.addLinks(outputDto.getContent());
        return new ResponseEntity<>(outputDto, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201")})
    @PostMapping("/")
    public HttpEntity<EntityModel<AlgorithmDto>> createAlgorithm(@Valid @RequestBody AlgorithmDto algo) {
        LOG.debug("Post to create new algorithm received.");
        // store and return algorithm
        Algorithm algorithm = algorithmService.save(ModelMapperUtils.convert(algo, Algorithm.class));
        // Convert To EntityModel
        EntityModel<AlgorithmDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(algorithm, AlgorithmDto.class));
        // Fill EntityModel with links
        algorithmAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<AlgorithmDto>> updateAlgorithm(@PathVariable UUID id,
                                                                 @Valid @RequestBody AlgorithmDto algo) {
        LOG.debug("Put to update algorithm with id: {}.", id);
        Algorithm updatedAlgorithm = algorithmService.update(id, ModelMapperUtils.convert(algo, Algorithm.class));
        // Convert To EntityModel
        EntityModel<AlgorithmDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(updatedAlgorithm, AlgorithmDto.class));
        // Fill EntityModel with links
        algorithmAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteAlgorithm(@PathVariable UUID id) {
        LOG.debug("Delete to remove algorithm with id: {}.", id);
        algorithmService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<AlgorithmDto>> getAlgorithm(@PathVariable UUID id) {
        LOG.debug("Get to retrieve algorithm with id: {}.", id);

        Algorithm algorithm = algorithmService.findById(id);
        // Convert To EntityModel
        EntityModel<AlgorithmDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(algorithm, AlgorithmDto.class));
        // Fill EntityModel with links
        algorithmAssembler.addLinks(dtoOutput);

        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/{id}/" + Constants.TAGS)
    public HttpEntity<CollectionModel<EntityModel<TagDto>>> getTags(@PathVariable UUID id) {
        Algorithm algorithm = algorithmService.findById(id);
        // Get Tags of Algorithm
        Set<Tag> tags = algorithm.getTags();
        // Translate Entity to DTO
        Set<TagDto> dtoTags = ModelMapperUtils.convertSet(tags, TagDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<TagDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTags);
        // Fill EntityModel Links
        tagAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addTagLink(resultCollection, id);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = { @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content) })
    @GetMapping("/{id}/" + Constants.PUBLICATIONS)
    public HttpEntity<CollectionModel<EntityModel<PublicationDto>>> getPublications(@PathVariable UUID id) {
        Algorithm algorithm = algorithmService.findById(id);
        // Get Publications of Algorithm
        Set<Publication> publications = algorithm.getPublications();
        // Translate Entity to DTO
        Set<PublicationDto> dtoPublications = ModelMapperUtils.convertSet(publications, PublicationDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<PublicationDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoPublications);
        // Fill EntityModel Links
        publicationAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addPublicationLink(resultCollection, id);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/{id}/" + Constants.PROBLEM_TYPES)
    public HttpEntity<CollectionModel<EntityModel<ProblemTypeDto>>> getProblemTypes(@PathVariable UUID id) {
        Algorithm algorithm = algorithmService.findById(id);
        // Get ProblemTypes of Algorithm
        Set<ProblemType> problemTypes = algorithm.getProblemTypes();
        // Translate Entity to DTO
        Set<ProblemTypeDto> dtoTypes = ModelMapperUtils.convertSet(problemTypes, ProblemTypeDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<ProblemTypeDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTypes);
        // Fill EntityModel Links
        problemTypeAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addProblemTypeLink(resultCollection, id);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/{sourceAlgorithmId}/" + Constants.ALGORITHM_RELATIONS)
    public HttpEntity<CollectionModel<EntityModel<AlgorithmRelationDto>>> getAlgorithmRelations(
            @PathVariable UUID sourceAlgorithmId) {
        // get AlgorithmRelations of Algorithm
        Set<AlgorithmRelation> algorithmRelations = algorithmService.getAlgorithmRelations(sourceAlgorithmId);
        // Get AlgorithmRelationDTOs of Algorithm
        Set<AlgorithmRelationDto> dtoALgorithmRelation = ModelMapperUtils.convertSet(algorithmRelations,
                AlgorithmRelationDto.class);
        // Generate CollectionModel
        CollectionModel<EntityModel<AlgorithmRelationDto>> resultCollection = HateoasUtils
                .generateCollectionModel(dtoALgorithmRelation);
        // Fill EntityModel Links
        algorithmRelationAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addAlgorithmRelationLink(resultCollection, sourceAlgorithmId);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @PutMapping("/{sourceAlgorithmId}/" + Constants.ALGORITHM_RELATIONS)
    public HttpEntity<EntityModel<AlgorithmRelationDto>> updateAlgorithmRelation(@PathVariable UUID sourceAlgorithmId,
                                                                                 @Valid @RequestBody AlgorithmRelationDto relation) {
        LOG.debug("Post to add algorithm relation received.");

        AlgorithmRelation algorithmRelation = algorithmService.addOrUpdateAlgorithmRelation(sourceAlgorithmId,
                ModelMapperUtils.convert(relation, AlgorithmRelation.class));
        AlgorithmRelationDto dtoOutput = ModelMapperUtils.convert(algorithmRelation, AlgorithmRelationDto.class);
        EntityModel<AlgorithmRelationDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
        algorithmRelationAssembler.addLinks(entityDto);
        return new ResponseEntity<>(entityDto, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @DeleteMapping("/{sourceAlgorithmId}/" + Constants.ALGORITHM_RELATIONS + "/{relationId}")
    public HttpEntity<AlgorithmRelationDto> deleteAlgorithmRelation(@PathVariable UUID sourceAlgorithmId,
                                                                    @PathVariable UUID relationId) {
        LOG.debug("Delete received to remove algorithm relation with id {}.", relationId);
        algorithmService.deleteAlgorithmRelation(sourceAlgorithmId, relationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @GetMapping("/{id}/" + Constants.QUANTUM_RESOURCES)
    public ResponseEntity<PagedModel<EntityModel<QuantumResourceDto>>> getQuantumResources(
            @PathVariable UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        var algorithm = algorithmService.findById(id);
        if (!(algorithm instanceof QuantumAlgorithm)) {
            return ResponseEntity.badRequest().build();
        }
        var resources = quantumResourceService.findAllResourcesByAlgorithmId(id,
                RestUtils.getPageableFromRequestParams(page, size));
        var typeDtoes = ModelMapperUtils.convertPage(resources, QuantumResourceDto.class);
        var pagedModel = quantumResourcePaginationAssembler.toModel(typeDtoes);
        quantumResourceAssembler.addLinks(pagedModel);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @PostMapping("/{id}/" + Constants.QUANTUM_RESOURCES)
    public ResponseEntity<?> addQuantumResource(
            @PathVariable UUID id,
            @Valid @RequestBody QuantumResourceDto resourceDto
    ) {
        var algorithm = algorithmService.findById(id);
        if (!(algorithm instanceof QuantumAlgorithm)) {
            return ResponseEntity.badRequest().build();
        }
        var resource = ModelMapperUtils.convert(resourceDto, QuantumResource.class);
        var updatedAlgorithm = quantumResourceService.addQuantumResourceToAlgorithm(
                (QuantumAlgorithm) algorithm,
                resource
        );
        EntityModel<AlgorithmDto> algoDto = HateoasUtils.generateEntityModel(
                ModelMapperUtils.convert(updatedAlgorithm, AlgorithmDto.class));
        algorithmAssembler.addLinks(algoDto);
        return ResponseEntity.ok(algoDto);
    }
}
