package org.planqk.atlas.core.util;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class CollectionUtils {
    /**
     * Performs the given action for each element of the given @link Collection.
     * To allow concurrent modifications the collection is copied before iterating over it.
     *
     * @param coll The collection to iterate over
     * @param action The action to be performed for each element
     */
    public static <T> void forEachOnCopy(Collection<T> coll, Consumer<? super T> action) {
        Objects.requireNonNull(coll);
        Objects.requireNonNull(action);
        final var copy = List.copyOf(coll);
        for (T t : copy) {
            action.accept(t);
        }
    }
}
