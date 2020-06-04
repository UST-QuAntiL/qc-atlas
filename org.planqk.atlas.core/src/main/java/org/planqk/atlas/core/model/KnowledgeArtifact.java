package org.planqk.atlas.core.model;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@NoArgsConstructor
public class KnowledgeArtifact extends HasId {
}
