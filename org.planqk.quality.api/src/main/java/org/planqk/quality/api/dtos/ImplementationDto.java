/*
 *  /*******************************************************************************
 *  * Copyright (c) 2020 University of Stuttgart
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License. You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License
 *  * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  * or implied. See the License for the specific language governing permissions and limitations under
 *  * the License.
 *  ******************************************************************************
 */

package org.planqk.quality.api.dtos;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.planqk.quality.model.Implementation;
import org.springframework.hateoas.RepresentationModel;

/**
 * Data transfer object for the model class Implementation ({@link org.planqk.quality.model.Implementation}).
 */
public class ImplementationDto extends RepresentationModel<ImplementationDto> {

    private Long id;

    private String name;

    private String programmingLanguage;

    private String selectionRule;

    private String requiredQubits;

    private List<ParameterDto> inputParameters;

    private List<ParameterDto> outputParameters;

    public ImplementationDto(){}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setSelectionRule(String selectionRule) {
        this.selectionRule = selectionRule;
    }

    public String getSelectionRule() {
        return selectionRule;
    }

    public String getRequiredQubits() {
        return requiredQubits;
    }

    public void setRequiredQubits(String requiredQubits) {
        this.requiredQubits = requiredQubits;
    }

    public List<ParameterDto> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(List<ParameterDto> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public List<ParameterDto> getOutputParameters() {
        return outputParameters;
    }

    public void setOutputParameters(List<ParameterDto> outputParameters) {
        this.outputParameters = outputParameters;
    }

    @Override
    public String toString() {
        return "ImplementationDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", programmingLanguage='" + programmingLanguage + '\'' +
                ", selectionRule='" + selectionRule + '\'' +
                ", requiredQubits='" + requiredQubits + '\'' +
                ", inputParameters=" + inputParameters +
                ", outputParameters=" + outputParameters +
                '}';
    }

    public static final class Converter {

        public static ImplementationDto convert(final Implementation object) {
            final ImplementationDto dto = new ImplementationDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setProgrammingLanguage(object.getProgrammingLanguage());
            dto.setSelectionRule(object.getSelectionRule());
            dto.setRequiredQubits(object.getRequiredQubits());
            dto.setInputParameters(object.getInputParameters().stream().map(ParameterDto.Converter::convert)
                    .collect(Collectors.toList()));
            dto.setOutputParameters(object.getOutputParameters().stream().map(ParameterDto.Converter::convert)
                    .collect(Collectors.toList()));
            return dto;
        }
    }
}
