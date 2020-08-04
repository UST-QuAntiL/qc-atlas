package org.planqk.atlas.web.controller;

import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.services.ComputeResourceService;
import org.planqk.atlas.core.services.ComputeResourcePropertyService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.ComputeResourcePropertyMixin;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.ComputeResourcePropertyDto;
import org.planqk.atlas.web.linkassembler.ComputeResourceAssembler;
import org.planqk.atlas.web.linkassembler.ComputeResourcePropertyAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all compute resources")
    @GetMapping()
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<ComputeResourceDto>>> getComputeResources(
            @Parameter(hidden = true) ListParameters listParameters) {
        var entities = computeResourceService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(computeResourceAssembler.toModel(entities));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201")
    }, description = "Define the basic properties of a compute resource. " +
            "References to sub-objects (e.g. a compute resource property) " +
            "can be added via sub-routes (e.g. /compute-resources/{id}/compute-resource-properties). " +
            "Custom ID will be ignored.")
    @PostMapping()
    public ResponseEntity<EntityModel<ComputeResourceDto>> createComputeResource(
            @Valid @RequestBody ComputeResourceDto computeResourceDto) {
        ComputeResource computeResource = computeResourceService.save(ModelMapperUtils.convert(computeResourceDto, ComputeResource.class));
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
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<ComputeResourceDto>> updateComputeResource(
            @PathVariable UUID id,
            @Valid @RequestBody ComputeResourceDto computeResourceDto) {
        ComputeResource computeResource = computeResourceService.update(id, ModelMapperUtils.convert(computeResourceDto, ComputeResource.class));
        return ResponseEntity.ok(computeResourceAssembler.toModel(computeResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute Resource with given id does not exist")
    }, description = "Delete a compute resource. " +
            "This also removes all references to other entities (e.g. software platform)")
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deleteComputeResource(
            @PathVariable UUID id) {
        // only deletes if not used in any CloudService or SoftwarePlatform
        // we have to decide if this is acceptable behavior - TODO
        computeResourceService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute Resource with given id does not exist")
    }, description = "Retrieve a specific compute resource and its basic properties.")
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<ComputeResourceDto>> getComputeResource(
            @PathVariable UUID id) {
        ComputeResource computeResource = computeResourceService.findById(id);
        return ResponseEntity.ok(computeResourceAssembler.toModel(computeResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute Resource with given id does not exist")
    }, description = "Get referenced compute resource properties for a compute resource.")
    @GetMapping("/{id}/" + Constants.COMPUTING_RESOURCES_PROPERTIES)
    @ListParametersDoc
    public HttpEntity<PagedModel<EntityModel<ComputeResourcePropertyDto>>> getComputingResourcePropertiesForComputeResource(
            @PathVariable UUID id,
            @Parameter(hidden = true) ListParameters listParameters
    ) {
        var resources = computeResourcePropertyService.findAllComputeResourcesPropertiesByComputeResourceId(id,
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
    @PostMapping("/{id}/" + Constants.COMPUTING_RESOURCES_PROPERTIES)
    public HttpEntity<EntityModel<ComputeResourceDto>> addComputingResourcePropertyToComputeResource(
            @PathVariable UUID id,
            @Valid @RequestBody ComputeResourcePropertyDto resourceDto
    ) {
        var ComputeResource = computeResourceService.findById(id);
        ValidationUtils.validateComputingResourceProperty(resourceDto);
        var resource = ModelMapperUtils.convert(resourceDto, ComputeResourceProperty.class);
        var updatedComputeResource = computeResourcePropertyService.addComputeResourcePropertyToComputeResource(ComputeResource, resource);
        return ResponseEntity.ok(computeResourceAssembler.toModel(updatedComputeResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")},
            description = "Update a computing resource of the algorithm. Custom ID will be ignored." +
                    "For computing resource type only ID is required, other computing resource type attributes will not change.")
    @PutMapping("/{crid}/" + Constants.COMPUTING_RESOURCES_PROPERTIES + "/{resourceId}")
    public HttpEntity<EntityModel<ComputeResourcePropertyDto>> updateComputingResourceResourcePropertyOfComputeResource(
            @PathVariable UUID crid,
            @PathVariable UUID resourceId,
            @RequestBody ComputeResourcePropertyDto resourceDto) {
        ComputeResourceProperty computeResourceProperty = computeResourcePropertyService.findComputeResourcePropertyById(resourceId);
        var computeResource = computeResourceService.findById(crid);
        if (Objects.isNull(computeResourceProperty.getComputeResource()) ||
                !computeResourceProperty.getComputeResource().getId().equals(crid)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ValidationUtils.validateComputingResourceProperty(resourceDto);
        var resource = computeResourcePropertyMixin.fromDto(resourceDto);
        resource.setId(resourceId);
        var updatedResource = computeResourcePropertyService.addComputeResourcePropertyToComputeResource(computeResource, resource);
        return ResponseEntity.ok(computeResourcePropertyAssembler.toModel(updatedResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Computing resource with the given id doesn't belong to this algorithm"),
            @ApiResponse(responseCode = "404", description = "Algorithm or computing resource with given id doesn't exist")},
            description = "Delete a computing resource of the algorithm.")
    @DeleteMapping("/{crid}/" + Constants.COMPUTING_RESOURCES_PROPERTIES + "/{resourceId}")
    public HttpEntity<Void> deleteComputingResourcePropertyFromComputeResource(
            @PathVariable UUID crid,
            @PathVariable UUID resourceId) {
        computeResourceService.findById(crid);
        var computingResourceProperty = computeResourcePropertyService.findComputeResourcePropertyById(resourceId);
        if (Objects.isNull(computingResourceProperty.getComputeResource()) ||
                !computingResourceProperty.getComputeResource().getId().equals(crid)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        computeResourcePropertyService.deleteComputeResourceProperty(resourceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
