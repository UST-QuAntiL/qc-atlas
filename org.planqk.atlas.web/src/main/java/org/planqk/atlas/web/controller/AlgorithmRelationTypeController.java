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

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.services.AlgoRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmRelationTypeDto;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationTypeAssembler;
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

@Tag(name = Constants.TAG_ALGORITHM_RELATION_TYPE)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.ALGO_RELATION_TYPES)
@AllArgsConstructor
@Slf4j
public class AlgorithmRelationTypeController {

    private final AlgoRelationTypeService algoRelationTypeService;
    private final AlgorithmRelationTypeAssembler algorithmRelationTypeAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "")
    @GetMapping
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<AlgorithmRelationTypeDto>>> getAlgorithmRelationTypes(
            @Parameter(hidden = true) ListParameters params) {
        var algorithmRelationTypes = algoRelationTypeService.findAll(params.getPageable());
        return ResponseEntity.ok(algorithmRelationTypeAssembler.toModel(algorithmRelationTypes));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201")
    }, description = "Custom ID will be ignored.")
    @PostMapping
    public ResponseEntity<EntityModel<AlgorithmRelationTypeDto>> createAlgorithmRelationType(
            @Validated(ValidationGroups.Create.class) @RequestBody AlgorithmRelationTypeDto AlgorithmRelationTypeDto) {
        var entityInput = ModelMapperUtils.convert(AlgorithmRelationTypeDto, AlgoRelationType.class);
        var savedAlgoRelationType = algoRelationTypeService.create(entityInput);
        return new ResponseEntity<>(algorithmRelationTypeAssembler.toModel(savedAlgoRelationType), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm relation with given id doesn't exist")
    }, description = "Custom ID will be ignored.")
    @PutMapping
    public ResponseEntity<EntityModel<AlgorithmRelationTypeDto>> updateAlgorithmRelationType(
            @Validated(ValidationGroups.Update.class) @RequestBody AlgorithmRelationTypeDto AlgorithmRelationTypeDto) {
        var entityInput = ModelMapperUtils.convert(AlgorithmRelationTypeDto, AlgoRelationType.class);
        var savedAlgoRelationType = algoRelationTypeService.update(entityInput);
        return ResponseEntity.ok(algorithmRelationTypeAssembler.toModel(savedAlgoRelationType));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm relation with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{algorithmRelationTypeId}")
    public ResponseEntity<Void> deleteAlgorithmRelationType(@PathVariable UUID algorithmRelationTypeId) {
        algoRelationTypeService.delete(algorithmRelationTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm relation with given id doesn't exist")
    }, description = "")
    @GetMapping("/{algorithmRelationTypeId}")
    public ResponseEntity<EntityModel<AlgorithmRelationTypeDto>> getAlgorithmRelationType(@PathVariable UUID algorithmRelationTypeId) {
        var algoRelationType = algoRelationTypeService.findById(algorithmRelationTypeId);
        return ResponseEntity.ok(algorithmRelationTypeAssembler.toModel(algoRelationType));
    }
}
