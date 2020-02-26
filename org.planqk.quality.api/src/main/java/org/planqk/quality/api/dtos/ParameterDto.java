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

import org.planqk.quality.model.DataType;
import org.planqk.quality.model.Parameter;

/**
 * Data transfer object for {@link Parameter}.
 */
public class ParameterDto {

    String name;

    DataType type;

    String restriction;

    String description;

    public ParameterDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ParameterDto{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", restriction='" + restriction + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public static final class Converter {

        public static ParameterDto convert(final Parameter object) {
            final ParameterDto dto = new ParameterDto();
            dto.setName(object.getName());
            dto.setType(object.getType());
            dto.setRestriction(object.getRestriction());
            dto.setDescription(object.getDescription());
            return dto;
        }

        public static Parameter convert(final ParameterDto object) {
            final Parameter param = new Parameter();
            param.setName(object.getName());
            param.setType(object.getType());
            param.setRestriction(object.getRestriction());
            param.setDescription(object.getDescription());
            return param;
        }
    }
}
