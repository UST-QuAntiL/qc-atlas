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

import java.util.Objects;
import java.util.UUID;

import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.core.services.ComputeResourceService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.ComputeResourcePropertyMixin;
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
import org.planqk.atlas.web.utils.ValidationUtils;

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

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.COMPUTE_RESOURCES)
@AllArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_EXECUTION_ENVIRONMENTS)
@Slf4j
public class ComputeResourceController {

    private final ComputeResourcePropertyService computeResourcePropertyService;
    private final ComputeResourcePropertyAssembler computeResourcePropertyAssembler;
    private final ComputeResourcePropertyMixin computeResourcePropertyMixin;
    private final ComputeResourceService computeResourceService;
    private final ComputeResourceAssembler computeResourceAssembler;
    private final SoftwarePlatformAssembler softwarePlatformAssembler;
    private final CloudServiceAssembler cloudServiceAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all compute resources")
    @GetMapping
    @ListParametersDoc
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
        ComputeResource computeResource = computeResourceService.save(
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
    @PutMapping
    public ResponseEntity<EntityModel<ComputeResourceDto>> updateComputeResource(
            @Validated(ValidationGroups.Update.class) @RequestBody ComputeResourceDto computeResourceDto) {
        ComputeResource computeResource = computeResourceService.update(
                computeResourceDto.getId(), ModelMapperUtils.convert(computeResourceDto, ComputeResource.class));
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
        // only deletes if not used in any CloudService or SoftwarePlatform
        // we have to decide if this is acceptable behavior - TODO
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
            @ApiResponse(responseCode = "404", content = @Content, description = "Resource doesn't exist")
    }, description = "Get referenced software platform for a compute resource")
    @GetMapping("/{computeResourceId}/" + Constants.SOFTWARE_PLATFORMS)
    @ListParametersDoc
    public ResponseEntity<CollectionModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatformsOfComputeResource(
            @PathVariable UUID computeResourceId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var softwarePlatforms = computeResourceService.findLinkedSoftwarePlatforms(computeResourceId, listParameters.getPageable());
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatforms));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", content = @Content, description = "Resource doesn't exist")
    }, description = "Get referenced cloud services for a compute resource")
    @GetMapping("/{computeResourceId}/" + Constants.CLOUD_SERVICES)
    @ListParametersDoc
    public ResponseEntity<CollectionModel<EntityModel<CloudServiceDto>>> getCloudServicesOfComputeResource(
            @PathVariable UUID computeResourceId,
            @Parameter(hidden = true) ListParameters listParameters) {
        var cloudServices = computeResourceService.findLinkedComputeResources(computeResourceId, listParameters.getPageable());
        return ResponseEntity.ok(cloudServiceAssembler.toModel(cloudServices));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute Resource with given id does not exist")
    }, description = "Get referenced compute resource properties for a compute resource.")
    @GetMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES)
    @ListParametersDoc
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
    @PostMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES)
    public ResponseEntity<EntityModel<ComputeResourceDto>> createComputingResourcePropertyForComputeResource(
            @PathVariable UUID computeResourceId,
            @Validated(ValidationGroups.Create.class) @RequestBody ComputeResourcePropertyDto resourceDto) {
        var ComputeResource = computeResourceService.findById(computeResourceId);
        var resource = ModelMapperUtils.convert(resourceDto, ComputeResourceProperty.class);
        ValidationUtils.validateComputingResourceProperty(resource);
        var updatedComputeResource = computeResourcePropertyService.addComputeResourcePropertyToComputeResource(ComputeResource, resource);
        return ResponseEntity.ok(computeResourceAssembler.toModel(updatedComputeResource));
    }

//     @Operation(responses = {
//             @ApiResponse(responseCode = "200"),
//             @ApiResponse(responseCode = "400"),
//             @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")},
//             description = "Update a computing resource of the algorithm. Custom ID will be ignored." +
//                     "For computing resource type only ID is required, other computing resource type attributes will not change.")
//     @PutMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES)
//     public ResponseEntity<EntityModel<ComputeResourcePropertyDto>> updateComputingResourceResourcePropertyOfComputeResource(
//             @PathVariable UUID computeResourceId,
//             @Validated(ValidationGroups.Update.class) @RequestBody ComputeResourcePropertyDto resourceDto) {
//         ComputeResourceProperty computeResourceProperty = computeResourcePropertyService.findComputeResourcePropertyById(resourceDto.getId());
//         var computeResource = computeResourceService.findById(computeResourceId);
//         if (Objects.isNull(computeResourceProperty.getComputeResource()) ||
//                 !computeResourceProperty.getComputeResource().getId().equals(computeResourceId)) {
//             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//         }
//         ValidationUtils.validateComputingResourceProperty(resourceDto);
//         var resource = computeResourcePropertyMixin.fromDto(resourceDto);
//         resource.setId(resourceDto.getId());
//         var updatedResource = computeResourcePropertyService.addComputeResourcePropertyToComputeResource(computeResource, resource);
//         return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(updatedResource));
//     }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Computing resource with the given id doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404", description = "Algorithm or computing resource with given id doesn't exist")},
            description = "Delete a computing resource of the algorithm.")
    @DeleteMapping("/{computeResourceId}/" + Constants.COMPUTE_RESOURCES_PROPERTIES + "/{resourceId}")
    public ResponseEntity<Void> deleteComputingResourcePropertyFromComputeResource(
            @PathVariable UUID computeResourceId,
            @PathVariable UUID resourceId) {
        computeResourceService.findById(computeResourceId);
        var computingResourceProperty = computeResourcePropertyService.findById(resourceId);
        if (Objects.isNull(computingResourceProperty.getComputeResource()) ||
                !computingResourceProperty.getComputeResource().getId().equals(computeResourceId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        computeResourcePropertyService.delete(resourceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
