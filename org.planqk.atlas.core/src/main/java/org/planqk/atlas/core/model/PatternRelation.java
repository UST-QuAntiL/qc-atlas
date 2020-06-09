package org.planqk.atlas.core.model;

import java.net.URI;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class PatternRelation extends HasId {

    @ManyToOne
    private Algorithm algorithm;

    private URI pattern;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private PatternRelationType patternRelationType;

    private String description;

}
