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

import org.planqk.atlas.web.controller.ApplicationAreaController;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ApplicationAreaAssembler extends GenericLinkAssembler<ApplicationAreaDto> {

    @Override
    public void addLinks(EntityModel<ApplicationAreaDto> resource) {
        resource.add(links.linkTo(methodOn(ApplicationAreaController.class).getApplicationAreaById(getId(resource))).withSelfRel());
    }

    private UUID getId(EntityModel<ApplicationAreaDto> resource) {
        return resource.getContent().getId();
    }
}
