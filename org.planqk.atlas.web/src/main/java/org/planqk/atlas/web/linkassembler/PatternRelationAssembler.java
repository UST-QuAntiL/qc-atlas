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

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.PatternRelationController;
import org.planqk.atlas.web.controller.PatternRelationTypeController;
import org.planqk.atlas.web.dtos.PatternRelationDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PatternRelationAssembler extends GenericLinkAssembler<PatternRelationDto> {

    @Override
    public void addLinks(EntityModel<PatternRelationDto> resource) {
        resource.add(
                links.linkTo(methodOn(PatternRelationController.class).getPatternRelation(getId(resource))).withSelfRel());
        resource.add(links.linkTo(methodOn(AlgorithmController.class).getAlgorithm(getAlgorithmId(resource)))
                .withRel(Constants.ALGORITHMS));
        resource.add(links.linkTo(methodOn(PatternRelationTypeController.class).getPatternRelationType(getTypeId(resource)))
                .withRel(Constants.PATTERN_RELATION_TYPES));
    }

    private UUID getId(EntityModel<PatternRelationDto> resource) {
        return resource.getContent().getId();
    }

    private UUID getAlgorithmId(EntityModel<PatternRelationDto> resource) {
        return resource.getContent().getAlgorithmId();
    }

    private UUID getTypeId(EntityModel<PatternRelationDto> resource) {
        return resource.getContent().getPatternRelationType().getId();
    }
}
