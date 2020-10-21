/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.planqk.atlas.web.linkassembler;

import java.util.Collection;
import java.util.stream.Collectors;

import org.planqk.atlas.web.utils.ModelMapperUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

public abstract class GenericLinkAssembler<T> {
    @Autowired
    protected LinkBuilderService links;

    @Autowired
    private PagedResourcesAssembler<T> pagedResourcesAssembler;

    private final Class<T> entityClass = inferEntityClass();

    public abstract void addLinks(EntityModel<T> resource);

    public void addLinks(CollectionModel<EntityModel<T>> resources) {
        addLinks(resources.getContent());
    }

    public void addLinks(Collection<EntityModel<T>> content) {
        for (EntityModel<T> entity : content) {
            addLinks(entity);
        }
    }

    public T getContent(EntityModel<T> resource) {
        return resource.getContent();
    }

    public <U> PagedModel<EntityModel<T>> toModel(Page<U> page, Class<T> entityClass) {
        final var entities = page.map(item -> ModelMapperUtils.convert(item, entityClass));
        final var model = pagedResourcesAssembler.toModel(entities);
        addLinks(model.getContent());
        return model;
    }

    public <U> PagedModel<EntityModel<T>> toModel(Page<U> page) {
        return toModel(page, entityClass);
    }

    public <U> CollectionModel<EntityModel<T>> toModel(Collection<U> collection, Class<T> entityClass) {
        final var entities = collection.stream().map(item ->
                new EntityModel<>(ModelMapperUtils.convert(item, entityClass))
        ).collect(Collectors.toUnmodifiableList());

        final var collectionModel = new CollectionModel<>(entities);
        addLinks(collectionModel);
        return collectionModel;
    }

    public <U> CollectionModel<EntityModel<T>> toModel(Collection<U> collection) {
        return toModel(collection, entityClass);
    }

    public <U> EntityModel<T> toModel(U entity, Class<T> entityClass) {
        EntityModel<T> entityModel = new EntityModel<>(ModelMapperUtils.convert(entity, entityClass));
        addLinks(entityModel);
        return entityModel;
    }

    public <U> EntityModel<T> toModel(U entity) {
        return toModel(entity, entityClass);
    }

    @SuppressWarnings("unchecked")
    private Class<T> inferEntityClass() {
        final var resolved = GenericTypeResolver.resolveTypeArgument(getClass(), GenericLinkAssembler.class);
        if (resolved == null)
            throw new RuntimeException("GenericLinkAssembler<T> sub-classes need to use a concrete T");

        return (Class<T>) resolved;
    }
}
