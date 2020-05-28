/********************************************************************************
 * Copyright (c) 2020 University of Stuttgart
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

package org.planqk.atlas.web.dtos;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

/**
 * Data transfer object for the model class Implementation ({@link org.planqk.atlas.core.model.Implementation}).
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class ImplementationDto extends RepresentationModel<ImplementationDto> {

    private UUID id;
    private String name;
    private URL fileLocation;
    private String inputFormat;
    private String outputFormat;
    private String description;
    private String contributors;
    private String assumptions;
    private String parameter;
    private String dependencies;

    private Map<String, Object> otherData = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getOtherJsonData() {
        return otherData;
    }

    @JsonAnySetter
    public void setOtherJsonData(String key, Object value) {
        otherData.put(key, value);
    }

    public static final class Converter {

        public static ImplementationDto convert(final Implementation object) {
            final ImplementationDto dto = new ImplementationDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setFileLocation(object.getFileLocation());
            dto.setInputFormat(object.getInputFormat());
            dto.setOutputFormat(object.getOutputFormat());
            dto.setDescription(object.getDescription());
            dto.setContributors(object.getContributors());
            dto.setAssumptions(object.getAssumptions());
            dto.setParameter(object.getParameter());
            dto.setDependencies(object.getDependencies());
            return dto;
        }

        public static Implementation convert(final ImplementationDto object, final Algorithm algo) {
            Implementation implementation = new Implementation();
            implementation.setName(object.getName());
            implementation.setFileLocation(object.getFileLocation());
            implementation.setImplementedAlgorithm(algo);
            implementation.setInputFormat(object.getInputFormat());
            implementation.setOutputFormat(object.getOutputFormat());
            implementation.setDependencies(object.getDescription());
            implementation.setContributors(object.getContributors());
            implementation.setAssumptions(object.getAssumptions());
            implementation.setParameter(object.getParameter());
            implementation.setDependencies(object.getDependencies());
            return implementation;
        }
    }
}
