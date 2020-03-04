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

package org.planqk.quality.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.springframework.lang.NonNull;

/**
 * Entity representing a quantum processing unit (Qpu).
 */
@Entity
public class Qpu extends HasId {

    private String name;

    private int qubitCount;

    private float t1;

    private float maxGateTime;

    @ManyToMany
    private List<Sdk> supportedSdks;

    @ManyToOne
    private Provider provider;

    public Qpu() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQubitCount() {
        return qubitCount;
    }

    public void setQubitCount(int qubitCount) {
        this.qubitCount = qubitCount;
    }

    public float getT1() {
        return t1;
    }

    public void setT1(float t1) {
        this.t1 = t1;
    }

    public float getMaxGateTime() {
        return maxGateTime;
    }

    public void setMaxGateTime(float maxGateTime) {
        this.maxGateTime = maxGateTime;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @NonNull
    public List<Sdk> getSupportedSdks() {
        if (Objects.isNull(supportedSdks)) {
            return new ArrayList<>();
        }
        return supportedSdks;
    }

    public void setSupportedSdks(List<Sdk> supportedSdks) {
        this.supportedSdks = supportedSdks;
    }
}
