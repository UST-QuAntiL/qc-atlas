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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    })
    @GetMapping()
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatforms(
            @Parameter(hidden = true) ListParameters listParameters) {
        var platforms = softwarePlatformService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(platforms));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
    }, description = "Custom ID will be ignored.")
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
    }, description = "Custom ID will be ignored.")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<SoftwarePlatformDto>> updateSoftwarePlatform(
            @PathVariable UUID id,
            @Valid @RequestBody SoftwarePlatformDto softwarePlatformDto) {
        var softwarePlatform = softwarePlatformService.update(
                id, ModelMapperUtils.convert(softwarePlatformDto, SoftwarePlatform.class));
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatform));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform with given id does not exist")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSoftwarePlatform(@PathVariable UUID id) {
        softwarePlatformService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Software Platform with given id does not exist"),
    })
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
    })
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
    })
    @PostMapping("/{id}" + Constants.IMPLEMENTATIONS + "/{implId}")
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
    })
    @GetMapping("/{id}" + Constants.IMPLEMENTATIONS + "/{implId}")
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
    })
    @DeleteMapping("/{id}" + Constants.IMPLEMENTATIONS + "/{implId}")
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
    })
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
    })
    @PostMapping("/{id}" + Constants.CLOUD_SERVICES + "/{csId}")
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
    })
    @DeleteMapping("/{id}" + Constants.CLOUD_SERVICES + "/{csId}")
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
    })
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
    })
    @PostMapping("/{id}" + Constants.COMPUTE_RESOURCES + "/{crId}")
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
    })
    @DeleteMapping("/{id}" + Constants.COMPUTE_RESOURCES + "/{crId}")
    public ResponseEntity<Void> deleteComputeResourceReferenceFromSoftwarePlatform(
            @PathVariable UUID id,
            @PathVariable UUID crId) {
        softwarePlatformService.deleteComputeResourceReference(id, crId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
