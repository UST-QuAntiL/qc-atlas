package org.planqk.atlas.web.utils;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ClassicAlgorithmDto;
import org.planqk.atlas.web.dtos.QuantumAlgorithmDto;
import org.springframework.data.domain.Page;

public class ModelMapperUtils {

    private static ModelMapper mapper = initModelMapper();

    public static <D, T> Set<D> convertSet(Set<T> entities, Class<D> dtoClass) {
        Set<D> resultSet = new HashSet<D>();
        for (T entity : entities) {
            resultSet.add(convert(entity, dtoClass));
        }
        return resultSet;
    }

    public static <D, T> Page<D> convertPage(Page<T> entities, Class<D> dtoClass) {
        return entities.map(objectEntity -> convert(objectEntity, dtoClass));
    }

	public static <D, T> D convert(final T entity, Class<D> outClass) {	
        return mapper.map(entity, outClass);
    }
    
    private static ModelMapper initModelMapper() {
    	ModelMapper mapper = new ModelMapper();
    	// Config ModelMapper to always use child classes of Algorithm and it's DTO
    	mapper.createTypeMap(ClassicAlgorithm.class, AlgorithmDto.class)
        .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), ClassicAlgorithmDto.class));
        mapper.createTypeMap(QuantumAlgorithm.class, AlgorithmDto.class)
        .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), QuantumAlgorithmDto.class));
        mapper.createTypeMap(ClassicAlgorithmDto.class, Algorithm.class)
        .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), ClassicAlgorithm.class));
        mapper.createTypeMap(QuantumAlgorithmDto.class, Algorithm.class)
        .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), QuantumAlgorithm.class));
    	
    	return mapper;
    }
}
