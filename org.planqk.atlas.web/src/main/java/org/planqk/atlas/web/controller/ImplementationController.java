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

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.ImplementationListDto;
import org.planqk.atlas.web.dtos.TagListDto;
import org.planqk.atlas.web.utils.RestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.planqk.atlas.web.Constants.ALGORITHM_LINK;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller to access and manipulate implementations of quantum algorithms.
 */
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHMS + "/{algoId}/" + Constants.IMPLEMENTATIONS)
public class ImplementationController {

    final private static Logger LOG = LoggerFactory.getLogger(ImplementationController.class);

    private final ImplementationService implementationService;
    private final AlgorithmService algorithmService;

    public ImplementationController(ImplementationService implementationService,
                                    AlgorithmService algorithmService) {
        this.implementationService = implementationService;
        this.algorithmService = algorithmService;
    }

    /**
     * Create a DTO object for a given {@link Implementation} with the contained data and the links to related objects.
     *
     * @param implementation the {@link Implementation} to create the DTO for
     * @return the created DTO
     */
    public static ImplementationDto createImplementationDto(Implementation implementation) {
        UUID algoId = implementation.getImplementedAlgorithm().getId();
        ImplementationDto dto = ImplementationDto.Converter.convert(implementation);
        dto.add(linkTo(methodOn(ImplementationController.class).getImplementation(algoId, implementation.getId())).withSelfRel());
        dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algoId)).withRel(Constants.ALGORITHM_LINK));
        dto.add(linkTo(methodOn(ImplementationController.class).getTags(algoId, implementation.getId())).withRel(Constants.TAGS));
        return dto;
    }

    @GetMapping("/")
    public HttpEntity<ImplementationListDto> getImplementations(@PathVariable UUID algoId) {
        LOG.debug("Get to retrieve all implementations received.");
        ImplementationListDto dtoList = new ImplementationListDto();

        // add all available implementations to the response
        for (Implementation impl : implementationService.findAll(RestUtils.getAllPageable())) {
            if (impl.getImplementedAlgorithm().getId().equals(algoId)) {
                dtoList.add(createImplementationDto(impl));
                dtoList.add(linkTo(methodOn(ImplementationController.class).getImplementation(algoId, impl.getId()))
                        .withRel(impl.getId().toString()));
            }
        }

        // add links and status code
        dtoList.add(linkTo(methodOn(ImplementationController.class).getImplementations(algoId)).withSelfRel());
        dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algoId)).withRel(ALGORITHM_LINK));
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping("/{implId}")
    public HttpEntity<ImplementationDto> getImplementation(@PathVariable UUID algoId, @PathVariable UUID implId) {
        LOG.debug("Get to retrieve implementation with id: {}.", implId);

        Optional<Implementation> implementationOptional = implementationService.findById(implId);
        if (!implementationOptional.isPresent()) {
            LOG.error("Unable to retrieve implementation with id {} form the repository.", implId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(createImplementationDto(implementationOptional.get()), HttpStatus.OK);
    }

    @PostMapping("/")
    public HttpEntity<ImplementationDto> createImplementation(@PathVariable UUID algoId, @RequestBody ImplementationDto impl) {
        LOG.debug("Post to create new implementation received.");

        Optional<Algorithm> algorithmOptional = algorithmService.findById(algoId);
        if (!algorithmOptional.isPresent()) {
            LOG.error("Unable to retrieve algorithm with id {} from the repository.", algoId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check consistency of the implementation object
        if (Objects.isNull(impl.getName()) || Objects.isNull(impl.getFileLocation())) {
            LOG.error("Received invalid implementation object for post request: {}", impl.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // store and return implementation
        Implementation implementation =
                implementationService.save(ImplementationDto.Converter.convert(impl, algorithmOptional.get()));
        return new ResponseEntity<>(createImplementationDto(implementation), HttpStatus.CREATED);
    }

    @GetMapping("/{implId}/" + Constants.TAGS)
    public HttpEntity<TagListDto> getTags(@PathVariable UUID algoId, @PathVariable UUID implId) {
        Optional<Implementation> implementationOptional = implementationService.findById(implId);
        if (!implementationOptional.isPresent()) {
            LOG.error("Unable to find implementation with id {} from the repository.", implId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TagListDto tagListDto = TagController.createTagDtoList(implementationOptional.get().getTags().stream());
        tagListDto.add(linkTo(methodOn(ImplementationController.class).getTags(algoId, implId)).withSelfRel());
        return new ResponseEntity<>(tagListDto, HttpStatus.OK);
    }
}
