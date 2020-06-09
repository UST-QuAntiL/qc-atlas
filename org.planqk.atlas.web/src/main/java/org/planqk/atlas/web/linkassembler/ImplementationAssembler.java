package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.ImplementationController;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class ImplementationAssembler extends GenericLinkAssembler<ImplementationDto> {

    @Override
    public void addLinks(EntityModel<ImplementationDto> resource) {
        resource.add(
                linkTo(methodOn(ImplementationController.class).getImplementation(getId(resource)))
                        .withSelfRel());
        resource.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(getAlgId(resource)))
                .withRel(Constants.ALGORITHM_LINK));
        resource.add(linkTo(methodOn(ImplementationController.class).getTags(getId(resource)))
                .withRel(Constants.TAGS));

    }

    public void addTagLink(CollectionModel<EntityModel<TagDto>> resultCollection, UUID implId) {
        resultCollection.add(linkTo(methodOn(ImplementationController.class).getTags(implId)).withSelfRel());
    }

    public UUID getId(EntityModel<ImplementationDto> resource) {
        return resource.getContent().getId();
    }

    public UUID getAlgId(EntityModel<ImplementationDto> resource) {
        return resource.getContent().getImplementedAlgorithm().getId();
    }

}
