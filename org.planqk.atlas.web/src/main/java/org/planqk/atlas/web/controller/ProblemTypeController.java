package org.planqk.atlas.web.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.ProblemTypeListDto;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.PROBLEM_TYPES)
public class ProblemTypeController {

	public static ProblemTypeListDto createProblemTypeDtoList(Stream<ProblemType> stream) {
		ProblemTypeListDto problemTypeListDto = new ProblemTypeListDto();
		problemTypeListDto.add(stream.map(problemType -> createTagDto(problemType)).collect(Collectors.toList()));
		return problemTypeListDto;
	}
	
	public static ProblemTypeDto createTagDto(ProblemType problemType) {
        ProblemTypeDto dto = ProblemTypeDto.Converter.convert(problemType);
        dto.add(linkTo(methodOn(ProblemTypeController.class).getProblemTypeById(problemType.getId())).withSelfRel());
        return dto;
    }
	
	@GetMapping("/{id}")
	public HttpEntity<?> getProblemTypeById(@PathVariable Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
