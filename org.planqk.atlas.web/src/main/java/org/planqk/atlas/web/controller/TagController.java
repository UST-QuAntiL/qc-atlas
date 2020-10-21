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

import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.TagAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_TAG)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.TAGS)
@ApiVersion("v1")
@AllArgsConstructor
@Slf4j
public class TagController {

    private final TagService tagService;

    private final TagAssembler tagAssembler;

    private final AlgorithmAssembler algorithmAssembler;

    private final ImplementationAssembler implementationAssembler;

    @Operation(responses = {
        @ApiResponse(responseCode = "200")
    }, description = "Retrieve all created tags.")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<TagDto>>> getTags(
        @Parameter(hidden = true) ListParameters listParameters) {
        return new ResponseEntity<>(tagAssembler.toModel(
            this.tagService.findAllByContent(listParameters.getSearch(), listParameters.getPageable())), HttpStatus.OK);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "201"),
        @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body.")
    }, description = "Create a new tag with its value and category.")
    @PostMapping
    public ResponseEntity<EntityModel<TagDto>> createTag(
        @Validated(ValidationGroups.Create.class) @RequestBody TagDto tagDto) {
        final Tag savedTag = this.tagService.create(ModelMapperUtils.convert(tagDto, Tag.class));
        return new ResponseEntity<>(tagAssembler.toModel(savedTag), HttpStatus.CREATED);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", description = "Tag with given value doesn't exist.")
    }, description = "Retrieve a specific tag.")
    @GetMapping("/{value}")
    public ResponseEntity<EntityModel<TagDto>> getTag(@PathVariable String value) {
        final Tag tag = this.tagService.findByValue(value);
        final var tagDto = tagAssembler.toModel(tag);
        return ResponseEntity.ok(tagDto);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200")
    }, description = "Retrieve all algorithms under a specific tag.")
    @GetMapping("/{value}/" + Constants.ALGORITHMS)
    public ResponseEntity<CollectionModel<EntityModel<AlgorithmDto>>> getAlgorithmsOfTag(@PathVariable String value) {
        final Tag tag = this.tagService.findByValue(value);
        final CollectionModel<EntityModel<AlgorithmDto>> algorithms = algorithmAssembler.toModel(tag.getAlgorithms());
        algorithmAssembler.addLinks(algorithms.getContent());
        tagAssembler.addAlgorithmLink(algorithms, tag.getValue());
        return new ResponseEntity<>(algorithms, HttpStatus.OK);
    }

    @Operation(responses = {
        @ApiResponse(responseCode = "200")
    }, description = "Retrieve all implementations under a specific tag.")
    @GetMapping("/{value}/" + Constants.IMPLEMENTATIONS)
    public ResponseEntity<CollectionModel<EntityModel<ImplementationDto>>> getImplementationsOfTag(
        @PathVariable String value) {
        final Tag tag = this.tagService.findByValue(value);
        final CollectionModel<EntityModel<ImplementationDto>> implementations = implementationAssembler.toModel(tag.getImplementations());
        implementationAssembler.addLinks(implementations.getContent());
        tagAssembler.addImplementationLink(implementations, tag.getValue());
        return new ResponseEntity<>(implementations, HttpStatus.OK);
    }
}
