package org.planqk.atlas.web.utils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.ImplementationController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.springframework.stereotype.Component;

@Component
public class DtoLinkEnhancer {

	public void addLinks(AlgorithmDto dto) {
		dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(dto.getId())).withSelfRel());
        dto.add(linkTo(methodOn(AlgorithmController.class).getTags(dto.getId())).withRel(Constants.TAGS));
        dto.add(linkTo(methodOn(ImplementationController.class).getImplementations(dto.getId())).withRel(Constants.IMPLEMENTATIONS));
        dto.add(linkTo(methodOn(AlgorithmController.class).getProblemTypes(dto.getId())).withRel(Constants.PROBLEM_TYPES));
	}
	
}
