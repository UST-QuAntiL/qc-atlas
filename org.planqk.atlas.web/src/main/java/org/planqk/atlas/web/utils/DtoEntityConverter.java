package org.planqk.atlas.web.utils;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmListDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ClassicAlgorithmDto;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.dtos.QuantumAlgorithmDto;
import org.planqk.atlas.web.dtos.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DtoEntityConverter {

	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private DtoLinkEnhancer linkEnhancer;

	public Algorithm convert(AlgorithmDto dto) {
		if (dto instanceof QuantumAlgorithmDto) {
			QuantumAlgorithmDto qDto = (QuantumAlgorithmDto) dto;
			return modelMapper.map(qDto, QuantumAlgorithm.class);
		} else if (dto instanceof ClassicAlgorithmDto) {
			ClassicAlgorithmDto cDto = (ClassicAlgorithmDto) dto;
			return modelMapper.map(cDto, ClassicAlgorithm.class);
		} else {
			return modelMapper.map(dto, Algorithm.class);
		}
	}

	public AlgorithmDto convert(Algorithm entity) {
		AlgorithmDto dto;
		if (entity instanceof QuantumAlgorithm) {
			dto = modelMapper.map((QuantumAlgorithm) entity, QuantumAlgorithmDto.class);
		} else if (entity instanceof ClassicAlgorithm) {
			dto = modelMapper.map((ClassicAlgorithm) entity, ClassicAlgorithmDto.class);
		} else {
			dto = modelMapper.map(entity, AlgorithmDto.class);
		}
		linkEnhancer.addLinks(dto);
		return dto;
	}

	public AlgorithmListDto convert(Page<Algorithm> entities) {
		return convert(new HashSet<Algorithm>(entities.getContent()));
	}

	public AlgorithmListDto convert(Set<Algorithm> entities) {
		AlgorithmListDto dtoList = new AlgorithmListDto();

		for (Algorithm entity : entities) {
			dtoList.add(convert(entity));
			linkEnhancer.addLinks(dtoList, entity);
		}

		return dtoList;
	}

	public Tag convert(TagDto dto) {
		return modelMapper.map(dto, Tag.class);
	}

	public TagDto convert(Tag entity) {
		TagDto dto;
		dto = modelMapper.map(entity, TagDto.class);
		linkEnhancer.addLinks(dto);
		return dto;
	}
	
	public AlgorithmRelation convert(AlgorithmRelationDto dto) {
		return modelMapper.map(dto, AlgorithmRelation.class);
	}
	
	public AlgorithmRelationDto convert(AlgorithmRelation entity) {
		return modelMapper.map(entity, AlgorithmRelationDto.class);
	}
	
	public AlgoRelationType convert(AlgoRelationTypeDto dto) {
		return modelMapper.map(dto, AlgoRelationType.class);
	}
	
	public AlgoRelationTypeDto convert(AlgoRelationType entity) {
		AlgoRelationTypeDto dto = modelMapper.map(entity, AlgoRelationTypeDto.class);
		linkEnhancer.addLinks(dto);
		return dto;
	}
	
	public ProblemType convert(ProblemTypeDto dto) {
		return modelMapper.map(dto, ProblemType.class);
	}
	
	public ProblemTypeDto convert(ProblemType entity) {
		return modelMapper.map(entity, ProblemTypeDto.class);
	}
}
