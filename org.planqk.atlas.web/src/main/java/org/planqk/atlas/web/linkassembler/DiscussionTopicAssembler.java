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
import org.planqk.atlas.web.controller.DiscussionTopicController;
import org.planqk.atlas.web.dtos.DiscussionTopicDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DiscussionTopicAssembler extends GenericLinkAssembler<DiscussionTopicDto> {

    @Override
    public void addLinks(EntityModel<DiscussionTopicDto> resource) {
        resource.add(links.linkTo(methodOn(DiscussionTopicController.class).getDiscussionTopic(this.getID(resource))).withSelfRel());
        resource.add(links.linkTo(methodOn(DiscussionTopicController.class).deleteDiscussionTopic(this.getID(resource))).withRel("delete"));
        resource.add(links.linkTo(methodOn(DiscussionTopicController.class).updateDiscussionTopic(this.getID(resource), getContent(resource))).withRel("update"));
        resource.add(links.linkTo(methodOn(DiscussionTopicController.class).getDiscussionComments(this.getID(resource), 0, 20)).withRel(Constants.DISCUSSION_COMMENTS));
    }

    private UUID getID(EntityModel<DiscussionTopicDto> resource) {
        return resource.getContent().getId();
    }
}
