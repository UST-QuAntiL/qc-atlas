package org.planqk.atlas.web.dtos;

import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;

public class ProblemTypeListDto extends RepresentationModel<ProblemTypeListDto> {
	
	@Getter
    private final List<ProblemTypeDto> problemTypeDtos = Lists.newArrayList();
	
	public void add(final List<ProblemTypeDto> problemTypes) {
        this.problemTypeDtos.addAll(problemTypes);
    }

    public void add(final ProblemTypeDto problemType) {
        this.problemTypeDtos.add(problemType);
    }
}
