package org.planqk.atlas.web.linkassembler;

import org.planqk.atlas.web.controller.SoftwarePlatformController;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
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
public class SoftwarePlatformAssembler implements SimpleRepresentationModelAssembler<SoftwarePlatformDto> {

    @Override
    public void addLinks(EntityModel<SoftwarePlatformDto> resource) {
        resource.add(linkTo(methodOn(SoftwarePlatformController.class).getSoftwarePlatform(getId(resource))).withSelfRel());

        resource.add(linkTo(methodOn(SoftwarePlatformController.class).deleteSoftwarePlatform(getId(resource))).withRel("delete"));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<SoftwarePlatformDto>> resources) {
        Iterator<EntityModel<SoftwarePlatformDto>> iter = resources.getContent().iterator();
        while (iter.hasNext()) {
            addLinks(iter.next());
        }
    }

    public void addLinks(Collection<EntityModel<SoftwarePlatformDto>> content) {
        addLinks(new CollectionModel<>(content));
    }

    private UUID getId(EntityModel<SoftwarePlatformDto> resource) {
        return resource.getContent().getId();
    }
}
