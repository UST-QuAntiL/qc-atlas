package org.planqk.atlas.web.dtos;

import java.util.UUID;

import javax.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlgoRelationTypeDto {
	
	private UUID id;
	
	@NotNull(message = "RelationType-Name must not be null!")
	private String name;
	
}
