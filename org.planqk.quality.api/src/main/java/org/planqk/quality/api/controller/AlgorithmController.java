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

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.planqk.quality.api.Constants;
import org.planqk.quality.api.dtos.entities.AlgorithmDto;
import org.planqk.quality.api.dtos.entities.AlgorithmListDto;
import org.planqk.quality.api.dtos.entities.ParameterDto;
import org.planqk.quality.api.dtos.entities.ParameterListDto;
import org.planqk.quality.model.Algorithm;
import org.planqk.quality.repository.AlgorithmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.planqk.quality.api.utils.RestUtils.parameterConsistent;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller to access and manipulate quantum algorithms.
 */
@RestController
@RequestMapping("/" + Constants.ALGORITHMS)
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
        for (Algorithm algo : algorithmRepository.findAll()) {
            dtoList.add(createAlgorithmDto(algo));
            dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algo.getId())).withRel(algo.getId().toString()));
        }

        // add self link and status code
        dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithms()).withSelfRel());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @PostMapping("/")
    public HttpEntity<AlgorithmDto> createAlgorithm(@RequestBody AlgorithmDto algo) {
        LOG.debug("Post to create new algorithm received.");

        if (Objects.isNull(algo.getName()) || Objects.isNull(algo.getRequiredQubits())) {
            LOG.error("Received invalid algorithm object for post request: {}", algo.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check consistency of passed parameters
        if (!parameterConsistent(algo.getInputParameters().getParameters(), algo.getOutputParameters().getParameters())) {
            LOG.error("Received invalid parameter dto for post request.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // store and return algorithm
        Algorithm algorithm = algorithmRepository.save(AlgorithmDto.Converter.convert(algo));
        return new ResponseEntity<>(createAlgorithmDto(algorithm), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public HttpEntity<AlgorithmDto> getAlgorithm(@PathVariable Long id) {
        LOG.debug("Get to retrieve algorithm with id: {}.", id);

        Optional<Algorithm> algorithmOptional = algorithmRepository.findById(id);
        if (!algorithmOptional.isPresent()) {
            LOG.error("Unable to retrieve algorithm with id {} from the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(createAlgorithmDto(algorithmOptional.get()), HttpStatus.OK);
    }

    @GetMapping("/{id}/" + Constants.INPUT_PARAMS)
    public HttpEntity<ParameterListDto> getInputParameters(@PathVariable Long id) {
        LOG.debug("Get to retrieve input parameters for algorithm with id: {}.", id);

        Optional<Algorithm> algorithmOptional = algorithmRepository.findById(id);
        if (!algorithmOptional.isPresent()) {
            LOG.error("Unable to retrieve algorithm with id {} form the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // convert all output parameters to corresponding dtos
        ParameterListDto parameterListDto = new ParameterListDto();
        parameterListDto.add(algorithmOptional.get().getInputParameters().stream()
                .map(ParameterDto.Converter::convert)
                .collect(Collectors.toList()));

        parameterListDto.add(linkTo(methodOn(AlgorithmController.class).getInputParameters(id)).withSelfRel());
        return new ResponseEntity<>(parameterListDto, HttpStatus.OK);
    }

    @GetMapping("/{id}/" + Constants.OUTPUT_PARAMS)
    public HttpEntity<ParameterListDto> getOutputParameters(@PathVariable Long id) {
        LOG.debug("Get to retrieve output parameters for algorithm with id: {}.", id);

        Optional<Algorithm> algorithmOptional = algorithmRepository.findById(id);
        if (!algorithmOptional.isPresent()) {
            LOG.error("Unable to retrieve algorithm with id {} form the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // convert all input parameters to corresponding dtos
        ParameterListDto parameterListDto = new ParameterListDto();
        parameterListDto.add(algorithmOptional.get().getOutputParameters().stream()
                .map(ParameterDto.Converter::convert)
                .collect(Collectors.toList()));

        parameterListDto.add(linkTo(methodOn(AlgorithmController.class).getOutputParameters(id)).withSelfRel());
        return new ResponseEntity<>(parameterListDto, HttpStatus.OK);
    }

    /**
     * Create a DTO object for a given {@link Algorithm} with the contained data and the links to related objects.
     *
     * @param algorithm the {@link Algorithm} to create the DTO for
     * @return the created DTO
     */
    private AlgorithmDto createAlgorithmDto(Algorithm algorithm) {
        AlgorithmDto dto = AlgorithmDto.Converter.convert(algorithm);
        dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algorithm.getId())).withSelfRel());
        dto.add(linkTo(methodOn(AlgorithmController.class).getInputParameters(algorithm.getId())).withRel(Constants.INPUT_PARAMS));
        dto.add(linkTo(methodOn(AlgorithmController.class).getOutputParameters(algorithm.getId())).withRel(Constants.OUTPUT_PARAMS));
        dto.add(linkTo(methodOn(ImplementationController.class).getImplementations(algorithm.getId())).withRel(Constants.IMPLEMENTATIONS));
        return dto;
    }
}
