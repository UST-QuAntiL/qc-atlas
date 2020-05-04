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

import java.net.URL;

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

    private Long id;

    private String name;

    private Object content;

    private URL fileLocation;

    private String inputFormat;

    private String outputFormat;

    public static final class Converter {

        public static ImplementationDto convert(final Implementation object) {
            final ImplementationDto dto = new ImplementationDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setFileLocation(object.getFileLocation());
            dto.setContent(object.getContent());
            dto.setInputFormat(object.getInputFormat());
            dto.setOutputFormat(object.getOutputFormat());
            return dto;
        }

        public static Implementation convert(final ImplementationDto object, final Algorithm algo) {
            Implementation implementation = new Implementation();
            implementation.setName(object.getName());
            implementation.setFileLocation(object.getFileLocation());
            implementation.setContent(object.getContent());
            implementation.setImplementedAlgorithm(algo);
            implementation.setInputFormat(object.getInputFormat());
            implementation.setOutputFormat(object.getOutputFormat());
            return implementation;
        }
    }
}
