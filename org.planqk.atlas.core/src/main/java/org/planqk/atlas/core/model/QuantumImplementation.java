package org.planqk.atlas.core.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class QuantumImplementation extends Implementation {

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private QuantumAlgorithm algorithm;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private Set<QuantumResource> requiredQuantumResources;

    @ManyToOne(fetch = FetchType.LAZY)
    private SoftwarePlatform usedSoftwarePlatform;
}
