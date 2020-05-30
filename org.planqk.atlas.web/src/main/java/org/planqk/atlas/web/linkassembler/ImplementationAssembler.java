package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.ImplementationController;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ImplementationAssembler implements SimpleRepresentationModelAssembler<ImplementationDto> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ImplementationAssembler.class);

	@Override
	public void addLinks(EntityModel<ImplementationDto> resource) {
		try {
			resource.add(linkTo(methodOn(ImplementationController.class).getImplementation(getAlgId(resource), getId(resource))).withSelfRel());
			resource.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(getAlgId(resource))).withRel(Constants.ALGORITHM_LINK));
			resource.add(linkTo(methodOn(ImplementationController.class).getTags(getAlgId(resource), getId(resource))).withRel(Constants.TAGS));
		} catch (NotFoundException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<ImplementationDto>> resources) {
		Iterator<EntityModel<ImplementationDto>> iter = resources.getContent().iterator();
        while(iter.hasNext()) {
        	addLinks(iter.next());
        }
	}
	
	public void addLinks(Collection<EntityModel<ImplementationDto>> content) {
		addLinks(new CollectionModel<EntityModel<ImplementationDto>>(content));
	}
	
	public void addTagLink(CollectionModel<EntityModel<TagDto>> resultCollection, UUID implId, UUID algoId) throws NotFoundException {
		resultCollection.add(linkTo(methodOn(ImplementationController.class).getTags(algoId, implId)).withSelfRel());
	}
	
	public UUID getId(EntityModel<ImplementationDto> resource) {
		return resource.getContent().getId();
	}
	
	public UUID getAlgId(EntityModel<ImplementationDto> resource) {
		return resource.getContent().getImplementedAlgorithm().getId();
	}

}
