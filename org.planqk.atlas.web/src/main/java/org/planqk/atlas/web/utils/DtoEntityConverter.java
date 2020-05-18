package org.planqk.atlas.web.utils;

import org.modelmapper.ModelMapper;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ClassicAlgorithmDto;
import org.planqk.atlas.web.dtos.QuantumAlgorithmDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DtoEntityConverter {

	@Autowired
	private ModelMapper modelMapper;
	
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
		if (entity instanceof QuantumAlgorithm) {
			QuantumAlgorithm qEntity = (QuantumAlgorithm) entity;
			return modelMapper.map(qEntity, QuantumAlgorithmDto.class);
		} else if (entity instanceof ClassicAlgorithm) {
			ClassicAlgorithm cEntity = (ClassicAlgorithm) entity;
			return modelMapper.map(cEntity, ClassicAlgorithmDto.class);
		} else {
			return modelMapper.map(entity, AlgorithmDto.class);
		}
	}
}
