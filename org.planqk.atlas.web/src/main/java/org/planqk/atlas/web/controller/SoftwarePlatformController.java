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

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.linkassembler.CloudServiceAssembler;
import org.planqk.atlas.web.linkassembler.ComputeResourceAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
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

@Tag(name = Constants.TAG_EXECUTION_ENVIRONMENTS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.SOFTWARE_PLATFORMS)
@ApiVersion("v1")
@AllArgsConstructor
@Slf4j
public class SoftwarePlatformController {

    private final SoftwarePlatformService softwarePlatformService;
    private final SoftwarePlatformAssembler softwarePlatformAssembler;
    private final ImplementationService implementationService;
    private final ImplementationAssembler implementationAssembler;
    private final ComputeResourceAssembler computeResourceAssembler;
    private final CloudServiceAssembler cloudServiceAssembler;

    private final LinkingService linkingService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
    }, description = "Retrieve all software platforms")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatforms(
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<SoftwarePlatform> entities;
        if (listParameters.getSearch() == null || listParameters.getSearch().isEmpty()) {
            entities = softwarePlatformService.findAll(listParameters.getPageable());
        } else {
            entities = softwarePlatformService.searchAllByName(listParameters.getSearch(), listParameters.getPageable());
        }
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(entities));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400")
    }, description = "Define the basic properties of a software platform. " +
            "References to sub-objects (e.g. a compute resource) " +
            "can be added via sub-routes (e.g. /software-platforms/{id}/compute-resources). " +
            "Custom ID will be ignored.")
    @PostMapping
    public ResponseEntity<EntityModel<SoftwarePlatformDto>> createSoftwarePlatform(
            @Validated({ValidationGroups.Create.class}) @RequestBody SoftwarePlatformDto softwarePlatformDto) {
        var savedPlatform = softwarePlatformService.create(ModelMapperUtils.convert(softwarePlatformDto, SoftwarePlatform.class));
        return new ResponseEntity<>(softwarePlatformAssembler.toModel(savedPlatform), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform with given id does not exist")
    }, description = "Update the basic properties of a software platform (e.g. name). " +
            "References to sub-objects (e.g. a compute resource) are not updated via this operation - " +
            "use the corresponding sub-route for updating them (e.g. /software-platforms/{id}/compute-resources). " +
            "Custom ID will be ignored.")
    @PutMapping("/{softwarePlatformId}")
    public ResponseEntity<EntityModel<SoftwarePlatformDto>> updateSoftwarePlatform(
            @PathVariable UUID softwarePlatformId,
            @Validated({ValidationGroups.Update.class}) @RequestBody SoftwarePlatformDto softwarePlatformDto) {
        softwarePlatformDto.setId(softwarePlatformId);
        var softwarePlatform = softwarePlatformService
                .update(
                        ModelMapperUtils.convert(softwarePlatformDto, SoftwarePlatform.class));
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatform));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform with given id does not exist")
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
            @ApiResponse(responseCode = "404", description = "Software Platform with given id does not exist"),
    }, description = "Retrieve a specific software platform and its basic properties.")
    @GetMapping("/{softwarePlatformId}")
    public ResponseEntity<EntityModel<SoftwarePlatformDto>> getSoftwarePlatform(
            @PathVariable UUID softwarePlatformId) {
        var softwarePlatform = softwarePlatformService.findById(softwarePlatformId);
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatform));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Implementation with given id does not exist"),
    }, description = "Get a specific implementations for a software platform.")
    @ListParametersDoc
    @GetMapping("/{softwarePlatformId}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<PagedModel<EntityModel<ImplementationDto>>> getImplementationsOfSoftwarePlatform(
            @PathVariable UUID softwarePlatformId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var implementations = softwarePlatformService.findLinkedImplementations(softwarePlatformId, listParameters.getPageable());
        return ResponseEntity.ok(implementationAssembler.toModel(implementations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software platform or publication does not exist")
    }, description = "Add a reference to an existing software platform" +
            "(that was previously created via a POST on /software-platforms/)." +
            "Custom ID will be ignored. For software platform only ID is required," +
            "other software platform attributes will not change." +
            "If the software platform doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{softwarePlatformId}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<Void> linkSoftwarePlatformAndImplementation(
            @PathVariable UUID softwarePlatformId,
            @Validated({ValidationGroups.IDOnly.class}) @RequestBody ImplementationDto implementationDto) {
        linkingService.linkImplementationAndSoftwarePlatform(implementationDto.getId(), softwarePlatformId);
        return ResponseEntity.noContent().build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software platform or publication does not exist")
    }, description = "Delete a reference to a software platform of the implementation")
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
            @ApiResponse(responseCode = "404", description = "Implementation doesn't exist")
    }, description = "Retrieve a specific implementation of the algorithm.")
    @GetMapping("/{softwarePlatformId}/" + Constants.IMPLEMENTATIONS + "/{implementationId}")
    public ResponseEntity<EntityModel<ImplementationDto>> getImplementationOfSoftwarePlatform(
            @PathVariable UUID softwarePlatformId,
            @PathVariable UUID implementationId) {
        softwarePlatformService.checkIfImplementationIsLinkedToSoftwarePlatform(softwarePlatformId, implementationId);

        var implementation = implementationService.findById(implementationId);
        return ResponseEntity.ok(implementationAssembler.toModel(implementation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Cloud Service with given id does not exist"),
    }, description = "Get referenced cloud services for a software platform.")
    @ListParametersDoc
    @GetMapping("/{softwarePlatformId}/" + Constants.CLOUD_SERVICES)
    public ResponseEntity<PagedModel<EntityModel<CloudServiceDto>>> getCloudServicesOfSoftwarePlatform(
            @PathVariable UUID softwarePlatformId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var cloudServices = softwarePlatformService.findLinkedCloudServices(softwarePlatformId, listParameters.getPageable());
        return ResponseEntity.ok(cloudServiceAssembler.toModel(cloudServices));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Cloud Service with given id does not exist"),
    }, description = "Add a reference to an existing cloud service (that was previously created via a POST on /cloud-services/). " +
            "Custom ID will be ignored. " +
            "For the cloud service only the ID is required, other cloud service attributes will not change. " +
            "If the cloud service doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{softwarePlatformId}/" + Constants.CLOUD_SERVICES)
    public ResponseEntity<Void> linkSoftwarePlatformAndCloudService(
            @PathVariable UUID softwarePlatformId,
            @Validated({ValidationGroups.IDOnly.class}) @RequestBody CloudServiceDto cloudServiceDto) {
        linkingService.linkSoftwarePlatformAndCloudService(softwarePlatformId, cloudServiceDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Cloud Service with given id does not exist"),
    }, description = "Delete a reference to an cloud service of the software platform.")
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
            @ApiResponse(responseCode = "404", description = "Software Platform or Compute Resource with given id does not exist"),
    }, description = "Get referenced compute resources for a software platform.")
    @ListParametersDoc
    @GetMapping("/{softwarePlatformId}/" + Constants.COMPUTE_RESOURCES)
    public ResponseEntity<PagedModel<EntityModel<ComputeResourceDto>>> getComputeResourcesOfSoftwarePlatform(
            @PathVariable UUID softwarePlatformId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var computeResources = softwarePlatformService.findLinkedComputeResources(softwarePlatformId, listParameters.getPageable());
        return ResponseEntity.ok(computeResourceAssembler.toModel(computeResources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Compute Resource with given id does not exist"),
    }, description = "Add a reference to an existing compute resource(that was previously created via a POST on /compute-resources/). " +
            "Custom ID will be ignored. " +
            "For the compute resource only the ID is required, other compute resource attributes will not change. " +
            "If the compute resource doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{softwarePlatformId}/" + Constants.COMPUTE_RESOURCES)
    public ResponseEntity<Void> linkSoftwarePlatformAndComputeResource(
            @PathVariable UUID softwarePlatformId,
            @Validated({ValidationGroups.IDOnly.class}) @RequestBody ComputeResourceDto computeResourceDto) {
        linkingService.linkSoftwarePlatformAndComputeResource(softwarePlatformId, computeResourceDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Compute Resource with given id does not exist"),
    }, description = "Delete a reference to an compute resource of the software platform.")
    @DeleteMapping("/{softwarePlatformId}/" + Constants.COMPUTE_RESOURCES + "/{computeResourceId}")
    public ResponseEntity<Void> unlinkSoftwarePlatformAndComputeResource(
            @PathVariable UUID softwarePlatformId,
            @PathVariable UUID computeResourceId) {
        linkingService.unlinkSoftwarePlatformAndComputeResource(softwarePlatformId, computeResourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
