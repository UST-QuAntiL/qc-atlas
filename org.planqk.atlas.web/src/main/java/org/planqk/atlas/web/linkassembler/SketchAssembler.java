package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.dtos.SketchDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SketchAssembler extends GenericLinkAssembler<SketchDto> {

    @Override
    public void addLinks(EntityModel<SketchDto> resource) {
        resource.add(links.linkTo(methodOn(AlgorithmController.class).getSketch(this.getAlgoId(), this.getSketchId(resource))).withSelfRel());
    }

    private UUID getSketchId(EntityModel<SketchDto> resource) {
        return resource.getContent().getId();
    }

    private UUID getAlgoId() {
        return UUID.randomUUID();
    }
}
