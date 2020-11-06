/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.services.FileService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.FileDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.linkassembler.FileAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
@ApiVersion("v1")
@AllArgsConstructor
@Slf4j
public class ImplementationGlobalController {

    private final ImplementationService implementationService;

    private final ImplementationAssembler implementationAssembler;

    private final FileAssembler fileAssembler;

    private final FileService fileService;

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
    }, description = "Retrieve all implementations unaffected by its implemented algorithm")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ImplementationDto>>> getImplementations(
        @Parameter(hidden = true) ListParameters listParameters) {
        final var implementations = implementationService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(implementationAssembler.toModel(implementations));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404",
            description = "Implementation with given ID doesn't exist")
    }, description = "Retrieve a specific implementation and its basic properties.")
    @GetMapping("/{implementationId}")
    public ResponseEntity<EntityModel<ImplementationDto>> getImplementation(
        @PathVariable UUID implementationId) {
        final var implementation = this.implementationService.findById(implementationId);
        return ResponseEntity.ok(implementationAssembler.toModel(implementation));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "201"),
        @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
    }, description = "Uploads and adds a file to a given implementation")
    @PostMapping("/{implementationId}/" + Constants.FILES)
    public ResponseEntity<EntityModel<FileDto>> createFileForImplementation(
        @PathVariable UUID implementationId,
        @RequestParam("file") MultipartFile multipartFile) {
        final File file = implementationService.addFileToImplementation(implementationId, multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileAssembler.toModel(file));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
    }, description = "Retrieve all files of an implementation")
    @GetMapping("/{implementationId}/" + Constants.FILES)
    public ResponseEntity<PagedModel<EntityModel<FileDto>>> getAllFilesOfImplementation(
        @PathVariable UUID implementationId,
        @Parameter(hidden = true) ListParameters listParameters
    ) {
        final Page<File> files =
            implementationService.findLinkedFiles(implementationId, listParameters.getPageable());
        return ResponseEntity.ok(fileAssembler.toModel(files));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404",
            description = "File of Implementation with given ID doesn't exist")
    }, description = "Retrieves a specific file of an Implementation and its basic properties.")
    @GetMapping("/{implementationId}/" + Constants.FILES + "/{fileId}")
    public ResponseEntity<EntityModel<FileDto>> getFileOfImplementation(
        @PathVariable UUID implementationId,
        @PathVariable UUID fileId
    ) {
        final File file =
            fileService.findById(fileId);
        return ResponseEntity.ok(fileAssembler.toModel(file));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404",
            description = "File of Implementation with given ID doesn't exist")
    }, description = "Downloads a specific file content of an Implementation")
    @GetMapping("/{implementationId}/" + Constants.FILES + "/{fileId}/content")
    public ResponseEntity<byte[]> downloadFileContent(
        @PathVariable UUID implementationId,
        @PathVariable UUID fileId
    ) {
        final File file =
            fileService.findById(fileId);
        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(file.getMimeType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
            .body(fileService.getFileContent(fileId));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "204"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404", description = "Not Found. Implementation or File with given IDs don't exist")
    }, description = "Delete a file of an implementation.")
    @DeleteMapping("/{implementationId}/" + Constants.FILES + "/{fileId}")
    public ResponseEntity<Void> deleteFileOfImplementation(@PathVariable UUID implementationId,
                                                           @PathVariable UUID fileId) {
        fileService.delete(fileId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
