package org.planqk.atlas.web.controller;

import java.util.UUID;

import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = "algorithm")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.IMPLEMENTATIONS)
@AllArgsConstructor
@Slf4j
public class GlobalImplementationController {
    private final ImplementationService implementationService;
    private final AlgorithmAssembler algorithmAssembler;
    private final ImplementationAssembler implementationAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")},
            description = "Retrieve a specific algorithm and its basic properties.")
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<AlgorithmDto>> getImplementedAlgorithm(@PathVariable UUID id) {
        var algorithm = implementationService.getImplementedAlgorithm(id);
        return ResponseEntity.ok(algorithmAssembler.toModel(algorithm));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
    }, description = "Retrieve all implementations")
    @GetMapping()
    @ListParametersDoc
    public ResponseEntity<PagedModel<EntityModel<ImplementationDto>>> getSoftwarePlatforms(
            @Parameter(hidden = true) ListParameters listParameters) {
        var platforms = implementationService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(implementationAssembler.toModel(platforms));
    }
}
