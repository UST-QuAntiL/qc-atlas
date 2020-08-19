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
import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.AlgoRelationService;
import org.planqk.atlas.core.services.AlgoRelationTypeService;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.PatternRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.ComputeResourcePropertyMixin;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.PatternRelationTypeDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationAssembler;
import org.planqk.atlas.web.linkassembler.ApplicationAreaAssembler;
import org.planqk.atlas.web.linkassembler.ComputeResourcePropertyAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.PatternRelationAssembler;
import org.planqk.atlas.web.linkassembler.ProblemTypeAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;
import org.planqk.atlas.web.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

    private final AlgoRelationService algoRelationService;
    private final AlgoRelationTypeService algoRelationTypeService;

    private final PatternRelationService patternRelationService;
    private final PatternRelationAssembler patternRelationAssembler;

    private final PatternRelationTypeService patternRelationTypeService;

    private final ImplementationService implementationService;
    private final ImplementationAssembler implementationAssembler;

    private final ProblemTypeAssembler problemTypeAssembler;

    private final ApplicationAreaAssembler applicationAreaAssembler;

    private final AlgorithmRelationAssembler algorithmRelationAssembler;

    private final PublicationAssembler publicationAssembler;

    private final ComputeResourcePropertyAssembler computeResourcePropertyAssembler;

    private final LinkingService linkingService;


    //    private final TagAssembler tagAssembler;

    private final ComputeResourcePropertyMixin computeResourcePropertyMixin;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all algorithms (quantum, hybrid and classic).")
    @GetMapping()
    @ListParametersDoc()
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
    @PostMapping()
    public ResponseEntity<EntityModel<AlgorithmDto>> createAlgorithm(@Valid @RequestBody AlgorithmDto algo) {
        Algorithm savedAlgorithm = algorithmService.save(ModelMapperUtils.convert(algo, Algorithm.class));
        return new ResponseEntity<>(algorithmAssembler.toModel(savedAlgorithm), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Algorithm doesn't exist")
    }, description = "Update the basic properties of an algorithm " +
            "(e.g. name). References to subobjects (e.g. a problemtype) are not updated via this operation " +
            "- use the corresponding subroute for updating them (e.g. algorithm/{id}/problem-type). " +
            "Custom ID will be ignored.")
    @PutMapping("/{algoId}")
    public ResponseEntity<EntityModel<AlgorithmDto>> updateAlgorithm(
            @PathVariable UUID algoId,
            @Valid @RequestBody AlgorithmDto algo) {
        Algorithm updatedAlgorithm = algorithmService.update(algoId, ModelMapperUtils.convert(algo, Algorithm.class));
        return ResponseEntity.ok(algorithmAssembler.toModel(updatedAlgorithm));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given id doesn't exist")
    }, description = "Delete an algorithm. This also deletes all entities that depend on it " +
            "(e.g., the algorithm's relation to another algorithm).")
    @DeleteMapping("/{algoId}")
    public ResponseEntity<Void> deleteAlgorithm(@PathVariable UUID algoId) {
        algorithmService.delete(algoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Algorithm doesn't exist")
    }, description = "Retrieve a specific algorithm and its basic properties.")
    @GetMapping("/{algoId}")
    public ResponseEntity<EntityModel<AlgorithmDto>> getAlgorithm(@PathVariable UUID algoId) {
        var algorithm = algorithmService.findById(algoId);
        return ResponseEntity.ok(algorithmAssembler.toModel(algorithm));
    }

//    @Operation(responses = {@ApiResponse(responseCode = "200")})
//    @GetMapping("/{id}/" + Constants.TAGS)
//    public HttpEntity<CollectionModel<EntityModel<TagDto>>> getTags(@PathVariable UUID id) {
//        Algorithm algorithm = algorithmService.findById(id);
//        return ResponseEntity.ok(tagsAssembler.toModel(algorithm.getTags()));
//    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Algorithm doesn't exist")
    }, description = "Get referenced publications for an algorithm.")
    @GetMapping("/{algoId}/" + Constants.PUBLICATIONS)
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<PublicationDto>>> getPublicationsForAlgorithm(
            @PathVariable UUID algoId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<Publication> publications = algorithmService.findPublications(algoId, listParameters.getPageable());
        return ResponseEntity.ok(publicationAssembler.toModel(publications));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content,
                    description = "algorithm or publication does not exist")
    }, description = "Add a reference to an existing publication " +
            "(that was previously created via a POST on /publications/). " +
            "Custom ID will be ignored. For publication only ID is required, " +
            "other publication attributes will not change. If the publication doesn't exist yet, a 404 error is returned.")
    @PostMapping("/{algoId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<Void> addPublicationReferenceToAlgorithm(
            @PathVariable UUID algoId,
            @PathVariable UUID publicationId) {
        linkingService.linkAlgorithmAndPublication(algoId, publicationId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm or publication with given ids do not exist or " +
                    "no relation between algorithm and publication")
    }, description = "Delete a reference to a publication of the algorithm.")
    @DeleteMapping("/{algoId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<Void> deletePublicationReferenceFromAlgorithm(
            @PathVariable UUID algoId,
            @PathVariable UUID publicationId) {
        linkingService.unlinkAlgorithmAndPublication(algoId, publicationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm does not exists in the database")
    }, description = "Get the problem types for an algorithm.")
    @GetMapping("/{algoId}/" + Constants.PROBLEM_TYPES)
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<ProblemTypeDto>>> getProblemTypesForAlgorithm(
            @PathVariable UUID algoId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<ProblemType> problemTypes = algorithmService.findProblemTypes(algoId, listParameters.getPageable());
        return ResponseEntity.ok(problemTypeAssembler.toModel(problemTypes));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "The id of the problem type to reference is null"),
            @ApiResponse(responseCode = "404", description = "Problem type or algorithm does not exists in the database")
    }, description = "Add a reference to an existing problemType " +
            "(that was previously created via a POST on /problem-types/). " +
            "Custom ID will be ignored. For problem type only ID is required, " +
            "other problem type attributes will not change. " +
            "If the problemType doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algoId}/" + Constants.PROBLEM_TYPES + "/{problemTypeId}")
    public ResponseEntity<Void> addProblemTypeReferenceToAlgorithm(
            @PathVariable UUID algoId,
            @PathVariable UUID problemTypeId) {
        linkingService.linkAlgorithmAndProblemType(algoId, problemTypeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "The id of the problem type reference is null"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm or problem type does not exists in the database")
    }, description = "Delete a reference to a problem types of the algorithm.")
    @DeleteMapping("/{algoId}/" + Constants.PROBLEM_TYPES + "/{problemTypeId}")
    public ResponseEntity<Void> deleteProblemTypeReferenceFromAlgorithm(
            @PathVariable UUID algoId,
            @PathVariable UUID problemTypeId) {
        linkingService.unlinkAlgorithmAndProblemType(algoId, problemTypeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm or problem type does not exists in the database")
    }, description = "Get the application areas for an algorithm.")
    @GetMapping("/{algoId}/" + Constants.APPLICATION_AREAS)
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<ApplicationAreaDto>>> getApplicationAreasForAlgorithm(
            @PathVariable UUID algoId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<ApplicationArea> applicationAreas = algorithmService.findApplicationAreas(algoId, listParameters.getPageable());
        return ResponseEntity.ok(applicationAreaAssembler.toModel(applicationAreas));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Application area or algorithm does not exists in the database")
    }, description = "Add a reference to an existing application area " +
            "(that was previously created via a POST on /application-area/). " +
            "For application area only ID is required, other attributes will not change. " +
            "If the applicationArea doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algoId}/" + Constants.APPLICATION_AREAS + "/{applicationAreaId}")
    public ResponseEntity<Void> addApplicationAreaReferenceToAlgorithm(
            @PathVariable UUID algoId,
            @PathVariable UUID applicationAreaId) {
        linkingService.linkAlgorithmAndApplicationArea(algoId, applicationAreaId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Application area or algorithm does not exists in the database")
    }, description = "Delete a reference to a applicationArea of an algorithm.")
    @DeleteMapping("/{algoId}/" + Constants.APPLICATION_AREAS + "/{applicationAreaId}")
    public ResponseEntity<Void> deleteApplicationAreaReferenceFromAlgorithm(
            @PathVariable UUID algoId,
            @PathVariable UUID applicationAreaId) {
        linkingService.unlinkAlgorithmAndApplicationArea(algoId, applicationAreaId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm or Pattern Type doesn't exist in the database")
    }, description = "Get pattern relations for an algorithms.")
    @GetMapping("/{algoId}/" + Constants.PATTERN_RELATIONS)
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<PatternRelationDto>>> getPatternRelationsForAlgorithm(
            @PathVariable UUID algoId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<PatternRelation> patternRelations = algorithmService.findPatternRelations(algoId, listParameters.getPageable());
        return ResponseEntity.ok(patternRelationAssembler.toModel(patternRelations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm or pattern type doesn't exist in the database")
    }, description = "Add a Pattern Relation from this Algorithm to a given Pattern. " +
            "Custom ID will be ignored. For pattern relation type only ID is required, " +
            "other pattern relation type attributes will not change.")
    @PostMapping("/{algoId}/" + Constants.PATTERN_RELATIONS + "{patternRelationId}")
    public ResponseEntity<Void> addPatternRelationReferenceToAlgorithm(
            @PathVariable UUID algoId,
            @PathVariable UUID patternRelationId) {
        linkingService.linkAlgorithmAndPatternRelation(algoId, patternRelationId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Pattern relation or algorithm with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{algoId}/" + Constants.PATTERN_RELATIONS + "/{patternRelationId}")
    public ResponseEntity<Void> deletePatternRelationReferenceFromAlgorithm(
            @PathVariable UUID algoId,
            @PathVariable UUID patternRelationId) {
        linkingService.unlinkAlgorithmAndPatternRelation(algoId, patternRelationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO Check if needed
    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "PatternRelation doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404",
                    description = "Pattern relation or algorithm with given id doesn't exist")
    }, description = "Update a references to a pattern. Custom ID will be ignored. " +
            "For pattern relation type only ID is required, other pattern relation type attributes will not change.")
    @PutMapping("/{algoId}/" + Constants.PATTERN_RELATIONS + "/{relationId}")
    public ResponseEntity<EntityModel<PatternRelationDto>> updatePatternRelations(
            @PathVariable UUID algoId,
            @PathVariable UUID relationId,
            @Valid @RequestBody PatternRelationDto relationDto) {
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
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm with the given id doesn't exist")
    }, description = "Retrieve all relations for an algorithm.")
    @GetMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS)
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<AlgorithmRelationDto>>> getAlgorithmRelationsForAlgorithm(
            @PathVariable UUID algoId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<AlgorithmRelation> algorithmRelations = algorithmService.findAlgorithmRelations(algoId, listParameters.getPageable());
        return ResponseEntity.ok(algorithmRelationAssembler.toModel(algorithmRelations));
    }

//    @Operation(responses = {
//            @ApiResponse(responseCode = "200"),
//            @ApiResponse(responseCode = "400"),
//            @ApiResponse(responseCode = "404",
//                    description = "Algorithm with given id doesn't exist")
//    }, description = "Add an algorithm relation from this algorithm to another given algorithm. " +
//            "Custom ID will be ignored. For algorithm relation type only ID is required, other " +
//            "algorithm relation type attributes will not change.")
//    @PostMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS + "/{algoRelationId}")
//    public ResponseEntity<Void> addAlgorithmRelationReferenceToAlgorithm(
//            @PathVariable UUID algoId,
//            @PathVariable UUID algoRelationId) {
//        algorithmService.addAlgorithmRelationReference(algoId, algoRelationId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @Operation(responses = {
//            @ApiResponse(responseCode = "200"),
//            @ApiResponse(responseCode = "404",
//                    description = "Algorithm relation or algorithm with given id doesn't exist")
//    }, description = "Delete a relation of the algorithm.")
//    @DeleteMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS + "/{algoRelationId}")
//    public HttpEntity<Void> deleteAlgorithmRelationReferenceFromAlgorithm(
//            @PathVariable UUID algoId,
//            @PathVariable UUID algoRelationId) {
//        algorithmService.findById(algoId);
//        algoRelationService.findById(relationId);
//        algoRelationService.delete(relationId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    // TODO check if needed
//    @Operation(responses = {
//            @ApiResponse(responseCode = "200"),
//            @ApiResponse(responseCode = "400",
//                    description = "AlgorithmRelation doesn't belong to this algorithm"),
//            @ApiResponse(responseCode = "404",
//                    description = "Algorithm with the given id doesn't exist")
//    }, description = "")
//    @GetMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS + "/{relationId}")
//    public HttpEntity<EntityModel<AlgorithmRelationDto>> getAlgorithmRelation(
//            @PathVariable UUID algoId, @PathVariable UUID relationId) {
//        algorithmService.findById(algoId);
//        AlgorithmRelation algorithmRelation = algoRelationService.findById(relationId);
//        if (!algorithmRelation.getSourceAlgorithm().getId().equals(algoId) &&
//                !algorithmRelation.getTargetAlgorithm().getId().equals(algoId)) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        return ResponseEntity.ok(algorithmRelationAssembler.toModel(algorithmRelation));
//    }
//
//    // TODO: check if needed
//    @Operation(responses = {
//            @ApiResponse(responseCode = "200"),
//            @ApiResponse(responseCode = "400",
//                    description = "AlgorithmRelation doesn't contain this algorithm as source or target"),
//            @ApiResponse(responseCode = "404",
//                    description = "Algorithm with the given id doesn't exist")
//    }, description = "Change an algorithm relation from this algorithm to another given algorithm. " +
//            "Custom ID will be ignored. For algorithm relation type only ID is required, " +
//            "other algorithm relation type attributes will not change.")
//    @PutMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS + "/{relationId}")
//    public HttpEntity<EntityModel<AlgorithmRelationDto>> updateAlgorithmRelation(
//            @PathVariable UUID algoId,
//            @PathVariable UUID relationId,
//            @Valid @RequestBody AlgorithmRelationDto relationDto) {
//        // check if relation exists and if it uses this algorithm as source or target
//        algorithmService.findById(algoId);
//        algoRelationService.findById(relationId);
//        if (!relationDto.getSourceAlgorithm().getId().equals(algoId) && !relationDto.getTargetAlgorithm().getId().equals(algoId)) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//        AlgorithmRelation updatedRelation = handleRelationUpdate(relationDto, relationId);
//        return ResponseEntity.ok(algorithmRelationAssembler.toModel(updatedRelation));
//    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")
    }, description = "Retrieve all implementations for the algorithm")
    @GetMapping("/{algoId}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<PagedModel<EntityModel<ImplementationDto>>> getImplementationsOfAlgorithm(
            @PathVariable UUID algoId) {
        var implementations = implementationService.findByImplementedAlgorithm(algoId, RestUtils.getAllPageable());
        return ResponseEntity.ok(implementationAssembler.toModel(implementations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm doesn't exist")
    }, description = "Create a new implementation for the algorithm. Custom ID will be ignored.")
    @PostMapping("/{algoId}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<EntityModel<ImplementationDto>> createImplementation(
            @PathVariable UUID algoId,
            @Valid @RequestBody ImplementationDto implementationDto) {
        Implementation savedImplementation = implementationService.create(
                ModelMapperUtils.convert(implementationDto, Implementation.class), algoId);
        return new ResponseEntity<>(implementationAssembler.toModel(savedImplementation), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Retrieve the required computing resources of an algorithm")
    @GetMapping("/{algoId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES)
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<ComputeResourcePropertyDto>>> getComputeResourcePropertiesForAlgorithm(
            @PathVariable UUID algoId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var resources = algorithmService.findComputeResourceProperties(algoId, listParameters.getPageable());
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(resources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Id of the passed computing resource type is null"),
            @ApiResponse(responseCode = "404",
                    description = "Computing resource type  or algorithm can not be found with the given Ids")
    }, description = "Add a computing resource (e.g. a certain number of qubits) that is required by an algorithm. " +
            "Custom ID will be ignored. For computing resource type only ID is required, " +
            "other computing resource type attributes will not change.")
    @PostMapping("/{algoId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES)
    public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> createComputeResourcePropertyForAlgorithm(
            @PathVariable UUID algoId,
            @Valid @RequestBody ComputeResourcePropertyDto resourceDto) {
        ValidationUtils.validateComputingResourceProperty(resourceDto);

        var resourceProperty = computeResourcePropertyMixin.fromDto(resourceDto);
        var createdResourceProperty = algorithmService.createComputeResourceProperty(algoId, resourceProperty);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(createdResourceProperty));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Computing resource with the given id doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm or computing resource with given id doesn't exist")
    }, description = "Delete a computing resource of the algorithm.")
    @DeleteMapping("/{algoId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES + "/{resourceId}")
    public ResponseEntity<Void> deleteComputeResourcePropertyOfAlgorithm(
            @PathVariable UUID algoId,
            @PathVariable UUID resourceId) {
        algorithmService.deleteComputeResourceProperty(algoId, resourceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @Operation(responses = {
//            @ApiResponse(responseCode = "200"),
//            @ApiResponse(responseCode = "400"),
//            @ApiResponse(responseCode = "404",
//                    description = "Algorithm with the given id doesn't exist")
//    }, description = "Update a computing resource of the algorithm. Custom ID will be ignored. " +
//            "For computing resource type only ID is required, other computing resource type attributes will not change.")
//    @PutMapping("/{algoId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES + "/{resourceId}")
//    public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> updateComputeResourcePropertyOfAlgorithm(
//            @PathVariable UUID algoId,
//            @PathVariable UUID resourceId,
//            @RequestBody ComputeResourcePropertyDto resourceDto) {
//        ComputeResourceProperty computeResourceProperty = computeResourcePropertyService
//                .findComputeResourcePropertyById(resourceId);
//        Algorithm algorithm = algorithmService.findById(algoId);
//        if (Objects.isNull(computeResourceProperty.getAlgorithm()) ||
//                !computeResourceProperty.getAlgorithm().getId().equals(algoId)) {
//            log.debug("Algorithm is not referenced from the computing resource to update!");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        ValidationUtils.validateComputingResourceProperty(resourceDto);
//        var resource = computeResourcePropertyMixin.fromDto(resourceDto);
//        resource.setId(resourceId);
//        var updatedResource = computeResourcePropertyService.addComputeResourcePropertyToAlgorithm(algorithm, resource);
//        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(updatedResource));
//    }
//
//    @Operation(responses = {
//            @ApiResponse(responseCode = "200"),
//            @ApiResponse(responseCode = "400",
//                    description = "Resource doesn't belong to this algorithm"),
//            @ApiResponse(responseCode = "404",
//                    description = "Algorithm with the given id doesn't exist")
//    }, description = "")
//    @GetMapping("/{algoId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES + "/{resourceId}")
//    public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> getComputingResource(
//            @PathVariable UUID algoId,
//            @PathVariable UUID resourceId) {
//        algorithmService.findById(algoId);
//        ComputeResourceProperty computeResourceProperty = computeResourcePropertyService
//                .findComputeResourcePropertyById(resourceId);
//        if (Objects.isNull(computeResourceProperty.getAlgorithm()) ||
//                !computeResourceProperty.getAlgorithm().getId().equals(algoId)) {
//            log.debug("Algorithm is not referenced from the computing resource to retrieve!");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(computeResourceProperty));
//    }

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
