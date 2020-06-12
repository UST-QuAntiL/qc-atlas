package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.planqk.atlas.web.controller.PatternRelationTypeController;
import org.planqk.atlas.web.dtos.PatternRelationTypeDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class PatternRelationTypeAssembler extends GenericLinkAssembler<PatternRelationTypeDto> {

    @Override
    public void addLinks(EntityModel<PatternRelationTypeDto> resource) {
        resource.add(linkTo(methodOn(PatternRelationTypeController.class).getPatternRelationType(getId(resource)))
                .withSelfRel());
        resource.add(linkTo(methodOn(PatternRelationTypeController.class).updatePatternRelationType(getId(resource),
                getContent(resource))).withRel("update"));
        resource.add(linkTo(methodOn(PatternRelationTypeController.class).deletePatternRelationType(getId(resource)))
                .withRel("delete"));
    }

    private UUID getId(EntityModel<PatternRelationTypeDto> resource) {
        return resource.getContent().getId();
    }
}
