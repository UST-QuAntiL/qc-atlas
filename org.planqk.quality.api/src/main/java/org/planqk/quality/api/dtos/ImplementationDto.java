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
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.planqk.quality.model.Algorithm;
import org.planqk.quality.model.Implementation;
import org.planqk.quality.model.Sdk;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

/**
 * Data transfer object for the model class Implementation ({@link org.planqk.quality.model.Implementation}).
 */
public class ImplementationDto extends RepresentationModel<ImplementationDto> {

    private Long id;

    private String name;

    private String programmingLanguage;

    private String selectionRule;

    private String requiredQubits;

    private String sdk;

    private ParameterListDto inputParameters;

    private ParameterListDto outputParameters;

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

    public String getSdk() {
        return sdk;
    }

    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    @NonNull
    public ParameterListDto getInputParameters() {
        if(Objects.isNull(inputParameters)){
            return new ParameterListDto();
        }
        return inputParameters;
    }

    public void setInputParameters(ParameterListDto inputParameters) {
        this.inputParameters = inputParameters;
    }

    @NonNull
    public ParameterListDto getOutputParameters() {
        if(Objects.isNull(outputParameters)){
            return new ParameterListDto();
        }
        return outputParameters;
    }

    public void setOutputParameters(ParameterListDto outputParameters) {
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
                ", sdk='" + sdk + '\'' +
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
            dto.setSdk(object.getSdk().getName());

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

        public static Implementation convert(final ImplementationDto object, final Sdk sdk, final Algorithm algo) {
            Implementation implementation = new Implementation();
            implementation.setName(object.getName());
            implementation.setRequiredQubits(object.getRequiredQubits());
            implementation.setProgrammingLanguage(object.getProgrammingLanguage());
            implementation.setSelectionRule(object.getSelectionRule());
            implementation.setSdk(sdk);
            implementation.setImplementedAlgorithm(algo);
            return implementation;
        }
    }
}
