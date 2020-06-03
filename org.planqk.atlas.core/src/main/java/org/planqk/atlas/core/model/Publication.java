package org.planqk.atlas.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
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

    @ManyToMany(mappedBy = "publications", fetch=FetchType.EAGER)
    private Set<Algorithm> algorithms;

    @ElementCollection
    private List<String> authors;

    @NonNull
    public Set<Algorithm> getAlgorithms() {
        if (Objects.isNull(algorithms)) {
            return new HashSet<>();
        }
        return algorithms;
    }

    @NonNull
    public List<String> getAuthors() {
        if (Objects.isNull(authors)) {
            return new ArrayList<>();
        }
        return authors;
    }
}
