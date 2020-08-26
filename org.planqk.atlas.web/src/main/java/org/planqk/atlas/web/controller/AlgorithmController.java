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

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.ComputeResourcePropertyMixin;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationAssembler;
import org.planqk.atlas.web.linkassembler.ApplicationAreaAssembler;
import org.planqk.atlas.web.linkassembler.ComputeResourcePropertyAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.PatternRelationAssembler;
import org.planqk.atlas.web.linkassembler.ProblemTypeAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.linkassembler.TagAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.planqk.atlas.web.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
 * Controller to access and manipulate quantum algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_ALGORITHM)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.ALGORITHMS)
@AllArgsConstructor
@Slf4j
public class AlgorithmController {

    private final AlgorithmService algorithmService;
    private final AlgorithmAssembler algorithmAssembler;

    private final AlgorithmRelationAssembler algorithmRelationAssembler;

    private final PatternRelationAssembler patternRelationAssembler;

    private final ImplementationService implementationService;
    private final ImplementationAssembler implementationAssembler;

    private final ProblemTypeAssembler problemTypeAssembler;

    private final ApplicationAreaAssembler applicationAreaAssembler;

    private final TagService tagService;
    private final TagAssembler tagAssembler;

    private final PublicationAssembler publicationAssembler;

    private final ComputeResourcePropertyService computeResourcePropertyService;
    private final ComputeResourcePropertyAssembler computeResourcePropertyAssembler;

    private final LinkingService linkingService;

    private final ComputeResourcePropertyMixin computeResourcePropertyMixin;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all algorithms (quantum, hybrid and classic).")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<AlgorithmDto>>> getAlgorithms(
            @Parameter(hidden = true) ListParameters listParameters) {
        return ResponseEntity.ok(algorithmAssembler.toModel(algorithmService.findAll(listParameters.getPageable(),
                listParameters.getSearch())));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
    }, description = "Define the basic properties of an algorithm. " +
            "References to sub-objects (e.g. a ProblemType) can be added via " +
            "sub-routes (e.g. /algorithm/id/problem-types). Custom ID will be ignored.")
    @PostMapping
    public ResponseEntity<EntityModel<AlgorithmDto>> createAlgorithm(
            @Validated(ValidationGroups.Create.class) @RequestBody AlgorithmDto algorithmDto) {
        Algorithm savedAlgorithm = algorithmService.create(ModelMapperUtils.convert(algorithmDto, Algorithm.class));
        return new ResponseEntity<>(algorithmAssembler.toModel(savedAlgorithm), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")
    }, description = "Update the basic properties of an algorithm " +
            "(e.g. name). References to subobjects (e.g. a problemtype) are not updated via this operation " +
            "- use the corresponding subroute for updating them (e.g. algorithm/{id}/problem-type). " +
            "Custom ID will be ignored.")
    @PutMapping
    public ResponseEntity<EntityModel<AlgorithmDto>> updateAlgorithm(
            @Validated(ValidationGroups.Update.class) @RequestBody AlgorithmDto algorithmDto) {
        Algorithm updatedAlgorithm = algorithmService.update(
                ModelMapperUtils.convert(algorithmDto, Algorithm.class));
        return ResponseEntity.ok(algorithmAssembler.toModel(updatedAlgorithm));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given id doesn't exist")
    }, description = "Delete an algorithm. This also deletes all entities that depend on it " +
            "(e.g., the algorithm's relation to another algorithm).")
    @DeleteMapping("/{algorithmId}")
    public ResponseEntity<Void> deleteAlgorithm(
            @PathVariable UUID algorithmId) {
        algorithmService.delete(algorithmId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")
    }, description = "Retrieve a specific algorithm and its basic properties.")
    @GetMapping("/{algorithmId}")
    public ResponseEntity<EntityModel<AlgorithmDto>> getAlgorithm(
            @PathVariable UUID algorithmId) {
        var algorithm = algorithmService.findById(algorithmId);
        return ResponseEntity.ok(algorithmAssembler.toModel(algorithm));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @GetMapping("/{algorithmId}/" + Constants.TAGS)
    public ResponseEntity<CollectionModel<EntityModel<TagDto>>> getTagsOfAlgorithm(
            @PathVariable UUID algorithmId) {
        Algorithm algorithm = algorithmService.findById(algorithmId);
        return ResponseEntity.ok(tagAssembler.toModel(algorithm.getTags()));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @PutMapping("/{algorithmId}/" + Constants.TAGS)
    public ResponseEntity<Void> addTagToAlgorithm(
            @PathVariable UUID algorithmId,
            @Validated @RequestBody TagDto tagDto) {
        tagService.addTagToAlgorithm(algorithmId, ModelMapperUtils.convert(tagDto, Tag.class));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @DeleteMapping("/{algorithmId}/" + Constants.TAGS)
    public ResponseEntity<Void> removeTagFromAlgorithm(
            @PathVariable UUID algorithmId,
            @Validated @RequestBody TagDto tagDto) {
        tagService.removeTagFromAlgorithm(algorithmId, ModelMapperUtils.convert(tagDto, Tag.class));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")
    }, description = "Get referenced publications for an algorithm.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.PUBLICATIONS)
    public ResponseEntity<PagedModel<EntityModel<PublicationDto>>> getPublicationsOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<Publication> publications = algorithmService.findPublications(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(publicationAssembler.toModel(publications));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "algorithm or publication does not exist")
    }, description = "Add a reference to an existing publication " +
            "(that was previously created via a POST on /publications/). " +
            "Custom ID will be ignored. For publication only ID is required, " +
            "other publication attributes will not change. If the publication doesn't exist yet, a 404 error is returned.")
    @PostMapping("/{algorithmId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<Void> linkAlgorithmAndPublication(
            @PathVariable UUID algorithmId,
            @PathVariable UUID publicationId) {
        linkingService.linkAlgorithmAndPublication(algorithmId, publicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm or publication with given ids do not exist or " +
                            "no relation between algorithm and publication")
    }, description = "Delete a reference to a publication of the algorithm.")
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
            @ApiResponse(responseCode = "404", description = "Algorithm does not exists in the database")
    }, description = "Get the problem types for an algorithm.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.PROBLEM_TYPES)
    public ResponseEntity<PagedModel<EntityModel<ProblemTypeDto>>> getProblemTypesOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<ProblemType> problemTypes = algorithmService.findProblemTypes(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(problemTypeAssembler.toModel(problemTypes));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "The id of the problem type to reference is null"),
            @ApiResponse(responseCode = "404", description = "Problem type or algorithm does not exists in the database")
    }, description = "Add a reference to an existing problemType " +
            "(that was previously created via a POST on /problem-types/). " +
            "Custom ID will be ignored. For problem type only ID is required, " +
            "other problem type attributes will not change. " +
            "If the problemType doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algorithmId}/" + Constants.PROBLEM_TYPES + "/{problemTypeId}")
    public ResponseEntity<Void> linkAlgorithmAndProblemType(
            @PathVariable UUID algorithmId,
            @PathVariable UUID problemTypeId) {
        linkingService.linkAlgorithmAndProblemType(algorithmId, problemTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400",
                    description = "The id of the problem type reference is null"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm or problem type does not exists in the database")
    }, description = "Delete a reference to a problem types of the algorithm.")
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
            @ApiResponse(responseCode = "404", description = "Algorithm or problem type does not exists in the database")
    }, description = "Get the application areas for an algorithm.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.APPLICATION_AREAS)
    public ResponseEntity<PagedModel<EntityModel<ApplicationAreaDto>>> getApplicationAreasOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<ApplicationArea> applicationAreas = algorithmService.findApplicationAreas(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(applicationAreaAssembler.toModel(applicationAreas));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Application area or algorithm does not exists in the database")
    }, description = "Add a reference to an existing application area " +
            "(that was previously created via a POST on /application-area/). " +
            "For application area only ID is required, other attributes will not change. " +
            "If the applicationArea doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algorithmId}/" + Constants.APPLICATION_AREAS + "/{applicationAreaId}")
    public ResponseEntity<Void> linkAlgorithmAndApplicationArea(
            @PathVariable UUID algorithmId,
            @PathVariable UUID applicationAreaId) {
        linkingService.linkAlgorithmAndApplicationArea(algorithmId, applicationAreaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Application area or algorithm does not exists in the database")
    }, description = "Delete a reference to a applicationArea of an algorithm.")
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
                    description = "Algorithm or Pattern Type doesn't exist in the database")
    }, description = "Get pattern relations for an algorithms.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS)
    public ResponseEntity<PagedModel<EntityModel<PatternRelationDto>>> getPatternRelationsOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<PatternRelation> patternRelations = algorithmService.findPatternRelations(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(patternRelationAssembler.toModel(patternRelations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm or pattern type doesn't exist in the database")
    }, description = "Add a Pattern Relation from this Algorithm to a given Pattern. " +
            "Custom ID will be ignored. For pattern relation type only ID is required, " +
            "other pattern relation type attributes will not change.")
    @PostMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS + "{patternRelationId}")
    public ResponseEntity<Void> linkAlgorithmAndPatternRelation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID patternRelationId) {
        linkingService.linkAlgorithmAndPatternRelation(algorithmId, patternRelationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Pattern relation or algorithm with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS + "/{patternRelationId}")
    public ResponseEntity<Void> unlinkAlgorithmAndPatternRelation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID patternRelationId) {
        linkingService.unlinkAlgorithmAndPatternRelation(algorithmId, patternRelationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm with the given id doesn't exist")
    }, description = "Retrieve all relations for an algorithm.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.ALGORITHM_RELATIONS)
    public ResponseEntity<PagedModel<EntityModel<AlgorithmRelationDto>>> getAlgorithmRelationsOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<AlgorithmRelation> algorithmRelations = algorithmService.findAlgorithmRelations(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(algorithmRelationAssembler.toModel(algorithmRelations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")
    }, description = "Retrieve all implementations for the algorithm")
    @GetMapping("/{algorithmId}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<PagedModel<EntityModel<ImplementationDto>>> getImplementationsOfAlgorithm(
            @PathVariable UUID algorithmId) {
        var implementations = implementationService.findByImplementedAlgorithm(algorithmId, RestUtils.getAllPageable());
        return ResponseEntity.ok(implementationAssembler.toModel(implementations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")
    }, description = "Create a new implementation for the algorithm. Custom ID will be ignored.")
    @PostMapping("/{algorithmId}/" + Constants.IMPLEMENTATIONS)
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
            @ApiResponse(responseCode = "404")
    }, description = "Retrieve the required computing resources of an algorithm")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES)
    public ResponseEntity<PagedModel<EntityModel<ComputeResourcePropertyDto>>> getComputeResourcePropertiesOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var resources = computeResourcePropertyService.findComputeResourcePropertiesOfAlgorithm(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(resources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400",
                    description = "Id of the passed computing resource type is null"),
            @ApiResponse(responseCode = "404",
                    description = "Computing resource type  or algorithm can not be found with the given Ids")
    }, description = "Add a computing resource (e.g. a certain number of qubits) that is required by an algorithm. " +
            "Custom ID will be ignored. For computing resource type only ID is required, " +
            "other computing resource type attributes will not change.")
    @PostMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES)
    public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> createComputeResourcePropertyForAlgorithm(
            @PathVariable UUID algorithmId,
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        var computeResourceProperty = computeResourcePropertyMixin.fromDto(computeResourcePropertyDto);
        ValidationUtils.validateComputingResourceProperty(computeResourceProperty);
        var createdComputeResourceProperty = computeResourcePropertyService
                .addComputeResourcePropertyToAlgorithm(algorithmId, computeResourceProperty);
        return new ResponseEntity<>(computeResourcePropertyAssembler.toModel(createdComputeResourceProperty), HttpStatus.CREATED);
    }
}
