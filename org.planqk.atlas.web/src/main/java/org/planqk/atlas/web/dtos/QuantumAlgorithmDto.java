package org.planqk.atlas.web.dtos;

import javax.validation.constraints.NotNull;

import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.QuantumComputationModel;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeName("QUANTUM")
@Relation(itemRelation = "algorithm", collectionRelation = "algorithms")
public class QuantumAlgorithmDto extends AlgorithmDto {

    private boolean nisqReady;

    @NotNull(message = "QuantumComputationModel must not be null!")
    private QuantumComputationModel quantumComputationModel;

    private String speedUp;

    @Override
    @Schema(type = "string", allowableValues = {"QUANTUM"})
    public ComputationModel getComputationModel() {
        return super.getComputationModel();
    }
}
