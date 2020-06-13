package org.planqk.atlas.core.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

    @OneToMany(mappedBy = "algorithm", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<QuantumResource> requiredQuantumResources = new HashSet<>();

    private String speedUp;

    public void addQuantumResource(@NonNull QuantumResource resource) {
        this.requiredQuantumResources.add(resource);
    }

}
