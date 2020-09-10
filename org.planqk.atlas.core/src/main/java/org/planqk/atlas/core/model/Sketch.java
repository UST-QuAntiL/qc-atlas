package org.planqk.atlas.core.model;

import static lombok.EqualsAndHashCode.Exclude;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String imageURL;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @Exclude
    @JsonIgnore
    private Algorithm algorithm;

    @OneToOne(mappedBy = "sketch", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @Exclude
    @JsonIgnore
    private Image image;

}
