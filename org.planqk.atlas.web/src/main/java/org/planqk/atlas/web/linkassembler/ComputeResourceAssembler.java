package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.controller.ComputeResourceController;
import org.planqk.atlas.web.dtos.ComputeResourceDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ComputeResourceAssembler extends GenericLinkAssembler<ComputeResourceDto> {

    @Override
    public void addLinks(EntityModel<ComputeResourceDto> resource) {
        resource.add(links.linkTo(methodOn(ComputeResourceController.class).getComputeResource(getId(resource))).withSelfRel());
    }

    private UUID getId(EntityModel<ComputeResourceDto> resource) {
        return resource.getContent().getId();
    }
}
