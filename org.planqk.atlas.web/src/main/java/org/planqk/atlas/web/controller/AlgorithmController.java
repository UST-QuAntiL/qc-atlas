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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmListDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationListDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.TagListDto;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.AlgorithmRelationAssembler;
import org.planqk.atlas.web.linkassembler.ProblemTypeAssembler;
import org.planqk.atlas.web.utils.DtoEntityConverter;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller to access and manipulate quantum algorithms.
 */
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGORITHMS)
public class AlgorithmController {

    final private static Logger LOG = LoggerFactory.getLogger(AlgorithmController.class);

    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private DtoEntityConverter modelConverter;
    @Autowired
	private PagedResourcesAssembler<AlgorithmDto> algorithmPaginationAssembler;
    @Autowired
	private PagedResourcesAssembler<AlgorithmRelationDto> algorithmRelationPaginationAssembler;
    @Autowired
    private ProblemTypeAssembler problemTypeAssembler;
    @Autowired
    private AlgorithmAssembler algorithmAssembler;
    @Autowired
    private AlgorithmRelationAssembler algorithmRelationAssembler;

    /**
     * Create a DTO object for a given {@link Algorithm} with the contained data and the links to related objects.
     *
     * @param algorithm the {@link Algorithm} to create the DTO for
     * @return the created DTO
     * @throws NotFoundException 
     */
    public static AlgorithmDto createAlgorithmDto(Algorithm algorithm) throws NotFoundException {
    	AlgorithmDto dto = AlgorithmDto.Converter.convert(algorithm);
        dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algorithm.getId())).withSelfRel());
        dto.add(linkTo(methodOn(AlgorithmController.class).getTags(algorithm.getId())).withRel(Constants.TAGS));
        dto.add(linkTo(methodOn(ImplementationController.class).getImplementations(algorithm.getId())).withRel(Constants.IMPLEMENTATIONS));
        dto.add(linkTo(methodOn(AlgorithmController.class).getProblemTypes(algorithm.getId())).withRel(Constants.PROBLEM_TYPES));
        return dto;
    }

//	public static AlgorithmRelationListDto createAlgorithmRelationDtoList(Stream<AlgorithmRelation> stream) {
//		AlgorithmRelationListDto algorithmRelationListDto = new AlgorithmRelationListDto();
//		algorithmRelationListDto.add(stream.map(algorithmRelation -> createAlgorithmRelationDto(algorithmRelation)).collect(Collectors.toList()));
//		return algorithmRelationListDto;
//	}
//
//	public static AlgorithmRelationDto createAlgorithmRelationDto(AlgorithmRelation algorithmRelation) {
//		AlgorithmRelationDto dto = AlgorithmRelationDto.Converter.convert(algorithmRelation);
//		dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algorithmRelation.getSourceAlgorithm().getId())).withSelfRel());
//		return dto;
//	}

    @GetMapping("/")
    public HttpEntity<AlgorithmListDto> getAlgorithms(@RequestParam(required = false) Integer page,
                                                      @RequestParam(required = false) Integer size) throws NotFoundException {
        LOG.debug("Get to retrieve all algorithms received.");
        return new ResponseEntity<>(modelConverter.convert(algorithmService.findAll(RestUtils.getPageableFromRequestParams(page, size))), HttpStatus.OK);
    }

    @PostMapping("/")
    public HttpEntity<AlgorithmDto> createAlgorithm(@Validated @RequestBody AlgorithmDto algo) throws NotFoundException {
        LOG.debug("Post to create new algorithm received.");

        // store and return algorithm
        Algorithm algorithm = algorithmService.save(modelConverter.convert(algo));

        return new ResponseEntity<>(modelConverter.convert(algorithm), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public HttpEntity<AlgorithmDto> updateAlgorithm(@PathVariable UUID id, @Validated @RequestBody AlgorithmDto algo) throws NotFoundException {
        LOG.debug("Put to update algorithm with id '" + id + "' received");
        
        return new ResponseEntity<>(modelConverter.convert(algorithmService.update(id, modelConverter.convert(algo))), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<AlgorithmDto> deleteAlgorithm(@PathVariable UUID id) throws NotFoundException {
        LOG.debug("Delete to remove algorithm with id '" + id + "' received");

        algorithmService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public HttpEntity<AlgorithmDto> getAlgorithm(@PathVariable UUID id) throws NotFoundException {
        LOG.debug("Get to retrieve algorithm with id: {}.", id);

        Algorithm algorithm = algorithmService.findById(id);

        return new ResponseEntity<>(modelConverter.convert(algorithm), HttpStatus.OK);
    }

    @GetMapping("/{id}/" + Constants.TAGS)
    public HttpEntity<TagListDto> getTags(@PathVariable UUID id) throws NotFoundException {
        Algorithm algorithm = algorithmService.findById(id);
        Set<Tag> tags = algorithm.getTags();
        TagListDto tagListDto = TagController.createTagDtoList(tags.stream());
        tagListDto.add(linkTo(methodOn(AlgorithmController.class).getTags(id)).withSelfRel());
        return new ResponseEntity<>(tagListDto, HttpStatus.OK);
    }

    @GetMapping("/{id}/" + Constants.PROBLEM_TYPES)
    public HttpEntity<?> getProblemTypes(@PathVariable UUID id) throws NotFoundException {
        Algorithm algorithm = algorithmService.findById(id);
        // Get ProblemTypes of Algorithm
        Set<ProblemType> problemTypes = algorithm.getProblemTypes();
        // Translate Entity to DTO
        Set<ProblemTypeDto> dtoTypes = modelConverter.convertSet(problemTypes, ProblemTypeDto.class);
        // Create CollectionModel
        CollectionModel<EntityModel<ProblemTypeDto>> resultCollection = HateoasUtils.generateCollectionModel(dtoTypes);
        // Fill EntityModel Links
        problemTypeAssembler.addLinks(resultCollection);
        // Fill Collection-Links
        algorithmAssembler.addProblemTypeLink(resultCollection, id);
        return new ResponseEntity<>(resultCollection, HttpStatus.OK);
    }

    @GetMapping("/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS)
    public HttpEntity<?> getAlgorithmRelations(@PathVariable UUID sourceAlgorithm_id) throws NotFoundException {
        // get AlgorithmRelations of Algorithm
        Page<AlgorithmRelation> algorithmRelations = algorithmService.getAlgorithmRelations(sourceAlgorithm_id);
        Page<AlgorithmRelationDto> dtos = ModelMapperUtils.convertPage(algorithmRelations, AlgorithmRelationDto.class);
        PagedModel<EntityModel<AlgorithmRelationDto>> pagedEntityOutput = algorithmRelationPaginationAssembler.toModel(dtos);
        algorithmRelationAssembler.addLinks(pagedEntityOutput);
        return new ResponseEntity<>(pagedEntityOutput, HttpStatus.OK);
    }

    @PutMapping("/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS)
    public HttpEntity<EntityModel<AlgorithmRelationDto>> updateAlgorithmRelation(@PathVariable UUID sourceAlgorithm_id,
    		@Validated @RequestBody AlgorithmRelationDto relation) throws NotFoundException {
        LOG.debug("Post to add algorithm relation received.");
        
        AlgorithmRelation algorithmRelation = algorithmService.addUpdateAlgorithmRelation(sourceAlgorithm_id, modelConverter.convert(relation));
        AlgorithmRelationDto dtoOutput = ModelMapperUtils.convert(algorithmRelation, AlgorithmRelationDto.class);
        EntityModel<AlgorithmRelationDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
        algorithmRelationAssembler.addLinks(entityDto);
        return new ResponseEntity<>(entityDto, HttpStatus.OK);
    }

    @DeleteMapping("/{sourceAlgorithm_id}/" + Constants.ALGORITHM_RELATIONS + "/{relation_id}")
    public HttpEntity<AlgorithmRelationDto> deleteAlgorithmRelation(@PathVariable UUID sourceAlgorithm_id, @PathVariable UUID relation_id)
    		throws NotFoundException {
        LOG.debug("Delete received to remove algorithm relation with id {}.", relation_id);
        algorithmService.deleteAlgorithmRelation(sourceAlgorithm_id, relation_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
