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

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.services.CloudServiceService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.linkassembler.CloudServiceAssembler;
import org.planqk.atlas.web.linkassembler.ComputeResourceAssembler;
import org.planqk.atlas.web.linkassembler.SoftwarePlatformAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.data.domain.Page;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = Constants.TAG_EXECUTION_ENVIRONMENTS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.CLOUD_SERVICES)
@AllArgsConstructor
@Slf4j
public class CloudServiceController {

    private final CloudServiceService cloudServiceService;

    private final CloudServiceAssembler cloudServiceAssembler;

    private final ComputeResourceAssembler computeResourceAssembler;

    private final SoftwarePlatformAssembler softwarePlatformAssembler;

    private final LinkingService linkingService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
    }, description = "Retrieve all cloud services.")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<CloudServiceDto>>> getCloudServices(
            @Parameter(hidden = true) ListParameters listParameters) {
        final Page<CloudService> entities;
        if (listParameters.getSearch() == null || listParameters.getSearch().isEmpty()) {
            entities = cloudServiceService.findAll(listParameters.getPageable());
        } else {
            entities = cloudServiceService.searchAllByName(listParameters.getSearch(), listParameters.getPageable());
        }
        return ResponseEntity.ok(cloudServiceAssembler.toModel(entities));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
    }, description = "Define the basic properties of a cloud service. " +
            "References to sub-objects (e.g. a compute resource) can be added via sub-routes " +
            "(e.g. POST on /" + Constants.CLOUD_SERVICES + "/{cloudServiceId}/" + Constants.COMPUTE_RESOURCES + ").")
    @PostMapping
    public ResponseEntity<EntityModel<CloudServiceDto>> createCloudService(
            @Validated(ValidationGroups.Create.class) @RequestBody CloudServiceDto cloudServiceDto) {
        final var savedCloudService = cloudServiceService.create(ModelMapperUtils.convert(cloudServiceDto, CloudService.class));
        return new ResponseEntity<>(cloudServiceAssembler.toModel(savedCloudService), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Cloud service with given ID doesn't exist.")
    }, description = "Update the basic properties of a cloud service (e.g. name). " +
            "References to sub-objects (e.g. a compute resource) are not updated via this operation - " +
            "use the corresponding sub-route for updating them (e.g. PUT on " + "/" + Constants.COMPUTE_RESOURCES + "/{computeResourceId}).")
    @PutMapping("/{cloudServiceId}")
    public ResponseEntity<EntityModel<CloudServiceDto>> updateCloudService(
            @PathVariable UUID cloudServiceId,
            @Validated(ValidationGroups.Update.class) @RequestBody CloudServiceDto cloudServiceDto) {
        cloudServiceDto.setId(cloudServiceId);
        final var updatedCloudService = cloudServiceService.update(
                ModelMapperUtils.convert(cloudServiceDto, CloudService.class));
        return ResponseEntity.ok(cloudServiceAssembler.toModel(updatedCloudService));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Cloud service with given ID doesn't exist."),
    }, description = "Delete a cloud service. " +
            "This also removes all references to other entities (e.g. compute resource).")
    @DeleteMapping("/{cloudServiceId}")
    public ResponseEntity<Void> deleteCloudService(
            @PathVariable UUID cloudServiceId) {
        cloudServiceService.delete(cloudServiceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Cloud service with given ID doesn't exist."),
    }, description = "Retrieve a specific cloud service and its basic properties.")
    @GetMapping("/{cloudServiceId}")
    public ResponseEntity<EntityModel<CloudServiceDto>> getCloudService(
            @PathVariable UUID cloudServiceId) {
        final var cloudService = cloudServiceService.findById(cloudServiceId);
        return ResponseEntity.ok(cloudServiceAssembler
                .toModel(ModelMapperUtils.convert(cloudService, CloudServiceDto.class)));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Cloud service with given ID doesn't exist.")
    }, description = "Retrieve referenced software platforms of an cloud service. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{cloudServiceId}/" + Constants.SOFTWARE_PLATFORMS)
    public ResponseEntity<PagedModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatformsOfCloudService(
            @PathVariable UUID cloudServiceId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var softwarePlatforms = cloudServiceService.findLinkedSoftwarePlatforms(cloudServiceId, listParameters.getPageable());
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatforms));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Cloud Service or Compute Resource with given IDs don't exist."),
    }, description = "Retrieve referenced compute resources of an cloud service. If none are found an empty list is returned.")
    @ListParametersDoc
    @GetMapping("/{cloudServiceId}/" + Constants.COMPUTE_RESOURCES)
    public ResponseEntity<PagedModel<EntityModel<ComputeResourceDto>>> getComputeResourcesOfCloudService(
            @PathVariable UUID cloudServiceId,
            @Parameter(hidden = true) ListParameters listParameters) {
        final var computeResources = cloudServiceService.findLinkedComputeResources(cloudServiceId, listParameters.getPageable());
        return ResponseEntity.ok(computeResourceAssembler.toModel(computeResources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404", description = "Not Found. Cloud Service or Compute Resource with given IDs don't exist or " +
                    "reference was already added."),
    }, description = "Add a reference to an existing compute resource " +
            "(that was previously created via a POST on e.g. /" + Constants.COMPUTE_RESOURCES + "). " +
            "Only the ID is required in the request body, other attributes will be ignored and not changed.")
    @PostMapping("/{cloudServiceId}/" + Constants.COMPUTE_RESOURCES)
    public ResponseEntity<Void> linkCloudServiceAndComputeResource(
            @PathVariable UUID cloudServiceId,
            @Validated({ValidationGroups.IDOnly.class}) @RequestBody ComputeResourceDto computeResourceDto) {
        linkingService.linkCloudServiceAndComputeResource(cloudServiceId, computeResourceDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Cloud Service or Compute Resource with given IDs don't exist or " +
                    "no reference exists."),
    }, description = "Delete a reference to a compute resource of a cloud service. " +
            "The reference has to be previously created via a POST on /" +
            Constants.CLOUD_SERVICES + "/{cloudServiceId}/" + Constants.COMPUTE_RESOURCES + ").")
    @DeleteMapping("/{cloudServiceId}/" + Constants.COMPUTE_RESOURCES + "/{computeResourceId}")
    public ResponseEntity<Void> unlinkCloudServiceAndComputeResource(
            @PathVariable UUID cloudServiceId,
            @PathVariable UUID computeResourceId) {
        linkingService.unlinkCloudServiceAndComputeResource(cloudServiceId, computeResourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
