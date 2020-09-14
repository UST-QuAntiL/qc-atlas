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

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.TagController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagAssembler extends GenericLinkAssembler<TagDto> {

    @Override
    public void addLinks(EntityModel<TagDto> resource) {
        resource.add(links.linkTo(methodOn(TagController.class).getTag(getName(resource))).withSelfRel());
        resource.add(links.linkTo(methodOn(TagController.class).getAlgorithmsOfTag(getName(resource)))
                .withRel(Constants.ALGORITHMS));
        resource.add(links.linkTo(methodOn(TagController.class).getImplementationsOfTag(getName(resource)))
                .withRel(Constants.IMPLEMENTATIONS));
    }

    public void addAlgorithmLink(CollectionModel<EntityModel<AlgorithmDto>> resources, String name) {
        resources.add(links.linkTo(methodOn(TagController.class).getAlgorithmsOfTag(name)).withSelfRel());
    }

    public void addImplementationLink(CollectionModel<EntityModel<ImplementationDto>> resources, String name) {
        resources.add(links.linkTo(methodOn(TagController.class).getImplementationsOfTag(name)).withSelfRel());
    }

    private String getName(EntityModel<TagDto> resource) {
        return resource.getContent().getValue();
    }
}
