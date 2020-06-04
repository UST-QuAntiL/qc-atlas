package org.planqk.atlas.core.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class AlgorithmRelation extends HasId {

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private Algorithm sourceAlgorithm;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "targetAlgorithm", referencedColumnName = "id")
    private Algorithm targetAlgorithm;

    @ManyToOne(fetch = FetchType.LAZY)
    private AlgoRelationType algoRelationType;

    private String description;

}
