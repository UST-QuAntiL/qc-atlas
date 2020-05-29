package org.planqk.atlas.web.controller;

import java.util.UUID;

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.exceptions.NoContentException;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.model.exceptions.SqlConsistencyException;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.linkassembler.ProblemTypeAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
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

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.PROBLEM_TYPES)
public class ProblemTypeController {

	@Autowired
	private ProblemTypeService problemTypeService;
	@Autowired
	private PagedResourcesAssembler<ProblemTypeDto> paginationAssembler;
	@Autowired
	private ProblemTypeAssembler problemTypeAssembler;

	@PostMapping("/")
	public HttpEntity<EntityModel<ProblemTypeDto>> createProblemType(@Validated @RequestBody ProblemTypeDto problemTypeDto) {
		// Convert DTO to Entity
		ProblemType entityInput = ModelMapperUtils.convert(problemTypeDto, ProblemType.class);
		// Save Entity
		ProblemType savedProblemType = problemTypeService.save(entityInput);
		// Convert Entity to DTO
		ProblemTypeDto dtoOutput = ModelMapperUtils.convert(savedProblemType, ProblemTypeDto.class);
		// Generate EntityModel
	    EntityModel<ProblemTypeDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
	    // Add Links
	    problemTypeAssembler.addLinks(entityDto);
		return new ResponseEntity<>(entityDto, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public HttpEntity<EntityModel<ProblemTypeDto>> updateProblemType(@PathVariable UUID id,
			@Validated @RequestBody ProblemTypeDto problemTypeDto) throws NotFoundException {
		// Convert DTO to Entity
		ProblemType entityInput = ModelMapperUtils.convert(problemTypeDto, ProblemType.class);
		// Update Entity
		ProblemType updatedEntity = problemTypeService.update(id, entityInput);
		// Convert Entity to DTO
		ProblemTypeDto dtoOutput = ModelMapperUtils.convert(updatedEntity, ProblemTypeDto.class);
		// Generate EntityModel
	    EntityModel<ProblemTypeDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
	    // Add Links
	    problemTypeAssembler.addLinks(entityDto);
		return new ResponseEntity<>(entityDto, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public HttpEntity<ProblemTypeDto> deleteProblemType(@PathVariable UUID id) throws SqlConsistencyException, NoContentException {
		problemTypeService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/")
	public HttpEntity<?> getProblemTypes(@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		// Generate Pageable
		Pageable p = RestUtils.getPageableFromRequestParams(page, size);
		// Get Entities using pagable
		Page<ProblemType> entities = problemTypeService.findAll(p);
		// Convert to DTO-Pageable
		Page<ProblemTypeDto> dtos = ModelMapperUtils.convertPage(entities, ProblemTypeDto.class);
		// Generate PagedModel with page links
		PagedModel<EntityModel<ProblemTypeDto>> pagedEntityOutput = paginationAssembler.toModel(dtos);
		// Add DTO links 
		problemTypeAssembler.addLinks(pagedEntityOutput.getContent());
		return new ResponseEntity<>(pagedEntityOutput, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public HttpEntity<EntityModel<ProblemTypeDto>> getProblemTypeById(@PathVariable UUID id) throws NotFoundException {
		ProblemType problemType = problemTypeService.findById(id);
		// Convert Entity to DTO
		ProblemTypeDto dtoOutput = ModelMapperUtils.convert(problemType, ProblemTypeDto.class);
		// Generate EntityModel
	    EntityModel<ProblemTypeDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
	    // Add Links
	    problemTypeAssembler.addLinks(entityDto);
		return new ResponseEntity<>(entityDto, HttpStatus.OK);
	}

}
