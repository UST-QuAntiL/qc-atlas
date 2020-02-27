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

package org.planqk.quality.execution;

import java.io.File;
import java.util.List;

import org.planqk.quality.model.Sdk;

/**
 * Interface for all plug-ins that can execute quantum algorithms defined with a certain programming language and using
 * certain SDKs.
 */
public interface IExecutor {

    /**
     * Execute the given quantum algorithm implementation with the given input parameters.
     *
     * @param algorithmImplementation the file containing the quantum algorithm implementation that should be executed
     * @param parameters the input parameters for the quantum algorithm execution
     * @return the list of output parameters for the execution
     */
    List<String> executeQuantumAlgorithm(File algorithmImplementation, List<String> parameters);

    /**
     * Returns a list of Sdk that are supported by the executor
     *
     * @return the list of supported SDK
     */
    List<Sdk> supportedSdks();

    /**
     * Returns a list of programming languages that are supported by the executor
     *
     * @return the list of supported programming languages
     */
    List<String> supportedProgrammingLanguages();
}
