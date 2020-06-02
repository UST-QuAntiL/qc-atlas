package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.planqk.atlas.web.controller.ProblemTypeController;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ProblemTypeAssembler implements SimpleRepresentationModelAssembler<ProblemTypeDto> {

	@Override
	public void addLinks(EntityModel<ProblemTypeDto> resource) {
		resource.add(linkTo(methodOn(ProblemTypeController.class).getProblemTypeById(resource.getContent().getId()))
				.withSelfRel());
		resource.add(
				linkTo(methodOn(ProblemTypeController.class).updateProblemType(getId(resource), getContent(resource)))
						.withRel("update"));
		resource.add(
				linkTo(methodOn(ProblemTypeController.class).deleteProblemType(getId(resource))).withRel("delete"));
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<ProblemTypeDto>> resources) {
		Iterator<EntityModel<ProblemTypeDto>> iter = resources.getContent().iterator();
		while (iter.hasNext()) {
			addLinks(iter.next());
		}
	}

	public void addLinks(Collection<EntityModel<ProblemTypeDto>> content) {
		addLinks(new CollectionModel<EntityModel<ProblemTypeDto>>(content));
	}

	private UUID getId(EntityModel<ProblemTypeDto> resource) {
		return resource.getContent().getId();
	}

	private ProblemTypeDto getContent(EntityModel<ProblemTypeDto> resource) {
		return resource.getContent();
	}

}
