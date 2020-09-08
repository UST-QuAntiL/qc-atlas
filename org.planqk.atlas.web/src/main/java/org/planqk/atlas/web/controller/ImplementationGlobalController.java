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

import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.linkassembler.ImplementationAssembler;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to access implementations outside of the context of its implemented algorithm.
 */
@Tag(name = Constants.TAG_IMPLEMENTATIONS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.IMPLEMENTATIONS)
@AllArgsConstructor
@Slf4j
public class ImplementationGlobalController {

    private final ImplementationService implementationService;

    private final ImplementationAssembler implementationAssembler;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
    }, description = "Retrieve all implementations unaffected by its implemented algorithm")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ImplementationDto>>> getImplementations(
            @Parameter(hidden = true) ListParameters listParameters) {
        var implementations = implementationService.findAll(listParameters.getPageable());
        return ResponseEntity.ok(implementationAssembler.toModel(implementations));
    }
}
