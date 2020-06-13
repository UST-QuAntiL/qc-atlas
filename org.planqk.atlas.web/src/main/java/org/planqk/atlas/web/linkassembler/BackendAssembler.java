package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.planqk.atlas.web.controller.BackendController;
import org.planqk.atlas.web.dtos.BackendDto;
import org.springframework.hateoas.EntityModel;

public class BackendAssembler extends GenericLinkAssembler<BackendDto> {

    @Override
    public void addLinks(EntityModel<BackendDto> resource) {
        resource.add(linkTo(methodOn(BackendController.class).getBackend(getId(resource))).withSelfRel());
    }

    private UUID getId(EntityModel<BackendDto> resource) {
        return resource.getContent().getId();
    }
}
