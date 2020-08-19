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

import java.util.Objects;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.PatternRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.linkassembler.PatternRelationAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_PATTERN_RELATION)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.PATTERN_RELATIONS)
@AllArgsConstructor
@Slf4j
public class PatternRelationController {

    private final AlgorithmService algorithmService;
    private final PatternRelationTypeService patternRelationTypeService;
    private final PatternRelationService patternRelationService;
    private final PatternRelationAssembler patternRelationAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all pattern relations")
    @GetMapping()
    @ListParametersDoc
    public HttpEntity<PagedModel<EntityModel<PatternRelationDto>>> getPatternRelations(
            @Parameter(hidden = true) ListParameters listParameters) {
        var patternRelations = patternRelationService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(patternRelationAssembler.toModel(patternRelations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Add a pattern relation from an algorithm to a given pattern." +
            "Custom ID will be ignored. For pattern relation type only ID is required," +
            "other pattern relation type attributes will not change.")
    @PostMapping()
    public HttpEntity<EntityModel<PatternRelationDto>> createPatternRelation(
            @Validated(ValidationGroups.Create.class) @RequestBody PatternRelationDto relationDto) {
        return new ResponseEntity<>(handlePatternRelationUpdate(relationDto, null), HttpStatus.CREATED);
    }

    @Operation(operationId = "updatePatternRelationTypeByPattern", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Update a reference to a pattern. " +
            "Custom ID will be ignored. For pattern relation type only ID is required, " +
            "other pattern relation type attributes will not change.")
    @PutMapping()
    public HttpEntity<EntityModel<PatternRelationDto>> updatePatternRelationType(
            @Validated(ValidationGroups.Update.class) @RequestBody PatternRelationDto relationDto) {
        return ResponseEntity.ok(handlePatternRelationUpdate(relationDto, relationDto.getId()));
    }

    @Operation(operationId = "getAllPatternRelationTypes", responses = {
            @ApiResponse(responseCode = "200")
    }, description = "")
    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<PatternRelationDto>>> getPatternRelationTypes(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        var entities = patternRelationService.findAll(p);
        return ResponseEntity.ok(patternRelationAssembler.toModel(entities));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<PatternRelationDto>> getPatternRelation(@PathVariable UUID id) {
        var patternRelation = patternRelationService.findById(id);
        return ResponseEntity.ok(patternRelationAssembler.toModel(patternRelation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Pattern relation with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deletePatternRelation(@PathVariable UUID id) {
        log.debug("Delete to remove PatternRelation with id: {}.", id);
        patternRelationService.findById(id);
        patternRelationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private EntityModel<PatternRelationDto> handlePatternRelationUpdate(PatternRelationDto relationDto, UUID relationId) {
        PatternRelation patternRelation = new PatternRelation();
        if (Objects.nonNull(relationId)) {
            patternRelation.setId(relationId);
        }

        // Convert Dto to PatternRelation by using content from the database
        Algorithm algorithm = algorithmService.findById(relationDto.getAlgorithm().getId());
        PatternRelationType patternRelationType = patternRelationTypeService.findById(relationDto.getPatternRelationType().getId());
        patternRelation.setAlgorithm(algorithm);
        patternRelation.setPattern(relationDto.getPattern());
        patternRelation.setDescription(relationDto.getDescription());
        patternRelation.setPatternRelationType(patternRelationType);

        // Store and return PatternRelation
        PatternRelation savedRelation = patternRelationService.save(patternRelation);
        return patternRelationAssembler.toModel(savedRelation);
    }
}
