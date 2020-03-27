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

package org.planqk.atlas.api.controller;

import java.util.Optional;

import org.planqk.atlas.api.Constants;
import org.planqk.atlas.api.services.TagService;
import org.planqk.atlas.api.utils.RestUtils;
import org.planqk.atlas.core.model.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

//
@RestController
@RequestMapping("/" + Constants.TAGS)
public class TagController {

    private TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(value = "/")
    HttpEntity<Page<Tag>> getTags(@RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer size) {

        Page<Tag> tags = this.tagService.findAll(RestUtils.getPageableFromRequestParams(page, size));
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    HttpEntity<Tag> createTag(@RequestBody Tag tag) {
        Tag savedTag = this.tagService.save(tag);
        return ResponseEntity.created(linkTo(methodOn(TagController.class).getTagById(savedTag.getId())).toUri()).build();
    }

    @GetMapping(value = "/{tagId}")
    HttpEntity<Optional<Tag>> getTagById(@PathVariable Long tagId) {
        Optional<Tag> tag = this.tagService.getTagById(tagId);
        return new ResponseEntity<>(tag, HttpStatus.OK);
    }
}
