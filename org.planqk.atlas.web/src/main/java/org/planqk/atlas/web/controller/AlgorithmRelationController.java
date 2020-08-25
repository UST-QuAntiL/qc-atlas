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
import org.planqk.atlas.core.services.AlgoRelationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_ALGORITHM_RELATIONS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.ALGORITHM_RELATIONS)
@AllArgsConstructor
@Slf4j
public class AlgorithmRelationController {

    private final AlgoRelationService algoRelationService;
    private final AlgorithmRelationAssembler algorithmRelationAssembler;

//    @Operation(responses = {
//            @ApiResponse(responseCode = "200")
//    }, description = "")
//    @GetMapping
//    @ListParametersDoc
//    public ResponseEntity<PagedModel<EntityModel<AlgorithmRelationDto>>> getAlgorithmRelations(
//            @Parameter(hidden = true) ListParameters params) {
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201")
    }, description = "Custom ID will be ignored.")
    @PostMapping
    public ResponseEntity<EntityModel<AlgorithmRelationDto>> createAlgorithmRelation(
            @Validated(ValidationGroups.Create.class) @RequestBody AlgorithmRelationDto algorithmRelationDto) {
        var entityInput = ModelMapperUtils.convert(algorithmRelationDto, AlgorithmRelation.class);
        var savedAlgoRelationType = algoRelationService.save(entityInput);
        return new ResponseEntity<>(algorithmRelationAssembler.toModel(savedAlgoRelationType), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm relation with given id doesn't exist")
    }, description = "Custom ID will be ignored.")
    @PutMapping
    public ResponseEntity<EntityModel<AlgorithmRelationDto>> updateAlgorithmRelation(
            @Validated(ValidationGroups.Update.class) @RequestBody AlgorithmRelationDto algorithmRelationDto) {
        var entityInput = ModelMapperUtils.convert(algorithmRelationDto, AlgorithmRelation.class);
        var savedAlgoRelationType = algoRelationService.update(entityInput);
        return ResponseEntity.ok(algorithmRelationAssembler.toModel(savedAlgoRelationType));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm relation with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{algorithmRelationId}")
    public ResponseEntity<Void> deleteAlgorithmRelation(@PathVariable UUID algorithmRelationId) {
        algoRelationService.delete(algorithmRelationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm relation with given id doesn't exist")
    }, description = "")
    @GetMapping("/{algorithmRelationId}")
    public ResponseEntity<EntityModel<AlgorithmRelationDto>> getAlgorithmRelation(@PathVariable UUID algorithmRelationId) {
        var algorithmRelation = algoRelationService.findById(algorithmRelationId);
        return ResponseEntity.ok(algorithmRelationAssembler.toModel(algorithmRelation));
    }
}
