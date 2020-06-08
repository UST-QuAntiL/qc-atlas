package org.planqk.atlas.web.linkassembler;

import org.planqk.atlas.web.controller.CloudServiceController;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CloudServiceAssembler implements SimpleRepresentationModelAssembler<CloudServiceDto> {

    @Override
    public void addLinks(EntityModel<CloudServiceDto> resource) {
        resource.add(linkTo(methodOn(CloudServiceController.class).getCloudService(getId(resource))).withSelfRel());

        resource.add(
                linkTo(methodOn(CloudServiceController.class).deleteCloudService(getId(resource))).withRel("delete"));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<CloudServiceDto>> resources) {
        Iterator<EntityModel<CloudServiceDto>> iter = resources.getContent().iterator();
        while (iter.hasNext()) {
            addLinks(iter.next());
        }
    }

    public void addLinks(Collection<EntityModel<CloudServiceDto>> content) {
        addLinks(new CollectionModel<>(content));
    }

    private UUID getId(EntityModel<CloudServiceDto> resource) {
        return resource.getContent().getId();
    }
}
