package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.controller.ProblemTypeController;
import org.planqk.atlas.web.dtos.ProblemTypeDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProblemTypeAssembler extends GenericLinkAssembler<ProblemTypeDto> {

    @Override
    public void addLinks(EntityModel<ProblemTypeDto> resource) {
        resource.add(links.linkTo(methodOn(ProblemTypeController.class).getProblemTypeById(getId(resource))).withSelfRel());
        resource.add(
                links.linkTo(methodOn(ProblemTypeController.class).updateProblemType(getId(resource), getContent(resource)))
                        .withRel("update"));
        resource.add(
                links.linkTo(methodOn(ProblemTypeController.class).deleteProblemType(getId(resource))).withRel("delete"));
    }

    private UUID getId(EntityModel<ProblemTypeDto> resource) {
        return resource.getContent().getId();
    }
}
