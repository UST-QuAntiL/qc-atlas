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

package org.planqk.atlas.core.model;

import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Entity representing an implementation of a certain quantum {@link Algorithm}.
 */
@Entity
public class Implementation extends Executable {

    private ProgrammingLanguage programmingLanguage;

    private String selectionRule;

    private URL fileLocation;

    @ManyToOne
    private Algorithm implementedAlgorithm;

    @ManyToOne
    private Sdk sdk;

    public Implementation() {
        super();
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

    public URL getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(URL fileLocation) {
        this.fileLocation = fileLocation;
    }

    public void setImplementedAlgorithm(Algorithm implementedAlgorithm) {
        this.implementedAlgorithm = implementedAlgorithm;
    }

    public Algorithm getImplementedAlgorithm() {
        return implementedAlgorithm;
    }

    public void setSdk(Sdk sdk) {
        this.sdk = sdk;
    }

    public Sdk getSdk() {
        return sdk;
    }
}
