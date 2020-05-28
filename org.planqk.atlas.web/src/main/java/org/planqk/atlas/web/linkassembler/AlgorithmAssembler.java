package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class AlgorithmAssembler implements SimpleRepresentationModelAssembler<AlgorithmDto> {

	@Override
	public void addLinks(EntityModel<AlgorithmDto> resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<AlgorithmDto>> resources) {
		// TODO Auto-generated method stub
	}
	
	public void addProblemTypeLink(CollectionModel<EntityModel<ProblemTypeDto>> resources, UUID id) {
		resources.add(linkTo(methodOn(AlgorithmController.class).getProblemTypes(id)).withSelfRel());
	}

}
