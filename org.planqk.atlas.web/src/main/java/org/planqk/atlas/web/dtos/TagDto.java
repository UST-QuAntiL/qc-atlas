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
import java.util.HashMap;
import java.util.Map;
import org.planqk.atlas.core.model.Tag;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class TagDto extends RepresentationModel<TagDto> {

    private String key;

    private String value;

    private UUID id;

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

        public static TagDto convert(final Tag object) {
            final TagDto dto = new TagDto();
            dto.setId(object.getId());
            dto.setKey(object.getKey());
            dto.setValue(object.getValue());
            return dto;
        }

        public static Tag convert(final TagDto object) {
            final Tag tag = new Tag();
            tag.setId(object.getId());
            tag.setKey(object.getKey());
            tag.setValue(object.getValue());
            return tag;
        }
    }
}
