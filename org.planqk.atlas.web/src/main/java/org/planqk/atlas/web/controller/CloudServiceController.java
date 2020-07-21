package org.planqk.atlas.web.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.services.CloudServiceService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.planqk.atlas.web.linkassembler.CloudServiceAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RequestParam;
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

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
    })
    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<CloudServiceDto>>> getCloudServices(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        var cloudServices = cloudServiceService.findAll(RestUtils.getPageableFromRequestParams(page, size));
        return ResponseEntity.ok(cloudServiceAssembler.toModel(cloudServices));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
    })
    @PostMapping()
    public HttpEntity<EntityModel<CloudServiceDto>> createCloudService(
            @Valid @RequestBody CloudServiceDto cloudServiceDto) {
        var savedCloudService = cloudServiceService.save(ModelMapperUtils.convert(cloudServiceDto, CloudService.class));
        return new ResponseEntity<>(cloudServiceAssembler.toModel(savedCloudService), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Cloud Service with given id does not exist")
    })
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<CloudServiceDto>> updateCloudService(
            @PathVariable UUID id,
            @Valid @RequestBody CloudServiceDto cloudServiceDto) {
        var updatedCloudService = cloudServiceService.update(id, ModelMapperUtils.convert(cloudServiceDto, CloudService.class));
        return ResponseEntity.ok(cloudServiceAssembler.toModel(updatedCloudService));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Cloud Service with given id does not exist"),
    })
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deleteCloudService(@PathVariable UUID id) {
        cloudServiceService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Cloud Service with given id does not exist"),
    })
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<CloudServiceDto>> getCloudService(
            @PathVariable UUID id) {
        var cloudServiceDto = ModelMapperUtils.convert(cloudServiceService.findById(id), CloudServiceDto.class);
        return ResponseEntity.ok(cloudServiceAssembler.toModel(cloudServiceDto));
    }
}
