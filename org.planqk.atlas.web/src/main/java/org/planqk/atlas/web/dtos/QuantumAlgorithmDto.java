package org.planqk.atlas.web.dtos;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.planqk.atlas.core.model.QuantumComputationModel;

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

    @NotNull(message = "QuantumComputationModel must not be null!")
    private QuantumComputationModel quantumComputationModel;

    private Set<QuantumResourceDto> requiredQuantumResources = new HashSet<>();
    private String speedUp;

}
