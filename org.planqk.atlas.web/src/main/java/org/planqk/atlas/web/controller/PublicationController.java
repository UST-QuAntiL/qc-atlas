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

import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to access and manipulate publication algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "publication")
@Slf4j
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@AllArgsConstructor
@RequestMapping("/" + Constants.PUBLICATIONS)
public class PublicationController {

    private PublicationService publicationService;
    private PublicationAssembler publicationAssembler;
    private AlgorithmAssembler algorithmAssembler;
    private PagedResourcesAssembler<PublicationDto> paginationAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<PublicationDto>>> getPublications(@RequestParam(required = false) Integer page,
                                                                               @RequestParam(required = false) Integer size) {
        log.debug("Get all publications");
        Pageable pageable = RestUtils.getPageableFromRequestParams(page, size);
        Page<PublicationDto> dtoPage = ModelMapperUtils.convertPage(publicationService.findAll(pageable), PublicationDto.class);
        PagedModel<EntityModel<PublicationDto>> outputModel = paginationAssembler.toModel(dtoPage);
        publicationAssembler.addLinks(outputModel.getContent());
        return new ResponseEntity<>(outputModel, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @PostMapping("/")
    public HttpEntity<EntityModel<PublicationDto>> createPublication(@Validated @RequestBody PublicationDto publicationDto) {
        log.debug("Create publication");
        Publication publication = publicationService.save(ModelMapperUtils.convert(publicationDto, Publication.class));
        EntityModel<PublicationDto> dtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(publication, PublicationDto.class));
        publicationAssembler.addLinks(dtoEntityModel);
        return new ResponseEntity<>(dtoEntityModel, HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<PublicationDto>> getPublication(@PathVariable UUID id) {
        log.debug("Get publication with id: {}", id);
        Publication publication = publicationService.findById(id);
        EntityModel<PublicationDto> dtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(publication, PublicationDto.class));
        publicationAssembler.addLinks(dtoEntityModel);
        return new ResponseEntity<>(dtoEntityModel, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<PublicationDto>> updatePublication(@PathVariable UUID id, @Validated @RequestBody PublicationDto pub) {
        log.debug("Put to update algorithm with id: {}", id);
        Publication publication = publicationService.update(id, ModelMapperUtils.convert(pub, Publication.class));
        EntityModel<PublicationDto> dtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(publication, PublicationDto.class));
        publicationAssembler.addLinks(dtoEntityModel);
        return new ResponseEntity<>(dtoEntityModel, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @DeleteMapping("/{id}")
    public HttpEntity<AlgorithmDto> deletePublication(@PathVariable UUID id) {
        log.debug("Delete to remove algorithm with id: {}", id);
        publicationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}/" + Constants.ALGORITHMS)
    public HttpEntity<CollectionModel<EntityModel<AlgorithmDto>>> getAlgorithms(@PathVariable UUID id) {
        log.debug("Get algorithms of Publication with id {}", id);
        Set<Algorithm> algorithms = publicationService.findPublicationAlgorithms(id);
        Set<AlgorithmDto> algorithmDtos = ModelMapperUtils.convertSet(algorithms, AlgorithmDto.class);
        CollectionModel<EntityModel<AlgorithmDto>> resultCollection = HateoasUtils.generateCollectionModel(algorithmDtos);
        algorithmAssembler.addLinks(resultCollection);
        publicationAssembler.addAlgorithmLink(resultCollection, id);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }
}





