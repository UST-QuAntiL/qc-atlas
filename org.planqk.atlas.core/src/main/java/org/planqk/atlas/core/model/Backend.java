package org.planqk.atlas.core.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A backend is a QPU or a Simulator which are both able to run Quantum Algorithms.
 * E.g. ibmq_rome or qasm_simulator.
 */

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "backends")
@Data
public class Backend extends HasId {

    private String name;
    private String vendor;
    private String technology;
    private QuantumComputationModel quantumComputationModel;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    private Set<BackendProperty> backendProperties = new HashSet<>();
}
