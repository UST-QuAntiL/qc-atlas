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

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.linkassembler.ProblemTypeAssembler;
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

@Tag(name = Constants.TAG_PROBLEM_TYPE)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.PROBLEM_TYPES)
@ApiVersion("v1")
@AllArgsConstructor
@Slf4j
public class ProblemTypeController {

    private final ProblemTypeService problemTypeService;
    private final ProblemTypeAssembler problemTypeAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all problem types.")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ProblemTypeDto>>> getProblemTypes(
            @Parameter(hidden = true) ListParameters listParameters) {
        return ResponseEntity.ok(problemTypeAssembler.toModel(problemTypeService.findAll(listParameters.getPageable(), listParameters.getSearch())));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
    }, description = "Define the basic properties of an problem type.")
    @PostMapping
    public ResponseEntity<EntityModel<ProblemTypeDto>> createProblemType(
            @Validated(ValidationGroups.Create.class) @RequestBody ProblemTypeDto problemTypeDto) {
        var savedProblemType = problemTypeService.create(ModelMapperUtils.convert(problemTypeDto, ProblemType.class));
        return new ResponseEntity<>(problemTypeAssembler.toModel(savedProblemType), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404", description = "Not Found. Problem type with given ID doesn't exist.")
    }, description = "Update the basic properties of an problem type (e.g. name).")
    @PutMapping("/{problemTypeId}")
    public ResponseEntity<EntityModel<ProblemTypeDto>> updateProblemType(
            @PathVariable UUID problemTypeId,
            @Validated(ValidationGroups.Update.class) @RequestBody ProblemTypeDto problemTypeDto) {
        problemTypeDto.setId(problemTypeId);
        var updatedProblemType = problemTypeService.update(
                ModelMapperUtils.convert(problemTypeDto, ProblemType.class));
        return ResponseEntity.ok(problemTypeAssembler.toModel(updatedProblemType));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Problem type with given ID doesn't exist.")
    }, description = "Delete an problem type. " +
            "This also removes all references to other entities (e.g. algorithm).")
    @DeleteMapping("/{problemTypeId}")
    public ResponseEntity<Void> deleteProblemType(@PathVariable UUID problemTypeId) {
        problemTypeService.delete(problemTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Problem type with given ID doesn't exist.")
    }, description = "Retrieve a specific problem type and its basic properties.")
    @GetMapping("/{problemTypeId}")
    public ResponseEntity<EntityModel<ProblemTypeDto>> getProblemType(@PathVariable UUID problemTypeId) {
        ProblemType problemType = problemTypeService.findById(problemTypeId);
        return ResponseEntity.ok(problemTypeAssembler.toModel(problemType));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Problem type with given ID doesn't exist.")
    }, description = "Retrieved all parent problem types of a specific problem type. " +
            "If a problem type has not parent an empty list is returned")
    @ListParametersDoc
    @GetMapping("/{problemTypeId}/" + Constants.PROBLEM_TYPE_PARENTS)
    public ResponseEntity<CollectionModel<EntityModel<ProblemTypeDto>>> getProblemTypeParentList(
            @PathVariable UUID problemTypeId) {
        var problemTypeParentList = problemTypeService.getParentList(problemTypeId);
        return ResponseEntity.ok(problemTypeAssembler.toModel(problemTypeParentList));
    }
}
