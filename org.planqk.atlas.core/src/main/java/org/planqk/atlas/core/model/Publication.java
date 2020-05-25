package org.planqk.atlas.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.net.URL;
import java.util.List;

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
}
