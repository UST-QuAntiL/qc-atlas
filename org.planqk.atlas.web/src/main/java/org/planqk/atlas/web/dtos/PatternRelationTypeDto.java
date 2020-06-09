package org.planqk.atlas.web.dtos;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Data
public class PatternRelationTypeDto {

    @NotNull(message = "Pattern-Relations-Type must have a name!")
    private String name;
    
}
