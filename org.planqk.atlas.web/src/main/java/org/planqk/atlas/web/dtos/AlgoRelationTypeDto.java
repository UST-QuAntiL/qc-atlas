package org.planqk.atlas.web.dtos;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlgoRelationTypeDto extends RepresentationModel<AlgoRelationTypeDto> {
	private Long id;
	private String name;
	
	public static final class Converter {

        public static AlgoRelationTypeDto convert(final AlgoRelationType object) {
            final AlgoRelationTypeDto dto = new AlgoRelationTypeDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            return dto;
        }

        public static AlgoRelationType convert(final AlgoRelationTypeDto dto) {
            final AlgoRelationType algoRelationType = new AlgoRelationType();
            algoRelationType.setId(dto.getId());
            algoRelationType.setName(dto.getName());
            return algoRelationType;
        }
    }
}
