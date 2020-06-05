package org.planqk.atlas.web.annotation;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.Assert.assertEquals;

class VersionedRequestHandlerMappingTest {

    @RequestMapping("/unversioned")
    static class Unversioned {
        @RequestMapping("/versioned")
        @ApiVersion("v1")
        public void versioned() {
        }
    }

    @RequestMapping("/versioned")
    @ApiVersion("v1")
    static class Versioned {
        @RequestMapping("/unversioned")
        public void unversioned() {
        }

        @RequestMapping("/versioned")
        @ApiVersion({ "v1", "v2" })
        public void versioned() {
        }
    }

    private VersionedRequestHandlerMapping mapping = new VersionedRequestHandlerMapping();

    @Test
    void getMappingForMethod() throws NoSuchMethodException {
        assertEquals(Set.of("/unversioned/v1/versioned"), getMethodPathMapping(Unversioned.class, "versioned"));
        assertEquals(Set.of("/versioned/v1/unversioned"), getMethodPathMapping(Versioned.class, "unversioned"));
        assertEquals(Set.of("/versioned/v1/versioned", "/versioned/v2/versioned"),
                getMethodPathMapping(Versioned.class, "versioned"));
    }

    private Set<String> getMethodPathMapping(Class<?> clazz, String methodName) throws NoSuchMethodException {
        return mapping.getMappingForMethod(clazz.getMethod(methodName), clazz).getPatternsCondition().getPatterns();
    }
}
