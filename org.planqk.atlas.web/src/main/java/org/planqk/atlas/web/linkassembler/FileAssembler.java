package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.planqk.atlas.web.controller.ImplementationGlobalController;
import org.planqk.atlas.web.dtos.FileDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class FileAssembler extends GenericLinkAssembler<FileDto> {

    @Override
    public void addLinks(EntityModel<FileDto> resource) {
        resource.add(links.linkTo(methodOn(ImplementationGlobalController.class)
                .getFileOfImplementation(getImplementationId(resource), getId(resource))).withSelfRel());
    }

    private UUID getId(EntityModel<FileDto> resource) {
        return resource.getContent().getId();
    }

    private UUID getImplementationId(EntityModel<FileDto> resource) {
        return resource.getContent().getImplementationId();
    }
}
