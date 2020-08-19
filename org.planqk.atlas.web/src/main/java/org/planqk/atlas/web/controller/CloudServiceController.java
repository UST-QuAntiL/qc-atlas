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

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.services.CloudServiceService;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
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
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.CLOUD_SERVICES)
@AllArgsConstructor
@Slf4j
public class CloudServiceController {

    private final CloudServiceService cloudServiceService;
    private final CloudServiceAssembler cloudServiceAssembler;
    private final ComputeResourceAssembler computeResourceAssembler;
    private final SoftwarePlatformAssembler softwarePlatformAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
    }, description = "Retrieve all cloud services")
    @GetMapping()
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<CloudServiceDto>>> getCloudServices(
            @Parameter(hidden = true) ListParameters listParameters) {
        Page<CloudService> entities;
        if (listParameters.getSearch() == null || listParameters.getSearch().isEmpty()) {
            entities = cloudServiceService.findAll(listParameters.getPageable());
        } else {
            entities = cloudServiceService.searchAllByName(listParameters.getSearch(), listParameters.getPageable());
        }
        return ResponseEntity.ok(cloudServiceAssembler.toModel(entities));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
    }, description = "Define the basic properties of a cloud service. " +
            "References to sub-objects (e.g. a compute resource) " +
            "can be added via sub-routes (e.g. /cloud-services/{id}/compute-resources). " +
            "Custom ID will be ignored.")
    @PostMapping()
    public ResponseEntity<EntityModel<CloudServiceDto>> createCloudService(
            @Valid @RequestBody CloudServiceDto cloudServiceDto) {
        var savedCloudService = cloudServiceService.save(ModelMapperUtils.convert(cloudServiceDto, CloudService.class));
        return new ResponseEntity<>(cloudServiceAssembler.toModel(savedCloudService), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Resource doesn't exist")
    }, description = "Get referenced software platform for a  cloud service")
    @GetMapping("/{id}/" + Constants.SOFTWARE_PLATFORMS)
    @ListParametersDoc
    public ResponseEntity<CollectionModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatformsForCloudService(
            @PathVariable UUID id,
            @Parameter(hidden = true) ListParameters listParameters
    ) {
        var softwarePlatforms = cloudServiceService.findLinkedSoftwarePlatforms(id, listParameters.getPageable());
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatforms));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Cloud Service with given id does not exist")
    }, description = "Update the basic properties of a cloud service (e.g. name). " +
            "References to sub-objects (e.g. a compute resource) are not updated via this operation - " +
            "use the corresponding sub-route for updating them (e.g. /cloud-services/{id}/compute-resources). " +
            "Custom ID will be ignored.")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CloudServiceDto>> updateCloudService(
            @PathVariable UUID id,
            @Valid @RequestBody CloudServiceDto cloudServiceDto) {
        var updatedCloudService = cloudServiceService.update(id, ModelMapperUtils.convert(cloudServiceDto, CloudService.class));
        return ResponseEntity.ok(cloudServiceAssembler.toModel(updatedCloudService));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Cloud Service with given id does not exist"),
    }, description = "Delete a cloud service. " +
            "This also removes all references to other entities (e.g. compute resource)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCloudService(@PathVariable UUID id) {
        cloudServiceService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Cloud Service with given id does not exist"),
    }, description = "Retrieve a specific cloud service and its basic properties.")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CloudServiceDto>> getCloudService(
            @PathVariable UUID id) {
        var cloudService = cloudServiceService.findById(id);
        var cloudServiceDto = ModelMapperUtils.convert(cloudService, CloudServiceDto.class);
        return ResponseEntity.ok(cloudServiceAssembler.toModel(cloudServiceDto));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Cloud Service or Compute Resource with given id does not exist"),
    }, description = "Get referenced compute resources for a software platform.")
    @GetMapping("/{id}/" + Constants.COMPUTE_RESOURCES)
    @ListParametersDoc()
    public ResponseEntity<PagedModel<EntityModel<ComputeResourceDto>>> getComputeResourcesForCloudService(
            @PathVariable UUID id,
            @Parameter(hidden = true) ListParameters listParameters) {
        var computeResources = cloudServiceService.findComputeResources(id, listParameters.getPageable());
        return ResponseEntity.ok(computeResourceAssembler.toModel(computeResources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Cloud Service or Compute Resource with given id does not exist"),
    }, description = "Add a reference to an existing compute resource (that was previously created via a POST on /compute-resources/). " +
            "Custom ID will be ignored. " +
            "For the compute resource only the ID is required, other compute resource attributes will not change. " +
            "If the compute resource doesn't exist yet, a 404 error is thrown.")
    @PostMapping("/{id}/" + Constants.COMPUTE_RESOURCES + "/{crId}")
    public ResponseEntity<Void> addComputeResourceReferenceToCloudService(
            @PathVariable UUID id,
            @PathVariable UUID crId) {
        cloudServiceService.addComputeResourceReference(id, crId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Cloud Service or Compute Resource with given id does not exist"),
    }, description = "Get a specific referenced compute resource of a cloud service.")
    @DeleteMapping("/{id}/" + Constants.COMPUTE_RESOURCES + "/{crId}")
    public ResponseEntity<Void> deleteComputeResourceReferenceFromCloudService(
            @PathVariable UUID id,
            @PathVariable UUID crId) {
        cloudServiceService.deleteComputeResourceReference(id, crId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
