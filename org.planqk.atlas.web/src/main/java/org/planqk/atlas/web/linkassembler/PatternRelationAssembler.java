package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.PatternRelationController;
import org.planqk.atlas.web.controller.PatternRelationTypeController;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class PatternRelationAssembler extends GenericLinkAssembler<PatternRelationDto> {

    @Override
    public void addLinks(EntityModel<PatternRelationDto> resource) {
        resource.add(
                linkTo(methodOn(PatternRelationController.class).getPatternRelation(getId(resource))).withSelfRel());
        resource.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(getAlgorithmId(resource)))
                .withRel("algorithm"));
        resource.add(linkTo(methodOn(PatternRelationTypeController.class).getPatternRelationType(getTypeId(resource)))
                .withRel("pattern-relation-type"));
    }

    private UUID getId(EntityModel<PatternRelationDto> resource) {
        return resource.getContent().getId();
    }

    private UUID getAlgorithmId(EntityModel<PatternRelationDto> resource) {
        return resource.getContent().getAlgorithm().getId();
    }

    private UUID getTypeId(EntityModel<PatternRelationDto> resource) {
        return resource.getContent().getPatternRelationType().getId();
    }

}
