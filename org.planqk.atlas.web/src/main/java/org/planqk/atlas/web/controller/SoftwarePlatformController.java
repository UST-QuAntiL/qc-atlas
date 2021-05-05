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

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
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
@RequestMapping("/" + Constants.SOFTWARE_PLATFORMS)
@AllArgsConstructor
@Slf4j
public class SoftwarePlatformController {

    private final SoftwarePlatformService softwarePlatformService;

    private final ImplementationService implementationService;

    private final LinkingService linkingService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
    }, description = "Retrieve all software platforms.")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<Page<SoftwarePlatformDto>> getSoftwarePlatforms(
            @Parameter(hidden = true) ListParameters listParameters) {
        final Page<SoftwarePlatform> entities;
        if (listParameters.getSearch() == null || listParameters.getSearch().isEmpty()) {
            entities = softwarePlatformService.findAll(listParameters.getPageable());
        } else {
            entities = softwarePlatformService.searchAllByName(listParameters.getSearch(), listParameters.getPageable());
        }
        return ResponseEntity.ok(ModelMapperUtils.convertPage(entities, SoftwarePlatformDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body.")
    }, description = "Define the basic properties of a software platform. " +
            "References to sub-objects (e.g. a compute resource) " +
            "can be added via sub-routes (e.g. via POST on /" + Constants.COMPUTE_RESOURCES + ").")
    @PostMapping
    public ResponseEntity<SoftwarePlatformDto> createSoftwarePlatform(
            @Validated( {ValidationGroups.Create.class}) @RequestBody SoftwarePlatformDto softwarePlatformDto) {
        final var savedPlatform = softwarePlatformService.create(ModelMapperUtils.convert(softwarePlatformDto, SoftwarePlatform.class));
        return new ResponseEntity<>(ModelMapperUtils.convert(savedPlatform, SoftwarePlatformDto.class), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software Platform with given ID doesn't exist.")
    }, description = "Update the basic properties of a software platform (e.g. name). " +
            "References to sub-objects (e.g. a compute resource) are not updated via this operation - " +
            "use the corresponding sub-route for updating them (e.g. via PUT on /" + Constants.COMPUTE_RESOURCES + "/{computeResourceId}).")
    @PutMapping("/{softwarePlatformId}")
    public ResponseEntity<SoftwarePlatformDto> updateSoftwarePlatform(
            @PathVariable UUID softwarePlatformId,
            @Validated( {ValidationGroups.Update.class}) @RequestBody SoftwarePlatformDto softwarePlatformDto) {
        softwarePlatformDto.setId(softwarePlatformId);
        final var softwarePlatform = softwarePlatformService
                .update(
                        ModelMapperUtils.convert(softwarePlatformDto, SoftwarePlatform.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(softwarePlatform, SoftwarePlatformDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software Platform with given ID doesn't exist.")
    }, description = "Delete a software platform. " +
            "This also removes all references to other entities (e.g. compute resource)")
    @DeleteMapping("/{softwarePlatformId}")
    public ResponseEntity<Void> deleteSoftwarePlatform(@PathVariable UUID softwarePlatformId) {
        softwarePlatformService.delete(softwarePlatformId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software Platform with given ID doesn't exist."),
    }, description = "Retrieve a specific software platform and its basic properties.")
    @GetMapping("/{softwarePlatformId}")
    public ResponseEntity<SoftwarePlatformDto> getSoftwarePlatform(
            @PathVariable UUID softwarePlatformId) {
        final var softwarePlatform = softwarePlatformService.findById(softwarePlatformId);
        return ResponseEntity.ok(ModelMapperUtils.convert(softwarePlatform, SoftwarePlatformDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software Platform or Implementation with given IDs don't exist."),
    }, description = "Get a specific implementations of a software platform. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{softwarePlatformId}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<Page<ImplementationDto>> getImplementationsOfSoftwarePlatform(
            @PathVariable UUID softwarePlatformId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var implementations = softwarePlatformService.findLinkedImplementations(softwarePlatformId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(implementations, ImplementationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software platform or implementation with given IDs don't exist or " +
                            "reference was already added.")
    }, description = "Add a reference to an existing implementation " +
            "(that was previously created via a POST on e.g. /" + Constants.ALGORITHMS + "/{algorithmId}/ " + Constants.IMPLEMENTATIONS + "). " +
            "Only the ID is required in the request body, other attributes will be ignored and not changed.")
    @PostMapping("/{softwarePlatformId}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<Void> linkSoftwarePlatformAndImplementation(
            @PathVariable UUID softwarePlatformId,
            @Validated( {ValidationGroups.IDOnly.class}) @RequestBody ImplementationDto implementationDto) {
        linkingService.linkImplementationAndSoftwarePlatform(implementationDto.getId(), softwarePlatformId);
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software platform or implementation with given IDs don't exist or " +
                            "no reference exists.")
    }, description = "Delete a reference to a implementation of an software platform. " +
            "The reference has to be previously created via a POST on " +
            "/" + Constants.SOFTWARE_PLATFORMS + "/{softwarePlatformId}/" + Constants.IMPLEMENTATIONS + ").")
    @DeleteMapping("/{softwarePlatformId}/" + Constants.IMPLEMENTATIONS + "/{implementationId}")
    public ResponseEntity<Void> unlinkSoftwarePlatformAndImplementation(
            @PathVariable UUID implementationId,
            @PathVariable UUID softwarePlatformId) {
        linkingService.unlinkImplementationAndSoftwarePlatform(implementationId, softwarePlatformId);
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software platform or implementation with given IDs don't exist.")
    }, description = "Retrieve a specific implementation of a software platform. If none are found an empty list is returned.")
    @GetMapping("/{softwarePlatformId}/" + Constants.IMPLEMENTATIONS + "/{implementationId}")
    public ResponseEntity<ImplementationDto> getImplementationOfSoftwarePlatform(
            @PathVariable UUID softwarePlatformId,
            @PathVariable UUID implementationId) {
        softwarePlatformService.checkIfImplementationIsLinkedToSoftwarePlatform(softwarePlatformId, implementationId);

        final var implementation = implementationService.findById(implementationId);
        return ResponseEntity.ok(ModelMapperUtils.convert(implementation, ImplementationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software platform or implementation with given IDs don't exist."),
    }, description = "Retrieve referenced cloud services of a software platform. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{softwarePlatformId}/" + Constants.CLOUD_SERVICES)
    public ResponseEntity<Page<CloudServiceDto>> getCloudServicesOfSoftwarePlatform(
            @PathVariable UUID softwarePlatformId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var cloudServices = softwarePlatformService.findLinkedCloudServices(softwarePlatformId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(cloudServices, CloudServiceDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software Platform or Cloud Service with given IDs don't exist or " +
                            "reference was already added."),
    }, description = "Add a reference to an existing cloud service " +
            "(that was previously created via a POST on e.g. /" + Constants.CLOUD_SERVICES + "). " +
            "Only the ID is required in the request body, other attributes will be ignored and not changed.")
    @PostMapping("/{softwarePlatformId}/" + Constants.CLOUD_SERVICES)
    public ResponseEntity<Void> linkSoftwarePlatformAndCloudService(
            @PathVariable UUID softwarePlatformId,
            @Validated( {ValidationGroups.IDOnly.class}) @RequestBody CloudServiceDto cloudServiceDto) {
        linkingService.linkSoftwarePlatformAndCloudService(softwarePlatformId, cloudServiceDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software Platform or Cloud Service with given IDs don't exist or " +
                            "no reference exists."),
    }, description = "Delete a reference to a {object} of an {object}. " +
            "The reference has to be previously created via a POST on " +
            "/" + Constants.SOFTWARE_PLATFORMS + "/{softwarePlatformId}/" + Constants.CLOUD_SERVICES + ").")
    @DeleteMapping("/{softwarePlatformId}/" + Constants.CLOUD_SERVICES + "/{cloudServiceId}")
    public ResponseEntity<Void> unlinkSoftwarePlatformAndCloudService(
            @PathVariable UUID softwarePlatformId,
            @PathVariable UUID cloudServiceId) {
        linkingService.unlinkSoftwarePlatformAndCloudService(softwarePlatformId, cloudServiceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software Platform or Compute Resource with given IDs don't exist."),
    }, description = "Retrieve referenced compute resources for a software platform. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{softwarePlatformId}/" + Constants.COMPUTE_RESOURCES)
    public ResponseEntity<Page<ComputeResourceDto>> getComputeResourcesOfSoftwarePlatform(
            @PathVariable UUID softwarePlatformId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var computeResources = softwarePlatformService.findLinkedComputeResources(softwarePlatformId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(computeResources, ComputeResourceDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software Platform or Compute Resource with given IDs don't exist or " +
                            "reference was already added."),
    }, description = "Add a reference to an existing compute resource " +
            "(that was previously created via a POST on e.g. /" + Constants.COMPUTE_RESOURCES + "). " +
            "Only the ID is required in the request body, other attributes will be ignored and not changed.")
    @PostMapping("/{softwarePlatformId}/" + Constants.COMPUTE_RESOURCES)
    public ResponseEntity<Void> linkSoftwarePlatformAndComputeResource(
            @PathVariable UUID softwarePlatformId,
            @Validated( {ValidationGroups.IDOnly.class}) @RequestBody ComputeResourceDto computeResourceDto) {
        linkingService.linkSoftwarePlatformAndComputeResource(softwarePlatformId, computeResourceDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Software Platform or Compute Resource with given IDs don't exist or " +
                            "no reference exists."),
    }, description = "Delete a reference to a {object} of an {object}. " +
            "The reference has to be previously created via a POST on " +
            "/" + Constants.SOFTWARE_PLATFORMS + "/{softwarePlatformId}/" + Constants.COMPUTE_RESOURCES + ").")
    @DeleteMapping("/{softwarePlatformId}/" + Constants.COMPUTE_RESOURCES + "/{computeResourceId}")
    public ResponseEntity<Void> unlinkSoftwarePlatformAndComputeResource(
            @PathVariable UUID softwarePlatformId,
            @PathVariable UUID computeResourceId) {
        linkingService.unlinkSoftwarePlatformAndComputeResource(softwarePlatformId, computeResourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
