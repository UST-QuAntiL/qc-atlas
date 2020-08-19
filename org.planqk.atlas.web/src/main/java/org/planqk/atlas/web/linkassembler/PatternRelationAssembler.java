package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.PatternRelationController;
import org.planqk.atlas.web.controller.PatternRelationTypeController;
import org.planqk.atlas.web.dtos.PatternRelationDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PatternRelationAssembler extends GenericLinkAssembler<PatternRelationDto> {

    @Override
    public void addLinks(EntityModel<PatternRelationDto> resource) {
        resource.add(
                links.linkTo(methodOn(PatternRelationController.class).getPatternRelation(getId(resource))).withSelfRel());
        resource.add(links.linkTo(methodOn(AlgorithmController.class).getAlgorithm(getAlgorithmId(resource)))
                .withRel(Constants.ALGORITHM));
        resource.add(links.linkTo(methodOn(PatternRelationTypeController.class).getPatternRelationType(getTypeId(resource)))
                .withRel(Constants.PATTERN_RELATION_TYPES));
    }

    private UUID getId(EntityModel<PatternRelationDto> resource) {
        return resource.getContent().getId();
    }

    private UUID getAlgorithmId(EntityModel<PatternRelationDto> resource) {
        return resource.getContent().getAlgorithmId();
    }

    private UUID getTypeId(EntityModel<PatternRelationDto> resource) {
        return resource.getContent().getPatternRelationType().getId();
    }
}
