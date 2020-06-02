package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.TagController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class TagAssembler implements SimpleRepresentationModelAssembler<TagDto> {

	@Override
	public void addLinks(EntityModel<TagDto> resource) {
		resource.add(linkTo(methodOn(TagController.class).getTagById(getId(resource))).withSelfRel());
		resource.add(linkTo(methodOn(TagController.class).getAlgorithmsOfTag(getId(resource)))
				.withRel(Constants.ALGORITHMS));
		resource.add(linkTo(methodOn(TagController.class).getImplementationsOfTag(getId(resource)))
				.withRel(Constants.IMPLEMENTATIONS));
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<TagDto>> resources) {
		Iterator<EntityModel<TagDto>> iter = resources.getContent().iterator();
		while (iter.hasNext()) {
			addLinks(iter.next());
		}
	}

	public void addLinks(Collection<EntityModel<TagDto>> content) {
		addLinks(new CollectionModel<EntityModel<TagDto>>(content));
	}

	public void addAlgorithmLink(CollectionModel<EntityModel<AlgorithmDto>> resources, UUID id)
			throws NotFoundException {
		resources.add(linkTo(methodOn(TagController.class).getAlgorithmsOfTag(id)).withSelfRel());
	}

	public void addImplementationLink(CollectionModel<EntityModel<ImplementationDto>> resources, UUID id)
			throws NotFoundException {
		resources.add(linkTo(methodOn(TagController.class).getImplementationsOfTag(id)).withSelfRel());
	}

	private UUID getId(EntityModel<TagDto> resource) {
		return resource.getContent().getId();
	}

}
