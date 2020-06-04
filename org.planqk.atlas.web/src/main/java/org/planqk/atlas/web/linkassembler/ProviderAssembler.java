package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.ProviderController;
import org.planqk.atlas.web.controller.QpuController;
import org.planqk.atlas.web.dtos.ProviderDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class ProviderAssembler extends GenericLinkAssembler<ProviderDto> {

	@Override
	public void addLinks(EntityModel<ProviderDto> resource) {
		resource.add(linkTo(methodOn(ProviderController.class).getProvider(getId(resource))).withSelfRel());
		resource.add(linkTo(methodOn(QpuController.class).getQpus(getId(resource), Constants.DEFAULT_PAGE_NUMBER,
				Constants.DEFAULT_PAGE_SIZE)).withRel(Constants.QPUS));
	}

	private UUID getId(EntityModel<ProviderDto> resource) {
		return resource.getContent().getId();
	}
}
