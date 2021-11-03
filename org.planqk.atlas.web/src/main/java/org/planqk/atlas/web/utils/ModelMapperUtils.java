/*******************************************************************************
 * Copyright (c) 2020-2021 the qc-atlas contributors.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.web.utils;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.module.jsr310.Jsr310Module;
import org.modelmapper.module.jsr310.Jsr310ModuleConfig;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ClassicImplementation;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.FileImplementationPackage;
import org.planqk.atlas.core.model.FunctionImplementationPackage;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationPackage;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.Qpu;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.QuantumImplementation;
import org.planqk.atlas.core.model.Simulator;
import org.planqk.atlas.core.model.TOSCAImplementationPackage;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.ClassicAlgorithmDto;
import org.planqk.atlas.web.dtos.ClassicImplementationDto;
import org.planqk.atlas.web.dtos.ComputeResourceDto;
import org.planqk.atlas.web.dtos.FileImplementationPackageDto;
import org.planqk.atlas.web.dtos.FunctionImplementationPackageDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.ImplementationPackageDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.QPUDto;
import org.planqk.atlas.web.dtos.QuantumAlgorithmDto;
import org.planqk.atlas.web.dtos.QuantumImplementationDto;
import org.planqk.atlas.web.dtos.SimulatorDto;
import org.planqk.atlas.web.dtos.TOSCAImplementationPackageDto;
import org.springframework.data.domain.Page;

import lombok.NonNull;

public final class ModelMapperUtils {

    public static final ModelMapper mapper = initModelMapper();

    private static AlgorithmService algorithmService;

    private ModelMapperUtils() {
    }

    public static <D, T> Page<D> convertPage(@NonNull Page<T> entities, @NonNull Class<D> dtoClass) {
        return entities.map(objectEntity -> convert(objectEntity, dtoClass));
    }

    public static <D, T> Collection<D> convertCollection(@NonNull Collection<T> entities, @NonNull Class<D> dtoClass) {
        return entities.stream().map(objectEntity -> convert(objectEntity, dtoClass)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static <D, T> D convert(final T entity, Class<D> outClass) {
        return mapper.map(entity, outClass);
    }

    private static ModelMapper initModelMapper() {
        final ModelMapper mapper = new ModelMapper();
        initializeConverters(mapper);
        initializeUUIDMappings(mapper);
        final Jsr310ModuleConfig config = Jsr310ModuleConfig.builder()
                .dateTimePattern("yyyy-MM-dd HH:mm:ss")
                .datePattern("yyyy-MM-dd HH:mm:ss")
                .zoneId(ZoneOffset.UTC)
                .build();
        mapper.registerModule(new Jsr310Module(config));

        return mapper;
    }

    private static void initializeConverters(ModelMapper mapper) {
        mapper.createTypeMap(ClassicAlgorithm.class, AlgorithmDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), ClassicAlgorithmDto.class));
        mapper.createTypeMap(QuantumAlgorithm.class, AlgorithmDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), QuantumAlgorithmDto.class));
        mapper.createTypeMap(ClassicAlgorithmDto.class, Algorithm.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), ClassicAlgorithm.class));
        mapper.createTypeMap(QuantumAlgorithmDto.class, Algorithm.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), QuantumAlgorithm.class));
        mapper.createTypeMap(FileImplementationPackage.class, ImplementationPackageDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), FileImplementationPackageDto.class));
        mapper.createTypeMap(TOSCAImplementationPackage.class, ImplementationPackageDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), TOSCAImplementationPackageDto.class));
        mapper.createTypeMap(FunctionImplementationPackage.class, ImplementationPackageDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), FunctionImplementationPackageDto.class));
        mapper.createTypeMap(FileImplementationPackageDto.class, ImplementationPackage.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), FileImplementationPackage.class));
        mapper.createTypeMap(TOSCAImplementationPackageDto.class, ImplementationPackage.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), TOSCAImplementationPackage.class));
        mapper.createTypeMap(FunctionImplementationPackageDto.class, ImplementationPackage.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), FunctionImplementationPackage.class));
        mapper.createTypeMap(Qpu.class, ComputeResourceDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), QPUDto.class));
        mapper.createTypeMap(Simulator.class, ComputeResourceDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), SimulatorDto.class));
        mapper.createTypeMap(QPUDto.class, ComputeResource.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), Qpu.class));
        mapper.createTypeMap(SimulatorDto.class, ComputeResource.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), Simulator.class));
    }

    private static void initializeUUIDMappings(ModelMapper mapper) {
        // Algorithm Relations
        mapper.createTypeMap(AlgorithmRelation.class, AlgorithmRelationDto.class)
                .addMapping(e -> e.getSourceAlgorithm().getId(), AlgorithmRelationDto::setSourceAlgorithmId)
                .addMapping(e -> e.getTargetAlgorithm().getId(), AlgorithmRelationDto::setTargetAlgorithmId);
        mapper.createTypeMap(AlgorithmRelationDto.class, AlgorithmRelation.class)
                .addMapping(e -> newAlgorithmWithId(e.getSourceAlgorithmId()), AlgorithmRelation::setSourceAlgorithm)
                .addMapping(e -> newAlgorithmWithId(e.getTargetAlgorithmId()), AlgorithmRelation::setTargetAlgorithm);

        //Classic Implementations
        mapper.createTypeMap(ClassicImplementation.class, ClassicImplementationDto.class)
                .addMapping(e -> e.getImplementedAlgorithm().getId(), ImplementationDto::setImplementedAlgorithmId);
        mapper.createTypeMap(ClassicImplementationDto.class, ClassicImplementation.class)
                .addMapping(e -> newAlgorithmWithId(e.getImplementedAlgorithmId()), Implementation::setImplementedAlgorithm);

        // Quantum Implementations
        mapper.createTypeMap(QuantumImplementation.class, QuantumImplementationDto.class)
                .addMapping(e -> e.getImplementedAlgorithm().getId(), ImplementationDto::setImplementedAlgorithmId);
        mapper.createTypeMap(QuantumImplementationDto.class, QuantumImplementation.class)
                .addMapping(e -> newAlgorithmWithId(e.getImplementedAlgorithmId()), Implementation::setImplementedAlgorithm);

        // Pattern Relations
        mapper.createTypeMap(PatternRelation.class, PatternRelationDto.class)
                .addMapping(e -> e.getAlgorithm().getId(), PatternRelationDto::setAlgorithmId);
        mapper.createTypeMap(PatternRelationDto.class, PatternRelation.class)
                .addMapping(e -> newAlgorithmWithId(e.getAlgorithmId()), PatternRelation::setAlgorithm);
    }

    private static Algorithm newAlgorithmWithId(UUID id) {
        //algorithmService is null here.
        final Algorithm actualAlgo = algorithmService.findById(id);
        if (actualAlgo.getComputationModel().equals(ComputationModel.CLASSIC)) {
            final var algo = new ClassicAlgorithm();
            algo.setId(id);
            return algo;
        }
        else {
            final var algo = new QuantumAlgorithm();
            algo.setId(id);
            return algo;
        }
    }
}
