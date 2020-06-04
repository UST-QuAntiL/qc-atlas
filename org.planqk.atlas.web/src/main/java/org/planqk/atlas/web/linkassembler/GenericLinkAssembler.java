package org.planqk.atlas.web.linkassembler;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public abstract class GenericLinkAssembler<T> {

	public abstract void addLinks(EntityModel<T> resource);

	public void addLinks(CollectionModel<EntityModel<T>> resources) {
		Iterator<EntityModel<T>> iter = resources.getContent().iterator();
		while (iter.hasNext()) {
			addLinks(iter.next());
		}
	}

	public void addLinks(Collection<EntityModel<T>> content) {
		addLinks(new CollectionModel<EntityModel<T>>(content));
	}

	public T getContent(EntityModel<T> resource) {
		return resource.getContent();
	}

}
