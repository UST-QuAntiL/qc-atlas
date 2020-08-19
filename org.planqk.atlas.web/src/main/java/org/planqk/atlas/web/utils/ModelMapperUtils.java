package org.planqk.atlas.web.utils;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.Qpu;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.Simulator;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.ClassicAlgorithmDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.QPUDto;
import org.planqk.atlas.web.dtos.QuantumAlgorithmDto;
import org.planqk.atlas.web.dtos.SimulatorDto;

import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

public class ModelMapperUtils {

    public static final ModelMapper mapper = initModelMapper();

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
        mapper.createTypeMap(Qpu.class, ComputeResourceDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), QPUDto.class));
        mapper.createTypeMap(Simulator.class, ComputeResourceDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), SimulatorDto.class));
        mapper.createTypeMap(QPUDto.class, ComputeResource.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), Qpu.class));
        mapper.createTypeMap(SimulatorDto.class, ComputeResource.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), Simulator.class));

        // Map Algorithm of PatternRelation to correct Subclass when mapping to PatternRelationDto
        mapper.createTypeMap(PatternRelation.class, PatternRelationDto.class)
                .addMappings(expression -> expression.map(PatternRelation::getAlgorithm, PatternRelationDto::setAlgorithm));
        mapper.createTypeMap(AlgorithmRelation.class, AlgorithmRelationDto.class)
                .addMappings(expression -> expression.map(e -> e.getSourceAlgorithm().getId(), AlgorithmRelationDto::setSourceAlgorithmId))
                .addMappings(expression -> expression.map(e -> e.getTargetAlgorithm().getId(), AlgorithmRelationDto::setTargetAlgorithmId));
        mapper.createTypeMap(AlgorithmRelationDto.class, AlgorithmRelation.class)
                .addMapping(e -> {
                    var algo = new Algorithm();
                    algo.setId(e.getSourceAlgorithmId());
                    return e;
                }, AlgorithmRelation::setSourceAlgorithm)
                .addMapping(e -> {
                    var algo = new Algorithm();
                    algo.setId(e.getTargetAlgorithmId());
                    return algo;
                }, AlgorithmRelation::setTargetAlgorithm);
        return mapper;
    }
}
