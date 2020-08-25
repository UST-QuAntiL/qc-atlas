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

import org.planqk.atlas.web.dtos.ImplementationDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class ImplementationAssembler extends GenericLinkAssembler<ImplementationDto> {

    @Override
    public void addLinks(EntityModel<ImplementationDto> resource) {
//        resource.add(
//                links.linkTo(methodOn(ImplementationController.class).getImplementation(getAlgId(resource), getId(resource)))
//                        .withSelfRel());
//        resource.add(links.linkTo(methodOn(AlgorithmController.class).getAlgorithm(getAlgId(resource)))
//                .withRel(Constants.ALGORITHM_LINK));
//        resource.add(links.linkTo(methodOn(ImplementationController.class).getPublicationsOfImplementation(getAlgId(resource), getId(resource))).withRel(Constants.PUBLICATIONS));

//        resource.add(links.linkTo(methodOn(ImplementationController.class).getTags(getId(resource)))
//                .withRel(Constants.TAGS));
    }

//    public void addTagLink(CollectionModel<EntityModel<TagDto>> resultCollection, UUID implId) {
//        resultCollection.add(links.linkTo(methodOn(ImplementationController.class).getTags(implId)).withSelfRel());
//    }

    public UUID getId(EntityModel<ImplementationDto> resource) {
        return resource.getContent().getId();
    }

    public UUID getAlgId(EntityModel<ImplementationDto> resource) {
        return resource.getContent().getImplementedAlgorithmId();
    }
}
