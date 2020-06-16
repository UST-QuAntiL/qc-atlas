package org.planqk.atlas.web.linkassembler;

import java.util.Collection;
import java.util.UUID;

import org.planqk.atlas.web.controller.CloudServiceController;
import org.planqk.atlas.web.dtos.CloudServiceDto;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CloudServiceAssembler extends GenericLinkAssembler<CloudServiceDto> {

    @Override
    public void addLinks(EntityModel<CloudServiceDto> resource) {
        resource.add(links.linkTo(methodOn(CloudServiceController.class).getCloudService(getId(resource))).withSelfRel());

        resource.add(
                links.linkTo(methodOn(CloudServiceController.class).deleteCloudService(getId(resource))).withRel("delete"));
    }

    public void addLinks(Collection<EntityModel<CloudServiceDto>> content) {
        addLinks(new CollectionModel<>(content));
    }

    private UUID getId(EntityModel<CloudServiceDto> resource) {
        return resource.getContent().getId();
    }
}
