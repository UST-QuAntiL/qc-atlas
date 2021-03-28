package org.planqk.atlas.web.dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Data transfer object for Revision {@link org.springframework.data.envers.repository.support.DefaultRevisionMetadata})
 */
@Data
public class RevisionDto {

    @JsonProperty("revisionId")
    private int revisionNumber;

    @JsonProperty("revisionDate")
    private Date revisionInstant;
}
