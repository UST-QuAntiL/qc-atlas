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

import java.util.Iterator;
import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.DiscussionTopicController;
import org.planqk.atlas.web.dtos.DiscussionCommentDto;
import org.planqk.atlas.web.dtos.DiscussionTopicDto;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DiscussionTopicAssembler implements SimpleRepresentationModelAssembler<DiscussionTopicDto> {

    @Override
    public void addLinks(EntityModel<DiscussionTopicDto> resource) {
        resource.add(linkTo(methodOn(DiscussionTopicController.class).getDiscussionTopic(this.getID(resource))).withSelfRel());
        resource.add(linkTo(methodOn(DiscussionTopicController.class).deleteDiscussionTopic(this.getID(resource))).withRel("delete"));
        resource.add(linkTo(methodOn(DiscussionTopicController.class).getDiscussionCommentsOfTopic(this.getID(resource))).withRel(Constants.DISCUSSION_COMMENTS));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<DiscussionTopicDto>> resources) {
        Iterator<EntityModel<DiscussionTopicDto>> iter = resources.getContent().iterator();
        while (iter.hasNext()) {
            addLinks(iter.next());
        }
    }

    private UUID getID(EntityModel<DiscussionTopicDto> resource) {
        return resource.getContent().getId();
    }

    public void addDiscussionCommentLink(CollectionModel<EntityModel<DiscussionCommentDto>> results, UUID id) {
        results.add(linkTo(methodOn(DiscussionTopicController.class).getDiscussionCommentsOfTopic(id)).withSelfRel());
    }
}
