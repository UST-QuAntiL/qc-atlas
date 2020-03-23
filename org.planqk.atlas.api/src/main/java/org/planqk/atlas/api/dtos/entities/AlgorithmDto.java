/*******************************************************************************
 * Copyright (c) 2020 University of Stuttgart
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.planqk.atlas.api.dtos.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.planqk.atlas.core.model.Algorithm;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

/**
 * Data transfer object for Algorithms ({@link org.planqk.atlas.core.model.Algorithm}).
 */
public class AlgorithmDto extends RepresentationModel<AlgorithmDto> {

    private Long id;

    private String name;

    private String depthFormula;

    private String widthFormula;

    private ParameterListDto inputParameters;

    private ParameterListDto outputParameters;

    private List<TagDto> tags;

    public AlgorithmDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepthFormula() {
        return depthFormula;
    }

    public void setDepthFormula(String depthFormula) {
        this.depthFormula = depthFormula;
    }

    public String getWidthFormula() {
        return widthFormula;
    }

    public void setWidthFormula(String widthFormula) {
        this.widthFormula = widthFormula;
    }

    @NonNull
    public ParameterListDto getInputParameters() {
        if (Objects.isNull(inputParameters)) {
            return new ParameterListDto();
        }
        return inputParameters;
    }

    public void setInputParameters(ParameterListDto inputParameters) {
        this.inputParameters = inputParameters;
    }

    @NonNull
    public ParameterListDto getOutputParameters() {
        if (Objects.isNull(outputParameters)) {
            return new ParameterListDto();
        }
        return outputParameters;
    }

    public void setOutputParameters(ParameterListDto outputParameters) {
        this.outputParameters = outputParameters;
    }

    @NonNull
    public List<TagDto> getTags() {
        if (Objects.isNull(tags)) {
            return new ArrayList<TagDto>();
        }
        return tags;
    }

    public void setTags(List<TagDto> tags) {

        this.tags = tags;
    }

    @Override
    public String toString() {
        return "AlgorithmDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", depthFormula='" + depthFormula + '\'' +
                ", widthFormula='" + widthFormula + '\'' +
                ", inputParameters=" + inputParameters +
                ", outputParameters=" + outputParameters +
                '}';
    }

    public static final class Converter {

        public static AlgorithmDto convert(final Algorithm object) {
            final AlgorithmDto dto = new AlgorithmDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setDepthFormula(object.getDepthFormula());
            dto.setWidthFormula(object.getWidthFormula());

            ParameterListDto inputParams = new ParameterListDto();
            inputParams.add(object.getInputParameters().stream().map(ParameterDto.Converter::convert)
                    .collect(Collectors.toList()));
            dto.setInputParameters(inputParams);

            ParameterListDto outputParams = new ParameterListDto();
            outputParams.add(object.getOutputParameters().stream().map(ParameterDto.Converter::convert)
                    .collect(Collectors.toList()));
            dto.setOutputParameters(outputParams);

            return dto;
        }

        public static Algorithm convert(final AlgorithmDto object) {
            final Algorithm algo = new Algorithm();
            algo.setName(object.getName());
            algo.setDepthFormula(object.getDepthFormula());
            algo.setWidthFormula(object.getWidthFormula());
            algo.setInputParameters(object.getInputParameters().getParameters().stream()
                    .map(ParameterDto.Converter::convert)
                    .collect(Collectors.toList()));
            algo.setOutputParameters(object.getOutputParameters().getParameters().stream()
                    .map(ParameterDto.Converter::convert)
                    .collect(Collectors.toList()));
            return algo;
        }
    }
}
