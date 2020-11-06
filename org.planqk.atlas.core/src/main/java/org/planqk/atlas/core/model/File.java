package org.planqk.atlas.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class File extends KnowledgeArtifact {

    private String name;

    private String mimeType;

    @Column(unique = true)
    private String fileURL;

}
