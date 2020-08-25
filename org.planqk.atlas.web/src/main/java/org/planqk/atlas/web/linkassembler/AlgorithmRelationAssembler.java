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

package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.controller.AlgorithmRelationTypeController;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AlgorithmRelationAssembler extends GenericLinkAssembler<AlgorithmRelationDto> {

    @Override
    public void addLinks(EntityModel<AlgorithmRelationDto> resource) {
        resource.add(links.linkTo(methodOn(AlgorithmController.class).getAlgorithm(getSourceAlgorithmId(resource)))
                .withRel("sourceAlgorithm"));
        resource.add(links.linkTo(methodOn(AlgorithmController.class).getAlgorithm(getTargetAlgorithmId(resource)))
                .withRel("targetAlgorithm"));
        resource.add(links.linkTo(
                methodOn(AlgorithmRelationTypeController.class).getAlgorithmRelationType(getAlgoRelationTypeId(resource)))
                .withRel("algoRelationType"));
    }

    private UUID getSourceAlgorithmId(EntityModel<AlgorithmRelationDto> resource) {
        return resource.getContent().getSourceAlgorithmId();
    }

    private UUID getTargetAlgorithmId(EntityModel<AlgorithmRelationDto> resource) {
        return resource.getContent().getTargetAlgorithmId();
    }

    private UUID getAlgoRelationTypeId(EntityModel<AlgorithmRelationDto> resource) {
        return resource.getContent().getAlgoRelationType().getId();
    }
}
