package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.controller.AlgorithmRelationTypeController;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AlgorithmRelationAssembler extends GenericLinkAssembler<AlgorithmRelationDto> {

    @Override
    public void addLinks(EntityModel<AlgorithmRelationDto> resource) {
        resource.add(links.linkTo(methodOn(AlgorithmController.class).getAlgorithm(getSourceAlgorithmId(resource)))
                .withRel("sourceAlgorithm"));
        resource.add(links.linkTo(methodOn(AlgorithmController.class).getAlgorithm(getTargetAlgorithmId(resource)))
                .withRel("targetAlgorithm"));
        resource.add(links.linkTo(
                methodOn(AlgorithmRelationTypeController.class).getAlgorithmRelationType(getAlgoRelationTypeId(resource)))
                .withRel("algoRelationType"));
    }

    private UUID getSourceAlgorithmId(EntityModel<AlgorithmRelationDto> resource) {
        return resource.getContent().getSourceAlgorithmId();
    }

    private UUID getTargetAlgorithmId(EntityModel<AlgorithmRelationDto> resource) {
        return resource.getContent().getTargetAlgorithmId();
    }

    private UUID getAlgoRelationTypeId(EntityModel<AlgorithmRelationDto> resource) {
        return resource.getContent().getAlgoRelationType().getId();
    }
}
