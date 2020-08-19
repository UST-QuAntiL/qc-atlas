package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.AlgorithmController;
import org.planqk.atlas.web.controller.ImplementationController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ApplicationAreaDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.planqk.atlas.web.utils.ListParameters;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AlgorithmAssembler extends GenericLinkAssembler<AlgorithmDto> {

    @Override
    public void addLinks(EntityModel<AlgorithmDto> resource) {
        resource.add(links.linkTo(methodOn(AlgorithmController.class).getAlgorithm(getId(resource))).withSelfRel());
//        resource.add(links.linkTo(methodOn(AlgorithmController.class).getTags(getId(resource))).withRel(Constants.TAGS));
//        resource.add(links.linkTo(methodOn(ImplementationController.class).getImplementations(getId(resource)))
//                .withRel(Constants.IMPLEMENTATIONS));
        resource.add(links.linkTo(methodOn(AlgorithmController.class)
                .getProblemTypesForAlgorithm(getId(resource), ListParameters.getDefault()))
                .withRel(Constants.PROBLEM_TYPES));
        resource.add(links.linkTo(methodOn(AlgorithmController.class)
                .getAlgorithmRelationsForAlgorithm(getId(resource), ListParameters.getDefault()))
                .withRel(Constants.ALGORITHM_RELATIONS));
        resource.add(links.linkTo(methodOn(AlgorithmController.class)
                .getPublicationsForAlgorithm(getId(resource), ListParameters.getDefault()))
                .withRel(Constants.PUBLICATIONS));
        resource.add(links.linkTo(methodOn(AlgorithmController.class)
                .getPatternRelationsForAlgorithm(getId(resource), ListParameters.getDefault()))
                .withRel(Constants.PATTERN_RELATIONS));
    }

    public void addProblemTypeLink(CollectionModel<EntityModel<ProblemTypeDto>> resources, UUID id) {
        resources.add(links.linkTo(methodOn(AlgorithmController.class)
                .getProblemTypesForAlgorithm(id, ListParameters.getDefault())).withSelfRel());
    }

    public void addApplicationAreaLink(CollectionModel<EntityModel<ApplicationAreaDto>> resources, UUID id) {
        resources.add(links.linkTo(methodOn(AlgorithmController.class)
                .getApplicationAreasForAlgorithm(id, ListParameters.getDefault())).withSelfRel());
    }

    public void addTagLink(CollectionModel<EntityModel<TagDto>> resources, UUID id) {
//        resources.add(links.linkTo(methodOn(AlgorithmController.class).getTags(id)).withSelfRel());
    }

    public void addPublicationLink(CollectionModel<EntityModel<PublicationDto>> resources, UUID id) {
        resources.add(links.linkTo(methodOn(AlgorithmController.class)
                .getPublicationsForAlgorithm(id, ListParameters.getDefault())).withSelfRel());
    }

    public void addAlgorithmRelationLink(CollectionModel<EntityModel<AlgorithmRelationDto>> resultCollection,
                                         UUID sourceAlgorithm_id) {
        resultCollection.add(
                links.linkTo(methodOn(AlgorithmController.class)
                        .getAlgorithmRelationsForAlgorithm(sourceAlgorithm_id, ListParameters.getDefault())).withSelfRel());
    }

    public void addPatternRelationLink(CollectionModel<EntityModel<PatternRelationDto>> resultCollection, UUID
            id) {
        resultCollection.add(
                links.linkTo(methodOn(AlgorithmController.class)
                        .getPatternRelationsForAlgorithm(id, ListParameters.getDefault())).withSelfRel());
    }

    private UUID getId(EntityModel<AlgorithmDto> resource) {
        return resource.getContent().getId();
    }
}
