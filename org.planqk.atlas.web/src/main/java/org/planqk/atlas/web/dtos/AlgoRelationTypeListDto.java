package org.planqk.atlas.web.dtos;

import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;

public class AlgoRelationTypeListDto extends RepresentationModel<AlgoRelationTypeListDto> {
	
	@Getter
    private final List<AlgoRelationTypeDto> algoRelationTypeDtos = Lists.newArrayList();
	
	public void add(final List<AlgoRelationTypeDto> algoRelationTypes) {
        this.algoRelationTypeDtos.addAll(algoRelationTypes);
    }

    public void add(final AlgoRelationTypeDto algoRelationTypes) {
        this.algoRelationTypeDtos.add(algoRelationTypes);
    }
}
