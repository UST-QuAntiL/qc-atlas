/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

package org.planqk.atlas.core.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ComputeResourceProperty extends HasId {

    @ManyToOne(fetch = FetchType.LAZY,
               cascade = CascadeType.MERGE)
    private ComputeResourcePropertyType computeResourcePropertyType;

    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_id")
    @EqualsAndHashCode.Exclude
    private Algorithm algorithm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implementation_id")
    @EqualsAndHashCode.Exclude
    private Implementation implementation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compute_resource_id")
    @EqualsAndHashCode.Exclude
    private ComputeResource computeResource;
}
