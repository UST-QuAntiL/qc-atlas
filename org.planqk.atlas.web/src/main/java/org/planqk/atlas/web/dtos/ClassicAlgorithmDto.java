package org.planqk.atlas.web.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeName("CLASSIC")
public class ClassicAlgorithmDto extends AlgorithmDto {

}
