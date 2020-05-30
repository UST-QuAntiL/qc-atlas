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

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.linkassembler.TagAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to access and manipulate implementations of quantum algorithms.
 */
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHMS + "/{algoId}/" + Constants.IMPLEMENTATIONS)
public class ImplementationController {

    final private static Logger LOG = LoggerFactory.getLogger(ImplementationController.class);

    @Autowired
    private ImplementationService implementationService;
    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private ImplementationAssembler implementationAssembler;
    @Autowired
    private TagAssembler tagAssembler;

    @GetMapping("/")
    public HttpEntity<CollectionModel<EntityModel<ImplementationDto>>> getImplementations(@PathVariable UUID algoId) {
        LOG.debug("Get to retrieve all implementations received.");

        Set<ImplementationDto> dtoList = new HashSet<ImplementationDto>();

        // add all available implementations to the response
        for (Implementation impl : implementationService.findAll(RestUtils.getAllPageable())) {
            if (impl.getImplementedAlgorithm().getId().equals(algoId)) {
                dtoList.add(ModelMapperUtils.convert(impl, ImplementationDto.class));
            }
        }
        // Generate CollectionModel
        CollectionModel<EntityModel<ImplementationDto>> dtoOutput = HateoasUtils.generateCollectionModel(dtoList);
        // Add EntityLinks
        implementationAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @GetMapping("/{implId}")
    public HttpEntity<EntityModel<ImplementationDto>> getImplementation(@PathVariable UUID algoId, @PathVariable UUID implId) throws NotFoundException {
        LOG.debug("Get to retrieve implementation with id: {}.", implId);

        Optional<Implementation> implementationOptional = implementationService.findById(implId);
        if (!implementationOptional.isPresent()) {
            LOG.error("Unable to retrieve implementation with id {} form the repository.", implId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Generate EntityModel
        EntityModel<ImplementationDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(implementationOptional.get(), ImplementationDto.class));
        // Fill Links
        implementationAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @PostMapping("/")
    public HttpEntity<EntityModel<ImplementationDto>> createImplementation(@PathVariable UUID algoId, @RequestBody ImplementationDto impl) throws NotFoundException {
        LOG.debug("Post to create new implementation received.");

        Algorithm algorithm = algorithmService.findById(algoId);

        // check consistency of the implementation object
        if (Objects.isNull(impl.getName()) || Objects.isNull(impl.getFileLocation())) {
            LOG.error("Received invalid implementation object for post request: {}", impl.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // store and return implementation
        Implementation input = ModelMapperUtils.convert(impl, Implementation.class);
        input.setImplementedAlgorithm(algorithm);
        // Generate EntityModel
        EntityModel<ImplementationDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(implementationService.save(input), ImplementationDto.class));
        // Add Links
        implementationAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }

    @GetMapping("/{implId}/" + Constants.TAGS)
    public HttpEntity<CollectionModel<EntityModel<TagDto>>> getTags(@PathVariable UUID algoId, @PathVariable UUID implId) {
        Optional<Implementation> implementationOptional = implementationService.findById(implId);
        if (!implementationOptional.isPresent()) {
            LOG.error("Unable to find implementation with id {} from the repository.", implId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Get Tags of Algorithm
        Set<Tag> tags = implementationOptional.get().getTags();
        // Translate Entity to DTO
        Set<TagDto> dtoTags = ModelMapperUtils.convertSet(tags, TagDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<TagDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTags);
        // Fill EntityModel Links
        tagAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        implementationAssembler.addTagLink(resultCollection, implId, algoId);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }
}
