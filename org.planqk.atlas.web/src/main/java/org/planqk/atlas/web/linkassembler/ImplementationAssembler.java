package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.ImplementationController;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.TagDto;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ImplementationAssembler extends GenericLinkAssembler<ImplementationDto> {

    @Override
    public void addLinks(EntityModel<ImplementationDto> resource) {
        resource.add(
                links.linkTo(methodOn(ImplementationController.class).getImplementation(getAlgId(resource), getId(resource)))
                        .withSelfRel());
        resource.add(links.linkTo(methodOn(AlgorithmController.class).getAlgorithm(getAlgId(resource)))
                .withRel(Constants.ALGORITHM_LINK));
        resource.add(links.linkTo(methodOn(ImplementationController.class).getTags(getAlgId(resource), getId(resource)))
                .withRel(Constants.TAGS));
    }

    public void addTagLink(CollectionModel<EntityModel<TagDto>> resultCollection, UUID algoId, UUID implId) {
        resultCollection.add(links.linkTo(methodOn(ImplementationController.class).getTags(algoId, implId)).withSelfRel());
    }

    public UUID getId(EntityModel<ImplementationDto> resource) {
        return resource.getContent().getId();
    }

    public UUID getAlgId(EntityModel<ImplementationDto> resource) {
        return resource.getContent().getImplementedAlgorithm().getId();
    }
}
