package org.planqk.atlas.web.dtos;

import java.util.Set;

import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.model.QuantumResource;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeName("QUANTUM")
public class QuantumAlgorithmDto extends AlgorithmDto {

    private boolean nisqReady;
	private QuantumComputationModel quantumComputationModel;
	private Set<QuantumResource> requiredQuantumResources;
	private String speedUp;
	
}