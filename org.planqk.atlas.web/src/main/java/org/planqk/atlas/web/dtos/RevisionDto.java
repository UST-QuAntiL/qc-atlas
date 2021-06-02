package org.planqk.atlas.web.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Data transfer object for Revision {@link org.hibernate.envers.DefaultRevisionEntity})
 */
@Data
public class RevisionDto {

    @JsonProperty("id")
    private int revisionNumber;

    @JsonProperty("creationDate")
    private String revisionInstant;
}
