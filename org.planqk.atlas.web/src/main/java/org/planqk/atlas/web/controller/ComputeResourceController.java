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

import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ComputeResourceService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.linkassembler.CloudServiceAssembler;
import org.planqk.atlas.web.linkassembler.ComputeResourceAssembler;
import org.planqk.atlas.web.linkassembler.ComputeResourcePropertyAssembler;
import org.planqk.atlas.web.linkassembler.SoftwarePlatformAssembler;
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
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
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

@Tag(name = Constants.TAG_EXECUTION_ENVIRONMENTS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.COMPUTE_RESOURCES)
@AllArgsConstructor
@Slf4j
public class ComputeResourceController {

    private final ComputeResourcePropertyService computeResourcePropertyService;
    private final ComputeResourcePropertyAssembler computeResourcePropertyAssembler;

    private final ComputeResourceService computeResourceService;
    private final ComputeResourceAssembler computeResourceAssembler;

    private final SoftwarePlatformAssembler softwarePlatformAssembler;

    private final CloudServiceAssembler cloudServiceAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all compute resources")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ComputeResourceDto>>> getComputeResources(
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<ComputeResource> entities;
        if (listParameters.getSearch() == null || listParameters.getSearch().isEmpty()) {
            entities = computeResourceService.findAll(listParameters.getPageable());
        } else {
            entities = computeResourceService.searchAllByName(listParameters.getSearch(), listParameters.getPageable());
        }
        return ResponseEntity.ok(computeResourceAssembler.toModel(entities));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400")
    }, description = "Define the basic properties of a compute resource. " +
            "References to sub-objects (e.g. a compute resource property) " +
            "can be added via sub-routes (e.g. /compute-resources/{id}/compute-resource-properties). " +
            "Custom ID will be ignored.")
    @PostMapping
    public ResponseEntity<EntityModel<ComputeResourceDto>> createComputeResource(
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourceDto computeResourceDto) {
        ComputeResource computeResource = computeResourceService.create(
                ModelMapperUtils.convert(computeResourceDto, ComputeResource.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(computeResourceAssembler.toModel(computeResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute Resource with given id does not exist")
    }, description = "Update the basic properties of a compute resource (e.g. name). " +
            "References to sub-objects (e.g. a compute resource property) are not updated via this operation - " +
            "use the corresponding sub-route for updating them (e.g. /compute-resources/{id}/compute-resource-properties). " +
            "Custom ID will be ignored.")
    @PutMapping("/{computeResourceId}")
    public ResponseEntity<EntityModel<ComputeResourceDto>> updateComputeResource(
            @PathVariable UUID computeResourceId,
            @Validated(ValidationGroups.Update.class) @RequestBody ComputeResourceDto computeResourceDto) {
        computeResourceDto.setId(computeResourceId);
        ComputeResource computeResource = computeResourceService.update(
                ModelMapperUtils.convert(computeResourceDto, ComputeResource.class));
        return ResponseEntity.ok(computeResourceAssembler.toModel(computeResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute Resource with given id does not exist")
    }, description = "Delete a compute resource. " +
            "This also removes all references to other entities (e.g. software platform)")
    @DeleteMapping("/{computeResourceId}")
    public ResponseEntity<Void> deleteComputeResource(
            @PathVariable UUID computeResourceId) {
        computeResourceService.delete(computeResourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute Resource with given id does not exist")
    }, description = "Retrieve a specific compute resource and its basic properties.")
    @GetMapping("/{computeResourceId}")
    public ResponseEntity<EntityModel<ComputeResourceDto>> getComputeResource(
            @PathVariable UUID computeResourceId) {
        ComputeResource computeResource = computeResourceService.findById(computeResourceId);
        return ResponseEntity.ok(computeResourceAssembler.toModel(computeResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Resource doesn't exist")
    }, description = "Get referenced software platform for a compute resource")
    @ListParametersDoc
    @GetMapping("/{computeResourceId}/" + Constants.SOFTWARE_PLATFORMS)
    public ResponseEntity<CollectionModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatformsOfComputeResource(
            @PathVariable UUID computeResourceId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var softwarePlatforms = computeResourceService.findLinkedSoftwarePlatforms(computeResourceId, listParameters.getPageable());
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatforms));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Resource doesn't exist")
    }, description = "Get referenced cloud services for a compute resource")
    @ListParametersDoc
    @GetMapping("/{computeResourceId}/" + Constants.CLOUD_SERVICES)
    public ResponseEntity<CollectionModel<EntityModel<CloudServiceDto>>> getCloudServicesOfComputeResource(
            @PathVariable UUID computeResourceId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var cloudServices = computeResourceService.findLinkedCloudServices(computeResourceId, listParameters.getPageable());
        return ResponseEntity.ok(cloudServiceAssembler.toModel(cloudServices));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute Resource with given id does not exist")
    }, description = "Get referenced compute resource properties for a compute resource.")
    @ListParametersDoc
    @GetMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
    public ResponseEntity<PagedModel<EntityModel<ComputeResourcePropertyDto>>> getComputingResourcePropertiesOfComputeResource(
            @PathVariable UUID computeResourceId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var resources = computeResourcePropertyService.findComputeResourcePropertiesOfComputeResource(computeResourceId,
                listParameters.getPageable());
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(resources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute Resource with given id does not exist")
    }, description = "Define the basic properties of a compute resource property and " +
            "add a reference to the defined compute resource property. " +
            "Custom ID will be ignored. ")
    @PostMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
    public ResponseEntity<EntityModel<ComputeResourceDto>> createComputingResourcePropertyForComputeResource(
            @PathVariable UUID computeResourceId,
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        var computeResourceProperty = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);

        var createdComputeResourceProperty = computeResourcePropertyService
                .addComputeResourcePropertyToComputeResource(computeResourceId, computeResourceProperty);
        return ResponseEntity.ok(computeResourceAssembler.toModel(createdComputeResourceProperty));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")},
            description = "Update a Compute resource property of an compute resource. " +
                    "For compute resource property type only ID is required, other compute resource property type " +
                    "attributes will not change.")
    @PutMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> updateComputeResourcePropertyOfAlgorithm(
            @PathVariable UUID computeResourceId,
            @PathVariable UUID computeResourcePropertyId,
            @Validated(ValidationGroups.Update.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfComputeResource(computeResourceId, computeResourcePropertyId);

        computeResourcePropertyDto.setId(computeResourcePropertyId);
        var resource = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);
        var updatedResource = computeResourcePropertyService.update(resource);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(updatedResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute resource property with given id doesn't exist"),
    }, description = "Delete a Compute resource property of an compute resource")
    @DeleteMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public HttpEntity<Void> deleteComputeResourceProperty(
            @PathVariable UUID computeResourceId,
            @PathVariable UUID computeResourcePropertyId) {
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfComputeResource(computeResourceId, computeResourcePropertyId);

        computeResourcePropertyService.delete(computeResourcePropertyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
    }, description = "Retrieve a specific compute resource property of an compute resource")
    @GetMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public HttpEntity<EntityModel<ComputeResourcePropertyDto>> getComputeResourceProperty(
            @PathVariable UUID computeResourceId,
            @PathVariable UUID computeResourcePropertyId) {
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfComputeResource(computeResourceId, computeResourcePropertyId);

        var resource = computeResourcePropertyService.findById(computeResourcePropertyId);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(resource));
    }
}
