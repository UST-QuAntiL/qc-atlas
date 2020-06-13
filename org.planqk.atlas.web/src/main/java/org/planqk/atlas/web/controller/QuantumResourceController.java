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

import java.util.UUID;

import org.planqk.atlas.core.services.QuantumResourceService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.QuantumResourceDto;
import org.planqk.atlas.web.linkassembler.QuantumResourceAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "quantum-resource")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.QUANTUM_RESOURCES)
@ApiVersion("v1")
@AllArgsConstructor
public class QuantumResourceController {
    private final QuantumResourceAssembler assembler;
    private final QuantumResourceService service;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<QuantumResourceDto>> getQuantumResource(@PathVariable UUID id) {
        var resource = service.findResourceById(id);
        var resourceDto = ModelMapperUtils.convert(resource, QuantumResourceDto.class);
        var entityModel = HateoasUtils.generateEntityModel(resourceDto);
        assembler.addLinks(entityModel);
        return ResponseEntity.ok(entityModel);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuantumResource(@PathVariable UUID id) {
        service.deleteQuantumResource(id);
        return ResponseEntity.ok().build();
    }
}
