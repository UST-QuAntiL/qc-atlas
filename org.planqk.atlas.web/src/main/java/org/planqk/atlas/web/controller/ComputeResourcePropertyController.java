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

import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.ComputeResourcePropertyMixin;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.linkassembler.ComputeResourcePropertyAssembler;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.planqk.atlas.web.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@Tag(name = Constants.TAG_COMPUTE_RESOURCE_PROPERTIES)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.COMPUTE_RESOURCES_PROPERTIES)
@AllArgsConstructor
@Slf4j
public class ComputeResourcePropertyController {

    private final ComputeResourcePropertyAssembler computeResourcePropertyAssembler;
    private final ComputeResourcePropertyService computeResourcePropertyService;
    private final ComputeResourcePropertyMixin computeResourcePropertyMixin;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
    }, description = "")
    @GetMapping("/{ComputeResourcePropertyId}")
    public HttpEntity<EntityModel<ComputeResourcePropertyDto>> getComputeResourceProperty(
            @PathVariable UUID ComputeResourcePropertyId) {
        var resource = computeResourcePropertyService.findById(ComputeResourcePropertyId);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(resource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")},
            description = "Update a computing resource of the algorithm. Custom ID will be ignored." +
                    "For computing resource type only ID is required, other computing resource type attributes will not change.")
    @PutMapping()
    public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> updateComputeResourceProperty(
            @Validated(ValidationGroups.Update.class) @RequestBody ComputeResourcePropertyDto resourceDto) {
        var resource = computeResourcePropertyMixin.fromDto(resourceDto);
        ValidationUtils.validateComputingResourceProperty(resource);
        var updatedResource = computeResourcePropertyService.update(resource);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(updatedResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Computing resource with given id doesn't exist"),
    }, description = "")
    @DeleteMapping("/{ComputeResourcePropertyId}")
    public HttpEntity<Void> deleteComputeResourceProperty(
            @PathVariable UUID ComputeResourcePropertyId) {
        computeResourcePropertyService.delete(ComputeResourcePropertyId);
        return ResponseEntity.noContent().build();
    }

}
