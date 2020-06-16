package org.planqk.atlas.web.linkassembler;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public abstract class GenericLinkAssembler<T> {
    @Autowired
    protected LinkBuilderService links;

    public abstract void addLinks(EntityModel<T> resource);

    public void addLinks(CollectionModel<EntityModel<T>> resources) {
        for (EntityModel<T> entity : resources.getContent()) {
            addLinks(entity);
        }
    }

    public void addLinks(Collection<EntityModel<T>> content) {
        addLinks(new CollectionModel<>(content));
    }

    public T getContent(EntityModel<T> resource) {
        return resource.getContent();
    }
}
