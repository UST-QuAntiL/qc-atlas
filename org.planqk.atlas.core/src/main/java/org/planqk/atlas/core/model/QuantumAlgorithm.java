package org.planqk.atlas.core.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class QuantumAlgorithm extends Algorithm {

	private boolean nisqReady;
	
	private QuantumComputationModel quantumComputationModel;
	
	@OneToMany(mappedBy = "algorithm", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Setter
	private Set<QuantumResource> requiredQuantumResources;
	
	private String speedUp;
	
	// TODO: Add implementations
	
}