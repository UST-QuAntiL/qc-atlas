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

import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.data.domain.Page;
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

@Tag(name = Constants.TAG_PATTERN_RELATION)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.PATTERN_RELATIONS)
@AllArgsConstructor
@Slf4j
public class PatternRelationController {

    private final PatternRelationService patternRelationService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all relations between pattern and algorithms.")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<Page<PatternRelationDto>> getPatternRelations(
            @Parameter(hidden = true) ListParameters listParameters) {
        final var patternRelations = patternRelationService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(patternRelations, PatternRelationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Algorithm or Pattern relation type with given IDs don't exist.")
    }, description = "Create a relation between a pattern and an algorithm." +
            "The pattern relation type has to be already created (e.g. via POST on /" + Constants.PATTERN_RELATION_TYPES + "). " +
            "As a result only the ID is required for the pattern relation type, other attributes will be ignored not changed.")
    @PostMapping
    public ResponseEntity<PatternRelationDto> createPatternRelation(
            @Validated( {ValidationGroups.Create.class}) @RequestBody PatternRelationDto patternRelationDto) {
        final var savedPatternRelation = patternRelationService.create(
                ModelMapperUtils.convert(patternRelationDto, PatternRelation.class));
        return new ResponseEntity<>(ModelMapperUtils.convert(savedPatternRelation, PatternRelationDto.class), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                         description = "Bad Request. Invalid request body or algorithm with given ID is not part of pattern relation."),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Algorithm, pattern relation or pattern relation type with given IDs don't exist.")
    }, description = "Update a relation between a pattern and an algorithm. " +
            "For the pattern relation type only the ID is required," +
            "other pattern relation type attributes will be ignored and not changed.")
    @PutMapping("/{patternRelationId}")
    public ResponseEntity<PatternRelationDto> updatePatternRelation(
            @PathVariable UUID patternRelationId,
            @Validated( {ValidationGroups.Update.class}) @RequestBody PatternRelationDto patternRelationDto) {
        patternRelationDto.setId(patternRelationId);
        final var savedPatternRelation = patternRelationService.update(
                ModelMapperUtils.convert(patternRelationDto, PatternRelation.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(savedPatternRelation, PatternRelationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Algorithm or pattern relation with given IDs don't exist.")
    }, description = "Delete a specific relation between a pattern and an algorithm. " +
            "The pattern relation type is not affected by this.")
    @DeleteMapping("/{patternRelationId}")
    public ResponseEntity<Void> deletePatternRelation(@PathVariable UUID patternRelationId) {
        patternRelationService.delete(patternRelationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Algorithm or pattern relation with given IDs don't exist.")
    }, description = "Retrieve a specific relation between a pattern and an algorithm.")
    @GetMapping("/{patternRelationId}")
    public ResponseEntity<PatternRelationDto> getPatternRelation(@PathVariable UUID patternRelationId) {
        final var patternRelation = patternRelationService.findById(patternRelationId);
        return ResponseEntity.ok(ModelMapperUtils.convert(patternRelation, PatternRelationDto.class));
    }
}
