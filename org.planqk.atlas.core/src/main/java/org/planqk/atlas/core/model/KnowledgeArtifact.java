package org.planqk.atlas.core.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public class KnowledgeArtifact extends HasId {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "knowledgeArtifact", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<DiscussionTopic> discussionTopics = new HashSet<>();

}
