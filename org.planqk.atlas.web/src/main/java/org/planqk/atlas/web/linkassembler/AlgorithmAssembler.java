/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.AlgorithmRelationController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.utils.ListParameters;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class AlgorithmAssembler extends GenericLinkAssembler<AlgorithmDto> {

    @Override
    public void addLinks(EntityModel<AlgorithmDto> resource) {
        resource.add(links.linkTo(methodOn(AlgorithmController.class).getAlgorithm(getId(resource))).withSelfRel());
        resource.add(links.linkTo(methodOn(AlgorithmController.class)
            .getProblemTypesOfAlgorithm(getId(resource), ListParameters.getDefault()))
            .withRel(Constants.PROBLEM_TYPES));
        resource.add(links.linkTo(methodOn(AlgorithmRelationController.class)
            .getAlgorithmRelationsOfAlgorithm(getId(resource), ListParameters.getDefault()))
            .withRel(Constants.ALGORITHM_RELATIONS));
        resource.add(links.linkTo(methodOn(AlgorithmController.class)
            .getPublicationsOfAlgorithm(getId(resource), ListParameters.getDefault()))
            .withRel(Constants.PUBLICATIONS));
        resource.add(links.linkTo(methodOn(AlgorithmController.class)
            .getPatternRelationsOfAlgorithm(getId(resource), ListParameters.getDefault()))
            .withRel(Constants.PATTERN_RELATIONS));
        resource.add(links.linkTo(methodOn(AlgorithmController.class)
            .getSketches(getId(resource)))
            .withRel(Constants.PATTERN_RELATIONS));
    }

    public void addProblemTypeLink(CollectionModel<EntityModel<ProblemTypeDto>> resources, UUID id) {
        resources.add(links.linkTo(methodOn(AlgorithmController.class)
            .getProblemTypesOfAlgorithm(id, ListParameters.getDefault())).withSelfRel());
    }

    public void addApplicationAreaLink(CollectionModel<EntityModel<ApplicationAreaDto>> resources, UUID id) {
        resources.add(links.linkTo(methodOn(AlgorithmController.class)
            .getApplicationAreasOfAlgorithm(id, ListParameters.getDefault())).withSelfRel());
    }

    public void addTagLink(CollectionModel<EntityModel<TagDto>> resources, UUID id) {
    }

    public void addPublicationLink(CollectionModel<EntityModel<PublicationDto>> resources, UUID id) {
        resources.add(links.linkTo(methodOn(AlgorithmController.class)
            .getPublicationsOfAlgorithm(id, ListParameters.getDefault())).withSelfRel());
    }

    public void addAlgorithmRelationLink(CollectionModel<EntityModel<AlgorithmRelationDto>> resultCollection,
                                         UUID sourceAlgorithm_id) {
        resultCollection.add(
            links.linkTo(methodOn(AlgorithmRelationController.class)
                .getAlgorithmRelationsOfAlgorithm(sourceAlgorithm_id, ListParameters.getDefault())).withSelfRel());
    }

    public void addPatternRelationLink(CollectionModel<EntityModel<PatternRelationDto>> resultCollection, UUID
        id) {
        resultCollection.add(
            links.linkTo(methodOn(AlgorithmController.class)
                .getPatternRelationsOfAlgorithm(id, ListParameters.getDefault())).withSelfRel());
    }

    private UUID getId(EntityModel<AlgorithmDto> resource) {
        return resource.getContent().getId();
    }
}
