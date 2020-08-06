package org.planqk.atlas.core.model;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@NoArgsConstructor
@Data
public class KnowledgeArtifact extends HasId {

    public Date creationDate;

    public Date lastModifiedAt;

}
