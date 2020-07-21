package org.planqk.atlas.web.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputingResourceProperty;
import org.planqk.atlas.core.services.ComputeResourceService;
import org.planqk.atlas.core.services.ComputingResourcePropertyService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.ComputingResourcePropertyDto;
import org.planqk.atlas.web.linkassembler.ComputeResourceAssembler;
import org.planqk.atlas.web.linkassembler.ComputingResourcePropertyAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;
import org.planqk.atlas.web.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

//@io.swagger.v3.oas.annotations.tags.Tag(name = "backend")
//@RestController
//@CrossOrigin(allowedHeaders = "*", origins = "*")
//@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.BACKENDS)
@AllArgsConstructor
//@io.swagger.v3.oas.annotations.tags.Tag(name = "backend")
public class ComputeResourceController {

    final private static Logger LOG = LoggerFactory.getLogger(ComputeResourceController.class);

    private final ComputingResourcePropertyService computingResourcePropertyService;
    private final ComputingResourcePropertyAssembler computingResourcePropertyAssembler;
    private final ComputeResourceService computeResourceService;
    private final ComputeResourceAssembler computeResourceAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<ComputeResourceDto>>> getComputeResources(@RequestParam(required = false) Integer page,
                                                                                       @RequestParam(required = false) Integer size) {
        LOG.debug("Get to retrieve all ComputeResources received.");
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        var entities = computeResourceService.findAll(p);
        return ResponseEntity.ok(computeResourceAssembler.toModel(entities));
    }

    @Operation(responses = {@ApiResponse(responseCode = "201")})
    @PostMapping()
    public HttpEntity<EntityModel<ComputeResourceDto>> createComputeResource(@Valid @RequestBody ComputeResourceDto ComputeResourceDto) {
        LOG.debug("Post to add a single ComputeResource received.");
        ComputeResource computeResource = computeResourceService.saveOrUpdate(ModelMapperUtils.convert(ComputeResourceDto, ComputeResource.class));
        return new ResponseEntity<>(computeResourceAssembler.toModel(computeResource), HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", description = "ComputeResource with given id doesn't exist")})
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deleteComputeResource(@PathVariable UUID id) {
        LOG.debug("Delete to remove the ComputeResource with id {} received.", id);
        // only deletes if not used in any CloudService or SoftwarePlatform
        // we have to decide if this is acceptable behavior - TODO
        computeResourceService.findById(id);
        computeResourceService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201")})
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<ComputeResourceDto>> updateComputeResource(@PathVariable UUID id, @Valid @RequestBody ComputeResourceDto ComputeResourceDto) {
        LOG.debug("Put to update a single ComputeResource received.");
        ComputeResource computeResource = computeResourceService.saveOrUpdate(ModelMapperUtils.convert(ComputeResourceDto, ComputeResource.class));
        return new ResponseEntity<>(computeResourceAssembler.toModel(computeResource), HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<ComputeResourceDto>> getComputeResource(@PathVariable UUID id) {
        LOG.debug("Get to retrieve ComputeResource with id {} received", id);
        ComputeResource computeResource = computeResourceService.findById(id);
        return ResponseEntity.ok(computeResourceAssembler.toModel(computeResource));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @GetMapping("/{id}/" + Constants.COMPUTING_RESOURCES_PROPERTIES)
    public HttpEntity<PagedModel<EntityModel<ComputingResourcePropertyDto>>> getQuantumResources(
            @PathVariable UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        var resources = computingResourcePropertyService.findAllComputingResourcesPropertiesByBackendId(id,
                RestUtils.getPageableFromRequestParams(page, size));
        return ResponseEntity.ok(computingResourcePropertyAssembler.toModel(resources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @PostMapping("/{id}/" + Constants.COMPUTING_RESOURCES_PROPERTIES)
    public HttpEntity<EntityModel<ComputeResourceDto>> addQuantumResource(
            @PathVariable UUID id,
            @Valid @RequestBody ComputingResourcePropertyDto resourceDto
    ) {
        var ComputeResource = computeResourceService.findById(id);
        ValidationUtils.validateComputingResourceProperty(resourceDto);
        var resource = ModelMapperUtils.convert(resourceDto, ComputingResourceProperty.class);
        var updatedComputeResource = computingResourcePropertyService.addComputingResourcePropertyToBackend(ComputeResource, resource);
        return ResponseEntity.ok(computeResourceAssembler.toModel(updatedComputeResource));
    }
}
