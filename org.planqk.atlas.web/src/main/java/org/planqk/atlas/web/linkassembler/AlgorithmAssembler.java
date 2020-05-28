package org.planqk.atlas.web.linkassembler;

import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class AlgorithmAssembler implements SimpleRepresentationModelAssembler<AlgorithmDto> {

	@Override
	public void addLinks(EntityModel<AlgorithmDto> resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<AlgorithmDto>> resources) {
		// TODO Auto-generated method stub
	}

}
