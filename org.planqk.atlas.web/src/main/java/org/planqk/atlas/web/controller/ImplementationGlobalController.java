/*******************************************************************************
 * Copyright (c) 2020-2021 the qc-atlas contributors.
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

import java.net.URI;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.PatternUriDto;
import org.planqk.atlas.web.dtos.RevisionDto;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller to access implementations outside of the context of its implemented algorithm.
 */
@Tag(name = Constants.TAG_IMPLEMENTATIONS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.IMPLEMENTATIONS)
@AllArgsConstructor
@Slf4j
public class ImplementationGlobalController {

    private final ImplementationService implementationService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
    }, description = "Retrieve all implementations unaffected by its implemented algorithm")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<Page<ImplementationDto>> getImplementations(@Parameter(hidden = true) ListParameters listParameters) {
        final var implementations = implementationService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(implementations, ImplementationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Implementation with given ID doesn't exist")
    }, description = "Retrieve a specific implementation and its basic properties.")
    @GetMapping("/{implementationId}")
    public ResponseEntity<ImplementationDto> getImplementation(@PathVariable UUID implementationId) {
        final var implementation = this.implementationService.findById(implementationId);
        return ResponseEntity.ok(ModelMapperUtils.convert(implementation, ImplementationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Implementation with given ID doesn't exist")
    }, description = "Retrieve all revisions of an implementation")
    @ListParametersDoc
    @GetMapping("/{implementationId}/" + Constants.REVISIONS)
    public ResponseEntity<Page<RevisionDto>> getImplementationRevisions(
            @PathVariable UUID implementationId, @Parameter(hidden = true) ListParameters listParameters) {
        final var implementationRevisions = implementationService.findImplementationRevisions(implementationId, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(implementationRevisions, RevisionDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Implementation with given ID and revision ID doesn't exist")
    }, description = "Retrieve a specific revision of an implementation and its basic properties")
    @GetMapping("/{implementationId}/" + Constants.REVISIONS + "/{revisionId}")
    public ResponseEntity<ImplementationDto> getImplementationRevision(
            @PathVariable UUID implementationId, @PathVariable Integer revisionId) {
        final Implementation implementationRevision = implementationService.findImplementationRevision(implementationId, revisionId).getEntity();
        return ResponseEntity.ok(ModelMapperUtils.convert(implementationRevision, ImplementationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404", description = "Not Found. Implementation with given IDs don't exist.")
    }, description = "Retrieve all patterns associated with a specific implementation.")
    @GetMapping("/{implementationId}/" + Constants.PATTERNS)
    public ResponseEntity<Collection<PatternUriDto>> getPatternsOfImplementation(
            @PathVariable UUID implementationId) {
        final Implementation implementation = implementationService.findById(implementationId);
        final Collection<PatternUriDto> patternUriDtos = implementation.getPatterns().stream().map(uri -> {
            final PatternUriDto dto = new PatternUriDto();
            dto.setPatternURI(uri);
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(patternUriDtos);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Implementation with given IDs don't exist.")
    }, description = "Add a pattern to an implementation. The pattern does not have to exist before adding it.")
    @PostMapping("/{implementationId}/" + Constants.PATTERNS)
    public ResponseEntity<Void> addPatternToImplementation(
            @PathVariable UUID implementationId,
            @Validated(ValidationGroups.Create.class) @RequestBody PatternUriDto patternDto) {
        final Implementation implementation = implementationService.findById(implementationId);
        final URI patternURI = patternDto.getPatternURI();
        implementation.addPattern(patternURI);
        implementationService.update(implementation);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Implementation with given IDs or Pattern don't exist.")
    }, description = "Remove a pattern from an implementation.")
    @DeleteMapping("/{implementationId}/" + Constants.PATTERNS)
    public ResponseEntity<Void> removePatternFromImplementation(
            @PathVariable UUID implementationId,
            @Validated(ValidationGroups.IDOnly.class) @RequestBody PatternUriDto patternDto) {
        final Implementation implementation = implementationService.findById(implementationId);
        implementation.removePattern(ModelMapperUtils.convert(patternDto, PatternUriDto.class).getPatternURI());
        implementationService.update(implementation);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404", description = "Not Found. Implementation with given IDs don't exist.")
    }, description = "Retrieve all implementations associated with a specific pattern.")
    @GetMapping("/" + Constants.PATTERNS)
    public ResponseEntity<Page<ImplementationDto>> getImplementationsOfPattern(
            @Parameter(hidden = true) ListParameters listParameters,
            @RequestParam("patternURI") URI patternURI) {
        final var implementations = implementationService.findByImplementedPatterns(patternURI, listParameters.getPageable());
        return ResponseEntity.ok(ModelMapperUtils.convertPage(implementations, ImplementationDto.class));
    }
}
