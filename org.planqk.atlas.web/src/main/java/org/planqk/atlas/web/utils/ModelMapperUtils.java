package org.planqk.atlas.web.utils;

import java.util.HashSet;
import java.util.Set;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ClassicAlgorithmDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.QuantumAlgorithmDto;

import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;

public class ModelMapperUtils {

    private static final ModelMapper mapper = initModelMapper();

    public static <D, T> Set<D> convertSet(@NonNull Set<T> entities, @NonNull Class<D> dtoClass) {
        Set<D> resultSet = new HashSet<D>();
        for (T entity : entities) {
            resultSet.add(convert(entity, dtoClass));
        }
        return resultSet;
    }

    public static <D, T> Page<D> convertPage(@NonNull Page<T> entities, @NonNull Class<D> dtoClass) {
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
        
        // Map Algorithm of PatternRelation to correct Subclass when mapping to PatternRelationDto
        TypeMap<PatternRelation, PatternRelationDto> typeMap = mapper.createTypeMap(PatternRelation.class, PatternRelationDto.class);
        typeMap.addMappings(mappingContext -> mappingContext.map(src -> src.getAlgorithm(), PatternRelationDto::setAlgorithm));

        return mapper;
    }
}
