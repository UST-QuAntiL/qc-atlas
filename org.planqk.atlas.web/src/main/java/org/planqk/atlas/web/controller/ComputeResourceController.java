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

import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ComputeResourceService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.data.domain.Page;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = Constants.TAG_EXECUTION_ENVIRONMENTS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.COMPUTE_RESOURCES)
@AllArgsConstructor
@Slf4j
public class ComputeResourceController {

    private final ComputeResourcePropertyService computeResourcePropertyService;

    private final ComputeResourceService computeResourceService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all compute resources.")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<Page<ComputeResourceDto>> getComputeResources(
            @Parameter(hidden = true) ListParameters listParameters) {
        final Page<ComputeResource> entities;
        if (listParameters.getSearch() == null || listParameters.getSearch().isEmpty()) {
            entities = computeResourceService.findAll(listParameters.getPageable());
        } else {
            entities = computeResourceService.searchAllByName(listParameters.getSearch(), listParameters.getPageable());
        }
        return ResponseEntity.ok(ModelMapperUtils.convertPage(entities, ComputeResourceDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body.")
    }, description = "Define the basic properties of a compute resource. " +
            "References to sub-objects (e.g. a compute resource property) can be added via sub-routes " +
            "(e.g. POST on /" + Constants.COMPUTE_RESOURCES + "/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + ").")
    @PostMapping
    public ResponseEntity<ComputeResourceDto> createComputeResource(
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourceDto computeResourceDto) {
        final ComputeResource computeResource = computeResourceService.create(
                ModelMapperUtils.convert(computeResourceDto, ComputeResource.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(ModelMapperUtils.convert(computeResource, ComputeResourceDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404", description = "Not Found. Compute Resource with given ID doesn't exist.")
    }, description = "Update the basic properties of a compute resource (e.g. name). " +
            "References to sub-objects (e.g. a compute resource property) are not updated via this operation - " +
            "use the corresponding sub-route for updating them (e.g. PUT on /" +
            Constants.COMPUTE_RESOURCES + "/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}).")
    @PutMapping("/{computeResourceId}")
    public ResponseEntity<ComputeResourceDto> updateComputeResource(
            @PathVariable UUID computeResourceId,
            @Validated(ValidationGroups.Update.class) @RequestBody ComputeResourceDto computeResourceDto) {
        computeResourceDto.setId(computeResourceId);
        final ComputeResource computeResource = computeResourceService.update(
                ModelMapperUtils.convert(computeResourceDto, ComputeResource.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(computeResource, ComputeResourceDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Compute Resource with given ID doesn't exist.")
    }, description = "Delete a compute resource. " +
            "This also removes all references to other entities (e.g. software platform).")
    @DeleteMapping("/{computeResourceId}")
    public ResponseEntity<Void> deleteComputeResource(
            @PathVariable UUID computeResourceId) {
        computeResourceService.delete(computeResourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Compute Resource with given ID doesn't exist.")
    }, description = "Retrieve a specific compute resource and its basic properties.")
    @GetMapping("/{computeResourceId}")
    public ResponseEntity<ComputeResourceDto> getComputeResource(
            @PathVariable UUID computeResourceId) {
        final ComputeResource computeResource = computeResourceService.findById(computeResourceId);
        return ResponseEntity.ok(ModelMapperUtils.convert(computeResource, ComputeResourceDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Compute Resource with given ID doesn't exist.")
    }, description = "Retrieve referenced software platform of a compute resource. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{computeResourceId}/" + Constants.SOFTWARE_PLATFORMS)
    public ResponseEntity<Page<SoftwarePlatformDto>> getSoftwarePlatformsOfComputeResource(
            @PathVariable UUID computeResourceId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var softwarePlatforms = computeResourceService.findLinkedSoftwarePlatforms(computeResourceId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(softwarePlatforms, SoftwarePlatformDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Compute Resource with given ID doesn't exist.")
    }, description = "Retrieve referenced cloud services of a compute resource. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{computeResourceId}/" + Constants.CLOUD_SERVICES)
    public ResponseEntity<Page<CloudServiceDto>> getCloudServicesOfComputeResource(
            @PathVariable UUID computeResourceId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var cloudServices = computeResourceService.findLinkedCloudServices(computeResourceId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(cloudServices, CloudServiceDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Compute Resource with given ID doesn't exist.")
    }, description = "Retrieve referenced compute resource properties of a compute resource. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
    public ResponseEntity<Page<ComputeResourcePropertyDto>> getComputeResourcePropertiesOfComputeResource(
            @PathVariable UUID computeResourceId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var resources = computeResourcePropertyService.findComputeResourcePropertiesOfComputeResource(computeResourceId,
                listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(resources, ComputeResourcePropertyDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Compute resource or compute resource property type with given IDs don't exist.")
    }, description = "Add a compute resource property (e.g. a certain number of qubits) that is provided by an compute resource. " +
            "The compute resource property type has to be already created (e.g. via POST on /" + Constants.COMPUTE_RESOURCE_PROPERTY_TYPES + "). " +
            "As a result only the ID is required for the compute resource property type, other attributes will be ignored not changed.")
    @PostMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES)
    public ResponseEntity<ComputeResourcePropertyDto> createComputeResourcePropertyForComputeResource(
            @PathVariable UUID computeResourceId,
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        final var computeResourceProperty = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);

        final var createdComputeResourceProperty = computeResourcePropertyService
                .addComputeResourcePropertyToComputeResource(computeResourceId, computeResourceProperty);
        return new ResponseEntity<>(ModelMapperUtils.convert(createdComputeResourceProperty, ComputeResourcePropertyDto.class), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Compute resource, compute resource property or compute resource type with given IDs don't exist.")
    }, description = "Update a Compute resource property of an compute resource. " +
            "For the compute resource property type only the ID is required, " +
            "other compute resource property type attributes will be ignored and not changed.")
    @PutMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public ResponseEntity<ComputeResourcePropertyDto> updateComputeResourcePropertyOfComputeResource(
            @PathVariable UUID computeResourceId,
            @PathVariable UUID computeResourcePropertyId,
            @Validated(ValidationGroups.Update.class) @RequestBody ComputeResourcePropertyDto computeResourcePropertyDto) {
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfComputeResource(computeResourceId, computeResourcePropertyId);

        computeResourcePropertyDto.setId(computeResourcePropertyId);
        final var resource = ModelMapperUtils.convert(computeResourcePropertyDto, ComputeResourceProperty.class);
        final var updatedResource = computeResourcePropertyService.update(resource);
        return ResponseEntity.ok(ModelMapperUtils.convert(updatedResource, ComputeResourcePropertyDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Compute resource or compute resource property with given IDs don't exist."),
    }, description = "Delete a Compute resource property of an compute resource. " +
            "The compute resource property type is not affected by this.")
    @DeleteMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public HttpEntity<Void> deleteComputeResourcePropertyOfComputeResource(
            @PathVariable UUID computeResourceId,
            @PathVariable UUID computeResourcePropertyId) {
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfComputeResource(computeResourceId, computeResourcePropertyId);

        computeResourcePropertyService.delete(computeResourcePropertyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Compute resource or compute resource property with given IDs don't exist."),
    }, description = "Retrieve a specific compute resource property of an compute resource.")
    @GetMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCE_PROPERTIES + "/{computeResourcePropertyId}")
    public ResponseEntity<ComputeResourcePropertyDto> getComputeResourcePropertyOfComputeResource(
            @PathVariable UUID computeResourceId,
            @PathVariable UUID computeResourcePropertyId) {
        computeResourcePropertyService.checkIfComputeResourcePropertyIsOfComputeResource(computeResourceId, computeResourcePropertyId);

        final var resource = computeResourcePropertyService.findById(computeResourcePropertyId);
        return ResponseEntity.ok(ModelMapperUtils.convert(resource, ComputeResourcePropertyDto.class));
    }
}
