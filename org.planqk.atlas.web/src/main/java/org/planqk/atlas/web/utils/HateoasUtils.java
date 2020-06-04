package org.planqk.atlas.web.utils;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;

public class HateoasUtils {

    public static <T> EntityModel<T> generateEntityModel(T object) {
        return new EntityModel<T>(object);
    }

    public static <T> CollectionModel<EntityModel<T>> generateCollectionModel(Set<T> objectSet) {
        Set<EntityModel<T>> dtoCollection = new HashSet<EntityModel<T>>();
        for (T object : objectSet) {
            dtoCollection.add(generateEntityModel(object));
        }
        return new CollectionModel<>(dtoCollection);
    }

    public static <T> PagedModel<EntityModel<T>> generatePagedModel(Page<T> pagedObject) {
        return PagedModel.wrap((Iterable<T>) pagedObject.getContent(), new PageMetadata(pagedObject.getSize(),
                pagedObject.getNumber(), pagedObject.getNumberOfElements(), pagedObject.getTotalPages()));
    }
}
