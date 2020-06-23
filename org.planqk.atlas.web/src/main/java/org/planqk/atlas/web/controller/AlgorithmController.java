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
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ComputingResource;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.core.services.ComputingResourceService;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.dtos.ComputingResourceDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationAssembler;
import org.planqk.atlas.web.linkassembler.ApplicationAreaAssembler;
import org.planqk.atlas.web.linkassembler.ComputingResourceAssembler;
import org.planqk.atlas.web.linkassembler.PatternRelationAssembler;
import org.planqk.atlas.web.linkassembler.ProblemTypeAssembler;
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

//import org.planqk.atlas.web.linkassembler.TagAssembler;

/**
 * Controller to access and manipulate quantum algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "algorithm")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHMS)
@AllArgsConstructor
public class AlgorithmController {

    final private static Logger LOG = LoggerFactory.getLogger(AlgorithmController.class);

    private final AlgorithmService algorithmService;
    private final ComputingResourceService computingResourceService;
    private final PatternRelationService patternRelationService;
    private final ProblemTypeService problemTypeService;
    private final ApplicationAreaService applicationAreaService;
    private final PublicationService publicationService;

    private final PagedResourcesAssembler<AlgorithmDto> algorithmPaginationAssembler;
    private final PagedResourcesAssembler<ComputingResourceDto> computingResourcePaginationAssembler;
    private final ProblemTypeAssembler problemTypeAssembler;
    private final ApplicationAreaAssembler applicationAreaAssembler;
    //    private final TagAssembler tagAssembler;
    private final AlgorithmAssembler algorithmAssembler;
    private final AlgorithmRelationAssembler algorithmRelationAssembler;
    private final PublicationAssembler publicationAssembler;
    private final ComputingResourceAssembler computingResourceAssembler;
    private final PatternRelationAssembler patternRelationAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Retrieve all algorithms (quantum, hybrid and classic)")
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

    @Operation(responses = {@ApiResponse(responseCode = "201")}, description = "Define the basic properties of an algorithm. References to subobjects (e.g. a problemtype) can be added via subroutes (e.g. /algorithm/id/problem-types)")
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

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Update the basic properties of an algorithm (e.g. name). References to subobjects (e.g. a problemtype) are not updated via this operation - use the corresponding subroute for updating them (e.g. algorithm/{id}/problem-type).")
    @PutMapping("/{algoId}")
    public HttpEntity<EntityModel<AlgorithmDto>> updateAlgorithm(@PathVariable UUID algoId,
                                                                 @Valid @RequestBody AlgorithmDto algo) {
        LOG.debug("Put to update algorithm with id: {}.", algoId);
        Algorithm updatedAlgorithm = algorithmService.update(algoId, ModelMapperUtils.convert(algo, Algorithm.class));
        // Convert To EntityModel
        EntityModel<AlgorithmDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(updatedAlgorithm, AlgorithmDto.class));
        // Fill EntityModel with links
        algorithmAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }


//    @Operation(responses = {@ApiResponse(responseCode = "200")})
//    @GetMapping("/{id}/" + Constants.TAGS)
//    public HttpEntity<CollectionModel<EntityModel<TagDto>>> getTags(@PathVariable UUID id) {
//        Algorithm algorithm = algorithmService.findById(id);
//        // Get Tags of Algorithm
//        Set<Tag> tags = algorithm.getTags();
//        // Translate Entity to DTO
//        Set<TagDto> dtoTags = ModelMapperUtils.convertSet(tags, TagDto.class);
//        // Create CollectionModel
//        CollectionModel<EntityModel<TagDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTags);
//        // Fill EntityModel Links
//        tagAssembler.addLinks(resultCollection);
//        // Fill Collection-Links
//        algorithmAssembler.addTagLink(resultCollection, id);
//        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
//    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Delete an algorithm. This also deletes all entities that depend on it (e.g., the algorith's relation to another algorithm.")
    @DeleteMapping("/{algoId}")
    public HttpEntity<?> deleteAlgorithm(@PathVariable UUID algoId) {
        LOG.debug("Delete to remove algorithm with id: {}.", algoId);
        algorithmService.delete(algoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Retrieve an specific algorithm and its basic properties")
    @GetMapping("/{algoId}")
    public HttpEntity<EntityModel<AlgorithmDto>> getAlgorithm(@PathVariable UUID algoId) {
        LOG.debug("Get to retrieve algorithm with id: {}.", algoId);

        Algorithm algorithm = algorithmService.findById(algoId);
        // Convert To EntityModel
        EntityModel<AlgorithmDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(algorithm, AlgorithmDto.class));
        // Fill EntityModel with links
        algorithmAssembler.addLinks(dtoOutput);

        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content, description = "algorithm doesn't exist")},
            description = "Get referenced publications for an algorithm")
    @GetMapping("/{algoId}/" + Constants.PUBLICATIONS)
    public HttpEntity<CollectionModel<EntityModel<PublicationDto>>> getPublications(@PathVariable UUID algoId) {
        Algorithm algorithm = algorithmService.findById(algoId);
        // Get Publications of Algorithm
        Set<Publication> publications = algorithm.getPublications();
        // Translate Entity to DTO
        Set<PublicationDto> dtoPublications = ModelMapperUtils.convertSet(publications, PublicationDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<PublicationDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoPublications);
        // Fill EntityModel Links
        publicationAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addPublicationLink(resultCollection, algoId);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404", content = @Content,
            description = "algorithm or publication does not exist")},
            description = "Add a reference to an existing publication (that was previously created via a POST on /publications/. If the publication doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algoId}/" + Constants.PUBLICATIONS)
    public HttpEntity<CollectionModel<EntityModel<PublicationDto>>> addPublication(@PathVariable UUID algoId, @Valid @RequestBody PublicationDto publicationDto) {
        Algorithm algorithm = algorithmService.findById(algoId);
        Publication publication = ModelMapperUtils.convert(publicationDto, Publication.class);
        // access publication in db to throw NoSuchElementException if it doesn't exist
        publicationService.findById(publication.getId());
        // Get ProblemTypes of Algorithm
        Set<Publication> publications = algorithm.getPublications();
        // add new problemtype
        publications.add(publication);
        // update and return update list:
        algorithm.setPublications(publications);
        Set<Publication> updatedPublications = algorithmService.save(algorithm).getPublications();
        Set<PublicationDto> dtoPublications = ModelMapperUtils.convertSet(updatedPublications, PublicationDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<PublicationDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoPublications);
        // Fill EntityModel Links
        publicationAssembler.addLinks(resultCollection);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")},
            description = "Update all references to existing publication (that were previously created via a POST on /publications/). The values (e.g. type) of each publication are not changes through this operation. If one of the publications doesn't exist yet, a 404 error is thrown.")
    @PutMapping("/{algoId}/" + Constants.PUBLICATIONS)
    public HttpEntity<CollectionModel<EntityModel<PublicationDto>>> updatePublications(@PathVariable UUID algoId, @Valid @RequestBody List<PublicationDto> publications) {
        Algorithm algorithm = algorithmService.findById(algoId);
        // access publication in db to throw NoSuchElementException if it doesn't exist
        Set<Publication> newPublications = new HashSet<>();
        publications.forEach(publicationDto -> {
            Publication publication = publicationService.findById(publicationDto.getId());
            newPublications.add(publication);
        });
        // update and return update list:
        algorithm.setPublications(newPublications);
        Set<Publication> updatedPublications = algorithmService.save(algorithm).getPublications();
        Set<PublicationDto> dtoPublications = ModelMapperUtils.convertSet(updatedPublications, PublicationDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<PublicationDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoPublications);
        // Fill EntityModel Links
        publicationAssembler.addLinks(resultCollection);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Get the problem types for an algorithm")
    @GetMapping("/{algoId}/" + Constants.PROBLEM_TYPES)
    public HttpEntity<CollectionModel<EntityModel<ProblemTypeDto>>> getProblemTypes(@PathVariable UUID algoId) {
        Algorithm algorithm = algorithmService.findById(algoId);
        // Get ProblemTypes of Algorithm
        Set<ProblemType> problemTypes = algorithm.getProblemTypes();
        // Translate Entity to DTO
        Set<ProblemTypeDto> dtoTypes = ModelMapperUtils.convertSet(problemTypes, ProblemTypeDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<ProblemTypeDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTypes);
        // Fill EntityModel Links
        problemTypeAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addProblemTypeLink(resultCollection, algoId);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404", description = "problem type or algorithm does not exists in the database")}, description = "Add a reference to an existing problemType (that was previously created via a POST on /problem-types/. If the problemType doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algoId}/" + Constants.PROBLEM_TYPES)
    public HttpEntity<CollectionModel<EntityModel<ProblemTypeDto>>> addProblemType(@PathVariable UUID algoId, @Valid @RequestBody ProblemTypeDto problemTypeDto) {
        Algorithm algorithm = algorithmService.findById(algoId);
        // access stored pattern relation -> if it does not exists, this throws a NoSuchElementException
        ProblemType problemType = problemTypeService.findById(problemTypeDto.getId());
        // Get ProblemTypes of Algorithm
        Set<ProblemType> problemTypes = algorithm.getProblemTypes();
        // add new problemtype
        problemTypes.add(problemType);
        // update and return update list:
        algorithm.setProblemTypes(problemTypes);
        Set<ProblemType> updatedProblemTypes = algorithmService.save(algorithm).getProblemTypes();
        Set<ProblemTypeDto> problemTypeDtos = ModelMapperUtils.convertSet(updatedProblemTypes, ProblemTypeDto.class);
        CollectionModel<EntityModel<ProblemTypeDto>> resultCollection = HateoasUtils.generateCollectionModel(problemTypeDtos);
        // Fill EntityModel Links
        problemTypeAssembler.addLinks(resultCollection);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Update all references to existing problemTypes (that were previously created via a POST on /problem-types/). The values (e.g. name) of each problem-types are not changes through this operation. If one of the problemType doesn't exist yet, a 404 error is thrown.")
    @PutMapping("/{algoId}/" + Constants.PROBLEM_TYPES)
    public HttpEntity<CollectionModel<EntityModel<ProblemTypeDto>>> updateProblemTypes(@PathVariable UUID algoId, @Valid @RequestBody List<ProblemTypeDto> problemTypeDtos) {
        Algorithm algorithm = algorithmService.findById(algoId);
        // access publication in db to throw NoSuchElementException if it doesn't exist
        Set<ProblemType> newProblemTypes = new HashSet<>();
        problemTypeDtos.forEach(problemTypeDto -> {
            ProblemType problemType = problemTypeService.findById(problemTypeDto.getId());
            newProblemTypes.add(problemType);
        });
        // update and return update list:
        algorithm.setProblemTypes(newProblemTypes);
        Set<ProblemType> updatedProblemTypes = algorithmService.save(algorithm).getProblemTypes();
        Set<ProblemTypeDto> problemTypeDtosResult = ModelMapperUtils.convertSet(updatedProblemTypes, ProblemTypeDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<ProblemTypeDto>> resultCollection = HateoasUtils.generateCollectionModel(problemTypeDtosResult);
        // Fill EntityModel Links
        problemTypeAssembler.addLinks(resultCollection);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    })

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Get the problem types for an algorithm")
    @GetMapping("/{algoId}/" + Constants.APPLICATION_AREAS)
    public HttpEntity<CollectionModel<EntityModel<ApplicationAreaDto>>> getApplicationAreas(@PathVariable UUID algoId) {
        Algorithm algorithm = algorithmService.findById(algoId);
        // Get ProblemTypes of Algorithm
        Set<ApplicationArea> applicationAreas = algorithm.getApplicationAreas();
        // Translate Entity to DTO
        Set<ApplicationAreaDto> dtoTypes = ModelMapperUtils.convertSet(applicationAreas, ApplicationAreaDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<ApplicationAreaDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTypes);
        // Fill EntityModel Links
        applicationAreaAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addApplicationAreaLink(resultCollection, algoId);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    })

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Get the problem types for an algorithm")
    @GetMapping("/{algoId}/" + Constants.APPLICATION_AREAS + "/{applicationAreaId}")
    public HttpEntity<EntityModel<ApplicationAreaDto>> getApplicationArea(@PathVariable UUID algoId, @PathVariable UUID applicationAreaId) {
        ApplicationArea applicationArea = applicationAreaService.findById(applicationAreaId);

        // Convert To EntityModel
        EntityModel<ApplicationAreaDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(applicationArea, ApplicationAreaDto.class));
        // Fill EntityModel with links
        applicationAreaAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404", description = "problem type or algorithm does not exists in the database")}, description = "Add a reference to an existing applicationArea (that was previously created via a POST on /application-area/. If the problemType doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{algoId}/" + Constants.APPLICATION_AREAS)
    public HttpEntity<CollectionModel<EntityModel<ApplicationAreaDto>>> add(@PathVariable UUID algoId, @Valid @RequestBody ApplicationAreaDto applicationAreaDto) {
        Algorithm algorithm = algorithmService.findById(algoId);
        // access stored pattern relation -> if it does not exists, this throws a NoSuchElementException
        ApplicationArea applicationArea = applicationAreaService.findById(applicationAreaDto.getId());
        // Get ProblemTypes of Algorithm
        Set<ApplicationArea> applicationAreas = algorithm.getApplicationAreas();
        // add new problemtype
        applicationAreas.add(applicationArea);
        // update and return update list:
        algorithm.setApplicationAreas(applicationAreas);
        Set<ApplicationArea> updatedApplicationAreas = algorithmService.save(algorithm).getApplicationAreas();
        Set<ApplicationAreaDto> dtos = ModelMapperUtils.convertSet(updatedApplicationAreas, ApplicationAreaDto.class);
        CollectionModel<EntityModel<ApplicationAreaDto>> resultCollection = HateoasUtils.generateCollectionModel(dtos);
        // Fill EntityModel Links
        applicationAreaAssembler.addLinks(resultCollection);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")}, description = "Get pattern relations for an algorithms.")
    @GetMapping("/{algoId}/" + Constants.PATTERN_RELATIONS)
    public HttpEntity<CollectionModel<EntityModel<PatternRelationDto>>> getPatternRelations(@PathVariable UUID algoId) {
        Algorithm algorithm = algorithmService.findById(algoId);
        // Get PatternRelations of Algorithm
        Set<PatternRelation> patternRelations = algorithm.getRelatedPatterns();
        // Translate Entity to DTO
        Set<PatternRelationDto> dtoTypes = ModelMapperUtils.convertSet(patternRelations, PatternRelationDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<PatternRelationDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTypes);
        // Fill EntityModel Links
        patternRelationAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addPatternRelationLink(resultCollection, algoId);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404", description = "pattern relation or algorithm doesn't exist in the database")}, description = "Add a reference to an existing pattern relation (that was previously created via a POST on /pattern-relation/. If the pattern relation doesn't exist yet, a 404 error is thrown.\"")
    @PostMapping("/{algoId}/" + Constants.PATTERN_RELATIONS)
    public HttpEntity<CollectionModel<EntityModel<PatternRelationDto>>> addPatternRelations(@PathVariable UUID algoId, @Valid @RequestBody PatternRelationDto patternRelationDto) {
        Algorithm algorithm = algorithmService.findById(algoId);
        PatternRelation patternRelation = ModelMapperUtils.convert(patternRelationDto, PatternRelation.class);

        // access stored pattern relation -> if it does not exists, this throws a NoSuchElementException
        PatternRelation loadedPatternRelation = patternRelationService.findById(patternRelation.getId());

        // Get ProblemTypes of Algorithm
        Set<PatternRelation> relatedPatterns = algorithm.getRelatedPatterns();
        // add new problemtype
        relatedPatterns.add(loadedPatternRelation);
        // update and return update list:
        algorithm.setRelatedPatterns(relatedPatterns);
        Set<PatternRelation> updatedPatternRelations = algorithmService.save(algorithm).getRelatedPatterns();
        Set<PatternRelationDto> result = ModelMapperUtils.convertSet(updatedPatternRelations, PatternRelationDto.class);
        CollectionModel<EntityModel<PatternRelationDto>> resultCollection = HateoasUtils.generateCollectionModel(result);
        // Fill EntityModel Links
        patternRelationAssembler.addLinks(resultCollection);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201")}, description = "Update all references to existing pattern relations (that were previously created via a POST on /pattern-relations/). The values (e.g. type) of each pattern relations are not changes through this operation. If one of the pattern relations doesn't exist yet, a 404 error is thrown.")
    @PutMapping("/{algoId}/" + Constants.PATTERN_RELATIONS)
    public HttpEntity<CollectionModel<EntityModel<PatternRelationDto>>> updatePatternRelations(@PathVariable UUID algoId, @Valid @RequestBody List<PatternRelationDto> patternRelationDtos) {
        Algorithm algorithm = algorithmService.findById(algoId);
        // access publication in db to throw NoSuchElementException if it doesn't exist
        Set<PatternRelation> newPatternRelations = new HashSet<>();
        patternRelationDtos.forEach(patternRelationDto -> {
            PatternRelation patternRelation = patternRelationService.findById(patternRelationDto.getId());
            newPatternRelations.add(patternRelation);
        });
        // update and return update list:
        algorithm.setRelatedPatterns(newPatternRelations);
        Set<PatternRelation> updatedPatternRelations = algorithmService.save(algorithm).getRelatedPatterns();
        Set<PatternRelationDto> patternRelationseDtosResult = ModelMapperUtils.convertSet(updatedPatternRelations, PatternRelationDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<PatternRelationDto>> resultCollection = HateoasUtils.generateCollectionModel(patternRelationseDtosResult);
        // Fill EntityModel Links
        patternRelationAssembler.addLinks(resultCollection);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Retrieve all relations for an algorithm.")
    @GetMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS)
    public HttpEntity<CollectionModel<EntityModel<AlgorithmRelationDto>>> getAlgorithmRelations(
            @PathVariable UUID algoId) {
        // get AlgorithmRelations of Algorithm
        Set<AlgorithmRelation> algorithmRelations = algorithmService.getAlgorithmRelations(algoId);
        // Get AlgorithmRelationDTOs of Algorithm
        Set<AlgorithmRelationDto> dtoALgorithmRelation = ModelMapperUtils.convertSet(algorithmRelations,
                AlgorithmRelationDto.class);
        // Generate CollectionModel
        CollectionModel<EntityModel<AlgorithmRelationDto>> resultCollection = HateoasUtils
                .generateCollectionModel(dtoALgorithmRelation);
        // Fill EntityModel Links
        algorithmRelationAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addAlgorithmRelationLink(resultCollection, algoId);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Add an relation to the algorithm")
    @PutMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS)
    public HttpEntity<EntityModel<AlgorithmRelationDto>> updateAlgorithmRelation(@PathVariable UUID algoId,
                                                                                 @Valid @RequestBody AlgorithmRelationDto relation) {
        LOG.debug("Post to add algorithm relation received.");

        AlgorithmRelation algorithmRelation = algorithmService.addOrUpdateAlgorithmRelation(algoId,
                ModelMapperUtils.convert(relation, AlgorithmRelation.class));
        AlgorithmRelationDto dtoOutput = ModelMapperUtils.convert(algorithmRelation, AlgorithmRelationDto.class);
        EntityModel<AlgorithmRelationDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
        algorithmRelationAssembler.addLinks(entityDto);
        return new ResponseEntity<>(entityDto, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Delete a relation of the algorithm")
    @DeleteMapping("/{algoId}/" + Constants.ALGORITHM_RELATIONS + "/{relationId}")
    public HttpEntity<AlgorithmRelationDto> deleteAlgorithmRelation(@PathVariable UUID algoId,
                                                                    @PathVariable UUID relationId) {
        LOG.debug("Delete received to remove algorithm relation with id {}.", relationId);
        algorithmService.deleteAlgorithmRelation(algoId, relationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Retrieve the required computing resources of an algorithm")
    @GetMapping("/{algoId}/" + Constants.COMPUTING_RESOURCES)
    public ResponseEntity<PagedModel<EntityModel<ComputingResourceDto>>> getComputingResources(
            @PathVariable UUID algoId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        var resources = computingResourceService.findAllResourcesByAlgorithmId(algoId, RestUtils.getPageableFromRequestParams(page, size));
        var typeDtoes = ModelMapperUtils.convertPage(resources, ComputingResourceDto.class);
        var pagedModel = computingResourcePaginationAssembler.toModel(typeDtoes);
        computingResourceAssembler.addLinks(pagedModel);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Add a computing resource (e.g. a certain number of qubits) that is requiered by an algorithm")
    @PostMapping("/{algoId}/" + Constants.COMPUTING_RESOURCES)
    public ResponseEntity<EntityModel<AlgorithmDto>> addComputingResource(
            @PathVariable UUID algoId,
            @Valid @RequestBody ComputingResourceDto resourceDto
    ) {
        var algorithm = algorithmService.findById(algoId);
        var resource = ModelMapperUtils.convert(resourceDto, ComputingResource.class);
        var updatedAlgorithm = computingResourceService.addComputingResourceToAlgorithm(
                algorithm,
                resource
        );
        EntityModel<AlgorithmDto> algoDto = HateoasUtils.generateEntityModel(
                ModelMapperUtils.convert(updatedAlgorithm, AlgorithmDto.class));
        algorithmAssembler.addLinks(algoDto);
        return ResponseEntity.ok(algoDto);
    }
}
