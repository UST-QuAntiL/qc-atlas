/*******************************************************************************
 * Copyright (c) 2020 University of Stuttgart
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

import javax.validation.Valid;

import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.services.ApplicationAreaService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.linkassembler.ApplicationAreaAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "application-areas")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.APPLICATION_AREAS)
@AllArgsConstructor
public class ApplicationAreaController {

    private ApplicationAreaService applicationAreaService;
    private PagedResourcesAssembler<ApplicationAreaDto> paginationAssembler;
    private ApplicationAreaAssembler applicationAreaAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "201")})
    @PostMapping()
    public HttpEntity<EntityModel<ApplicationAreaDto>> createApplicationArea(
            @Valid @RequestBody ApplicationAreaDto applicationAreaDto) {
        // Convert DTO to Entity
        ApplicationArea entityInput = ModelMapperUtils.convert(applicationAreaDto, ApplicationArea.class);
        // Save Entity
        ApplicationArea savedApplicationArea = applicationAreaService.save(entityInput);
        // Convert Entity to DTO
        ApplicationAreaDto dtoOutput = ModelMapperUtils.convert(savedApplicationArea, ApplicationAreaDto.class);
        // Generate EntityModel
        EntityModel<ApplicationAreaDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
        // Add Links
        applicationAreaAssembler.addLinks(entityDto);
        return new ResponseEntity<>(entityDto, HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<ApplicationAreaDto>> updateApplicationArea(@PathVariable UUID id,
                                                                             @Valid @RequestBody ApplicationAreaDto applicationAreaDto) {
        // Convert DTO to Entity
        ApplicationArea entityInput = ModelMapperUtils.convert(applicationAreaDto, ApplicationArea.class);
        // Update Entity
        ApplicationArea updatedEntity = applicationAreaService.update(id, entityInput);
        // Convert Entity to DTO
        ApplicationAreaDto dtoOutput = ModelMapperUtils.convert(updatedEntity, ApplicationAreaDto.class);
        // Generate EntityModel
        EntityModel<ApplicationAreaDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
        // Add Links
        applicationAreaAssembler.addLinks(entityDto);
        return new ResponseEntity<>(entityDto, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @DeleteMapping("/{id}")
    public HttpEntity<ApplicationAreaDto> deleteApplicationArea(@PathVariable UUID id) {
        ApplicationArea applicationArea = applicationAreaService.findById(id);
        applicationAreaService.delete(applicationArea);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<ApplicationAreaDto>>> getApplicationAreas(
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        // Generate Pageable
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        // Get Entities using pagable
        Page<ApplicationArea> entities = applicationAreaService.findAll(p);
        // Convert to DTO-Pageable
        Page<ApplicationAreaDto> dtos = ModelMapperUtils.convertPage(entities, ApplicationAreaDto.class);
        // Generate PagedModel with page links
        PagedModel<EntityModel<ApplicationAreaDto>> pagedEntityOutput = paginationAssembler.toModel(dtos);
        // Add DTO links
        applicationAreaAssembler.addLinks(pagedEntityOutput.getContent());
        return new ResponseEntity<>(pagedEntityOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<ApplicationAreaDto>> getApplicationAreaById(@PathVariable UUID id) {
        ApplicationArea applicationArea = applicationAreaService.findById(id);
        // Convert Entity to DTO
        ApplicationAreaDto dtoOutput = ModelMapperUtils.convert(applicationArea, ApplicationAreaDto.class);
        // Generate EntityModel
        EntityModel<ApplicationAreaDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
        // Add Links
        applicationAreaAssembler.addLinks(entityDto);
        return new ResponseEntity<>(entityDto, HttpStatus.OK);
    }
}
