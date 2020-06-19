package org.planqk.atlas.core.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
public class QuantumAlgorithm extends Algorithm {

    private boolean nisqReady;

    private QuantumComputationModel quantumComputationModel;

    @OneToMany(mappedBy = "algorithm", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<QuantumResource> requiredQuantumResources = new HashSet<>();

    private String speedUp;

    @OneToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "algorithm_implementation",
            joinColumns = @JoinColumn(name = "algorithm_id"),
            inverseJoinColumns = @JoinColumn(name = "implementation_id"))
    @EqualsAndHashCode.Exclude
    private Set<QuantumImplementation> implementations;

    public void addQuantumResource(@NonNull QuantumResource resource) {
        this.requiredQuantumResources.add(resource);
    }
}
