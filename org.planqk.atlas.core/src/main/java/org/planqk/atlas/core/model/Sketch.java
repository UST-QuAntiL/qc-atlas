package org.planqk.atlas.core.model;

import javax.persistence.Entity;
import javax.persistence.Lob;

import org.hibernate.annotations.Type;

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
    private String image;

    private String description;

}
