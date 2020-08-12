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

import javax.validation.Valid;

import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.linkassembler.ApplicationAreaAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_APPLICATION_AREAS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.APPLICATION_AREAS)
@AllArgsConstructor
@Slf4j
public class ApplicationAreaController {

    private final ApplicationAreaService applicationAreaService;
    private final ApplicationAreaAssembler applicationAreaAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "201")
    }, description = "Custom ID will be ignored.")
    @PostMapping()
    public HttpEntity<EntityModel<ApplicationAreaDto>> createApplicationArea(
            @Valid @RequestBody ApplicationAreaDto applicationAreaDto) {
        var entityInput = ModelMapperUtils.convert(applicationAreaDto, ApplicationArea.class);
        var savedEntity = applicationAreaService.save(entityInput);
        return new ResponseEntity<>(applicationAreaAssembler.toModel(savedEntity), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Custom ID will be ignored.")
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<ApplicationAreaDto>> updateApplicationArea(
            @PathVariable UUID id,
            @Valid @RequestBody ApplicationAreaDto applicationAreaDto) {
        var entityInput = ModelMapperUtils.convert(applicationAreaDto, ApplicationArea.class);
        var savedEntity = applicationAreaService.update(id, entityInput);
        return ResponseEntity.ok(applicationAreaAssembler.toModel(savedEntity));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Application area with given id doesn't exist")
    }, description = "")
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deleteApplicationArea(@PathVariable UUID id) {
        ApplicationArea applicationArea = applicationAreaService.findById(id);
        applicationArea.getAlgorithms().forEach(algorithm -> algorithm.removeApplicationArea(applicationArea));
        applicationAreaService.delete(applicationArea);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "")
    @GetMapping()
    @ListParametersDoc()
    public HttpEntity<PagedModel<EntityModel<ApplicationAreaDto>>> getApplicationAreas(
            @Parameter(hidden = true) ListParameters listParameters) {
        return ResponseEntity.ok(applicationAreaAssembler
                .toModel(applicationAreaService.findAll(listParameters.getPageable(), listParameters.getSearch())));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "")
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<ApplicationAreaDto>> getApplicationAreaById(@PathVariable UUID id) {
        ApplicationArea applicationArea = applicationAreaService.findById(id);
        return ResponseEntity.ok(applicationAreaAssembler.toModel(applicationArea));
    }
}
