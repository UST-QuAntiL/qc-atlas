package org.planqk.atlas.web.utils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.ImplementationController;
import org.planqk.atlas.web.controller.TagController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmListDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.springframework.stereotype.Component;

@Component
public class DtoLinkEnhancer {

	public void addLinks(AlgorithmDto dto) {
		dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(dto.getId())).withSelfRel());
		dto.add(linkTo(methodOn(AlgorithmController.class).getTags(dto.getId())).withRel(Constants.TAGS));
		dto.add(linkTo(methodOn(ImplementationController.class).getImplementations(dto.getId()))
				.withRel(Constants.IMPLEMENTATIONS));
		dto.add(linkTo(methodOn(AlgorithmController.class).getProblemTypes(dto.getId()))
				.withRel(Constants.PROBLEM_TYPES));
	}

	public void addLinks(AlgorithmListDto dtoList, Algorithm algorithm) {
		dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algorithm.getId()))
				.withRel(algorithm.getId().toString()));
	}

	public void addLinks(AlgorithmListDto dtoList) {
		dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithms(null, null)).withSelfRel());
	}

	public void addLinks(TagDto dto) {
		dto.add(linkTo(methodOn(TagController.class).getTagById(dto.getId())).withSelfRel());
		dto.add(linkTo(methodOn(TagController.class).getAlgorithmsOfTag(dto.getId())).withRel(Constants.ALGORITHMS));
		dto.add(linkTo(methodOn(TagController.class).getImplementationsOfTag(dto.getId()))
				.withRel(Constants.IMPLEMENTATIONS));
	}

}
