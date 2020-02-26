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
import org.planqk.quality.api.dtos.ImplementationDto;
import org.planqk.quality.api.dtos.ImplementationListDto;
import org.planqk.quality.api.dtos.ParameterDto;
import org.planqk.quality.api.dtos.ParameterListDto;
import org.planqk.quality.model.Algorithm;
import org.planqk.quality.model.Implementation;
import org.planqk.quality.model.Sdk;
import org.planqk.quality.repository.AlgorithmRepository;
import org.planqk.quality.repository.ImplementationRepository;
import org.planqk.quality.repository.SdkRepository;
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

import static org.planqk.quality.api.Constants.ALGORITHM_LINK;
import static org.planqk.quality.api.utils.RestUtils.parameterConsistent;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller to access and manipulate implementations of quantum algorithms.
 */
@RestController
@RequestMapping(Constants.ALGORITHMS + "/{algoId}/" + Constants.IMPLEMENTATIONS)
public class ImplementationController {

    final private static Logger LOG = LoggerFactory.getLogger(ImplementationController.class);

    private final ImplementationRepository implementationRepository;

    private final AlgorithmRepository algorithmRepository;

    private final SdkRepository sdkRepository;

    public ImplementationController(ImplementationRepository implementationRepository,
                                    AlgorithmRepository algorithmRepository, SdkRepository sdkRepository) {
        this.implementationRepository = implementationRepository;
        this.algorithmRepository = algorithmRepository;
        this.sdkRepository = sdkRepository;
    }

    @GetMapping("/")
    public HttpEntity<ImplementationListDto> getImplementations(@PathVariable Long algoId) {
        LOG.debug("Get to retrieve all implementations received.");
        ImplementationListDto dtoList = new ImplementationListDto();

        // add all available implementations to the response
        for(Implementation impl : implementationRepository.findAll()) {
            dtoList.add(createImplementationDto(algoId, impl));
            dtoList.add(linkTo(methodOn(ImplementationController.class).getImplementation(algoId, impl.getId()))
                    .withRel(impl.getId().toString()));
        }

        // add links and status code
        dtoList.add(linkTo(methodOn(ImplementationController.class).getImplementations(algoId)).withSelfRel());
        dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algoId)).withRel(ALGORITHM_LINK));
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping("/{implId}")
    public HttpEntity<ImplementationDto> getImplementation(@PathVariable Long algoId, @PathVariable Long implId) {
        LOG.debug("Get to retrieve implementation with id: {}.", implId);

        Optional<Implementation> implementationOptional = implementationRepository.findById(implId);
        if(!implementationOptional.isPresent()){
            LOG.error("Unable to retrieve implementation with id {} form the repository.", implId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(createImplementationDto(algoId, implementationOptional.get()), HttpStatus.OK);
    }

    @PostMapping("/")
    public HttpEntity<ImplementationDto> createImplementation(@PathVariable Long algoId, @RequestBody ImplementationDto impl) {
        LOG.debug("Post to create new implementation received.");

        Optional<Algorithm> algorithmOptional = algorithmRepository.findById(algoId);
        if(!algorithmOptional.isPresent()){
            LOG.error("Unable to retrieve algorithm with id {} from the repository.", algoId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check consistency of the implementation object
        if (Objects.isNull(impl.getName()) || Objects.isNull(impl.getRequiredQubits())
                || Objects.isNull(impl.getProgrammingLanguage()) || Objects.isNull(impl.getSelectionRule())
                || Objects.isNull(impl.getSdk())) {
            LOG.error("Received invalid implementation object for post request: {}", impl.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // retrieve referenced Sdk and abort if not present
        Optional<Sdk> sdkOptional = sdkRepository.findByName(impl.getSdk());
        if(!sdkOptional.isPresent()){
            LOG.error("Unable to retrieve Sdk with name {} from the repository.", impl.getSdk());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check consistency of passed parameters
        if(!parameterConsistent(impl.getInputParameters().getParameters(), impl.getOutputParameters().getParameters())){
            LOG.error("Received invalid parameter dto for post request.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LOG.debug("Received post request contains consistent data. Storing entity...");

        // store and return implementation
        Implementation implementation =
                implementationRepository.save(ImplementationDto.Converter.convert(impl, sdkOptional.get(), algorithmOptional.get()));
        return new ResponseEntity<>(createImplementationDto(algoId, implementation), HttpStatus.OK);
    }

    @GetMapping("/{id}/" + Constants.INPUT_PARAMS)
    public HttpEntity<ParameterListDto> getInputParameters(@PathVariable Long id) {
        LOG.debug("Get to retrieve input parameters for implementation with id: {}.", id);

        Optional<Implementation> implementationOptional = implementationRepository.findById(id);
        if(!implementationOptional.isPresent()){
            LOG.error("Unable to retrieve implementation with id {} form the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // convert all input parameters to corresponding dtos
        ParameterListDto parameterListDto = new ParameterListDto();
        parameterListDto.add(implementationOptional.get().getInputParameters().stream()
                .map(ParameterDto.Converter::convert)
                .collect(Collectors.toList()));

        parameterListDto.add(linkTo(methodOn(AlgorithmController.class).getInputParameters(id)).withSelfRel());
        return new ResponseEntity<>(parameterListDto, HttpStatus.OK);
    }

    @GetMapping("/{id}/" + Constants.OUTPUT_PARAMS)
    public HttpEntity<ParameterListDto> getOutputParameters(@PathVariable Long id) {
        LOG.debug("Get to retrieve output parameters for implementation with id: {}.", id);

        Optional<Implementation> implementationOptional = implementationRepository.findById(id);
        if(!implementationOptional.isPresent()){
            LOG.error("Unable to retrieve implementation with id {} form the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // convert all output parameters to corresponding dtos
        ParameterListDto parameterListDto = new ParameterListDto();
        parameterListDto.add(implementationOptional.get().getOutputParameters().stream()
                .map(ParameterDto.Converter::convert)
                .collect(Collectors.toList()));

        parameterListDto.add(linkTo(methodOn(AlgorithmController.class).getOutputParameters(id)).withSelfRel());
        return new ResponseEntity<>(parameterListDto, HttpStatus.OK);
    }

    /**
     * Create a DTO object for a given {@link Implementation} with the contained data and the links to related objects.
     *
     * @param algoId the Id of the Algorithm this Implementation belongs to
     * @param implementation the {@link Implementation} to create the DTO for
     * @return the created DTO
     */
    private ImplementationDto createImplementationDto(Long algoId, Implementation implementation){
        ImplementationDto dto = ImplementationDto.Converter.convert(implementation);
        dto.add(linkTo(methodOn(ImplementationController.class).getImplementation(algoId, implementation.getId())).withSelfRel());
        dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algoId)).withRel(Constants.ALGORITHM_LINK));

        Sdk usedSdk = implementation.getSdk();
        if(Objects.nonNull(usedSdk)){
            dto.add(linkTo(methodOn(SdkController.class).getSdk(usedSdk.getId())).withRel(Constants.USED_SDK));
        }

        return dto;
    }
}
