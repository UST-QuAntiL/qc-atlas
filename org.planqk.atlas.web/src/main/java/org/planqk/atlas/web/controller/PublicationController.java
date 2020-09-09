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
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final PublicationAssembler publicationAssembler;
    private final AlgorithmService algorithmService;
    private final AlgorithmAssembler algorithmAssembler;
    private final ImplementationService implementationService;
    private final ImplementationAssembler implementationAssembler;
    private final LinkingService linkingService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<PublicationDto>>> getPublications(
            @Parameter(hidden = true) ListParameters listParameters) {
        var entities = publicationService.findAll(listParameters.getPageable(), listParameters.getSearch());
        return ResponseEntity.ok(publicationAssembler.toModel(entities));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @PostMapping
    public ResponseEntity<EntityModel<PublicationDto>> createPublication(
            @Validated(ValidationGroups.Create.class) @RequestBody PublicationDto publicationDto) {
        Publication publication = publicationService.create(ModelMapperUtils.convert(publicationDto, Publication.class));
        return new ResponseEntity<>(publicationAssembler.toModel(publication), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @PutMapping("/{publicationId}")
    public ResponseEntity<EntityModel<PublicationDto>> updatePublication(
            @PathVariable UUID publicationId,
            @Validated(ValidationGroups.Update.class) @RequestBody PublicationDto publicationDto) {
        publicationDto.setId(publicationId);
        Publication publication = publicationService.update(
                ModelMapperUtils.convert(publicationDto, Publication.class));
        return new ResponseEntity<>(publicationAssembler.toModel(publication), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Retrieve a publication")
    @GetMapping("/{publicationId}")
    public ResponseEntity<EntityModel<PublicationDto>> getPublication(@PathVariable UUID publicationId) {
        Publication publication = publicationService.findById(publicationId);
        return new ResponseEntity<>(publicationAssembler.toModel(publication), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Publication with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{publicationId}")
    public ResponseEntity<Void> deletePublication(@PathVariable UUID publicationId) {
        publicationService.delete(publicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @ListParametersDoc
    @GetMapping("/{publicationId}/" + Constants.ALGORITHMS)
    public ResponseEntity<PagedModel<EntityModel<AlgorithmDto>>> getAlgorithmsOfPublication(
            @PathVariable UUID publicationId,
            @Parameter(hidden = true) ListParameters params) {
        var publications = publicationService.findLinkedAlgorithms(publicationId, params.getPageable());
        return ResponseEntity.ok(algorithmAssembler.toModel(publications));
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
    @PostMapping("/{publicationId}/" + Constants.ALGORITHMS)
    public ResponseEntity<Void> linkPublicationAndAlgorithm(
            @PathVariable UUID publicationId,
            @Validated({ValidationGroups.Update.class}) @RequestBody AlgorithmDto algorithmDto) {
        linkingService.linkAlgorithmAndPublication(algorithmDto.getId(), publicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm or publication with given IDs don't exist or " +
                    "no relation between them exists")
    }, description = "Delete a reference to a publication of an algorithm. The reference has to be previously created " +
            "via a POST on /" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.PUBLICATIONS + "/{publicationId}).")
    @DeleteMapping("/{publicationId}/" + Constants.ALGORITHMS + "/{algorithmId}")
    public ResponseEntity<Void> unlinkPublicationAndAlgorithm(
            @PathVariable UUID algorithmId,
            @PathVariable UUID publicationId) {
        linkingService.unlinkAlgorithmAndPublication(algorithmId, publicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with given ID doesn't exist")
    }, description = "Retrieve a specific algorithm of a publication.")
    @GetMapping("/{publicationId}/" + Constants.ALGORITHMS + "/{algorithmId}")
    public ResponseEntity<EntityModel<AlgorithmDto>> getAlgorithmOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID algorithmId) {
        publicationService.checkIfAlgorithmIsLinkedToPublication(publicationId, algorithmId);

        var algorithm = algorithmService.findById(algorithmId);
        return ResponseEntity.ok(algorithmAssembler.toModel(algorithm));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Retrieve all Implementations referencing a specific publication.")
    @ListParametersDoc
    @GetMapping("/{publicationId}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<PagedModel<EntityModel<ImplementationDto>>> getImplementationsOfPublication(
            @PathVariable UUID publicationId,
            @Parameter(hidden = true) ListParameters params) {
        var implementations = publicationService.findLinkedImplementations(publicationId, params.getPageable());
        return ResponseEntity.ok(implementationAssembler.toModel(implementations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Implementation doesn't exist")
    }, description = "Retrieve a specific implementation of the algorithm.")
    @GetMapping("/{publicationId}/" + Constants.IMPLEMENTATIONS + "/{implementationId}")
    public ResponseEntity<EntityModel<ImplementationDto>> getImplementationOfPublication(
            @PathVariable UUID publicationId,
            @PathVariable UUID implementationId) {
        publicationService.checkIfImplementationIsLinkedToPublication(publicationId, implementationId);

        var implementation = implementationService.findById(implementationId);
        return ResponseEntity.ok(implementationAssembler.toModel(implementation));
    }
}





