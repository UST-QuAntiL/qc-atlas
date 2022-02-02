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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.planqk.atlas.core.model.ToscaApplication;
import org.planqk.atlas.core.services.ToscaApplicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ToscaApplicationDto;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_TOSCA)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.TOSCA_APPLICATIONS)
@AllArgsConstructor
@Slf4j
public class ToscaApplicationController {

    private final ToscaApplicationService toscaApplicationService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all TOSCA applications.")
    @ListParametersDoc
    @GetMapping()
    public ResponseEntity<Page<ToscaApplicationDto>> getApplications(
            @Parameter(hidden = true) ListParameters listParameters) {
        return ResponseEntity.ok(ModelMapperUtils.convertPage(toscaApplicationService.findAll(listParameters.getPageable()), ToscaApplicationDto.class));
    }
    

    @Operation(responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
    }, description = "Create a TOSCA Application.")
    @PostMapping()
    public ResponseEntity<ToscaApplicationDto> createApplication(@RequestPart("file") MultipartFile file,
                                                                 @RequestPart("name") String name) {
        final ToscaApplication savedToscaApplication = this.toscaApplicationService.createFromFile(file, name);
        return new ResponseEntity<>(ModelMapperUtils.convert(savedToscaApplication, ToscaApplicationDto.class), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. TOSCA application with given ID doesn't exist.")
    }, description = "Retrieve a specific TOSCA application and its basic properties.")
    @GetMapping("/{toscaApplicationId}")
    public ResponseEntity<ToscaApplicationDto> getApplication(
            @PathVariable UUID toscaApplicationId) {
        final var application = this.toscaApplicationService.findById(toscaApplicationId);
        return ResponseEntity.ok(ModelMapperUtils.convert(application, ToscaApplicationDto.class));
    }

    @PutMapping("/{toscaApplicationId}")
    public ResponseEntity<ToscaApplicationDto> updateApplication(
            @PathVariable UUID toscaApplicationId,
            @Validated(ValidationGroups.Update.class) @RequestBody ToscaApplicationDto toscaApplicationDto) {
        toscaApplicationDto.setId(toscaApplicationId);
        final ToscaApplication updatedApplication = this.toscaApplicationService.update(
                ModelMapperUtils.convert(toscaApplicationDto, ToscaApplication.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(updatedApplication, ToscaApplicationDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Not Found. TOSCA application  with given ID doesn't exist.")
    }, description = "Delete a TOSCA application.")
    @DeleteMapping("/{toscaApplicationId}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable UUID toscaApplicationId) {
        this.toscaApplicationService.delete(toscaApplicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
