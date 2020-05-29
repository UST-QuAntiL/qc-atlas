package org.planqk.atlas.web.utils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.web.controller.AlgoRelationTypeController;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmListDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.springframework.stereotype.Component;

@Component
public class DtoLinkEnhancer {

	public void addLinks(AlgorithmDto dto) {
	}

	public void addLinks(AlgorithmListDto dtoList, Algorithm algorithm) {
		dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(algorithm.getId()))
				.withRel(algorithm.getId().toString()));
	}

	public void addLinks(AlgorithmListDto dtoList) {
		dtoList.add(linkTo(methodOn(AlgorithmController.class).getAlgorithms(null, null)).withSelfRel());
	}

	public void addLinks(TagDto dto) {
	}
	
	public void addLinks(AlgoRelationTypeDto dto) {
		dto.add(linkTo(methodOn(AlgoRelationTypeController.class).getAlgoRelationTypeById(dto.getId())).withSelfRel());
	}

}
