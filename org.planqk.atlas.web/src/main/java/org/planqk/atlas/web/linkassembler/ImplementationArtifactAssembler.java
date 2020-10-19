package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.planqk.atlas.web.controller.ImplementationGlobalController;
import org.planqk.atlas.web.dtos.ImplementationArtifactDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class ImplementationArtifactAssembler extends GenericLinkAssembler<ImplementationArtifactDto> {

    @Override
    public void addLinks(EntityModel<ImplementationArtifactDto> resource) {
        resource.add(links.linkTo(methodOn(ImplementationGlobalController.class)
                .getImplementationArtifactOfImplementation(getImplementationId(resource), getId(resource))).withSelfRel());
    }

    private UUID getId(EntityModel<ImplementationArtifactDto> resource) {
        return resource.getContent().getId();
    }

    private UUID getImplementationId(EntityModel<ImplementationArtifactDto> resource) {
        return resource.getContent().getImplementationId();
    }
}
