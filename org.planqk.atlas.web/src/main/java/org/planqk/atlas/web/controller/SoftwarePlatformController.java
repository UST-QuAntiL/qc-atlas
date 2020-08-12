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

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.Constants;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_EXECUTION_ENVIRONMENTS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.SOFTWARE_PLATFORMS)
@AllArgsConstructor
@Slf4j
public class SoftwarePlatformController {

    private final SoftwarePlatformService softwarePlatformService;
    private final SoftwarePlatformAssembler softwarePlatformAssembler;
    private final ImplementationAssembler implementationAssembler;
    private final ComputeResourceAssembler computeResourceAssembler;
    private final CloudServiceAssembler cloudServiceAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
    }, description = "Retrieve all software platforms")
    @GetMapping()
    @ListParametersDoc
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
    @PostMapping()
    public ResponseEntity<EntityModel<SoftwarePlatformDto>> createSoftwarePlatform(
            @Valid @RequestBody SoftwarePlatformDto platformDto) {
        var savedPlatform = softwarePlatformService.save(ModelMapperUtils.convert(platformDto, SoftwarePlatform.class));
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
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<SoftwarePlatformDto>> updateSoftwarePlatform(
            @PathVariable UUID id,
            @Valid @RequestBody SoftwarePlatformDto softwarePlatformDto) {
        var softwarePlatform = softwarePlatformService
                .update(id, ModelMapperUtils.convert(softwarePlatformDto, SoftwarePlatform.class));
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatform));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform with given id does not exist")
    }, description = "Delete a software platform. " +
            "This also removes all references to other entities (e.g. compute resource)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSoftwarePlatform(@PathVariable UUID id) {
        softwarePlatformService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform with given id does not exist"),
    }, description = "Retrieve a specific software platform and its basic properties.")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SoftwarePlatformDto>> getSoftwarePlatform(
            @PathVariable UUID id) {
        var softwarePlatform = softwarePlatformService.findById(id);
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatform));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Implementation with given id does not exist"),
    }, description = "Get referenced implementations for a software platform.")
    @GetMapping("/{id}/" + Constants.IMPLEMENTATIONS)
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<ImplementationDto>>> getImplementationsForSoftwarePlatform(
            @PathVariable UUID id,
            @Parameter(hidden = true) ListParameters listParameters) {
        var implementations = softwarePlatformService.findImplementations(id, listParameters.getPageable());
        return ResponseEntity.ok(implementationAssembler.toModel(implementations));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Implementation with given id does not exist"),
    }, description = "Add a reference to an existing implementation (that was previously created via a POST on /implementations/). " +
            "Custom ID will be ignored. " +
            "For the implementation only the ID is required, other implementation attributes will not change. " +
            "If the implementation doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{id}/" + Constants.IMPLEMENTATIONS + "/{implId}")
    public ResponseEntity<Void> addImplementationReferenceToSoftwarePlatform(
            @PathVariable UUID id,
            @PathVariable UUID implId) {
        softwarePlatformService.addImplementationReference(id, implId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Implementation with given id does not exist"),
    }, description = "Get a specific referenced implementation of a software platform.")
    @GetMapping("/{id}/" + Constants.IMPLEMENTATIONS + "/{implId}")
    public ResponseEntity<EntityModel<ImplementationDto>> getImplementationForSoftwarePlatform(
            @PathVariable UUID id,
            @PathVariable UUID implId) {
        var implementation = softwarePlatformService.getImplementation(id, implId);
        return ResponseEntity.ok(implementationAssembler.toModel(implementation));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Implementation with given id does not exist"),
    }, description = "Delete a reference to an implementation of the software platform.")
    @DeleteMapping("/{id}/" + Constants.IMPLEMENTATIONS + "/{implId}")
    public ResponseEntity<Void> deleteImplementationReferenceFromSoftwarePlatform(
            @PathVariable UUID id,
            @PathVariable UUID implId) {
        softwarePlatformService.deleteImplementationReference(id, implId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Cloud Service with given id does not exist"),
    }, description = "Get referenced cloud services for a software platform.")
    @GetMapping("/{id}/" + Constants.CLOUD_SERVICES)
    @ListParametersDoc()
    public ResponseEntity<PagedModel<EntityModel<CloudServiceDto>>> getCloudServicesForSoftwarePlatform(
            @PathVariable UUID id,
            @Parameter(hidden = true) ListParameters listParameters) {
        var cloudServices = softwarePlatformService.findCloudServices(id, listParameters.getPageable());
        return ResponseEntity.ok(cloudServiceAssembler.toModel(cloudServices));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Cloud Service with given id does not exist"),
    }, description = "Add a reference to an existing cloud service (that was previously created via a POST on /cloud-services/). " +
            "Custom ID will be ignored. " +
            "For the cloud service only the ID is required, other cloud service attributes will not change. " +
            "If the cloud service doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{id}/" + Constants.CLOUD_SERVICES + "/{csId}")
    public ResponseEntity<Void> addCloudServiceReferenceToSoftwarePlatform(
            @PathVariable UUID id,
            @PathVariable UUID csId) {
        softwarePlatformService.addCloudServiceReference(id, csId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Cloud Service with given id does not exist"),
    }, description = "Delete a reference to an cloud service of the software platform.")
    @DeleteMapping("/{id}/" + Constants.CLOUD_SERVICES + "/{csId}")
    public ResponseEntity<Void> deleteCloudServiceReferenceFromSoftwarePlatform(
            @PathVariable UUID id,
            @PathVariable UUID csId) {
        softwarePlatformService.deleteCloudServiceReference(id, csId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Compute Resource with given id does not exist"),
    }, description = "Get referenced compute resources for a software platform.")
    @GetMapping("/{id}/" + Constants.COMPUTE_RESOURCES)
    @ListParametersDoc()
    public ResponseEntity<PagedModel<EntityModel<ComputeResourceDto>>> getComputeResourcesForSoftwarePlatform(
            @PathVariable UUID id,
            @Parameter(hidden = true) ListParameters listParameters) {
        var computeResources = softwarePlatformService.findComputeResources(id, listParameters.getPageable());
        return ResponseEntity.ok(computeResourceAssembler.toModel(computeResources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Compute Resource with given id does not exist"),
    }, description = "Add a reference to an existing compute resource(that was previously created via a POST on /compute-resources/). " +
            "Custom ID will be ignored. " +
            "For the compute resource only the ID is required, other compute resource attributes will not change. " +
            "If the compute resource doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{id}/" + Constants.COMPUTE_RESOURCES + "/{crId}")
    public ResponseEntity<Void> addComputeResourceReferenceToSoftwarePlatform(
            @PathVariable UUID id,
            @PathVariable UUID crId) {
        softwarePlatformService.addComputeResourceReference(id, crId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform or Compute Resource with given id does not exist"),
    }, description = "Delete a reference to an compute resource of the software platform.")
    @DeleteMapping("/{id}/" + Constants.COMPUTE_RESOURCES + "/{crId}")
    public ResponseEntity<Void> deleteComputeResourceReferenceFromSoftwarePlatform(
            @PathVariable UUID id,
            @PathVariable UUID crId) {
        softwarePlatformService.deleteComputeResourceReference(id, crId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
