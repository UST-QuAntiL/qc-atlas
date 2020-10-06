package org.planqk.atlas.core.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;
import org.springframework.http.MediaType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Image extends KnowledgeArtifact {

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] image;

    private String mimeType;

    @OneToOne
    @JoinColumn(name = "sketch_id")
    private Sketch sketch;

}
