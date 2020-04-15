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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.planqk.atlas.core.model.ExecutionResult;
import org.planqk.atlas.core.model.ProgrammingLanguage;
import org.planqk.atlas.core.model.Qpu;
import org.planqk.atlas.nisq.analyzer.execution.IExecutor;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Executor to execute implementations in the python programming language.
 */
@Service
public class PythonExecutor implements IExecutor {

    final private static Logger LOG = LoggerFactory.getLogger(PythonExecutor.class);

    private static final List<ProgrammingLanguage> supportedProgrammingLanguages = new ArrayList<ProgrammingLanguage>() {{
        add(ProgrammingLanguage.Python);
    }};
    private static final List<String> supportedSdks = new ArrayList<String>() {{
        add("Qiskit");
        add("Forest");
    }};

    @Override
    public void executeQuantumAlgorithmImplementation(URL algorithmImplementationURL, Qpu qpu, Map<String, String> parameters, ExecutionResult executionResult) {
        LOG.debug("Executing quantum algorithm implementation with Python executor plugin!");

        // copy the implementation file from the URL
        File file;
        try {
            file = File.createTempFile("temp", null);
            file.deleteOnExit();
            FileUtils.copyURLToFile(algorithmImplementationURL, file);
        } catch (IOException e) {
            LOG.error("Unable to execute quantum algorithm implementation. Exception while downloading implementation file: {}", e.getMessage());
            // TODO: set status to failed and add status code
            return;
        }

        // TODO: delete
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        LOG.debug("Deletion of the temporary file returned: {} ", file.delete());

        // TODO: execute passed script, change status to running, wait for results/errors and change status
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
