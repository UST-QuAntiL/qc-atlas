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

import org.planqk.atlas.core.model.Provider;
import org.planqk.atlas.core.model.Qpu;
import org.planqk.atlas.core.services.ProviderService;
import org.planqk.atlas.core.services.QpuService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.QpuDto;
import org.planqk.atlas.web.linkassembler.QpuAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to access and manipulate quantum processing units (QPUs).
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "qpu")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.QPUS)
@ApiVersion("v1")
@AllArgsConstructor
public class QpuController {

    private final static Logger LOG = LoggerFactory.getLogger(QpuController.class);
    
    private QpuService qpuService;
    private ProviderService providerService;
    private PagedResourcesAssembler<QpuDto> paginationAssembler;
    private QpuAssembler qpuAssembler;

    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<QpuDto>>> getQpus(@RequestParam UUID providerId, @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size) {
        LOG.debug("Get to retrieve all QPUs received.");
        // Generate Pageable
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        // Generate PageDTO
        Page<QpuDto> pagedQpus = ModelMapperUtils.convertPage(qpuService.findAll(p), QpuDto.class); 
        // Generate PagedModel
        PagedModel<EntityModel<QpuDto>> dtoOutput = paginationAssembler.toModel(pagedQpus);
        // Add EntityModel Links
        qpuAssembler.addLinks(dtoOutput.getContent());
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content)
    })
    @GetMapping("/{qpuId}")
    public HttpEntity<EntityModel<QpuDto>> getQpu(@PathVariable UUID qpuId, @RequestParam UUID providerId) {
        LOG.debug("Get to retrieve QPU with id: {}.", qpuId);
        // Get Qpu
        Qpu qpu = qpuService.findById(qpuId);
        // Convert to EntityModel-DTO
        EntityModel<QpuDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(qpu, QpuDto.class));
        // Add Links
        qpuAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content)
    })
    @PostMapping("/")
    public HttpEntity<EntityModel<QpuDto>> createQpu(@RequestParam UUID providerId, @Validated @RequestBody QpuDto qpuRequest) {
        LOG.debug("Post to create new QPU received.");
        // Get provider if possible
        Provider provider = providerService.findById(providerId);
        // Convert input of request to Entity and add Algorithm
        Qpu inputEntity = ModelMapperUtils.convert(qpuRequest, Qpu.class);
        inputEntity.setProvider(provider);

        // store and return QPU
        Qpu qpu = qpuService.save(inputEntity);
        // Generate EntityModel for Output
        EntityModel<QpuDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(qpu, QpuDto.class));
        // Add Links
        qpuAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }
}
