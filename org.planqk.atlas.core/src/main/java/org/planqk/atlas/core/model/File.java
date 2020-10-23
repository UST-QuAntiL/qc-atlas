package org.planqk.atlas.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implementation_id")
    @EqualsAndHashCode.Exclude
    private Implementation implementation;
}
