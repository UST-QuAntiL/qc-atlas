package org.planqk.atlas.core.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImplementationArtifact extends KnowledgeArtifact{

    private String name;

    private String mimeType;

    private String fileURL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implementation_id")
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Implementation implementation;
}
