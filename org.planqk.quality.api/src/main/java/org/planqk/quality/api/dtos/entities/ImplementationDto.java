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

package org.planqk.quality.api.dtos.entities;

import java.net.URL;
import java.util.Objects;
import java.util.stream.Collectors;

import org.planqk.quality.model.Algorithm;
import org.planqk.quality.model.Implementation;
import org.planqk.quality.model.ProgrammingLanguage;
import org.planqk.quality.model.Sdk;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

/**
 * Data transfer object for the model class Implementation ({@link org.planqk.quality.model.Implementation}).
 */
public class ImplementationDto extends RepresentationModel<ImplementationDto> {

    private Long id;

    private String name;

    private ProgrammingLanguage programmingLanguage;

    private String selectionRule;

    private String depthFormula;

    private String widthFormula;

    private String sdk;

    private URL fileLocation;

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

    public void setProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public ProgrammingLanguage getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setSelectionRule(String selectionRule) {
        this.selectionRule = selectionRule;
    }

    public String getSelectionRule() {
        return selectionRule;
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

    public String getSdk() {
        return sdk;
    }

    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    public URL getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(URL fileLocation) {
        this.fileLocation = fileLocation;
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
                ", depthFormula='" + depthFormula + '\'' +
                ", widthFormula='" + widthFormula + '\'' +
                ", sdk='" + sdk + '\'' +
                ", fileLocation='" + fileLocation + '\'' +
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
            dto.setDepthFormula(object.getDepthFormula());
            dto.setWidthFormula(object.getWidthFormula());
            dto.setFileLocation(object.getFileLocation());
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
            implementation.setDepthFormula(object.getDepthFormula());
            implementation.setWidthFormula(object.getWidthFormula());
            implementation.setProgrammingLanguage(object.getProgrammingLanguage());
            implementation.setSelectionRule(object.getSelectionRule());
            implementation.setFileLocation(object.getFileLocation());
            implementation.setSdk(sdk);
            implementation.setImplementedAlgorithm(algo);
            return implementation;
        }
    }
}
