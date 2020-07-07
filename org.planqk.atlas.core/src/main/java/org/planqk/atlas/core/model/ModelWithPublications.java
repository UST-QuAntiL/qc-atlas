package org.planqk.atlas.core.model;

import java.util.Set;

public interface ModelWithPublications {
    Set<Publication> getPublications();

    void setPublications(Set<Publication> publications);
}
