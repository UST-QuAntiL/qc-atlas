package org.planqk.atlas.web.dtos;

import org.planqk.atlas.core.model.ProblemType;
import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProblemTypeDto extends RepresentationModel<ProblemTypeDto> {
	private Long id;
	private String name;
	private Long parentProblemType;
	
	public static final class Converter {

        public static ProblemTypeDto convert(final ProblemType object) {
            final ProblemTypeDto dto = new ProblemTypeDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setParentProblemType(object.getParentProblemType());
            return dto;
        }

        public static ProblemType convert(final ProblemTypeDto dto) {
            final ProblemType problemType = new ProblemType();
            problemType.setId(dto.getId());
            problemType.setName(dto.getName());
            problemType.setParentProblemType(dto.getParentProblemType());
            return problemType;
        }
    }
}
