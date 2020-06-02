package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.ProviderController;
import org.planqk.atlas.web.controller.QpuController;
import org.planqk.atlas.web.dtos.QpuDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class QpuAssembler implements SimpleRepresentationModelAssembler<QpuDto> {

	@Override
	public void addLinks(EntityModel<QpuDto> resource) {
		resource.add(
				linkTo(methodOn(QpuController.class).getQpu(getProviderId(resource), getId(resource))).withSelfRel());
		resource.add(linkTo(methodOn(ProviderController.class).getProvider(getProviderId(resource)))
				.withRel(Constants.PROVIDER));
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<QpuDto>> resources) {
		Iterator<EntityModel<QpuDto>> iter = resources.getContent().iterator();
		while (iter.hasNext()) {
			addLinks(iter.next());
		}
	}

	public void addLinks(Collection<EntityModel<QpuDto>> content) {
		addLinks(new CollectionModel<EntityModel<QpuDto>>(content));
	}

	private UUID getId(EntityModel<QpuDto> resource) {
		return resource.getContent().getId();
	}

	private UUID getProviderId(EntityModel<QpuDto> resource) {
		return resource.getContent().getProvider().getId();
	}

}
