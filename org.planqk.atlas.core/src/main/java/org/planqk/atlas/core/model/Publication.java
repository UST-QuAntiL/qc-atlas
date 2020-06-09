package org.planqk.atlas.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.net.URL;
import java.util.*;

/**
 * Entity which represents the publication.
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Data
public class Publication extends KnowledgeArtifact {

    private String doi;
    private URL url;
    private String title;

    @ElementCollection
    private List<String> authors;

    @NonNull
    public List<String> getAuthors() {
        if (Objects.isNull(authors)) {
            return new ArrayList<>();
        }
        return authors;
    }
}
