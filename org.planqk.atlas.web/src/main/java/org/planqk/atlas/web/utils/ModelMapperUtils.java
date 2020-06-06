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

    private static ModelMapper mapper = new ModelMapper();

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

    @SuppressWarnings("unchecked")
	public static <D, T> D convert(final T entity, Class<D> outClass) {
    	// TODO: Check if there is a better way to do this
    	if (outClass == AlgorithmDto.class) {
    		return (D) convertAlgorithm((Algorithm) entity);
    	} 
    	
    	if (outClass == Algorithm.class) {
    		return (D) convertAlgorithmDto((AlgorithmDto) entity);
    	}
    	
        return mapper.map(entity, outClass);
    }
    
    private static AlgorithmDto convertAlgorithm(Algorithm alg) {
    	if (alg instanceof QuantumAlgorithm) {
    		return mapper.map(alg, QuantumAlgorithmDto.class);
    	} 
    	return mapper.map(alg, ClassicAlgorithmDto.class);
    }
    
    private static Algorithm convertAlgorithmDto(AlgorithmDto dto) {
    	if (dto instanceof QuantumAlgorithmDto) {
    		return mapper.map(dto, QuantumAlgorithm.class);
    	} 
    	return mapper.map(dto, ClassicAlgorithm.class);
    }
}
