package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.controller.AlgoRelationTypeController;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AlgoRelationTypeAssembler extends GenericLinkAssembler<AlgoRelationTypeDto> {

    @Override
    public void addLinks(EntityModel<AlgoRelationTypeDto> resource) {
        resource.add(links.linkTo(methodOn(AlgoRelationTypeController.class).getAlgorithmRelationType(getId(resource)))
                .withSelfRel());
    }

    private UUID getId(EntityModel<AlgoRelationTypeDto> resource) {
        return resource.getContent().getId();
    }
}
