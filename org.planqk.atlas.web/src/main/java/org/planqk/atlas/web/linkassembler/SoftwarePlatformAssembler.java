package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.controller.SoftwarePlatformController;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SoftwarePlatformAssembler extends GenericLinkAssembler<SoftwarePlatformDto> {

    @Override
    public void addLinks(EntityModel<SoftwarePlatformDto> resource) {
        resource.add(
                links.linkTo(methodOn(SoftwarePlatformController.class).getSoftwarePlatform(getId(resource))).withSelfRel());
    }

    private UUID getId(EntityModel<SoftwarePlatformDto> resource) {
        return resource.getContent().getId();
    }
}
