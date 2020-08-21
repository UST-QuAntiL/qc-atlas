package org.planqk.atlas.core.model;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
public class QuantumAlgorithm extends Algorithm {

    private boolean nisqReady;

    private QuantumComputationModel quantumComputationModel;

    private String speedUp;

//    @OneToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
//    @JoinTable(name = "algorithm_implementation",
//            joinColumns = @JoinColumn(name = "algorithm_id"),
//            inverseJoinColumns = @JoinColumn(name = "implementation_id"))
//    @EqualsAndHashCode.Exclude
//    private Set<QuantumImplementation> implementations;
}
