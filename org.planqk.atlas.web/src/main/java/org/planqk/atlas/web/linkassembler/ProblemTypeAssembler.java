package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.planqk.atlas.web.controller.ProblemTypeController;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class ProblemTypeAssembler extends GenericLinkAssembler<ProblemTypeDto> {

	@Override
	public void addLinks(EntityModel<ProblemTypeDto> resource) {
		resource.add(linkTo(methodOn(ProblemTypeController.class).getProblemTypeById(getId(resource))).withSelfRel());
		resource.add(
				linkTo(methodOn(ProblemTypeController.class).updateProblemType(getId(resource), getContent(resource)))
						.withRel("update"));
		resource.add(
				linkTo(methodOn(ProblemTypeController.class).deleteProblemType(getId(resource))).withRel("delete"));
	}

	private UUID getId(EntityModel<ProblemTypeDto> resource) {
		return resource.getContent().getId();
	}

}
