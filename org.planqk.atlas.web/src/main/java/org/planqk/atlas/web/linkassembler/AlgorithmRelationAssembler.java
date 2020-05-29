package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.planqk.atlas.web.controller.AlgoRelationTypeController;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class AlgorithmRelationAssembler implements SimpleRepresentationModelAssembler<AlgorithmRelationDto> {
	
	private static final Logger LOG = LoggerFactory.getLogger(AlgorithmRelationAssembler.class);

	@Override
	public void addLinks(EntityModel<AlgorithmRelationDto> resource) {
		try {
			resource.add(linkTo(methodOn(AlgoRelationTypeController.class).getAlgoRelationTypeById(resource.getContent()
					.getAlgoRelationType().getId())).withRel("getAlgoRelationTypeById"));
			resource.add(linkTo(methodOn(AlgoRelationTypeController.class).updateAlgoRelationType(resource.getContent()
					.getAlgoRelationType().getId(), resource.getContent().getAlgoRelationType())).withRel("updateAlgoRelationType"));
			resource.add(linkTo(methodOn(AlgoRelationTypeController.class).deleteAlgoRelationType(resource.getContent()
					.getAlgoRelationType().getId())).withRel("deleteAlgoRelationType"));
			resource.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(resource.getContent()
					.getSourceAlgorithm().getId())).withRel("getSourceAlgorithm"));
			resource.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(resource.getContent()
					.getTargetAlgorithm().getId())).withRel("getTargetAlgorithm"));			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<AlgorithmRelationDto>> resources) {
		Iterator<EntityModel<AlgorithmRelationDto>> iter = resources.getContent().iterator();
		while(iter.hasNext()) {
			addLinks(iter.next());
		}
	}
	
	public void addLinks(Collection<EntityModel<AlgorithmRelationDto>> content) {
		addLinks(new CollectionModel<EntityModel<AlgorithmRelationDto>>(content));
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
}
