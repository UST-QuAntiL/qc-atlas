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

package org.planqk.quality.execution.python;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.planqk.quality.execution.IExecutor;
import org.planqk.quality.model.ProgrammingLanguage;
import org.planqk.quality.model.Sdk;
import org.springframework.stereotype.Service;

/**
 * Executor to execute implementations in the python programming language.
 */
@Service
public class PythonExecutor implements IExecutor {

    private static final List<ProgrammingLanguage> supportedProgrammingLanguages = new ArrayList<ProgrammingLanguage>() {{add(ProgrammingLanguage.Python);}};
    private static final List<String> supportedSdks = new ArrayList<String>(){{add("Qiskit"); add("Forest");}};

    @Override
    public Map<String, String> executeQuantumAlgorithm(File algorithmImplementation, Map<String, String> parameters) throws RuntimeException{
        // TODO: execute passed script and return resulting output
        return null;
    }

    @Override
    public List<String> supportedSdks() {
        return supportedSdks;
    }

    @Override
    public List<ProgrammingLanguage> supportedProgrammingLanguages() {
        return supportedProgrammingLanguages;
    }
}
