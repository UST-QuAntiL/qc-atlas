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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ComputingResourceProperty;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.services.AlgoRelationService;
import org.planqk.atlas.core.services.AlgoRelationTypeService;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.core.services.ComputingResourcePropertyService;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.PatternRelationTypeService;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.ComputingResourceMixin;
import org.planqk.atlas.web.controller.mixin.PublicationMixin;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.dtos.ComputingResourcePropertyDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.PatternRelationTypeDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationAssembler;
import org.planqk.atlas.web.linkassembler.ApplicationAreaAssembler;
import org.planqk.atlas.web.linkassembler.ComputingResourcePropertyAssembler;
import org.planqk.atlas.web.linkassembler.PatternRelationAssembler;
import org.planqk.atlas.web.linkassembler.ProblemTypeAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;
import org.planqk.atlas.web.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
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

/**
 * Controller to access and manipulate quantum algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "algorithm")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.ALGORITHMS)
@AllArgsConstructor
public class AlgorithmController {

    final private static Logger LOG = LoggerFactory.getLogger(AlgorithmController.class);

    private final AlgorithmService algorithmService;
    private final AlgoRelationService algoRelationService;
    private final AlgoRelationTypeService algoRelationTypeService;
    private final ComputingResourcePropertyService computingResourcePropertyService;
    private final PatternRelationService patternRelationService;
    private final PatternRelationTypeService patternRelationTypeService;
    private final ProblemTypeService problemTypeService;
    private final ApplicationAreaService applicationAreaService;

    private final ProblemTypeAssembler problemTypeAssembler;
    private final ApplicationAreaAssembler applicationAreaAssembler;
    //    private final TagAssembler tagAssembler;
    private final AlgorithmAssembler algorithmAssembler;
    private final AlgorithmRelationAssembler algorithmRelationAssembler;
    private final PublicationAssembler publicationAssembler;
    private final ComputingResourcePropertyAssembler computingResourcePropertyAssembler;
    private final PatternRelationAssembler patternRelationAssembler;

    private final PublicationMixin publicationMixin;
    private final ComputingResourceMixin computingResourceMixin;

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Retrieve all algorithms (quantum, hybrid and classic).")
    @GetMapping()
    @ListParametersDoc()
    public HttpEntity<PagedModel<EntityModel<AlgorithmDto>>> getAlgorithms(ListParameters listParameters) {
        LOG.debug("Get to retrieve all algorithms received.");
        return ResponseEntity.ok(algorithmAssembler.toModel(algorithmService.findAll(listParameters.getPageable(),
                listParameters.getSearch())));
    }

    @Operation(responses = {@ApiResponse(responseCode = "201")}, description = "Define the basic properties of an algorithm. References to subobjects (e.g. a problemtype) can be added via subroutes (e.g. /algorithm/id/problem-types). Custom ID will be ignored.")
    @PostMapping()
    public HttpEntity<EntityModel<AlgorithmDto>> createAlgorithm(@Valid @RequestBody AlgorithmDto algo) {
        LOG.debug("Post to create new algorithm received.");
        Algorithm algorithm = algorithmService.save(ModelMapperUtils.convert(algo, Algorithm.class));
        return new ResponseEntity<>(algorithmAssembler.toModel(algorithm), HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Update the basic properties of an algorithm (e.g. name). References to subobjects (e.g. a problemtype) are not updated via this operation - use the corresponding subroute for updating them (e.g. algorithm/{id}/problem-type). Custom ID will be ignored.")
    @PutMapping("/{algoId}")
    public HttpEntity<EntityModel<AlgorithmDto>> updateAlgorithm(@PathVariable UUID algoId,
                                                                 @Valid @RequestBody AlgorithmDto algo) {
        LOG.debug("Put to update algorithm with id: {}.", algoId);
        Algorithm updatedAlgorithm = algorithmService.update(algoId, ModelMapperUtils.convert(algo, Algorithm.class));
        return ResponseEntity.ok(algorithmAssembler.toModel(updatedAlgorithm));
    }

//    @Operation(responses = {@ApiResponse(responseCode = "200")})
//    @GetMapping("/{id}/" + Constants.TAGS)
//    public HttpEntity<CollectionModel<EntityModel<TagDto>>> getTags(@PathVariable UUID id) {
//        Algorithm algorithm = algorithmService.findById(id);
//        return ResponseEntity.ok(tagsAssembler.toModel(algorithm.getTags()));
//    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given id doesn't exist")},
            description = "Delete an algorithm. This also deletes all entities that depend on it (e.g., the algorithm's relation to another algorithm).")
    @DeleteMapping("/{algoId}")
    public HttpEntity<Void> deleteAlgorithm(@PathVariable UUID algoId) {
        LOG.debug("Delete to remove algorithm with id: {}.", algoId);
        algorithmService.delete(algoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Retrieve a specific algorithm and its basic properties.")
    @GetMapping("/{algoId}")
    public HttpEntity<EntityModel<AlgorithmDto>> getAlgorithm(@PathVariable UUID algoId) {
        LOG.debug("Get to retrieve algorithm with id: {}.", algoId);
        var algorithm = algorithmService.findById(algoId);
        return ResponseEntity.ok(algorithmAssembler.toModel(algorithm));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Algorithm doesn't exist")},
            description = "Get referenced publications for an algorithm.")
    @GetMapping("/{algoId}/" + Constants.PUBLICATIONS)
    public HttpEntity<CollectionModel<EntityModel<PublicationDto>>> getPublications(@PathVariable UUID algoId) {
        Algorithm algorithm = algorithmService.findById(algoId);
        return ResponseEntity.ok(publicationAssembler.toModel(algorithm.getPublications()));
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404", content = @Content,
            description = "algorithm or publication does not exist")},
            description = "Add a reference to an existing publication (that was previously created via a POST on /publications/). Custom ID will be ignored. For publication only ID is required, other publication attributes will not change. If the publication doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algoId}/" + Constants.PUBLICATIONS)
    public HttpEntity<CollectionModel<EntityModel<PublicationDto>>> addPublication(@PathVariable UUID algoId, @RequestBody PublicationDto publicationDto) {
        var algorithm = algorithmService.findById(algoId);
        publicationMixin.addPublication(algorithm, publicationDto);
        algorithm = algorithmService.save(algorithm);
        return ResponseEntity.ok(publicationAssembler.toModel(algorithm.getPublications()));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Get a specific referenced publication of an algorithm.")
    @GetMapping("/{algoId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public HttpEntity<EntityModel<PublicationDto>> getPublication(@PathVariable UUID algoId, @PathVariable UUID publicationId) {
        var algorithm = algorithmService.findById(algoId);
        return ResponseEntity.ok(publicationAssembler.toModel(publicationMixin.getPublication(algorithm, publicationId)));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Algorithm or publication with given ids do not exist or no relation between algorithm and publication")},
            description = "Delete a reference to a publication of the algorithm.")
    @DeleteMapping("/{algoId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public HttpEntity<Void> deleteReferenceToPublication(@PathVariable UUID algoId, @PathVariable UUID publicationId) {
        Algorithm algorithm = algorithmService.findById(algoId);
        publicationMixin.unlinkPublication(algorithm, publicationId);
        algorithmService.save(algorithm);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Algorithm does not exists in the database")},
            description = "Get the problem types for an algorithm.")
    @GetMapping("/{algoId}/" + Constants.PROBLEM_TYPES)
    public HttpEntity<CollectionModel<EntityModel<ProblemTypeDto>>> getProblemTypes(@PathVariable UUID algoId) {
        Algorithm algorithm = algorithmService.findById(algoId);
        return ResponseEntity.ok(problemTypeAssembler.toModel(algorithm.getProblemTypes()));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "The id of the problem type to reference is null"),
            @ApiResponse(responseCode = "404", description = "Problem type or algorithm does not exists in the database")},
            description = "Add a reference to an existing problemType (that was previously created via a POST on /problem-types/). Custom ID will be ignored. For problem type only ID is required, other problem type attributes will not change. If the problemType doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algoId}/" + Constants.PROBLEM_TYPES)
    public HttpEntity<CollectionModel<EntityModel<ProblemTypeDto>>> addProblemType(@PathVariable UUID algoId, @RequestBody ProblemTypeDto problemTypeDto) {
        if (Objects.isNull(problemTypeDto.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Algorithm algorithm = algorithmService.findById(algoId);
        ProblemType problemType = problemTypeService.findById(problemTypeDto.getId());

        // Get ProblemTypes of Algorithm
        Set<ProblemType> problemTypes = algorithm.getProblemTypes();
        problemTypes.add(problemType);
        algorithm.setProblemTypes(problemTypes);

        Set<ProblemType> updatedProblemTypes = algorithmService.save(algorithm).getProblemTypes();
        return ResponseEntity.ok(problemTypeAssembler.toModel(updatedProblemTypes));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Algorithm or problem type does not exists in the database")},
            description = "Get a specific problem type for an algorithm.")
    @GetMapping("/{algoId}/" + Constants.PROBLEM_TYPES + "/{problemTypeId}")
    public HttpEntity<EntityModel<ProblemTypeDto>> getSpecificProblemTypes(@PathVariable UUID algoId, @PathVariable UUID problemTypeId) {
        Algorithm algorithm = algorithmService.findById(algoId);
        final var problemType = algorithm.getProblemTypes().stream().filter(pt -> pt.getId().equals(problemTypeId)).findFirst();
        if (problemType.isEmpty()) {
            LOG.info("Trying to get ApplicationArea that not referenced by the algorithm");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(problemTypeAssembler.toModel(problemType));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Algorithm or problem type does not exists in the database")},
            description = "Delete a reference to a problem types of the algorithm.")
    @DeleteMapping("/{algoId}/" + Constants.PROBLEM_TYPES + "/{problemTypeId}")
    public HttpEntity<Void> deleteReferenceToProblemTypes(@PathVariable UUID algoId, @PathVariable UUID problemTypeId) {
        problemTypeService.findById(problemTypeId);
        Algorithm algorithm = algorithmService.findById(algoId);
        Set<ProblemType> problemTypes = algorithm.getProblemTypes();
        problemTypes.removeIf(problemType -> problemType.getId().equals(problemTypeId));
        algorithm.setProblemTypes(problemTypes);
        algorithmService.save(algorithm);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Algorithm does not exists in the database")},
            description = "Get the problem types for an algorithm.")
    @GetMapping("/{algoId}/" + Constants.APPLICATION_AREAS)
    public HttpEntity<CollectionModel<EntityModel<ApplicationAreaDto>>> getApplicationAreas(@PathVariable UUID algoId) {
        Algorithm algorithm = algorithmService.findById(algoId);
        Set<ApplicationArea> applicationAreas = algorithm.getApplicationAreas();
        return ResponseEntity.ok(applicationAreaAssembler.toModel(applicationAreas));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Algorithm or application area does not exists in the database")},
            description = "Get a specific applicationArea of an algorithm.")
    @GetMapping("/{algoId}/" + Constants.APPLICATION_AREAS + "/{applicationAreaId}")
    public HttpEntity<EntityModel<ApplicationAreaDto>> getApplicationArea(@PathVariable UUID algoId, @PathVariable UUID applicationAreaId) {
        var applicationAreas = algorithmService.findById(algoId).getApplicationAreas();
        final var applicationArea = applicationAreas.stream().filter(pt -> pt.getId().equals(applicationAreaId)).findFirst();
        if (applicationArea.isEmpty()) {
            LOG.info("Trying to get ApplicationArea that not referenced by the algorithm");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(applicationAreaAssembler.toModel(applicationArea));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Algorithm does not exists in the database")},
            description = "Delete a reference to a applicationArea of an algorithm.")
    @DeleteMapping("/{algoId}/" + Constants.APPLICATION_AREAS + "/{applicationAreaId}")
    public HttpEntity<Void> deleteReferenceToApplicationArea(@PathVariable UUID algoId, @PathVariable UUID applicationAreaId) {
        Algorithm algorithm = algorithmService.findById(algoId);
        Set<ApplicationArea> applicationAreas = algorithm.getApplicationAreas();
        applicationAreas.removeIf(applicationArea -> applicationArea.getId().equals(applicationAreaId));
        algorithm.setApplicationAreas(applicationAreas);
        algorithmService.save(algorithm);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "404", description = "Problem type or algorithm does not exists in the database")},
            description = "Add a reference to an existing application area (that was previously created via a POST on /application-area/). For application area only ID is required, other attributes will not change. If the applicationArea doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algoId}/" + Constants.APPLICATION_AREAS)
    public HttpEntity<CollectionModel<EntityModel<ApplicationAreaDto>>> addApplicationArea(@PathVariable UUID algoId, @RequestBody ApplicationAreaDto applicationAreaDto) {
        Algorithm algorithm = algorithmService.findById(algoId);
        ApplicationArea applicationArea = applicationAreaService.findById(applicationAreaDto.getId());

        Set<ApplicationArea> applicationAreas = algorithm.getApplicationAreas();
        applicationAreas.add(applicationArea);
        algorithm.setApplicationAreas(applicationAreas);

        var updatedApplicationAreas = algorithmService.save(algorithm).getApplicationAreas();
        return ResponseEntity.ok(applicationAreaAssembler.toModel(updatedApplicationAreas));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm or Pattern Type doesn't exist in the database")},
            description = "Get pattern relations for an algorithms.")
    @GetMapping("/{algoId}/" + Constants.PATTERN_RELATIONS)
    public HttpEntity<CollectionModel<EntityModel<PatternRelationDto>>> getPatternRelations(@PathVariable UUID algoId) {
        Algorithm algorithm = algorithmService.findById(algoId);
        return ResponseEntity.ok(patternRelationAssembler.toModel(algorithm.getRelatedPatterns()));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "404", description = "Algorithm or pattern type doesn't exist in the database")},
            description = "Add a Pattern Relation from this Algorithm to a given Pattern. Custom ID will be ignored. For pattern relation type only ID is required, other pattern relation type attributes will not change.")
    @PostMapping("/{algoId}/" + Constants.PATTERN_RELATIONS)
    public HttpEntity<EntityModel<PatternRelationDto>> createPatternRelation(@PathVariable UUID algoId,
                                                                             @RequestBody PatternRelationDto relationDto) {
        LOG.debug("Post to create new PatternRelation received.");

        // This endpoint always creates a new PatternRelation, regardless of what the user passes in.
        relationDto.setId(null);

        var algorithm = algorithmService.findById(algoId);
        var saved = savePatternRelationFromDto(algorithm, relationDto);
        return new ResponseEntity<>(patternRelationAssembler.toModel(saved), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "PatternRelation doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404", description = "Pattern relation or algorithm with given id doesn't exist")},
            description = "Get a certain pattern relation for an algorithm.")
    @GetMapping("/{algoId}/" + Constants.PATTERN_RELATIONS + "/{relationId}")
    public HttpEntity<EntityModel<PatternRelationDto>> getPatternRelation(@PathVariable UUID algoId, @PathVariable UUID relationId) {
        LOG.debug("Get to retrieve PatternRelation with Id {} received.", relationId);
        var patternRelations = algorithmService.findById(algoId).getRelatedPatterns();
        var patternRelation = patternRelations.stream().filter(pr -> pr.getId().equals(relationId))
                .findFirst().orElseThrow(NoSuchElementException::new);
        return ResponseEntity.ok(patternRelationAssembler.toModel(patternRelation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "PatternRelation doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404", description = "Pattern relation or algorithm with given id doesn't exist")},
            description = "Update a references to a pattern. Custom ID will be ignored. For pattern relation type only ID is required, other pattern relation type attributes will not change.")
    @PutMapping("/{algoId}/" + Constants.PATTERN_RELATIONS + "/{relationId}")
    public HttpEntity<EntityModel<PatternRelationDto>> updatePatternRelations(@PathVariable UUID algoId, @PathVariable UUID relationId, @Valid @RequestBody PatternRelationDto relationDto) {
        LOG.debug("Put to update pattern relation with Id {} received.", relationId);
        PatternRelation patternRelation = patternRelationService.findById(relationId);
        if (!patternRelation.getAlgorithm().getId().equals(algoId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        var algorithm = algorithmService.findById(algoId);
        var saved = savePatternRelationFromDto(algorithm, relationDto);
        return ResponseEntity.ok(patternRelationAssembler.toModel(saved));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Pattern relation or algorithm with given id doesn't exist")})
    @DeleteMapping("/{algoId}/" + Constants.PATTERN_RELATIONS + "/{relationId}")
    public HttpEntity<Void> deletePatternRelation(@PathVariable UUID algoId,
                                                  @PathVariable UUID relationId) {
        LOG.debug("Delete received to remove pattern relation with id {}.", relationId);
        algorithmService.findById(algoId);
        patternRelationService.findById(relationId);
        patternRelationService.deleteById(relationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "AlgorithmRelation doesn't contain this algorithm as source or target"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given id doesn't exist")},
            description = "Add an algorithm relation from this algorithm to another given algorithm. Custom ID will be ignored. For algorithm relation type only ID is required, other algorithm relation type attributes will not change.")
    @PostMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS)
    public HttpEntity<EntityModel<AlgorithmRelationDto>> addAlgorithmRelation(
            @PathVariable UUID algoId,
            @RequestBody AlgorithmRelationDto relationDto
    ) {
        LOG.debug("Post to create algorithm relations received.");
        algorithmService.findById(algoId);
        if (!relationDto.getSourceAlgorithm().getId().equals(algoId) && !relationDto.getTargetAlgorithm().getId().equals(algoId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        AlgorithmRelation updatedRelation = handleRelationUpdate(relationDto, null);
        return ResponseEntity.ok(algorithmRelationAssembler.toModel(updatedRelation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")},
            description = "Retrieve all relations for an algorithm.")
    @GetMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS)
    public HttpEntity<CollectionModel<EntityModel<AlgorithmRelationDto>>> getAlgorithmRelations(
            @PathVariable UUID algoId) {
        algorithmService.findById(algoId);
        var algorithmRelations = algorithmService.getAlgorithmRelations(algoId);
        return ResponseEntity.ok(algorithmRelationAssembler.toModel(algorithmRelations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "AlgorithmRelation doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")})
    @GetMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS + "/{relationId}")
    public HttpEntity<EntityModel<AlgorithmRelationDto>> getAlgorithmRelation(
            @PathVariable UUID algoId, @PathVariable UUID relationId) {
        LOG.debug("Retrieving algorithm relation with id {} for algorithm with id {}", relationId, algoId);
        algorithmService.findById(algoId);
        AlgorithmRelation algorithmRelation = algoRelationService.findById(relationId);
        if (!algorithmRelation.getSourceAlgorithm().getId().equals(algoId) && !algorithmRelation.getTargetAlgorithm().getId().equals(algoId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(algorithmRelationAssembler.toModel(algorithmRelation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "AlgorithmRelation doesn't contain this algorithm as source or target"),
            @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")},
            description = "Change an algorithm relation from this algorithm to another given algorithm. Custom ID will be ignored. For algorithm relation type only ID is required, other algorithm relation type attributes will not change.")
    @PutMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS + "/{relationId}")
    public HttpEntity<EntityModel<AlgorithmRelationDto>> updateAlgorithmRelation(@PathVariable UUID algoId, @PathVariable UUID relationId,
                                                                                 @Valid @RequestBody AlgorithmRelationDto relationDto) {
        LOG.debug("Put to update algorithm relations with Id {} received.", relationId);

        // check if relation exists and if it uses this algorithm as source or target
        algorithmService.findById(algoId);
        algoRelationService.findById(relationId);
        if (!relationDto.getSourceAlgorithm().getId().equals(algoId) && !relationDto.getTargetAlgorithm().getId().equals(algoId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        AlgorithmRelation updatedRelation = handleRelationUpdate(relationDto, relationId);
        return ResponseEntity.ok(algorithmRelationAssembler.toModel(updatedRelation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Algorithm relation or algorithm with given id doesn't exist")},
            description = "Delete a relation of the algorithm.")
    @DeleteMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS + "/{relationId}")
    public HttpEntity<Void> deleteAlgorithmRelation(@PathVariable UUID algoId,
                                                    @PathVariable UUID relationId) {
        LOG.debug("Delete received to remove algorithm relation with id {}.", relationId);
        algorithmService.findById(algoId);
        algoRelationService.findById(relationId);
        algoRelationService.delete(relationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Retrieve the required computing resources of an algorithm")
    @GetMapping("/{algoId}/" + Constants.COMPUTING_RESOURCES_PROPERTIES)
    public HttpEntity<PagedModel<EntityModel<ComputingResourcePropertyDto>>> getComputingResources(
            @PathVariable UUID algoId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        algorithmService.findById(algoId);
        var resources = computingResourcePropertyService.findAllComputingResourcesPropertyByAlgorithmId(algoId, RestUtils.getPageableFromRequestParams(page, size));
        return ResponseEntity.ok(computingResourcePropertyAssembler.toModel(resources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Id of the passed computing resource type is null"),
            @ApiResponse(responseCode = "404", description = "Computing resource type  or algorithm can not be found with the given Ids")
    }, description = "Add a computing resource (e.g. a certain number of qubits) that is required by an algorithm. Custom ID will be ignored. For computing resource type only ID is required, other computing resource type attributes will not change.")
    @PostMapping("/{algoId}/" + Constants.COMPUTING_RESOURCES_PROPERTIES)
    public HttpEntity<EntityModel<ComputingResourcePropertyDto>> addComputingResource(
            @PathVariable UUID algoId,
            @Valid @RequestBody ComputingResourcePropertyDto resourceDto
    ) {
        var algorithm = algorithmService.findById(algoId);

        ValidationUtils.validateComputingResourceProperty(resourceDto);

        var resource = computingResourceMixin.fromDto(resourceDto);
        var updatedResource = computingResourcePropertyService.addComputingResourcePropertyToAlgorithm(algorithm, resource);
        return ResponseEntity.ok(computingResourcePropertyAssembler.toModel(updatedResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Resource doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")})
    @GetMapping("/{algoId}/" + Constants.COMPUTING_RESOURCES_PROPERTIES + "/{resourceId}")
    public HttpEntity<EntityModel<ComputingResourcePropertyDto>> getComputingResource(
            @PathVariable UUID algoId, @PathVariable UUID resourceId) {
        LOG.debug("Get received to retrieve computing resource with id {}.", resourceId);

        algorithmService.findById(algoId);
        ComputingResourceProperty computingResourceProperty = computingResourcePropertyService.findComputingResourcePropertyById(resourceId);
        if (Objects.isNull(computingResourceProperty.getAlgorithm()) || !computingResourceProperty.getAlgorithm().getId().equals(algoId)) {
            LOG.debug("Algorithm is not referenced from the computing resource to retrieve!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(computingResourcePropertyAssembler.toModel(computingResourceProperty));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")},
            description = "Update a computing resource of the algorithm. Custom ID will be ignored. For computing resource type only ID is required, other computing resource type attributes will not change.")
    @PutMapping("/{algoId}/" + Constants.COMPUTING_RESOURCES_PROPERTIES + "/{resourceId}")
    public HttpEntity<EntityModel<ComputingResourcePropertyDto>> updateComputingResource(@PathVariable UUID algoId,
                                                                                         @PathVariable UUID resourceId,
                                                                                         @RequestBody ComputingResourcePropertyDto resourceDto) {
        LOG.debug("Put received to update computing resource with id {}.", resourceId);
        ComputingResourceProperty computingResourceProperty = computingResourcePropertyService.findComputingResourcePropertyById(resourceId);
        Algorithm algorithm = algorithmService.findById(algoId);
        if (Objects.isNull(computingResourceProperty.getAlgorithm()) || !computingResourceProperty.getAlgorithm().getId().equals(algoId)) {
            LOG.debug("Algorithm is not referenced from the computing resource to update!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ValidationUtils.validateComputingResourceProperty(resourceDto);
        var resource = computingResourceMixin.fromDto(resourceDto);
        resource.setId(resourceId);
        var updatedResource = computingResourcePropertyService.addComputingResourcePropertyToAlgorithm(algorithm, resource);
        return ResponseEntity.ok(computingResourcePropertyAssembler.toModel(updatedResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Computing resource with the given id doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404", description = "Algorithm or computing resource with given id doesn't exist")}, description = "Delete a computing resource of the algorithm.")
    @DeleteMapping("/{algoId}/" + Constants.COMPUTING_RESOURCES_PROPERTIES + "/{resourceId}")
    public HttpEntity<Void> deleteComputingResource(@PathVariable UUID algoId,
                                                    @PathVariable UUID resourceId) {
        LOG.debug("Delete received to remove computing resource with id {}.", resourceId);
        algorithmService.findById(algoId);
        var computingResourceProperty = computingResourcePropertyService.findComputingResourcePropertyById(resourceId);
        if (Objects.isNull(computingResourceProperty.getAlgorithm()) || !computingResourceProperty.getAlgorithm().getId().equals(algoId)) {
            LOG.debug("Algorithm is not referenced from the computing resource to delete!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        computingResourcePropertyService.deleteComputingResourceProperty(resourceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private AlgorithmRelation handleRelationUpdate(AlgorithmRelationDto relationDto, UUID relationId) {
        AlgorithmRelation resource = new AlgorithmRelation();
        if (Objects.nonNull(relationId)) {
            resource.setId(relationId);
        }
        resource.setAlgoRelationType(algoRelationTypeService.findById(relationDto.getAlgoRelationType().getId()));
        resource.setSourceAlgorithm(algorithmService.findById(relationDto.getSourceAlgorithm().getId()));
        resource.setTargetAlgorithm(algorithmService.findById(relationDto.getTargetAlgorithm().getId()));
        resource.setDescription(relationDto.getDescription());
        return algoRelationService.save(resource);
    }

    private PatternRelation savePatternRelationFromDto(Algorithm algorithm, PatternRelationDto relationDto) {
        // always use current state of this algorithm/pattern type and do not overwrite when saving relations
        var patternRelationType = patternRelationTypeService.findById(relationDto.getPatternRelationType().getId());
        relationDto.setAlgorithm(ModelMapperUtils.convert(algorithm, AlgorithmDto.class));
        relationDto.setPatternRelationType(ModelMapperUtils.convert(patternRelationType, PatternRelationTypeDto.class));

        return patternRelationService.save(ModelMapperUtils.convert(relationDto, PatternRelation.class));
    }
}
