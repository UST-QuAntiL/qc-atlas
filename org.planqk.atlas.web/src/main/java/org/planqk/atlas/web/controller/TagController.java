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

package org.planqk.atlas.web.controller;

import java.util.Optional;

import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.entities.TagDto;
import org.planqk.atlas.web.dtos.entities.TagListDto;
import org.planqk.atlas.web.utils.RestUtils;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

//
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.TAGS)
public class TagController {

    private TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(value = "/")
    HttpEntity<TagListDto> getTags(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer size) {
        Page<Tag> tags = this.tagService.findAll(RestUtils.getPageableFromRequestParams(page, size));
        TagListDto dtoList = new TagListDto();
        for (Tag tag : tags) {
            dtoList.add(createTagDto(tag));
            dtoList.add(linkTo(methodOn(TagController.class).getTagById(tag.getId())).withSelfRel());
        }
        dtoList.add(linkTo(methodOn(TagController.class).getTags(null, null)).withSelfRel());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    HttpEntity<TagDto> createTag(@RequestBody Tag tag) {
        TagDto savedTag = TagDto.Converter.convert(this.tagService.save(tag));

        savedTag.add(linkTo(methodOn(TagController.class).getTagById(savedTag.getId())).withSelfRel());
        return new ResponseEntity<>(savedTag, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{tagId}")
    HttpEntity<TagDto> getTagById(@PathVariable Long tagId) {
        Optional<Tag> tagOptional = this.tagService.getTagById(tagId);
        if (!tagOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(createTagDto(tagOptional.get()), HttpStatus.OK);
    }

    /**
     * Create a DTO object for a given {@link Tag} with the contained data and the links to related objects.
     *
     * @param tag the {@link Tag} to create the DTO for
     * @return the created DTO
     */
    private TagDto createTagDto(Tag tag) {
        TagDto dto = TagDto.Converter.convert(tag);
        dto.add(linkTo(methodOn(TagController.class).getTagById(tag.getId())).withSelfRel());
        return dto;
    }
}
