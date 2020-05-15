package org.planqk.atlas.web.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.services.AlgoRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;
import org.planqk.atlas.web.dtos.AlgoRelationTypeListDto;
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
@RequestMapping("/" + Constants.ALGO_RELATION_TYPES)
public class AlgoRelationTypeController {
	
	private AlgoRelationTypeService algoRelationTypeService;

	public AlgoRelationTypeController(AlgoRelationTypeService algoRelationTypeService) {
		this.algoRelationTypeService = algoRelationTypeService;
	}

	public static AlgoRelationTypeListDto createAlgoRelationTypeDtoList(Stream<AlgoRelationType> stream) {
		AlgoRelationTypeListDto algoRelationTypeListDto = new AlgoRelationTypeListDto();
		algoRelationTypeListDto.add(stream.map(algoRelationType -> createAlgoRelationTypeDto(algoRelationType)).collect(Collectors.toList()));
		return algoRelationTypeListDto;
	}

	public static AlgoRelationTypeDto createAlgoRelationTypeDto(AlgoRelationType algoRelationType) {
		AlgoRelationTypeDto dto = AlgoRelationTypeDto.Converter.convert(algoRelationType);
		dto.add(linkTo(methodOn(AlgoRelationTypeController.class).getAlgoRelationTypeById(algoRelationType.getId())).withSelfRel());
		return dto;
	}

	@PostMapping("/")
	public HttpEntity<AlgoRelationTypeDto> createAlgoRelationType(@RequestBody AlgoRelationType algoRelationType) {
		AlgoRelationTypeDto savedAlgoRelationType = AlgoRelationTypeDto.Converter.convert(algoRelationTypeService.save(algoRelationType));
		savedAlgoRelationType.add(linkTo(methodOn(AlgoRelationTypeController.class).getAlgoRelationTypeById(savedAlgoRelationType.getId()))
				.withSelfRel());
		return new ResponseEntity<>(savedAlgoRelationType, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public HttpEntity<AlgoRelationTypeDto> updateAlgoRelationType(@PathVariable Long id,
			@RequestBody AlgoRelationTypeDto algoRelationTypeDto) {
		AlgoRelationTypeDto savedAlgoRelationType = createAlgoRelationTypeDto(
				algoRelationTypeService.update(id, AlgoRelationTypeDto.Converter.convert(algoRelationTypeDto)));
		savedAlgoRelationType.add(linkTo(methodOn(AlgoRelationTypeController.class).getAlgoRelationTypeById(savedAlgoRelationType.getId()))
				.withSelfRel());
		return new ResponseEntity<>(savedAlgoRelationType, HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public HttpEntity<AlgoRelationTypeDto> updateAlgoRelationType(@PathVariable Long id) {
		if (algoRelationTypeService.findById(id).isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		algoRelationTypeService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/")
	public HttpEntity<AlgoRelationTypeListDto> getAlgoRelationTypes(@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size) {
		return new ResponseEntity<>(
				createAlgoRelationTypeDtoList(
						algoRelationTypeService.findAll(RestUtils.getPageableFromRequestParams(page, size)).stream()),
				HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public HttpEntity<AlgoRelationTypeDto> getAlgoRelationTypeById(@PathVariable Long id) {
		Optional<AlgoRelationType> algoRelationTypeOpt = algoRelationTypeService.findById(id);
		if (algoRelationTypeOpt.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(createAlgoRelationTypeDto(algoRelationTypeOpt.get()), HttpStatus.OK);
	}
}
