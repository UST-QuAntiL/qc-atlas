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
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
 * Controller to access and manipulate classic, hybrid and quantum algorithms.
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

    private final PatternRelationService patternRelationService;
    private final PatternRelationAssembler patternRelationAssembler;

    private final ImplementationService implementationService;
    private final ImplementationAssembler implementationAssembler;

    private final ProblemTypeService problemTypeService;
    private final ProblemTypeAssembler problemTypeAssembler;

    private final ApplicationAreaService applicationAreaService;
    private final ApplicationAreaAssembler applicationAreaAssembler;

    private final TagService tagService;
    private final TagAssembler tagAssembler;

    private final PublicationService publicationService;
    private final PublicationAssembler publicationAssembler;

    private final ComputeResourcePropertyService computeResourcePropertyService;
    private final ComputeResourcePropertyAssembler computeResourcePropertyAssembler;

    private final LinkingService linkingService;

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
            @ApiResponse(responseCode = "400", description = "Request body has invalid fields"),
    }, description = "Define the basic properties of an algorithm. " +
            "References to sub-objects (e.g. a ProblemType) can be added via " +
            "sub-routes (e.g. POST on /" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.PROBLEM_TYPES + ").")
    @PostMapping
    public ResponseEntity<EntityModel<AlgorithmDto>> createAlgorithm(
            @Validated(ValidationGroups.Create.class) @RequestBody AlgorithmDto algorithmDto) {
        Algorithm savedAlgorithm = algorithmService.create(ModelMapperUtils.convert(algorithmDto, Algorithm.class));
        return new ResponseEntity<>(algorithmAssembler.toModel(savedAlgorithm), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
    }, description = "Update the basic properties of an algorithm (e.g. name). " +
            "References to sub-objects (e.g. a ProblemType) are not updated via this operation " +
            "- use the corresponding sub-route for updating them (e.g. /" + Constants.PROBLEM_TYPES + ").")
    @PutMapping("/{algorithmId}")
    public ResponseEntity<EntityModel<AlgorithmDto>> updateAlgorithm(
            @PathVariable UUID algorithmId,
            @Validated(ValidationGroups.Update.class) @RequestBody AlgorithmDto algorithmDto) {
        algorithmDto.setId(algorithmId);
        Algorithm updatedAlgorithm = algorithmService.update(
                ModelMapperUtils.convert(algorithmDto, Algorithm.class));
        return ResponseEntity.ok(algorithmAssembler.toModel(updatedAlgorithm));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
    }, description = "Delete an algorithm. This also deletes all entities that depend on it " +
            "(e.g., the algorithm's relations to other algorithms).")
    @DeleteMapping("/{algorithmId}")
    public ResponseEntity<Void> deleteAlgorithm(
            @PathVariable UUID algorithmId) {
        algorithmService.delete(algorithmId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
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
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
    }, description = "Retrieve all tags associated with a specific algorithm")
    @GetMapping("/{algorithmId}/" + Constants.TAGS)
    public ResponseEntity<CollectionModel<EntityModel<TagDto>>> getTagsOfAlgorithm(
            @PathVariable UUID algorithmId) {
        Algorithm algorithm = algorithmService.findById(algorithmId);
        return ResponseEntity.ok(tagAssembler.toModel(algorithm.getTags()));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
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
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
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
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
    }, description = "Get referenced publications for an algorithm.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.PUBLICATIONS)
    public ResponseEntity<PagedModel<EntityModel<PublicationDto>>> getPublicationsOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<Publication> publications = algorithmService.findLinkedPublications(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(publicationAssembler.toModel(publications));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm or publication with given IDs don't exist or " +
                    "relation between them already exists")
    }, description = "Add a reference to an existing publication " +
            "(that was previously created via a POST on /" + Constants.PUBLICATIONS + "). " +
            "For publication only ID is required, other publication attributes will not change. " +
            "If the publication doesn't exist yet, a 404 error is returned.")
    @PostMapping("/{algorithmId}/" + Constants.PUBLICATIONS)
    public ResponseEntity<Void> linkAlgorithmAndPublication(
            @PathVariable UUID algorithmId,
            @Validated({ValidationGroups.Update.class}) @RequestBody PublicationDto publicationDto) {
        linkingService.linkAlgorithmAndPublication(algorithmId, publicationDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm or publication with given IDs don't exist or " +
                            "no relation between them exists")
    }, description = "Delete a reference to a publication of an algorithm. The reference has to be previously created " +
            "via a POST on /" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.PUBLICATIONS + "/{publicationId}).")
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
            @ApiResponse(responseCode = "404")
    }, description = "Retrieve a publication of an algorithm")
    @GetMapping("/{algorithmId}/" + Constants.PUBLICATIONS + "/{publicationId}")
    public ResponseEntity<EntityModel<PublicationDto>> getPublicationOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID publicationId) {
        Publication publication = publicationService.findById(publicationId);
        return new ResponseEntity<>(publicationAssembler.toModel(publication), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
    }, description = "Get the problem types for an algorithm.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.PROBLEM_TYPES)
    public ResponseEntity<PagedModel<EntityModel<ProblemTypeDto>>> getProblemTypesOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<ProblemType> problemTypes = algorithmService.findLinkedProblemTypes(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(problemTypeAssembler.toModel(problemTypes));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "The ID of the algorithm or problem type is not a valid UUID"),
            @ApiResponse(responseCode = "404", description = "Algorithm or problem type with given IDs don't exist or " +
                    "relation between already exists")
    }, description = "Add a reference to an existing ProblemType " +
            "(that was previously created via a POST on /" + Constants.PROBLEM_TYPES + "). " +
            "For problem type only ID is required, other problem type attributes will not change. " +
            "If the ProblemType doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algorithmId}/" + Constants.PROBLEM_TYPES)
    public ResponseEntity<Void> linkAlgorithmAndProblemType(
            @PathVariable UUID algorithmId,
            @Validated({ValidationGroups.Update.class}) @RequestBody ProblemTypeDto problemTypeDto) {
        linkingService.linkAlgorithmAndProblemType(algorithmId, problemTypeDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "The ID of the algorithm or problem type is not a valid UUID"),
            @ApiResponse(responseCode = "404", description = "Algorithm or problem type with given IDs don't exist or " +
                    "no relation between them exists")
    }, description = "Delete a reference to a problem types of an algorithm. The reference has to be previously created " +
            "via a POST on /" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.PROBLEM_TYPES + "/{problemTypeId}).")
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
            @ApiResponse(responseCode = "404", description = "Problem type with given id doesn't exist")
    }, description = "Retrieve a specific problem type of an algorithm")
    @GetMapping("/{algorithmId}/" + Constants.PROBLEM_TYPES + "/{problemTypeId}")
    public ResponseEntity<EntityModel<ProblemTypeDto>> getProblemType(
            @PathVariable UUID algorithmId,
            @PathVariable UUID problemTypeId) {
        ProblemType problemType = problemTypeService.findById(problemTypeId);
        return ResponseEntity.ok(problemTypeAssembler.toModel(problemType));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "The ID of the algorithm or application area is not a valid UUID"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
    }, description = "Get the application areas for an algorithm.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.APPLICATION_AREAS)
    public ResponseEntity<PagedModel<EntityModel<ApplicationAreaDto>>> getApplicationAreasOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<ApplicationArea> applicationAreas = algorithmService.findLinkedApplicationAreas(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(applicationAreaAssembler.toModel(applicationAreas));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm or application area with given IDs don't exist")
    }, description = "Add a reference to an existing application area " +
            "(that was previously created via a POST on /" + Constants.APPLICATION_AREAS + "). " +
            "For application area only ID is required, other attributes will not change. " +
            "If the applicationArea doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algorithmId}/" + Constants.APPLICATION_AREAS)
    public ResponseEntity<Void> linkAlgorithmAndApplicationArea(
            @PathVariable UUID algorithmId,
            @Validated({ValidationGroups.Update.class}) ApplicationAreaDto applicationAreaDto) {
        linkingService.linkAlgorithmAndApplicationArea(algorithmId, applicationAreaDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm or application area with given IDs don't exist")
    }, description = "Delete a reference to an application area of an algorithm. The reference has to be previously " +
            "created via a POST on /" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.PROBLEM_TYPES +
            "/{problemTypeId}).")
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
            @ApiResponse(responseCode = "404", description = "Application area with given id doesn't exist")
    }, description = "Get a specific application area of an algorithm")
    @GetMapping("/{algorithmId}/" + Constants.APPLICATION_AREAS + "/{applicationAreaId}")
    public ResponseEntity<EntityModel<ApplicationAreaDto>> getApplicationAreaOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID applicationAreaId) {
        ApplicationArea applicationArea = applicationAreaService.findById(applicationAreaId);
        return ResponseEntity.ok(applicationAreaAssembler.toModel(applicationArea));
    }

    // TODO decide if move to ImplementationController
    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
    }, description = "Retrieve all implementations for an algorithm")
    @GetMapping("/{algorithmId}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<PagedModel<EntityModel<ImplementationDto>>> getImplementationsOfAlgorithm(
            @PathVariable UUID algorithmId) {
        var implementations = implementationService.findByImplementedAlgorithm(algorithmId, RestUtils.getAllPageable());
        return ResponseEntity.ok(implementationAssembler.toModel(implementations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
    }, description = "Retrieve the required compute resource properties of an algorithm")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
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
                    description = "Compute resource type or Algorithm with given ID doesn't exist")
    }, description = "Add a compute resource property (e.g. a certain number of qubits) that is required by an algorithm. " +
            "For computr resource property type only ID is required, " +
            "other compute resource property type attributes will not change.")
    @PostMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
    public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> createComputeResourcePropertyForAlgorithm(
            @PathVariable UUID algorithmId,
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        var computeResourceProperty = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);
        var createdComputeResourceProperty = computeResourcePropertyService
                .addComputeResourcePropertyToAlgorithm(algorithmId, computeResourceProperty);
        return new ResponseEntity<>(computeResourcePropertyAssembler.toModel(createdComputeResourceProperty), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")},
            description = "Update a Compute resource property of an algorithm. " +
                    "For compute resource property type only ID is required, other compute resource property type " +
                    "attributes will not change.")
    @PutMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> updateComputeResourcePropertyOfAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID computeResourcePropertyId,
            @Validated(ValidationGroups.Update.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        computeResourcePropertyDto.setId(computeResourcePropertyId);
        var resource = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);
        var updatedResource = computeResourcePropertyService.update(resource);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(updatedResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute resource property with given id doesn't exist"),
    }, description = "Delete a Compute resource property of an algorithm")
    @DeleteMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public HttpEntity<Void> deleteComputeResourceProperty(
            @PathVariable UUID algorithmId,
            @PathVariable UUID computeResourcePropertyId) {
        computeResourcePropertyService.delete(computeResourcePropertyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
    }, description = "Retrieve a specific compute resource property of an algorithm")
    @GetMapping("/{algorithmId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public HttpEntity<EntityModel<ComputeResourcePropertyDto>> getComputeResourceProperty(
            @PathVariable UUID algorithmId,
            @PathVariable UUID computeResourcePropertyId) {
        var resource = computeResourcePropertyService.findById(computeResourcePropertyId);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(resource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Algorithm or pattern relation type with given IDs doesn't exist")
    }, description = "Retrieve pattern relations for an algorithms.")
    @ListParametersDoc
    @GetMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS)
    public ResponseEntity<PagedModel<EntityModel<PatternRelationDto>>> getPatternRelationsOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<PatternRelation> patternRelations = algorithmService.findLinkedPatternRelations(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(patternRelationAssembler.toModel(patternRelations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Add a pattern relation from an algorithm to a given pattern." +
            "Custom ID will be ignored. For pattern relation type only ID is required," +
            "other pattern relation type attributes will not change.")
    @PostMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS)
    public ResponseEntity<EntityModel<PatternRelationDto>> createPatternRelation(
            @PathVariable UUID algorithmId,
            @Validated( {ValidationGroups.Create.class}) @RequestBody PatternRelationDto patternRelationDto) {
        var savedPatternRelation = patternRelationService.create(
                ModelMapperUtils.convert(patternRelationDto, PatternRelation.class));
        return new ResponseEntity<>(patternRelationAssembler.toModel(savedPatternRelation), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "PatternRelation doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404",
                    description = "Pattern relation or algorithm with given id doesn't exist")
    }, description = "Update a reference to a pattern. " +
            "Custom ID will be ignored. For pattern relation type only ID is required, " +
            "other pattern relation type attributes will not change.")
    @PutMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS + "/{patternRelationId}")
    public ResponseEntity<EntityModel<PatternRelationDto>> updatePatternRelation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID patternRelationId,
            @Validated( {ValidationGroups.Update.class}) @RequestBody PatternRelationDto patternRelationDto) {
        patternRelationDto.setId(patternRelationId);
        var savedPatternRelation = patternRelationService.update(
                ModelMapperUtils.convert(patternRelationDto, PatternRelation.class));
        return ResponseEntity.ok(patternRelationAssembler.toModel(savedPatternRelation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Retrieve a specific pattern relation")
    @GetMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS + "/{patternRelationId}")
    public ResponseEntity<EntityModel<PatternRelationDto>> getPatternRelation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID patternRelationId) {
        var patternRelation = patternRelationService.findById(patternRelationId);
        return ResponseEntity.ok(patternRelationAssembler.toModel(patternRelation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Pattern relation with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{algorithmId}/" + Constants.PATTERN_RELATIONS + "/{patternRelationId}")
    public ResponseEntity<Void> deletePatternRelation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID patternRelationId) {
        patternRelationService.delete(patternRelationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
