package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.controller.PatternRelationTypeController;
import org.planqk.atlas.web.dtos.PatternRelationTypeDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PatternRelationTypeAssembler extends GenericLinkAssembler<PatternRelationTypeDto> {

    @Override
    public void addLinks(EntityModel<PatternRelationTypeDto> resource) {
        resource.add(links.linkTo(methodOn(PatternRelationTypeController.class).getPatternRelationType(getId(resource)))
                .withSelfRel());
        resource.add(links.linkTo(methodOn(PatternRelationTypeController.class).updatePatternRelationType(getId(resource),
                getContent(resource))).withRel("update"));
        resource.add(links.linkTo(methodOn(PatternRelationTypeController.class).deletePatternRelationType(getId(resource)))
                .withRel("delete"));
    }

    private UUID getId(EntityModel<PatternRelationTypeDto> resource) {
        return resource.getContent().getId();
    }
}
