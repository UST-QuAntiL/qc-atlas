package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.controller.BackendController;
import org.planqk.atlas.web.dtos.BackendDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BackendAssembler extends GenericLinkAssembler<BackendDto> {

    @Override
    public void addLinks(EntityModel<BackendDto> resource) {
        resource.add(links.linkTo(methodOn(BackendController.class).getBackend(getId(resource))).withSelfRel());
    }

    private UUID getId(EntityModel<BackendDto> resource) {
        return resource.getContent().getId();
    }
}
