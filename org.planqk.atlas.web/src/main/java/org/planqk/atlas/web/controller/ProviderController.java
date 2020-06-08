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

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.ProviderDto;
import org.planqk.atlas.web.linkassembler.ProviderAssembler;
import org.planqk.atlas.core.services.ProviderService;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;
import org.planqk.atlas.core.model.Provider;

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
import org.springframework.web.bind.annotation.*;

/**
 * Controller to access and manipulate quantum computing providers.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "provider")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.PROVIDERS)
@ApiVersion("v1")
@AllArgsConstructor
public class ProviderController {

    final private static Logger LOG = LoggerFactory.getLogger(ProviderController.class);

    private ProviderService providerService;
    private PagedResourcesAssembler<ProviderDto> paginationAssembler;
    private ProviderAssembler providerAssembler;

    @Operation(responses = { @ApiResponse(responseCode = "200") })
    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<ProviderDto>>> getProviders(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        LOG.debug("Get to retrieve all providers received.");
        // Generate Pageable
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        // Retrieve Page of DTOs
        Page<ProviderDto> tags = ModelMapperUtils.convertPage(providerService.findAll(p), ProviderDto.class);
        // Generate PagedModel
        PagedModel<EntityModel<ProviderDto>> outputDto = paginationAssembler.toModel(tags);
        providerAssembler.addLinks(outputDto.getContent());
        return new ResponseEntity<>(outputDto, HttpStatus.OK);
    }

    @Operation(responses = { @ApiResponse(responseCode = "200") })
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<ProviderDto>> getProvider(@PathVariable UUID id) {
        LOG.debug("Get to retrieve provider with id: {}.", id);

        Provider provider = providerService.findById(id);

        // Generate EntityModel
        EntityModel<ProviderDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(provider, ProviderDto.class));
        providerAssembler.addLinks(dtoOutput);

        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = { @ApiResponse(responseCode = "201") })
    @PostMapping("/")
    public HttpEntity<EntityModel<ProviderDto>> createProvider(@Valid @RequestBody ProviderDto providerDto) {
        LOG.debug("Post to create new provider received.");
        // Save incoming provider inside database
        Provider provider = providerService.save(ModelMapperUtils.convert(providerDto, Provider.class));
        // Convert saved provider to EntityModelDTO
        EntityModel<ProviderDto> outputDto = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(provider, ProviderDto.class));
        // Add Links
        providerAssembler.addLinks(outputDto);
        return new ResponseEntity<>(outputDto, HttpStatus.CREATED);
    }
}
