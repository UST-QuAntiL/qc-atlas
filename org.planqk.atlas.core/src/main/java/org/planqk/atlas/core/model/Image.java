package org.planqk.atlas.core.model;

import java.sql.Blob;
import javax.persistence.Entity;
import javax.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Image extends KnowledgeArtifact {

    @Lob
    private byte[] image;


}
