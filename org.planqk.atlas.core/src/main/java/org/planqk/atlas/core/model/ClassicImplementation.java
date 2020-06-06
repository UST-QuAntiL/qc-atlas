package org.planqk.atlas.core.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ClassicImplementation extends Implementation {

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private ClassicAlgorithm algorithm;

}
