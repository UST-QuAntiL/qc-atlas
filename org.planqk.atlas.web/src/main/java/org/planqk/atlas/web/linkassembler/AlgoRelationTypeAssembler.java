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
        resource.add(links.linkTo(methodOn(AlgoRelationTypeController.class).getAlgoRelationTypeById(getId(resource)))
                .withSelfRel());
        resource.add(links.linkTo(methodOn(AlgoRelationTypeController.class).updateAlgoRelationType(getId(resource),
                getContent(resource))).withRel("update"));
        resource.add(links.linkTo(methodOn(AlgoRelationTypeController.class).deleteAlgoRelationType(getId(resource)))
                .withRel("delete"));
    }

    private UUID getId(EntityModel<AlgoRelationTypeDto> resource) {
        return resource.getContent().getId();
    }
}
