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

import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.linkassembler.PatternRelationAssembler;
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

@Tag(name = Constants.TAG_PATTERN_RELATION)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.PATTERN_RELATIONS)
@AllArgsConstructor
@Slf4j
public class PatternRelationController {

    private final PatternRelationService patternRelationService;
    private final PatternRelationAssembler patternRelationAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all pattern relations")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<PatternRelationDto>>> getPatternRelations(
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
    @PostMapping
    public ResponseEntity<EntityModel<PatternRelationDto>> createPatternRelation(
            @Validated( {ValidationGroups.Create.class}) @RequestBody PatternRelationDto patternRelationDto) {
        var savedPatternRelation = patternRelationService.create(
                ModelMapperUtils.convert(patternRelationDto, PatternRelation.class));
        return new ResponseEntity<>(patternRelationAssembler.toModel(savedPatternRelation), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "PatternRelation doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404",
                    description = "Pattern relation or algorithm with given id doesn't exist")
    }, description = "Update a reference to a pattern. " +
            "Custom ID will be ignored. For pattern relation type only ID is required, " +
            "other pattern relation type attributes will not change.")
    @PutMapping("/{patternRelationId}")
    public ResponseEntity<EntityModel<PatternRelationDto>> updatePatternRelation(
            @PathVariable UUID patternRelationId,
            @Validated( {ValidationGroups.Update.class}) @RequestBody PatternRelationDto patternRelationDto) {
        patternRelationDto.setId(patternRelationId);
        var savedPatternRelation = patternRelationService.update(
                ModelMapperUtils.convert(patternRelationDto, PatternRelation.class));
        return ResponseEntity.ok(patternRelationAssembler.toModel(savedPatternRelation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @GetMapping("/{patternRelationId}")
    public ResponseEntity<EntityModel<PatternRelationDto>> getPatternRelation(@PathVariable UUID patternRelationId) {
        var patternRelation = patternRelationService.findById(patternRelationId);
        return ResponseEntity.ok(patternRelationAssembler.toModel(patternRelation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Pattern relation with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{patternRelationId}")
    public ResponseEntity<Void> deletePatternRelation(@PathVariable UUID patternRelationId) {
        patternRelationService.delete(patternRelationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
