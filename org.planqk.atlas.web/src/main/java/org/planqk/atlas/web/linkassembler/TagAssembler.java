package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.TagController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class TagAssembler extends GenericLinkAssembler<TagDto> {

    @Override
    public void addLinks(EntityModel<TagDto> resource) {
        resource.add(linkTo(methodOn(TagController.class).getTagById(getId(resource))).withSelfRel());
        resource.add(linkTo(methodOn(TagController.class).getAlgorithmsOfTag(getId(resource)))
                .withRel(Constants.ALGORITHMS));
        resource.add(linkTo(methodOn(TagController.class).getImplementationsOfTag(getId(resource)))
                .withRel(Constants.IMPLEMENTATIONS));
    }

    public void addAlgorithmLink(CollectionModel<EntityModel<AlgorithmDto>> resources, UUID id) {
        resources.add(linkTo(methodOn(TagController.class).getAlgorithmsOfTag(id)).withSelfRel());
    }

    public void addImplementationLink(CollectionModel<EntityModel<ImplementationDto>> resources, UUID id) {
        resources.add(linkTo(methodOn(TagController.class).getImplementationsOfTag(id)).withSelfRel());
    }

    private UUID getId(EntityModel<TagDto> resource) {
        return resource.getContent().getId();
    }

}
