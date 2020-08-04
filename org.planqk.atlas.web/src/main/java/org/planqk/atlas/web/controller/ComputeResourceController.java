package org.planqk.atlas.web.controller;

import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputingResourceProperty;
import org.planqk.atlas.core.services.ComputeResourceService;
import org.planqk.atlas.core.services.ComputingResourcePropertyService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.mixin.ComputingResourceMixin;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.ComputingResourcePropertyDto;
import org.planqk.atlas.web.linkassembler.ComputeResourceAssembler;
import org.planqk.atlas.web.linkassembler.ComputingResourcePropertyAssembler;
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

    private final ComputingResourcePropertyService computingResourcePropertyService;
    private final ComputingResourcePropertyAssembler computingResourcePropertyAssembler;
    private final ComputingResourceMixin computingResourceMixin;
    private final ComputeResourceService computeResourceService;
    private final ComputeResourceAssembler computeResourceAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping()
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<ComputeResourceDto>>> getComputeResources(
            @Parameter(hidden = true) ListParameters listParameters) {
        var entities = computeResourceService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(computeResourceAssembler.toModel(entities));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201")
    })
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
    })
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
    })
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
    })
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
    })
    @GetMapping("/{id}/" + Constants.COMPUTING_RESOURCES_PROPERTIES)
    @ListParametersDoc
    public HttpEntity<PagedModel<EntityModel<ComputingResourcePropertyDto>>> getComputingResourcePropertiesForComputeResource(
            @PathVariable UUID id,
            @Parameter(hidden = true) ListParameters listParameters
    ) {
        var resources = computingResourcePropertyService.findAllComputingResourcesPropertiesByComputeResourceId(id,
                listParameters.getPageable());
        return ResponseEntity.ok(computingResourcePropertyAssembler.toModel(resources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Compute Resource with given id does not exist")
    })
    @PostMapping("/{id}/" + Constants.COMPUTING_RESOURCES_PROPERTIES)
    public HttpEntity<EntityModel<ComputeResourceDto>> addComputingResourcePropertyToComputeResource(
            @PathVariable UUID id,
            @Valid @RequestBody ComputingResourcePropertyDto resourceDto
    ) {
        var ComputeResource = computeResourceService.findById(id);
        ValidationUtils.validateComputingResourceProperty(resourceDto);
        var resource = ModelMapperUtils.convert(resourceDto, ComputingResourceProperty.class);
        var updatedComputeResource = computingResourcePropertyService.addComputingResourcePropertyToComputeResource(ComputeResource, resource);
        return ResponseEntity.ok(computeResourceAssembler.toModel(updatedComputeResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Algorithm with the given id doesn't exist")},
            description = "Update a computing resource of the algorithm. Custom ID will be ignored." +
                    "For computing resource type only ID is required, other computing resource type attributes will not change.")
    @PutMapping("/{crid}/" + Constants.COMPUTING_RESOURCES_PROPERTIES + "/{resourceId}")
    public HttpEntity<EntityModel<ComputingResourcePropertyDto>> updateComputingResourceResourcePropertyOfComputeResource(
            @PathVariable UUID crid,
            @PathVariable UUID resourceId,
            @RequestBody ComputingResourcePropertyDto resourceDto) {
        ComputingResourceProperty computingResourceProperty = computingResourcePropertyService.findComputingResourcePropertyById(resourceId);
        var computeResource = computeResourceService.findById(crid);
        if (Objects.isNull(computingResourceProperty.getComputeResource()) ||
                !computingResourceProperty.getComputeResource().getId().equals(crid)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ValidationUtils.validateComputingResourceProperty(resourceDto);
        var resource = computingResourceMixin.fromDto(resourceDto);
        resource.setId(resourceId);
        var updatedResource = computingResourcePropertyService.addComputingResourcePropertyToComputeResource(computeResource, resource);
        return ResponseEntity.ok(computingResourcePropertyAssembler.toModel(updatedResource));
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
        var computingResourceProperty = computingResourcePropertyService.findComputingResourcePropertyById(resourceId);
        if (Objects.isNull(computingResourceProperty.getComputeResource()) ||
                !computingResourceProperty.getComputeResource().getId().equals(crid)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        computingResourcePropertyService.deleteComputingResourceProperty(resourceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
