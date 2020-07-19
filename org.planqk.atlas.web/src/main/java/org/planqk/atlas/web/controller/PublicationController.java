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

import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.AlgorithmMixin;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;

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
 * Controller to access and manipulate publication algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "publication")
@Slf4j
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@AllArgsConstructor
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.PUBLICATIONS)
public class PublicationController {

    private final PublicationService publicationService;
    private final PublicationAssembler publicationAssembler;
    private final AlgorithmAssembler algorithmAssembler;
    private final ImplementationAssembler implementationAssembler;
    private final AlgorithmMixin algorithmMixin;

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping()
    @ListParametersDoc()
    public HttpEntity<PagedModel<EntityModel<PublicationDto>>> getPublications(@Parameter(hidden = true) ListParameters listParameters) {
        log.debug("Get all publications");
        var entities = publicationService.findAll(listParameters.getPageable(), listParameters.getSearch());
        return ResponseEntity.ok(publicationAssembler.toModel(entities));
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")}, description = "Custom ID will be ignored.")
    @PostMapping()
    public HttpEntity<EntityModel<PublicationDto>> createPublication(@Validated @RequestBody PublicationDto publicationDto) {
        log.debug("Create publication");
        Publication publication = publicationService.save(ModelMapperUtils.convert(publicationDto, Publication.class));
        return new ResponseEntity<>(publicationAssembler.toModel(publication), HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<PublicationDto>> getPublication(@PathVariable UUID id) {
        log.debug("Get publication with id: {}", id);
        Publication publication = publicationService.findById(id);
        return new ResponseEntity<>(publicationAssembler.toModel(publication), HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")}, description = "Custom ID will be ignored.")
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<PublicationDto>> updatePublication(@PathVariable UUID id, @Validated @RequestBody PublicationDto pub) {
        log.debug("Put to update publication with id: {}", id);
        Publication publication = publicationService.update(id, ModelMapperUtils.convert(pub, Publication.class));
        return new ResponseEntity<>(publicationAssembler.toModel(publication), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Publication with given id doesn't exist")})
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deletePublication(@PathVariable UUID id) {
        log.debug("Delete to remove publication with id: {}", id);
        Publication publication = publicationService.findById(id);
        publication.getAlgorithms().forEach(algorithm -> algorithm.removePublication(publication));
        publication.getImplementations().forEach(implementation -> implementation.removePublication(publication));
        publicationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}/" + Constants.ALGORITHMS)
    public HttpEntity<CollectionModel<EntityModel<AlgorithmDto>>> getAlgorithms(@PathVariable UUID id) {
        log.debug("Get algorithms of Publication with id {}", id);
        var publication = publicationService.findById(id);
        return new ResponseEntity<>(algorithmAssembler.toModel(publication.getAlgorithms()), HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Get a specific referenced algorithm of a publication.")
    @GetMapping("/{id}/" + Constants.ALGORITHMS + "/{algoId}")
    public HttpEntity<EntityModel<AlgorithmDto>> getAlgorithm(@PathVariable UUID id, @PathVariable UUID algoId) {
        var publication = publicationService.findById(id);
        return ResponseEntity.ok(algorithmAssembler.toModel(algorithmMixin.getAlgorithm(publication, algoId)));
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404", content = @Content,
            description = "algorithm or publication does not exist")},
            description = "Add a reference to an existing algorithm (that was previously created via a POST on /algorithms/). Custom ID will be ignored. For algorithm only ID is required, other algorithm attributes will not change. If the algorithm doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{id}/" + Constants.ALGORITHMS + "/{algoId}")
    public HttpEntity<CollectionModel<EntityModel<AlgorithmDto>>> addAlgorithm(@PathVariable UUID id, @PathVariable UUID algoId) {
        var publication = publicationService.findById(id);
        algorithmMixin.addAlgorithm(publication, algoId);
        publication = publicationService.save(publication);
        return ResponseEntity.ok(algorithmAssembler.toModel(publication.getAlgorithms()));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Algorithm or publication with given ids do not exist or no relation between algorithm and publication")},
            description = "Delete a reference to a algorithm of the publication.")
    @DeleteMapping("/{id}/" + Constants.ALGORITHMS + "/{algoId}")
    public HttpEntity<Void> deleteReferenceToAlgorithm(@PathVariable UUID id, @PathVariable UUID algoId) {
        var publication = publicationService.findById(id);
        algorithmMixin.unlinkAlgorithm(publication, algoId);
        publicationService.save(publication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}/" + Constants.IMPLEMENTATIONS)
    public HttpEntity<CollectionModel<EntityModel<ImplementationDto>>> getImplementations(@PathVariable UUID id) {
        log.debug("Get implementations of Publication with id {}", id);
        Set<Implementation> implementations = publicationService.findPublicationImplementations(id);
        CollectionModel<EntityModel<ImplementationDto>> resultCollection = implementationAssembler.toModel(implementations);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }
}





