package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.TagController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagAssembler extends GenericLinkAssembler<TagDto> {

    @Override
    public void addLinks(EntityModel<TagDto> resource) {
        resource.add(links.linkTo(methodOn(TagController.class).getTagById(getId(resource))).withSelfRel());
        resource.add(links.linkTo(methodOn(TagController.class).getAlgorithmsOfTag(getId(resource)))
                .withRel(Constants.ALGORITHMS));
        resource.add(links.linkTo(methodOn(TagController.class).getImplementationsOfTag(getId(resource)))
                .withRel(Constants.IMPLEMENTATIONS));
    }

    public void addAlgorithmLink(CollectionModel<EntityModel<AlgorithmDto>> resources, UUID id) {
        resources.add(links.linkTo(methodOn(TagController.class).getAlgorithmsOfTag(id)).withSelfRel());
    }

    public void addImplementationLink(CollectionModel<EntityModel<ImplementationDto>> resources, UUID id) {
        resources.add(links.linkTo(methodOn(TagController.class).getImplementationsOfTag(id)).withSelfRel());
    }

    private UUID getId(EntityModel<TagDto> resource) {
        return resource.getContent().getId();
    }
}
