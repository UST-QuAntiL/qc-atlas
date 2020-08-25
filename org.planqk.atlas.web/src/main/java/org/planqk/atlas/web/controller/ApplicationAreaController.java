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

import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.linkassembler.ApplicationAreaAssembler;
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

@Tag(name = Constants.TAG_APPLICATION_AREAS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.APPLICATION_AREAS)
@AllArgsConstructor
@Slf4j
public class ApplicationAreaController {

    private final ApplicationAreaService applicationAreaService;
    private final ApplicationAreaAssembler applicationAreaAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "")
    @GetMapping()
    @ListParametersDoc()
    public ResponseEntity<PagedModel<EntityModel<ApplicationAreaDto>>> getApplicationAreas(
            @Parameter(hidden = true) ListParameters listParameters) {
        return ResponseEntity.ok(applicationAreaAssembler
                .toModel(applicationAreaService.findAll(listParameters.getPageable(), listParameters.getSearch())));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
    }, description = "Custom ID will be ignored.")
    @PostMapping()
    public ResponseEntity<EntityModel<ApplicationAreaDto>> createApplicationArea(
            @Validated(ValidationGroups.Create.class) @RequestBody ApplicationAreaDto applicationAreaDto) {
        var savedApplicationArea = applicationAreaService.create(
                ModelMapperUtils.convert(applicationAreaDto, ApplicationArea.class));
        return new ResponseEntity<>(applicationAreaAssembler.toModel(savedApplicationArea), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Application area with given id does not exist"),
    }, description = "Custom ID will be ignored.")
    @PutMapping
    public ResponseEntity<EntityModel<ApplicationAreaDto>> updateApplicationArea(
            @Validated(ValidationGroups.Update.class) @RequestBody ApplicationAreaDto applicationAreaDto) {
        var updatedApplicationArea = applicationAreaService.update(
                ModelMapperUtils.convert(applicationAreaDto, ApplicationArea.class));
        return ResponseEntity.ok(applicationAreaAssembler.toModel(updatedApplicationArea));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Application area with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{applicationAreaId}")
    public ResponseEntity<Void> deleteApplicationArea(@PathVariable UUID applicationAreaId) {
        applicationAreaService.delete(applicationAreaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Application area with given id doesn't exist")
    }, description = "")
    @GetMapping("/{applicationAreaId}")
    public ResponseEntity<EntityModel<ApplicationAreaDto>> getApplicationArea(@PathVariable UUID applicationAreaId) {
        ApplicationArea applicationArea = applicationAreaService.findById(applicationAreaId);
        return ResponseEntity.ok(applicationAreaAssembler.toModel(applicationArea));
    }
}
