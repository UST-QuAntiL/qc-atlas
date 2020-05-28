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
import org.planqk.atlas.core.model.Provider;
import org.planqk.atlas.core.model.Qpu;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

/**
 * Data transfer object for the model class {@link Qpu}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class QpuDto extends RepresentationModel<ProviderDto> {

    private UUID id;

    private String name;

    private int numberOfQubits;

    private float t1;

    private float maxGateTime;

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

        public static QpuDto convert(final Qpu object) {
            QpuDto dto = new QpuDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setNumberOfQubits(object.getQubitCount());
            dto.setT1(object.getT1());
            dto.setMaxGateTime(object.getMaxGateTime());
            return dto;
        }

        public static Qpu convert(final QpuDto object, final Provider provider) {
            Qpu qpu = new Qpu();
            qpu.setName(object.getName());
            qpu.setQubitCount(object.getNumberOfQubits());
            qpu.setT1(object.getT1());
            qpu.setMaxGateTime(object.getMaxGateTime());
            qpu.setProvider(provider);
            return qpu;
        }
    }
}
