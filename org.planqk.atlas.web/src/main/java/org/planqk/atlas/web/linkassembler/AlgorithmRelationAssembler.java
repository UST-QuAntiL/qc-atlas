package org.planqk.atlas.web.linkassembler;

import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class AlgorithmRelationAssembler implements SimpleRepresentationModelAssembler<AlgorithmRelationDto> {

	private static final Logger LOG = LoggerFactory.getLogger(AlgorithmAssembler.class);

	@Override
	public void addLinks(EntityModel<AlgorithmRelationDto> resource) {
		// TODO Auto-generated method stub
        LOG.info("Implementation required!");
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<AlgorithmRelationDto>> resources) {
		// TODO Auto-generated method stub

	}

}
