/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.planqk.atlas.core.services.AlgorithmRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.AlgorithmRelationTypeDto;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationTypeAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = Constants.TAG_ALGORITHM_RELATION_TYPE)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHM_RELATION_TYPES)
@ApiVersion("v1")
@AllArgsConstructor
@Slf4j
public class AlgorithmRelationTypeController {

    private final AlgorithmRelationTypeService algorithmRelationTypeService;

    private final AlgorithmRelationTypeAssembler algorithmRelationTypeAssembler;

    @Operation(responses = {
        @ApiResponse(responseCode = "200")
    }, description = "Retrieve all algorithm relation types.")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<AlgorithmRelationTypeDto>>> getAlgorithmRelationTypes(
        @Parameter(hidden = true) ListParameters params) {
        final var algorithmRelationTypes = algorithmRelationTypeService.findAll(params.getPageable());
        return ResponseEntity.ok(algorithmRelationTypeAssembler.toModel(algorithmRelationTypes));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "201"),
        @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
    }, description = "Define the basic properties of an algorithm relation type.")
    @PostMapping
    public ResponseEntity<EntityModel<AlgorithmRelationTypeDto>> createAlgorithmRelationType(
        @Validated(ValidationGroups.Create.class) @RequestBody AlgorithmRelationTypeDto AlgorithmRelationTypeDto) {
        final var savedAlgorithmRelationType = algorithmRelationTypeService.create(
            ModelMapperUtils.convert(AlgorithmRelationTypeDto, AlgorithmRelationType.class));
        return new ResponseEntity<>(algorithmRelationTypeAssembler.toModel(savedAlgorithmRelationType), HttpStatus.CREATED);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
        @ApiResponse(responseCode = "404",
            description = "Not Found. Algorithm relation type with given ID doesn't exist")
    }, description = "Update the basic properties of an algorithm relation type (e.g. name).")
    @PutMapping("/{algorithmRelationTypeId}")
    public ResponseEntity<EntityModel<AlgorithmRelationTypeDto>> updateAlgorithmRelationType(
        @PathVariable UUID algorithmRelationTypeId,
        @Validated(ValidationGroups.Update.class) @RequestBody AlgorithmRelationTypeDto algorithmRelationTypeDto) {
        algorithmRelationTypeDto.setId(algorithmRelationTypeId);
        final var savedAlgorithmRelationType = algorithmRelationTypeService.update(
            ModelMapperUtils.convert(algorithmRelationTypeDto, AlgorithmRelationType.class));
        return ResponseEntity.ok(algorithmRelationTypeAssembler.toModel(savedAlgorithmRelationType));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "204"),
        @ApiResponse(responseCode = "400",
            description = "Bad Request. Algorithm relation type is still in use by at least one algorithm relation"),
        @ApiResponse(responseCode = "404",
            description = "Not Found. Algorithm relation type with given ID doesn't exist")
    }, description = "Delete an algorithm relation type.")
    @DeleteMapping("/{algorithmRelationTypeId}")
    public ResponseEntity<Void> deleteAlgorithmRelationType(@PathVariable UUID algorithmRelationTypeId) {
        algorithmRelationTypeService.delete(algorithmRelationTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404",
            description = "Not Found. Algorithm relation type with given ID doesn't exist")
    }, description = "Retrieve a specific algorithm relation type and its basic properties.")
    @GetMapping("/{algorithmRelationTypeId}")
    public ResponseEntity<EntityModel<AlgorithmRelationTypeDto>> getAlgorithmRelationType(
        @PathVariable UUID algorithmRelationTypeId) {
        final var algorithmRelationType = algorithmRelationTypeService.findById(algorithmRelationTypeId);
        return ResponseEntity.ok(algorithmRelationTypeAssembler.toModel(algorithmRelationType));
    }
}
