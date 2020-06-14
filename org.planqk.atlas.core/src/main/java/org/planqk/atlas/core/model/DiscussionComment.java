package org.planqk.atlas.core.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.engine.internal.JoinSequence;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper=true)
@Entity
@Data
public class DiscussionComment extends HasId {

    private String text;
    private OffsetDateTime date;

    @OneToOne
    @JoinColumn(name = "parentComment")
    private DiscussionComment replyTo;

    @ManyToOne
    @JoinColumn(name="discussionTopic")
    private DiscussionTopic discussionTopic;
}
