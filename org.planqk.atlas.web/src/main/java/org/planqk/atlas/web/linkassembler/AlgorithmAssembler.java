package org.planqk.atlas.web.linkassembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.ImplementationController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class AlgorithmAssembler extends GenericLinkAssembler<AlgorithmDto> {

    @Override
    public void addLinks(EntityModel<AlgorithmDto> resource) {

        resource.add(linkTo(methodOn(AlgorithmController.class).getAlgorithm(getId(resource))).withSelfRel());
        resource.add(linkTo(methodOn(AlgorithmController.class).updateAlgorithm(getId(resource), getContent(resource)))
                .withRel("update"));
        resource.add(linkTo(methodOn(AlgorithmController.class).deleteAlgorithm(getId(resource))).withRel("delete"));
        resource.add(linkTo(methodOn(AlgorithmController.class).getTags(getId(resource))).withRel(Constants.TAGS));
        resource.add(linkTo(methodOn(ImplementationController.class).getImplementations(getId(resource)))
                .withRel(Constants.IMPLEMENTATIONS));
        resource.add(linkTo(methodOn(AlgorithmController.class).getProblemTypes(getId(resource)))
                .withRel(Constants.PROBLEM_TYPES));
        resource.add(linkTo(methodOn(AlgorithmController.class).getAlgorithmRelations(getId(resource)))
                .withRel(Constants.ALGORITHM_RELATIONS));
        resource.add(linkTo(methodOn(AlgorithmController.class).getPublications(getId(resource)))
                .withRel(Constants.PUBLICATIONS));

    }

    public void addProblemTypeLink(CollectionModel<EntityModel<ProblemTypeDto>> resources, UUID id) {
        resources.add(linkTo(methodOn(AlgorithmController.class).getProblemTypes(id)).withSelfRel());
    }

    public void addTagLink(CollectionModel<EntityModel<TagDto>> resources, UUID id) {
        resources.add(linkTo(methodOn(AlgorithmController.class).getTags(id)).withSelfRel());
    }
    
    public void addPublicationLink(CollectionModel<EntityModel<PublicationDto>> resources, UUID id) {
        resources.add(linkTo(methodOn(AlgorithmController.class).getPublications(id)).withSelfRel());
    }

    public void addAlgorithmRelationLink(CollectionModel<EntityModel<AlgorithmRelationDto>> resultCollection,
            UUID sourceAlgorithm_id) {
        resultCollection.add(
                linkTo(methodOn(AlgorithmController.class).getAlgorithmRelations(sourceAlgorithm_id)).withSelfRel());
    }

    private UUID getId(EntityModel<AlgorithmDto> resource) {
        return resource.getContent().getId();
    }

}
