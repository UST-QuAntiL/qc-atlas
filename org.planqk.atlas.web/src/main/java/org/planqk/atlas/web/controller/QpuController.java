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

import org.planqk.atlas.core.model.Provider;
import org.planqk.atlas.core.model.Qpu;
import org.planqk.atlas.core.services.ProviderService;
import org.planqk.atlas.core.services.QpuService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.QpuDto;
import org.planqk.atlas.web.dtos.QpuListDto;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
 * Controller to access and manipulate quantum processing units (QPUs).
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "qpu")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.QPUS)
@ApiVersion("v1")
public class QpuController {

    private final static Logger LOG = LoggerFactory.getLogger(QpuController.class);
    private final QpuService qpuService;
    private final ProviderService providerService;

    public QpuController(QpuService qpuService, ProviderService providerService) {
        this.qpuService = qpuService;
        this.providerService = providerService;
    }

    @GetMapping("/")
    public HttpEntity<QpuListDto> getQpus(@RequestParam UUID providerId,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size) {
        LOG.debug("Get to retrieve all QPUs received.");
        QpuListDto qpuListDto = new QpuListDto();

        // add all available algorithms to the response
        for (Qpu qpu : qpuService.findAll(RestUtils.getAllPageable())) {
            if (qpu.getProvider().getId().equals(providerId)) {
                qpuListDto.add(createQpuDto(qpu));
                qpuListDto.add(linkTo(methodOn(QpuController.class).getQpu(qpu.getId())).withRel(qpu.getId().toString()));
            }
        }

        qpuListDto.add(linkTo(methodOn(QpuController.class).getQpus(providerId, Constants.DEFAULT_PAGE_NUMBER, Constants.DEFAULT_PAGE_SIZE)).withSelfRel());
        return new ResponseEntity<>(qpuListDto, HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content)
    })
    @GetMapping("/{qpuId}")
    public HttpEntity<QpuDto> getQpu(@PathVariable UUID qpuId) {
        LOG.debug("Get to retrieve QPU with id: {}.", qpuId);

        Optional<Qpu> qpuOptional = qpuService.findById(qpuId);
        if (!qpuOptional.isPresent()) {
            LOG.error("Unable to retrieve QPU with id {} from the repository.", qpuId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(createQpuDto(qpuOptional.get()), HttpStatus.OK);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content)
    })
    @PostMapping("/")
    public HttpEntity<QpuDto> createQpu(@RequestParam UUID providerId, @RequestBody QpuDto qpuRequest) {
        LOG.debug("Post to create new QPU received.");

        Optional<Provider> providerOptional = providerService.findById(providerId);
        if (!providerOptional.isPresent()) {
            LOG.error("Unable to retrieve provider with id {} from the repository.", providerId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // check consistency of the QPU object
        if (Objects.isNull(qpuRequest.getName())) {
            LOG.error("Received invalid QPU object for post request: {}", qpuRequest.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // store and return QPU
        Qpu qpu = qpuService.save(QpuDto.Converter.convert(qpuRequest, providerOptional.get()));
        return new ResponseEntity<>(createQpuDto(qpu), HttpStatus.OK);
    }

    /**
     * Create a DTO object for a given {@link Qpu}.
     *
     * @param qpu        the {@link Qpu} to create the DTO for
     * @return the created DTO
     */
    private QpuDto createQpuDto(Qpu qpu) {
        QpuDto qpuDto = QpuDto.Converter.convert(qpu);
        qpuDto.add(linkTo(methodOn(QpuController.class).getQpu(qpu.getId())).withSelfRel());
        qpuDto.add(linkTo(methodOn(ProviderController.class).getProvider(qpu.getProvider().getId())).withRel(Constants.PROVIDER));
        return qpuDto;
    }
}
