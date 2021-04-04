package org.planqk.atlas.web.dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.server.core.Relation;

import lombok.Data;

/**
 * Data transfer object for Revision {@link org.hibernate.envers.DefaultRevisionEntity})
 */
@Relation(itemRelation = "revision", collectionRelation = "revisions")
@Data
public class RevisionDto {

    @JsonProperty("id")
    private int revisionNumber;

    @JsonProperty("creationDate")
    private Date revisionInstant;
}
