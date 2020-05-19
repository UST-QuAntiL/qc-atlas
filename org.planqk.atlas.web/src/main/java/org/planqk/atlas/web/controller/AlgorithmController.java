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
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmListDto;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller to access and manipulate quantum algorithms.
 */
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHMS)
public class AlgorithmController {

    final private static Logger LOG = LoggerFactory.getLogger(AlgorithmController.class);

    private AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    /**
     * Create a DTO object for a given {@link Algorithm} with the contained data and the links to related objects.
     *
     * @param algorithm the {@link Algorithm} to create the DTO for
     * @return the created DTO
     */
    public static AlgorithmDto createAlgorithmDto(Algorithm algorithm) {
        AlgorithmDto dto = AlgorithmDto.Converter.convert(algorithm);
        dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algorithm.getId())).withSelfRel());
        dto.add(linkTo(methodOn(AlgorithmController.class).getTags(algorithm.getId())).withRel(Constants.TAGS));
        dto.add(linkTo(methodOn(ImplementationController.class).getImplementations(algorithm.getId())).withRel(Constants.IMPLEMENTATIONS));
        return dto;
    }

    @GetMapping("/")
    public HttpEntity<AlgorithmListDto> getAlgorithms(@RequestParam(required = false) Integer page,
                                                      @RequestParam(required = false) Integer size) {
        LOG.debug("Get to retrieve all algorithms received.");
        AlgorithmListDto dtoList = new AlgorithmListDto();

        // add all available algorithms to the response
        for (Algorithm algo : algorithmService.findAll(RestUtils.getPageableFromRequestParams(page, size))) {
            dtoList.add(createAlgorithmDto(algo));
            dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algo.getId())).withRel(algo.getId().toString()));
        }

        // add self link and status code
        dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithms(null, null)).withSelfRel());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @PostMapping("/")
    public HttpEntity<AlgorithmDto> createAlgorithm(@RequestBody AlgorithmDto algo) {
        LOG.debug("Post to create new algorithm received.");

        if (Objects.isNull(algo.getName())) {
            LOG.error("Received invalid algorithm object for post request: {}", algo.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // store and return algorithm
        Algorithm algorithm = algorithmService.save(AlgorithmDto.Converter.convert(algo));
        return new ResponseEntity<>(createAlgorithmDto(algorithm), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public HttpEntity<AlgorithmDto> getAlgorithm(@PathVariable UUID id) {
        LOG.debug("Get to retrieve algorithm with id: {}.", id);

        Optional<Algorithm> algorithmOptional = algorithmService.findById(id);
        if (!algorithmOptional.isPresent()) {
            LOG.error("Unable to retrieve algorithm with id {} from the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(createAlgorithmDto(algorithmOptional.get()), HttpStatus.OK);
    }

    @GetMapping("/{id}/" + Constants.TAGS)
    public HttpEntity<TagListDto> getTags(@PathVariable UUID id) {
        Optional<Algorithm> algorithmOptional = algorithmService.findById(id);
        if (!algorithmOptional.isPresent()) {
            LOG.error("Unable to retrieve algorithm with id {} form the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Set<Tag> tags = algorithmOptional.get().getTags();
        TagListDto tagListDto = TagController.createTagDtoList(tags.stream());
        tagListDto.add(linkTo(methodOn(AlgorithmController.class).getTags(id)).withSelfRel());
        return new ResponseEntity<>(tagListDto, HttpStatus.OK);
    }
}
