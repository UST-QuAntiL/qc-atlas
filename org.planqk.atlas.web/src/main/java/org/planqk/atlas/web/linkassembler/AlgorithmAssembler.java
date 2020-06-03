package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.ImplementationController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class AlgorithmAssembler implements SimpleRepresentationModelAssembler<AlgorithmDto> {

	@Override
	public void addLinks(EntityModel<AlgorithmDto> resource) {

			resource.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(getId(resource))).withSelfRel());
			resource.add(linkTo(methodOn(AlgorithmController.class).updateAlgorithm(getId(resource), getContent(resource))).withRel("update"));
			resource.add(linkTo(methodOn(AlgorithmController.class).deleteAlgorithm(getId(resource))).withRel("delete"));
			resource.add(linkTo(methodOn(AlgorithmController.class).getTags(getId(resource))).withRel(Constants.TAGS));
			resource.add(linkTo(methodOn(ImplementationController.class).getImplementations(getId(resource))).withRel(Constants.IMPLEMENTATIONS));
			resource.add(linkTo(methodOn(AlgorithmController.class).getProblemTypes(getId(resource))).withRel(Constants.PROBLEM_TYPES));
			resource.add(linkTo(methodOn(AlgorithmController.class).getAlgorithmRelations(getId(resource))).withRel(Constants.ALGORITHM_RELATIONS));

	}

	@Override
	public void addLinks(CollectionModel<EntityModel<AlgorithmDto>> resources) {
		Iterator<EntityModel<AlgorithmDto>> iter = resources.getContent().iterator();
        while(iter.hasNext()) {
        	addLinks(iter.next());
        }
	}

	public void addLinks(Collection<EntityModel<AlgorithmDto>> content) {
		addLinks(new CollectionModel<EntityModel<AlgorithmDto>>(content));
	}

	public void addProblemTypeLink(CollectionModel<EntityModel<ProblemTypeDto>> resources, UUID id) {
		resources.add(linkTo(methodOn(AlgorithmController.class).getProblemTypes(id)).withSelfRel());
	}

	public void addTagLink(CollectionModel<EntityModel<TagDto>> resources, UUID id) {
		resources.add(linkTo(methodOn(AlgorithmController.class).getTags(id)).withSelfRel());
	}

	public void addAlgorithmRelationLink(CollectionModel<EntityModel<AlgorithmRelationDto>> resultCollection,
			UUID sourceAlgorithm_id) {
		resultCollection.add(linkTo(methodOn(AlgorithmController.class).getAlgorithmRelations(sourceAlgorithm_id)).withSelfRel());
	}

	private UUID getId(EntityModel<AlgorithmDto> resource) {
		return resource.getContent().getId();
	}

	private AlgorithmDto getContent(EntityModel<AlgorithmDto> resource) {
		return resource.getContent();
	}

}
