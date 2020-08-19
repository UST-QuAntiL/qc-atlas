package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.dtos.ImplementationDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class ImplementationAssembler extends GenericLinkAssembler<ImplementationDto> {

    @Override
    public void addLinks(EntityModel<ImplementationDto> resource) {
//        resource.add(
//                links.linkTo(methodOn(ImplementationController.class).getImplementation(getAlgId(resource), getId(resource)))
//                        .withSelfRel());
//        resource.add(links.linkTo(methodOn(AlgorithmController.class).getAlgorithm(getAlgId(resource)))
//                .withRel(Constants.ALGORITHM_LINK));
//        resource.add(links.linkTo(methodOn(ImplementationController.class).getPublicationsOfImplementation(getAlgId(resource), getId(resource))).withRel(Constants.PUBLICATIONS));

//        resource.add(links.linkTo(methodOn(ImplementationController.class).getTags(getId(resource)))
//                .withRel(Constants.TAGS));
    }

//    public void addTagLink(CollectionModel<EntityModel<TagDto>> resultCollection, UUID implId) {
//        resultCollection.add(links.linkTo(methodOn(ImplementationController.class).getTags(implId)).withSelfRel());
//    }

    public UUID getId(EntityModel<ImplementationDto> resource) {
        return resource.getContent().getId();
    }

    public UUID getAlgId(EntityModel<ImplementationDto> resource) {
        return resource.getContent().getImplementedAlgorithmId();
    }
}
