/*
 *  /*******************************************************************************
 *  * Copyright (c) 2020 University of Stuttgart
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License. You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License
 *  * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  * or implied. See the License for the specific language governing permissions and limitations under
 *  * the License.
 *  ******************************************************************************
 */

package org.planqk.quality.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.planqk.quality.api.Constants;
import org.planqk.quality.api.dtos.AlgorithmDto;
import org.planqk.quality.api.dtos.AlgorithmListDto;
import org.planqk.quality.api.dtos.ImplementationDto;
import org.planqk.quality.api.dtos.ParameterDto;
import org.planqk.quality.model.Algorithm;
import org.planqk.quality.model.Implementation;
import org.planqk.quality.model.Sdk;
import org.planqk.quality.repository.AlgorithmRepository;
import org.planqk.quality.repository.ImplementationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller to access and manipulate quantum algorithms.
 */
@RestController
@RequestMapping(Constants.ALGORITHMS)
public class AlgorithmController {

    final private static Logger LOG = LoggerFactory.getLogger(AlgorithmController.class);

    private final AlgorithmRepository algorithmRepository;

    public AlgorithmController(AlgorithmRepository algorithmRepository) {
        this.algorithmRepository = algorithmRepository;
    }

    @GetMapping("/")
    public HttpEntity<AlgorithmListDto> getAlgorithms() {
        LOG.debug("Get to retrieve all algorithms received.");
        AlgorithmListDto dtoList = new AlgorithmListDto();

        // add all available algorithms to the response
        for(Algorithm algo : algorithmRepository.findAll()) {
            dtoList.add(createAlgorithmDto(algo));
            dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algo.getId())).withRel(algo.getId().toString()));
        };

        // add self link and status code
        dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithms()).withSelfRel());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @PostMapping("/")
    public HttpEntity<AlgorithmDto> createAlgorithm(@RequestBody AlgorithmDto algo) {
        LOG.debug("Post to create new algorithm received.");

        if(Objects.isNull(algo) || Objects.isNull(algo.getName()) || Objects.isNull(algo.getRequiredQubits())){
            LOG.error("Received invalid algorithm object for post request: {}", algo.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check consistency of passed parameters
        List<ParameterDto> parameters = new ArrayList<>();
        parameters.addAll(algo.getInputParameters().getParameters());
        parameters.addAll(algo.getOutputParameters().getParameters());
        for(ParameterDto param : parameters){
            if(Objects.isNull(param.getName()) || Objects.isNull(param.getType())){
                LOG.error("Received invalid parameter dto for post request: {}", param.toString());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        // store and return algorithm
        Algorithm algorithm = algorithmRepository.save(AlgorithmDto.Converter.convert(algo));
        return new ResponseEntity<>(createAlgorithmDto(algorithm), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public HttpEntity<AlgorithmDto> getAlgorithm(@PathVariable Long id) {
        LOG.debug("Get to retrieve algorithm with id: {}.", id);

        Optional<Algorithm> algorithmOptional = algorithmRepository.findById(id);
        if(!algorithmOptional.isPresent()){
            LOG.error("Unable to retrieve algorithm with id {} form the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(createAlgorithmDto(algorithmOptional.get()), HttpStatus.OK);
    }

    @GetMapping("/{id}/parameters")
    public HttpEntity<AlgorithmDto> getParameters(@PathVariable Long id) {
        // TODO
        return new ResponseEntity<>(null, HttpStatus.OK); // TODO
    }

    /**
     * Create a DTO object for a given {@link Algorithm} with the contained data and the links to related objects.
     *
     * @param algorithm the {@link Algorithm} to create the DTO for
     * @return the created DTO
     */
    private AlgorithmDto createAlgorithmDto(Algorithm algorithm){
        AlgorithmDto dto = AlgorithmDto.Converter.convert(algorithm);
        dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algorithm.getId())).withSelfRel());
        return dto;
    }
}
