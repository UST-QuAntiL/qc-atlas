package org.planqk.atlas.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper=true)
@Data
@Entity
public class DiscussionTopic extends KnowledgeArtifact {

    private String title;
    private String Description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private OffsetDateTime date;

    @OneToMany(mappedBy="discussionTopic",orphanRemoval=true, cascade = { CascadeType.ALL })
    private Set<DiscussionComment> discussionComments = new HashSet<>();

}
