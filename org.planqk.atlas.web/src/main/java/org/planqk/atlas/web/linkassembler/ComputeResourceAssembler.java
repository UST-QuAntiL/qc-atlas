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

import org.planqk.atlas.web.controller.ComputeResourceController;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class ComputeResourceAssembler extends GenericLinkAssembler<ComputeResourceDto> {

    @Override
    public void addLinks(EntityModel<ComputeResourceDto> resource) {
        resource.add(getLinks().linkTo(methodOn(ComputeResourceController.class).getComputeResource(getId(resource))).withSelfRel());
    }

    private UUID getId(EntityModel<ComputeResourceDto> resource) {
        return resource.getContent().getId();
    }
}
