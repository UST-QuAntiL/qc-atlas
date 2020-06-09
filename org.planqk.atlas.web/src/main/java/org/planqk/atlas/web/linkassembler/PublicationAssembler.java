package org.planqk.atlas.web.linkassembler;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.PublicationController;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PublicationAssembler extends GenericLinkAssembler<PublicationDto> {

    @Override
    public void addLinks(EntityModel<PublicationDto> resource){
            resource.add(linkTo(methodOn(PublicationController.class).getPublication(this.getId(resource))).withSelfRel());
            resource.add(linkTo(methodOn(PublicationController.class).updatePublication(this.getId(resource), this.getContent(resource))).withRel("update"));
            resource.add(linkTo(methodOn(PublicationController.class).deletePublication(this.getId(resource))).withRel("delete"));
            resource.add(linkTo(methodOn(PublicationController.class).getAlgorithms(this.getId(resource))).withRel(Constants.ALGORITHMS));
    }

    private UUID getId(EntityModel<PublicationDto> resource){
        return resource.getContent().getId();
    }

    public void addAlgorithmLink(CollectionModel<EntityModel<AlgorithmDto>> ressources, UUID id){
        ressources.add(linkTo(methodOn(PublicationController.class).getAlgorithms(id)).withSelfRel());
    }

}
