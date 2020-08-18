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

import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.PublicationMixin;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.UpdateValidationGroup;
import org.planqk.atlas.web.utils.ValidationGroup;

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
@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_PUBLICATION)
@Slf4j
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@AllArgsConstructor
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.PUBLICATIONS)
public class PublicationController {

    private final PublicationService publicationService;
    private final AlgorithmService algorithmService;
    private final ImplementationService implementationService;
    private final PublicationAssembler publicationAssembler;
    private final AlgorithmAssembler algorithmAssembler;
    private final ImplementationAssembler implementationAssembler;
    private final PublicationMixin publicationMixIn;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "")
    @GetMapping()
    @ListParametersDoc()
    public HttpEntity<PagedModel<EntityModel<PublicationDto>>> getPublications(
            @Parameter(hidden = true) ListParameters listParameters) {
        var entities = publicationService.findAll(listParameters.getPageable(), listParameters.getSearch());
        return ResponseEntity.ok(publicationAssembler.toModel(entities));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @PostMapping()
    public HttpEntity<EntityModel<PublicationDto>> createPublication(
            @Validated(ValidationGroup.class) @RequestBody PublicationDto publicationDto) {
        Publication publication = publicationService.save(ModelMapperUtils.convert(publicationDto, Publication.class));
        return new ResponseEntity<>(publicationAssembler.toModel(publication), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Custom ID will be ignored.")
    @PutMapping()
    public HttpEntity<EntityModel<PublicationDto>> updatePublication(
            @Validated({ValidationGroup.class, UpdateValidationGroup.class}) @RequestBody PublicationDto pub) {
        Publication publication = publicationService.update(pub.getId(), ModelMapperUtils.convert(pub, Publication.class));
        return new ResponseEntity<>(publicationAssembler.toModel(publication), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<PublicationDto>> getPublication(@PathVariable UUID id) {
        Publication publication = publicationService.findById(id);
        return new ResponseEntity<>(publicationAssembler.toModel(publication), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Publication with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deletePublication(@PathVariable UUID id) {
        publicationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @GetMapping("/{id}/" + Constants.ALGORITHMS)
    public HttpEntity<CollectionModel<EntityModel<AlgorithmDto>>> getPublicationAlgorithms(@PathVariable UUID id) {
        var publication = publicationService.findById(id);
        return new ResponseEntity<>(algorithmAssembler.toModel(publication.getAlgorithms()), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Get a specific referenced algorithm of a publication.")
    @GetMapping("/{id}/" + Constants.ALGORITHMS + "/{algoId}")
    public HttpEntity<EntityModel<AlgorithmDto>> getPublicationAlgorithm(
            @PathVariable UUID id,
            @PathVariable UUID algoId) {
        publicationService.findById(id);
        return ResponseEntity.ok(algorithmAssembler.toModel(algorithmService.findById(algoId)));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content, description = "algorithm or publication does not exist")
    }, description = "Add a reference to an existing algorithm (that was previously created via a POST on /algorithms/). " +
            "Custom ID will be ignored. For algorithm only ID is required, other algorithm attributes will not change. " +
            "If the algorithm doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{id}/" + Constants.ALGORITHMS + "/{algoId}")
    public HttpEntity<CollectionModel<EntityModel<AlgorithmDto>>> linkAlgorithmWithPublication(
            @PathVariable UUID id,
            @PathVariable UUID algoId) {
        var algorithm = algorithmService.findById(algoId);
        publicationMixIn.addPublication(algorithm, id);
        algorithmService.save(algorithm);
        return ResponseEntity.ok(algorithmAssembler.toModel(publicationService.findById(id).getAlgorithms()));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description =
                    "Algorithm or publication with given ids do not exist or no relation between algorithm and publication")
    }, description = "Delete a reference to a algorithm of the publication.")
    @DeleteMapping("/{id}/" + Constants.ALGORITHMS + "/{algoId}")
    public HttpEntity<Void> unlinkAlgorithmFromPublication(
            @PathVariable UUID id,
            @PathVariable UUID algoId) {
        var algorithm = algorithmService.findById(algoId);
        publicationMixIn.unlinkPublication(algorithm, id);
        algorithmService.save(algorithm);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @GetMapping("/{id}/" + Constants.IMPLEMENTATIONS)
    public HttpEntity<CollectionModel<EntityModel<ImplementationDto>>> getPublicationImplementations(
            @PathVariable UUID id) {
        var publication = publicationService.findById(id);
        return new ResponseEntity<>(implementationAssembler.toModel(publication.getImplementations()), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Get a specific referenced implementation of a publication.")
    @GetMapping("/{id}/" + Constants.IMPLEMENTATIONS + "/{implId}")
    public HttpEntity<EntityModel<ImplementationDto>> getPublicationImplementation(
            @PathVariable UUID id,
            @PathVariable UUID implId) {
        publicationService.findById(id);
        return ResponseEntity.ok(implementationAssembler.toModel(implementationService.findById(implId)));
    }
}





