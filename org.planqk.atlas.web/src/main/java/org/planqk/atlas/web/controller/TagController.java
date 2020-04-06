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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Sdk;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.services.TagService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.entities.AlgorithmDto;
import org.planqk.atlas.web.dtos.entities.AlgorithmListDto;
import org.planqk.atlas.web.dtos.entities.ImplementationDto;
import org.planqk.atlas.web.dtos.entities.ImplementationListDto;
import org.planqk.atlas.web.dtos.entities.TagDto;
import org.planqk.atlas.web.dtos.entities.TagListDto;
import org.planqk.atlas.web.utils.RestUtils;

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

    @GetMapping(value = "/{tagId}/" + Constants.ALGORITHMS)
    HttpEntity<AlgorithmListDto> getAlgorithmsOfTag(@PathVariable Long tagId) {
        Optional<Tag> tagOptional = this.tagService.getTagById(tagId);
        if (!tagOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Algorithm> algorithms = tagOptional.get().getAlgorithms();
        AlgorithmListDto algorithmListDto = new AlgorithmListDto();
        for (Algorithm algo : algorithms) {
            AlgorithmDto dto = AlgorithmDto.Converter.convert(algo);
            dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algo.getId())).withSelfRel());
            dto.add(linkTo(methodOn(AlgorithmController.class).getInputParameters(algo.getId())).withRel(Constants.INPUT_PARAMS));
            dto.add(linkTo(methodOn(AlgorithmController.class).getOutputParameters(algo.getId())).withRel(Constants.OUTPUT_PARAMS));
            dto.add(linkTo(methodOn(AlgorithmController.class).getSelectionParams(algo.getId())).withRel(Constants.SELECTION_PARAMS));
            dto.add(linkTo(methodOn(AlgorithmController.class).getTags(algo.getId())).withRel(Constants.TAGS));
            dto.add(linkTo(methodOn(ImplementationController.class).getImplementations(algo.getId())).withRel(Constants.IMPLEMENTATIONS));

            algorithmListDto.add(dto);
        }
        algorithmListDto.add(linkTo(methodOn(TagController.class).getAlgorithmsOfTag(tagId)).withSelfRel());
        return new ResponseEntity<>(algorithmListDto, HttpStatus.OK);
    }

    @GetMapping(value = "/{tagId}/" + Constants.IMPLEMENTATIONS)
    HttpEntity<ImplementationListDto> getImplementationsOfTag(@PathVariable Long tagId) {
        Optional<Tag> tagOptional = this.tagService.getTagById(tagId);
        if (!tagOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Implementation> implementations = tagOptional.get().getImplementations();
        ImplementationListDto implementationListDto = new ImplementationListDto();
        for (Implementation implementation : implementations) {
            ImplementationDto dto = ImplementationDto.Converter.convert(implementation);
            dto.add(linkTo(methodOn(ImplementationController.class).getImplementation(
                    implementation.getImplementedAlgorithm().getId(), implementation.getId())).withSelfRel());
            dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(implementation.getImplementedAlgorithm().getId())).withRel(Constants.ALGORITHM_LINK));
            dto.add(linkTo(methodOn(ImplementationController.class).getInputParameters(implementation.getId())).withRel(Constants.INPUT_PARAMS));
            dto.add(linkTo(methodOn(ImplementationController.class).getOutputParameters(implementation.getId())).withRel(Constants.OUTPUT_PARAMS));
            dto.add(linkTo(methodOn(AlgorithmController.class).getTags(implementation.getId())).withRel(Constants.TAGS));
            Sdk usedSdk = implementation.getSdk();
            if (Objects.nonNull(usedSdk)) {
                dto.add(linkTo(methodOn(SdkController.class).getSdk(usedSdk.getId())).withRel(Constants.USED_SDK));
            }

            implementationListDto.add(dto);
        }
        implementationListDto.add(linkTo(methodOn(TagController.class).getAlgorithmsOfTag(tagId)).withSelfRel());
        return new ResponseEntity<>(implementationListDto, HttpStatus.OK);
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
