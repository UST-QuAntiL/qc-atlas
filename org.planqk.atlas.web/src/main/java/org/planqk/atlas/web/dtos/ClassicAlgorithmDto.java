package org.planqk.atlas.web.dtos;

import org.planqk.atlas.core.model.ComputationModel;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeName("CLASSIC")
@Relation(itemRelation = "algorithm", collectionRelation = "algorithms")
public class ClassicAlgorithmDto extends AlgorithmDto {
    @Override
    @Schema(type = "string", allowableValues = {"CLASSIC"})
    public ComputationModel getComputationModel() {
        return super.getComputationModel();
    }
}
