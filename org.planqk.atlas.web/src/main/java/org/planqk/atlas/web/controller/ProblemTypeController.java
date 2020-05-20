package org.planqk.atlas.web.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.ProblemTypeListDto;
import org.planqk.atlas.web.utils.RestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

	private ProblemTypeService problemTypeService;

	public ProblemTypeController(ProblemTypeService problemTypeService) {
		this.problemTypeService = problemTypeService;
	}

	public static ProblemTypeListDto createProblemTypeDtoList(Stream<ProblemType> stream) {
		ProblemTypeListDto problemTypeListDto = new ProblemTypeListDto();
		problemTypeListDto.add(stream.map(problemType -> createProblemTypeDto(problemType)).collect(Collectors.toList()));
		return problemTypeListDto;
	}

	public static ProblemTypeDto createProblemTypeDto(ProblemType problemType) {
		ProblemTypeDto dto = ProblemTypeDto.Converter.convert(problemType);
		dto.add(linkTo(methodOn(ProblemTypeController.class).getProblemTypeById(problemType.getId())).withSelfRel());
		return dto;
	}

	@PostMapping("/")
	public HttpEntity<ProblemTypeDto> createProblemType(@RequestBody ProblemType problemType) {
		ProblemTypeDto savedProblemType = ProblemTypeDto.Converter.convert(problemTypeService.save(problemType));
		savedProblemType.add(linkTo(methodOn(ProblemTypeController.class).getProblemTypeById(savedProblemType.getId()))
				.withSelfRel());
		return new ResponseEntity<>(savedProblemType, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public HttpEntity<ProblemTypeDto> updateProblemType(@PathVariable UUID id,
			@RequestBody ProblemTypeDto problemTypeDto) {
		ProblemTypeDto savedProblemType = createProblemTypeDto(
				problemTypeService.update(id, ProblemTypeDto.Converter.convert(problemTypeDto)));
		savedProblemType.add(linkTo(methodOn(ProblemTypeController.class).getProblemTypeById(savedProblemType.getId()))
				.withSelfRel());
		return new ResponseEntity<>(savedProblemType, HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public HttpEntity<ProblemTypeDto> updateProblemType(@PathVariable UUID id) {
		if (problemTypeService.findById(id).isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		problemTypeService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/")
	public HttpEntity<ProblemTypeListDto> getProblemTypes(@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		return new ResponseEntity<>(
				createProblemTypeDtoList(
						problemTypeService.findAll(RestUtils.getPageableFromRequestParams(page, size)).stream()),
				HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public HttpEntity<ProblemTypeDto> getProblemTypeById(@PathVariable UUID id) {
		Optional<ProblemType> problemTypeOpt = problemTypeService.findById(id);
		if (problemTypeOpt.isPresent()) {
			return new ResponseEntity<>(createProblemTypeDto(problemTypeOpt.get()), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

}
