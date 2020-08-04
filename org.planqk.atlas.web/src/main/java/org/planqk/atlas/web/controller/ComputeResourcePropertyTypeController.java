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

import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyTypeDto;
import org.planqk.atlas.web.linkassembler.QuantumResourcePropertyTypeAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "compute-resource-properties-types")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.COMPUTING_RESOURCE_PROPERTY_TYPES)
@AllArgsConstructor
@Slf4j
public class ComputeResourcePropertyTypeController {

    private final QuantumResourcePropertyTypeAssembler assembler;
    private final ComputeResourcePropertyService service;
    private final PagedResourcesAssembler<ComputeResourcePropertyTypeDto> paginationAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Computing resource type with given id doesn't exist"),
    })
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<ComputeResourcePropertyTypeDto>> getComputingResourcePropertyType(@PathVariable UUID id) {
        var resourceType = service.findComputeResourcePropertyTypeById(id);
        return ResponseEntity.ok(assembler.toModel(resourceType));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Computing resource type with given id doesn't exist"),
    })
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deleteComputingResourcePropertyType(@PathVariable UUID id) {
        service.findComputeResourcePropertyTypeById(id);
        service.deleteComputeResourcePropertyType(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Custom ID will be ignored.")
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<ComputeResourcePropertyTypeDto>> updateComputingResourcePropertyType(@PathVariable UUID id,
                                                                                                       @Valid @RequestBody ComputeResourcePropertyTypeDto computeResourcePropertyTypeDto) {
        computeResourcePropertyTypeDto.setId(id);
        var inputEntity = ModelMapperUtils.convert(computeResourcePropertyTypeDto, ComputeResourcePropertyType.class);
        var savedEntity = service.saveComputeResourcePropertyType(inputEntity);
        return ResponseEntity.ok(assembler.toModel(savedEntity));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<ComputeResourcePropertyTypeDto>>> getResourcePropertyTypes(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        var types = service.findAllComputeResourcePropertyTypes(p);
        return ResponseEntity.ok(assembler.toModel(types));
    }

    @Operation(responses = {@ApiResponse(responseCode = "201")}, description = "Custom ID will be ignored.")
    @PostMapping()
    public HttpEntity<EntityModel<ComputeResourcePropertyTypeDto>> createComputingResourcePropertyType(
            @Valid @RequestBody ComputeResourcePropertyTypeDto resourceTypeDto) {
        var resourceType = ModelMapperUtils.convert(resourceTypeDto, ComputeResourcePropertyType.class);
        var savedResourceType = service.saveComputeResourcePropertyType(resourceType);
        return new ResponseEntity<>(assembler.toModel(savedResourceType), HttpStatus.CREATED);
    }
}
