package org.planqk.atlas.core.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A compute resource is a QPU or a Simulator which are both able to run Quantum Algorithms. E.g. ibmq_rome or qasm_simulator.
 */

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ComputeResource extends HasId {

    private String name;
    private String vendor;
    private String technology;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private Set<ComputingResourceProperty> providedQuantumResources = new HashSet<>();

    private QuantumComputationModel quantumComputationModel;
}
