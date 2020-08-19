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

import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.services.PatternRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.PatternRelationTypeDto;
import org.planqk.atlas.web.linkassembler.PatternRelationTypeAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;
import org.planqk.atlas.web.utils.ValidationGroups;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = Constants.TAG_PATTERN_RELATION_TYPE)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.PATTERN_RELATION_TYPES)
@AllArgsConstructor
@Slf4j
public class PatternRelationTypeController {

    private final PatternRelationTypeService patternRelationTypeService;
    private final PatternRelationTypeAssembler patternRelationTypeAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400")
    }, description = "Custom ID will be ignored.")
    @PostMapping()
    public ResponseEntity<EntityModel<PatternRelationTypeDto>> createPatternRelationType(
            @Validated(ValidationGroups.Create.class) @RequestBody PatternRelationTypeDto typeDto) {
        PatternRelationType savedRelationType = patternRelationTypeService
                .save(ModelMapperUtils.convert(typeDto, PatternRelationType.class));
        return new ResponseEntity<>(patternRelationTypeAssembler.toModel(savedRelationType), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "Custom ID will be ignored.")
    @PutMapping()
    public ResponseEntity<EntityModel<PatternRelationTypeDto>> updatePatternRelationType(
            @Validated(ValidationGroups.Update.class) @RequestBody PatternRelationTypeDto typeDto) {
        var relationType = patternRelationTypeService.update(typeDto.getId(),
                ModelMapperUtils.convert(typeDto, PatternRelationType.class));
        return ResponseEntity.ok(patternRelationTypeAssembler.toModel(relationType));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "")
    @GetMapping()
    public ResponseEntity<PagedModel<EntityModel<PatternRelationTypeDto>>> getPatternRelationTypes(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        var entities = patternRelationTypeService.findAll(p);
        return ResponseEntity.ok(patternRelationTypeAssembler.toModel(entities));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    }, description = "")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PatternRelationTypeDto>> getPatternRelationType(@PathVariable UUID id) {
        var patternRelationType = patternRelationTypeService.findById(id);
        return ResponseEntity.ok(patternRelationTypeAssembler.toModel(patternRelationType));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Pattern relation type with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatternRelationType(@PathVariable UUID id) {
        patternRelationTypeService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
