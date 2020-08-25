package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.controller.AlgorithmRelationTypeController;
import org.planqk.atlas.web.dtos.AlgorithmRelationTypeDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AlgorithmRelationTypeAssembler extends GenericLinkAssembler<AlgorithmRelationTypeDto> {

    @Override
    public void addLinks(EntityModel<AlgorithmRelationTypeDto> resource) {
        resource.add(links.linkTo(methodOn(AlgorithmRelationTypeController.class).getAlgorithmRelationType(getId(resource)))
                .withSelfRel());
    }

    private UUID getId(EntityModel<AlgorithmRelationTypeDto> resource) {
        return resource.getContent().getId();
    }
}
