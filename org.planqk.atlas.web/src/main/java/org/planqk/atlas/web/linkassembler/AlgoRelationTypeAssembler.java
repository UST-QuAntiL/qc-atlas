package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.planqk.atlas.web.controller.AlgoRelationTypeController;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class AlgoRelationTypeAssembler implements SimpleRepresentationModelAssembler<AlgoRelationTypeDto> {

	@Override
	public void addLinks(EntityModel<AlgoRelationTypeDto> resource) {
		resource.add(linkTo(methodOn(AlgoRelationTypeController.class).getAlgoRelationTypeById(getId(resource)))
				.withSelfRel());
		resource.add(linkTo(methodOn(AlgoRelationTypeController.class).updateAlgoRelationType(getId(resource),
				getContent(resource))).withRel("update"));
		resource.add(linkTo(methodOn(AlgoRelationTypeController.class).deleteAlgoRelationType(getId(resource)))
				.withRel("delete"));

	}

	@Override
	public void addLinks(CollectionModel<EntityModel<AlgoRelationTypeDto>> resources) {
		Iterator<EntityModel<AlgoRelationTypeDto>> iter = resources.getContent().iterator();
		while (iter.hasNext()) {
			addLinks(iter.next());
		}
	}

	public void addLinks(Collection<EntityModel<AlgoRelationTypeDto>> content) {
		addLinks(new CollectionModel<EntityModel<AlgoRelationTypeDto>>(content));
	}

//	public EntityModel<AlgoRelationTypeDto> generateEntityModel(AlgoRelationTypeDto dto) {
//		EntityModel<AlgoRelationTypeDto> entityModel = new EntityModel<AlgoRelationTypeDto>(dto);
//		addLinks(entityModel);
//		return entityModel;
//	}
//	
//	public CollectionModel<EntityModel<AlgoRelationTypeDto>> generateCollectionModel(Set<AlgoRelationTypeDto> dtos) {
//		// Create EntityModel and fill each with links
//		Collection<EntityModel<AlgoRelationTypeDto>> dtoCollection = new HashSet<EntityModel<AlgoRelationTypeDto>>();
//		for (AlgoRelationTypeDto dto: dtos) {
//			dtoCollection.add(generateEntityModel(dto));
//		}
//		// Return CollectionModel
//		CollectionModel<EntityModel<AlgoRelationTypeDto>> resources = new CollectionModel<>(dtoCollection);	
//		return resources;
//	}

	private UUID getId(EntityModel<AlgoRelationTypeDto> resource) {
		return resource.getContent().getId();
	}

	private AlgoRelationTypeDto getContent(EntityModel<AlgoRelationTypeDto> resource) {
		return resource.getContent();
	}

}
