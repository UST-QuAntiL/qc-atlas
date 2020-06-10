package org.planqk.atlas.core.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Entity;
import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper=true)
@Entity
@Data
public class DiscussionComment extends HasId {

    private String text;
    private OffsetDateTime date;
}
