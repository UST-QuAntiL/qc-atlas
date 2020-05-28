package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
		try {
			resource.add(linkTo(methodOn(ProblemTypeController.class).getProblemTypeById(resource.getContent().getId()))
					.withSelfRel());
			resource.add(linkTo(
					methodOn(ProblemTypeController.class).updateProblemType(getId(resource), getContent(resource)))
							.withRel("update"));
			resource.add(
					linkTo(methodOn(ProblemTypeController.class).deleteProblemType(getId(resource))).withRel("delete"));
		} catch (Exception e) {
			// TODO: Exception handling
		}
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<ProblemTypeDto>> resources) {

	}
	
	public void addLinks(Collection<EntityModel<ProblemTypeDto>> content) {
		Iterator<EntityModel<ProblemTypeDto>> iterator = content.iterator();
		while(iterator.hasNext()) {
			addLinks(iterator.next());
		}
	}
	
	public EntityModel<ProblemTypeDto> generateEntityModel(ProblemTypeDto dto) {
		EntityModel<ProblemTypeDto> entityModel = new EntityModel<ProblemTypeDto>(dto);
		addLinks(entityModel);
		return entityModel;
	}
	
	public CollectionModel<EntityModel<ProblemTypeDto>> generateCollectionModel(Set<ProblemTypeDto> dtos) {
		// Create EntityModel and fill each with links
		Collection<EntityModel<ProblemTypeDto>> dtoCollection = new HashSet<EntityModel<ProblemTypeDto>>();
		for (ProblemTypeDto dto: dtos) {
			dtoCollection.add(generateEntityModel(dto));
		}
		// Return CollectionModel
		CollectionModel<EntityModel<ProblemTypeDto>> resources = new CollectionModel<>(dtoCollection);	
		return resources;
	}

	private UUID getId(EntityModel<ProblemTypeDto> resource) {
		return resource.getContent().getId();
	}

	private ProblemTypeDto getContent(EntityModel<ProblemTypeDto> resource) {
		return resource.getContent();
	}

}
