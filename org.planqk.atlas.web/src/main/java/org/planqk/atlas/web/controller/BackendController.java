package org.planqk.atlas.web.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.Backend;
import org.planqk.atlas.core.model.ComputingResourceProperty;
import org.planqk.atlas.core.services.BackendService;
import org.planqk.atlas.core.services.ComputingResourcePropertyService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.BackendDto;
import org.planqk.atlas.web.dtos.ComputingResourcePropertyDto;
import org.planqk.atlas.web.linkassembler.BackendAssembler;
import org.planqk.atlas.web.linkassembler.ComputingResourcePropertyAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

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
public class BackendController {

    final private static Logger LOG = LoggerFactory.getLogger(BackendController.class);

    private final ComputingResourcePropertyService quantumResourceService;
    private final ComputingResourcePropertyAssembler quantumResourceAssembler;
    private BackendService backendService;
    private BackendAssembler backendAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<BackendDto>>> getBackends(@RequestParam(required = false) Integer page,
                                                                       @RequestParam(required = false) Integer size) {
        LOG.debug("Get to retrieve all Backends received.");
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        var entities = backendService.findAll(p);
        return ResponseEntity.ok(backendAssembler.toModel(entities));
    }

    @Operation(responses = {@ApiResponse(responseCode = "201")})
    @PostMapping()
    public HttpEntity<EntityModel<BackendDto>> createBackend(@Valid @RequestBody BackendDto backendDto) {
        LOG.debug("Post to add a single Backend received.");
        Backend backend = backendService.saveOrUpdate(ModelMapperUtils.convert(backendDto, Backend.class));
        return new ResponseEntity<>(backendAssembler.toModel(backend), HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", description = "Backend with given id doesn't exist")})
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deleteBackend(@PathVariable UUID id) {
        LOG.debug("Delete to remove the Backend with id {} received.", id);
        // only deletes if not used in any CloudService or SoftwarePlatform
        // we have to decide if this is acceptable behavior - TODO
        backendService.findById(id);
        backendService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201")})
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<BackendDto>> updateBackend(@PathVariable UUID id, @Valid @RequestBody BackendDto backendDto) {
        LOG.debug("Put to update a single Backend received.");
        Backend backend = backendService.saveOrUpdate(ModelMapperUtils.convert(backendDto, Backend.class));
        return new ResponseEntity<>(backendAssembler.toModel(backend), HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<BackendDto>> getBackend(@PathVariable UUID id) {
        LOG.debug("Get to retrieve Backend with id {} received", id);
        Backend backend = backendService.findById(id);
        return ResponseEntity.ok(backendAssembler.toModel(backend));
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
        var resources = quantumResourceService.findAllComputingResourcesPropertiesByBackendId(id,
                RestUtils.getPageableFromRequestParams(page, size));
        return ResponseEntity.ok(quantumResourceAssembler.toModel(resources));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @PostMapping("/{id}/" + Constants.COMPUTING_RESOURCES_PROPERTIES)
    public HttpEntity<EntityModel<BackendDto>> addQuantumResource(
            @PathVariable UUID id,
            @Valid @RequestBody ComputingResourcePropertyDto resourceDto
    ) {
        var backend = backendService.findById(id);
        var resource = ModelMapperUtils.convert(resourceDto, ComputingResourceProperty.class);
        var updatedBackend = quantumResourceService.addComputingResourcePropertyToBackend(backend, resource);
        return ResponseEntity.ok(backendAssembler.toModel(updatedBackend));
    }
}
