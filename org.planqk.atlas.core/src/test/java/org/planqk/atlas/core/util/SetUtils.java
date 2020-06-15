package org.planqk.atlas.core.util;

import java.util.Arrays;
import java.util.HashSet;

public class SetUtils {

    public static <T> HashSet<T> hashSetOf(T... elems) {
        return new HashSet<T>(Arrays.asList(elems));
    }
}
