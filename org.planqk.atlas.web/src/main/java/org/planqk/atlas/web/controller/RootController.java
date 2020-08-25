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

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.utils.ListParameters;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Root controller to access all entities within Quality, trigger the hardware selection, and execution of quantum
 * algorithms.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_ROOT)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/")
@Slf4j
public class RootController {

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/")
    public HttpEntity<RepresentationModel<?>> root() {
        // EntityModel can unfortunately not be used here because it requires non null content
        // OpenAPI does not care about ? in this case because we only provide links anyway
        RepresentationModel<?> responseEntity = new RepresentationModel<>();

        // add links to sub-controllers
        responseEntity.add(linkTo(methodOn(RootController.class).root()).withSelfRel());
        responseEntity.add(linkTo(methodOn(AlgorithmController.class)
                .getAlgorithms(ListParameters.getDefault())).withRel(Constants.ALGORITHMS));
        responseEntity.add(linkTo(methodOn(SoftwarePlatformController.class)
                .getSoftwarePlatforms(ListParameters.getDefault())).withRel(Constants.SOFTWARE_PLATFORMS));
        responseEntity.add(linkTo(methodOn(CloudServiceController.class)
                .getCloudServices(ListParameters.getDefault())).withRel(Constants.CLOUD_SERVICES));
        responseEntity.add(linkTo(methodOn(ComputeResourceController.class)
                .getComputeResources(ListParameters.getDefault())).withRel(Constants.COMPUTE_RESOURCES));
        // This controller will be used/tested and included in the future
        responseEntity.add(linkTo(
                methodOn(TagController.class).getTags(ListParameters.getDefault()))
                .withRel(Constants.TAGS));
        responseEntity.add(linkTo(
                methodOn(PublicationController.class).getPublications(ListParameters.getDefault()))
                .withRel(Constants.PUBLICATIONS));

        return ResponseEntity.ok(responseEntity);
    }
}
