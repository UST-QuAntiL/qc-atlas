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

import org.planqk.quality.api.Application;
import org.planqk.quality.api.Constants;
import org.planqk.quality.api.dtos.ImplementationListDto;
import org.planqk.quality.model.Implementation;
import org.planqk.quality.repository.ImplementationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller to access and manipulate implementations of quantum algorithms.
 */
@RestController
@RequestMapping(Constants.IMPLEMENTATIONS)
public class ImplementationController {

    final private static Logger LOG = LoggerFactory.getLogger(ImplementationController.class);

    private final ImplementationRepository implementationRepository;

    public ImplementationController(ImplementationRepository implementationRepository) {
        this.implementationRepository = implementationRepository;
    }

    @GetMapping("/")
    public HttpEntity<ImplementationListDto> getImplementations() {
        LOG.debug("Get to retrieve all implementations received.");
        ImplementationListDto dto = new ImplementationListDto();

        // add all available implementations to the response
        for(Implementation impl : implementationRepository.findAll()) {
            // TODO
        };

        // add links to related resources
        dto.add(linkTo(methodOn(ImplementationController.class).getImplementations()).withSelfRel());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
