/*******************************************************************************
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

package org.planqk.atlas.nisq.analyzer.execution.python;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.planqk.atlas.core.model.ProgrammingLanguage;
import org.planqk.atlas.nisq.analyzer.execution.IExecutor;

import org.springframework.stereotype.Service;

/**
 * Executor to execute implementations in the python programming language.
 */
@Service
public class PythonExecutor implements IExecutor {

    private static final List<ProgrammingLanguage> supportedProgrammingLanguages = new ArrayList<ProgrammingLanguage>() {{
        add(ProgrammingLanguage.Python);
    }};
    private static final List<String> supportedSdks = new ArrayList<String>() {{
        add("Qiskit");
        add("Forest");
    }};

    @Override
    public Map<String, String> executeQuantumAlgorithmImplementation(File algorithmImplementation, Map<String, String> parameters) throws RuntimeException {

        // TODO: delete
        try (BufferedReader br = new BufferedReader(new FileReader(algorithmImplementation))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
