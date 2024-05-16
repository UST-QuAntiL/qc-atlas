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

import java.util.UUID;

import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.Solution;
import org.planqk.atlas.core.services.FileService;
import org.planqk.atlas.core.services.SolutionService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.FileDto;
import org.planqk.atlas.web.dtos.SolutionDto;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller to access and manipulate implementations of quantum algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_SOLUTION)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.SOLUTIONS)
@AllArgsConstructor
@Slf4j
public class SolutionController {

    private final SolutionService solutionService;

    private final FileService fileService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all solutions.")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<Page<SolutionDto>> getSolutions(
            @Parameter(hidden = true) ListParameters listParameters) {
        return ResponseEntity.ok(ModelMapperUtils.convertPage(solutionService.findAll(listParameters.getPageable()), SolutionDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
    }, description = "Define the basic properties of a solution. ")
    @PostMapping
    public ResponseEntity<SolutionDto> createSolution(
            @Validated(ValidationGroups.Create.class) @RequestBody SolutionDto solutionDto) {
        final Solution savedSolution = solutionService.create(ModelMapperUtils.convert(solutionDto, Solution.class));

        return new ResponseEntity<>(ModelMapperUtils.convert(savedSolution, SolutionDto.class), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Solution with the given ID doesn't exist.")
    }, description = "Update the basic properties of a solution. ")
    @PutMapping("/{solutionId}")
    public ResponseEntity<SolutionDto> updateSolution(
            @PathVariable UUID solutionId,
            @Validated(ValidationGroups.Update.class) @RequestBody SolutionDto solutionDto) {
        solutionDto.setId(solutionId);
        final Solution updatedSolution = solutionService.update(ModelMapperUtils.convert(solutionDto, Solution.class));

        return ResponseEntity.ok(ModelMapperUtils.convert(updatedSolution, SolutionDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Solution with the given ID doesn't exist.")
    }, description = "Delete a solution. ")
    @DeleteMapping("/{solutionId}")
    public ResponseEntity<Void> deleteSolution(
            @PathVariable UUID solutionId) {
        solutionService.delete(solutionId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "404",
                    description = "Not Found. Solution with the given ID doesn't exist.")
    }, description = "Retrieve a specific solution.")
    @GetMapping("/{solutionId}")
    public ResponseEntity<SolutionDto> getSolution(
            @PathVariable UUID solutionId) {
        final var solution = solutionService.findById(solutionId);

        return ResponseEntity.ok(ModelMapperUtils.convert(solution, SolutionDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
    }, description = "Uploads and adds a file to a given solution")
    @PostMapping(value = "/{solutionId}/" + Constants.FILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDto> createFileForSolution(
            @PathVariable UUID solutionId,
            @RequestParam("file") MultipartFile multipartFile) {
        final File file = solutionService.addFileToSolution(solutionId, multipartFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(ModelMapperUtils.convert(file, FileDto.class));
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
    }, description = "Retrieve the file of a solution")
    @GetMapping("/{solutionId}/" + Constants.FILE)
    public ResponseEntity<FileDto> getFileOfSolution(
            @PathVariable UUID solutionId
    ) {
        final var solution = solutionService.findById(solutionId);

        return ResponseEntity.ok(ModelMapperUtils.convert(solution.getFile(), FileDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",
                    description = "File of Solution with given ID doesn't exist")
    }, description = "Downloads a specific file content of a Solution")
    @GetMapping("/{solutionId}/" + Constants.FILE + "/content")
    public ResponseEntity<byte[]> downloadFileContent(
            @PathVariable UUID solutionId
    ) {
        final File file = solutionService.findById(solutionId).getFile();

        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .body(fileService.getFileContent(file.getId()));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. Solution or File with given IDs doesn't exist")
    }, description = "Delete a file of a solution.")
    @DeleteMapping("/{solutionId}/" + Constants.FILE)
    public ResponseEntity<Void> deleteFileOfSolution(
            @PathVariable UUID solutionId) {
        final File file = solutionService.findById(solutionId).getFile();

        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final var solution = solutionService.findById(solutionId);
        solution.setFile(null);
        fileService.delete(file.getId());

        solutionService.update(solution);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
