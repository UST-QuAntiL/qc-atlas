package org.planqk.atlas.web.dtos;

import java.util.UUID;

import javax.validation.constraints.*;

import org.planqk.atlas.core.model.AlgoRelationType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class AlgoRelationTypeDto {
	
	private UUID id;
	
	@NotNull(message = "RelationType-Name must not be null!")
	private String name;
	
//	public static final class Converter {
//
//        public static AlgoRelationTypeDto convert(final AlgoRelationType object) {
//            final AlgoRelationTypeDto dto = new AlgoRelationTypeDto();
//            dto.setId(object.getId());
//            dto.setName(object.getName());
//            return dto;
//        }
//
//        public static AlgoRelationType convert(final AlgoRelationTypeDto dto) {
//            final AlgoRelationType algoRelationType = new AlgoRelationType();
//            algoRelationType.setId(dto.getId());
//            algoRelationType.setName(dto.getName());
//            return algoRelationType;
//        }
//    }
}
