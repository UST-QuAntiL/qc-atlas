package org.planqk.atlas.core.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.EqualsAndHashCode.Exclude;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entity representing a sketch with an image and a description.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sketch extends KnowledgeArtifact {


    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String image;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @Exclude
    @JsonIgnore
    private Algorithm algorithm;

}
