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

import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
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

@Tag(name = Constants.TAG_APPLICATION_AREAS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.APPLICATION_AREAS)
@AllArgsConstructor
@Slf4j
public class ApplicationAreaController {

    private final ApplicationAreaService applicationAreaService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all application areas")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<Page<ApplicationAreaDto>> getApplicationAreas(
            @Parameter(hidden = true) ListParameters listParameters) {
        return ResponseEntity.ok(ModelMapperUtils
                .convertPage(applicationAreaService.findAll(listParameters.getPageable(), listParameters.getSearch()), ApplicationAreaDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
    }, description = "Define the basic properties of an application area.")
    @PostMapping
    public ResponseEntity<ApplicationAreaDto> createApplicationArea(
            @Validated(ValidationGroups.Create.class) @RequestBody ApplicationAreaDto applicationAreaDto) {
        final var savedApplicationArea = applicationAreaService.create(
                ModelMapperUtils.convert(applicationAreaDto, ApplicationArea.class));
        return new ResponseEntity<>(ModelMapperUtils.convert(savedApplicationArea, ApplicationAreaDto.class), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Application area with given ID does not exist"),
    }, description = "Update the basic properties of an application area (e.g. name).")
    @PutMapping("/{applicationAreaId}")
    public ResponseEntity<ApplicationAreaDto> updateApplicationArea(
            @PathVariable UUID applicationAreaId,
            @Validated(ValidationGroups.Update.class) @RequestBody ApplicationAreaDto applicationAreaDto) {
        applicationAreaDto.setId(applicationAreaId);
        final var updatedApplicationArea = applicationAreaService.update(
                ModelMapperUtils.convert(applicationAreaDto, ApplicationArea.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(updatedApplicationArea, ApplicationAreaDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Application area with given ID doesn't exist")
    }, description = "Delete an application area. This removes the application area from all algorithms it is references in.")
    @DeleteMapping("/{applicationAreaId}")
    public ResponseEntity<Void> deleteApplicationArea(@PathVariable UUID applicationAreaId) {
        applicationAreaService.delete(applicationAreaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Application area with given ID doesn't exist")
    }, description = "Retrieve a specific application area and its basic properties.")
    @GetMapping("/{applicationAreaId}")
    public ResponseEntity<ApplicationAreaDto> getApplicationArea(@PathVariable UUID applicationAreaId) {
        final ApplicationArea applicationArea = applicationAreaService.findById(applicationAreaId);
        return ResponseEntity.ok(ModelMapperUtils.convert(applicationArea, ApplicationAreaDto.class));
    }
}
