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

import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.services.AlgorithmRelationService;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationAssembler;
import org.planqk.atlas.web.utils.ControllerValidationUtils;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

@Tag(name = Constants.TAG_ALGORITHM)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHMS + "/{algorithmId}/" + Constants.ALGORITHM_RELATIONS)
@ApiVersion("v1")
@AllArgsConstructor
@Slf4j
public class AlgorithmRelationController {

    private final AlgorithmRelationService algorithmRelationService;
    private final AlgorithmRelationAssembler algorithmRelationAssembler;

    private final AlgorithmService algorithmService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Algorithm with given ID doesn't exist.")
    }, description = "Retrieve all relations of an algorithm.")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<AlgorithmRelationDto>>> getAlgorithmRelationsOfAlgorithm(
            @PathVariable UUID algorithmId,
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<AlgorithmRelation> algorithmRelations = algorithmService.findLinkedAlgorithmRelations(algorithmId, listParameters.getPageable());
        return ResponseEntity.ok(algorithmRelationAssembler.toModel(algorithmRelations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or algorithm relation type with given IDs don't exist.")
    }, description = "Create a relation between two algorithms." +
            "The algorithm relation type has to be already created (e.g. via POST on /" + Constants.ALGORITHM_RELATION_TYPES + "). " +
            "As a result only the ID is required for the algorithm relation type, other attributes will be ignored not changed.")
    @PostMapping
    public ResponseEntity<EntityModel<AlgorithmRelationDto>> createAlgorithmRelation(
            @PathVariable UUID algorithmId,
            @Validated(ValidationGroups.Create.class) @RequestBody AlgorithmRelationDto algorithmRelationDto) {
        ControllerValidationUtils.checkIfAlgorithmIsInAlgorithmRelationDTO(algorithmId, algorithmRelationDto);

        var savedAlgorithmRelation = algorithmRelationService.create(
                ModelMapperUtils.convert(algorithmRelationDto, AlgorithmRelation.class));
        return new ResponseEntity<>(algorithmRelationAssembler.toModel(savedAlgorithmRelation), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm, algorithm relation or algorithm relation type with given IDs don't exist.")
    }, description = "Update a relation between two algorithms. " +
            "For the algorithm relation type only the ID is required," +
            "other algorithm relation type attributes will be ignored and not changed.")
    @PutMapping("/{algorithmRelationId}")
    public ResponseEntity<EntityModel<AlgorithmRelationDto>> updateAlgorithmRelation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID algorithmRelationId,
            @Validated(ValidationGroups.Update.class) @RequestBody AlgorithmRelationDto algorithmRelationDto) {
        ControllerValidationUtils.checkIfAlgorithmIsInAlgorithmRelationDTO(algorithmId, algorithmRelationDto);
        algorithmRelationService.checkIfAlgorithmIsInAlgorithmRelation(algorithmId, algorithmRelationId);

        algorithmRelationDto.setId(algorithmRelationId);
        var savedAlgorithmRelation = algorithmRelationService.update(
                ModelMapperUtils.convert(algorithmRelationDto, AlgorithmRelation.class));
        return ResponseEntity.ok(algorithmRelationAssembler.toModel(savedAlgorithmRelation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or algorithm relation with given IDs don't exist.")
    }, description = "Delete a specific relation between a two algorithms. " +
            "The algorithm relation type is not affected by this.")
    @DeleteMapping("/{algorithmRelationId}")
    public ResponseEntity<Void> deleteAlgorithmRelation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID algorithmRelationId) {
        algorithmRelationService.checkIfAlgorithmIsInAlgorithmRelation(algorithmId, algorithmRelationId);

        algorithmRelationService.delete(algorithmRelationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Algorithm or algorithm relation with given IDs don't exist.")
    }, description = "Retrieve a specific relation between two algorithms.")
    @GetMapping("/{algorithmRelationId}")
    public ResponseEntity<EntityModel<AlgorithmRelationDto>> getAlgorithmRelation(
            @PathVariable UUID algorithmId,
            @PathVariable UUID algorithmRelationId) {
        algorithmRelationService.checkIfAlgorithmIsInAlgorithmRelation(algorithmId, algorithmRelationId);

        var algorithmRelation = algorithmRelationService.findById(algorithmRelationId);
        return ResponseEntity.ok(algorithmRelationAssembler.toModel(algorithmRelation));
    }
}
