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

package org.planqk.quality.control;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.planqk.quality.execution.IExecutor;
import org.planqk.quality.model.Implementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Control service that handles all internal control flow and invokes the required functionality on behalf of the API.
 */
@Service
public class QualityControlService {

    final private static Logger LOG = LoggerFactory.getLogger(QualityControlService.class);

    final private List<IExecutor> executorList;

    public QualityControlService(List<IExecutor> executorList) {
        this.executorList = executorList;
    }

    /**
     * Execute the given quantum algorithm implementation with the given input parameters and return the corresponding
     * output of the execution.
     *
     * @param implementation  the quantum algorithm implementation that shall be executed
     * @param inputParameters the input parameters for the execution as key/value pairs
     * @return the output parameters of the execution as key/value pairs
     * @throws RuntimeException is thrown in case the execution of the algorithm implementation fails
     */
    public Map<String, String> executeQuantumAlgorithm(Implementation implementation, Map<String, String> inputParameters) throws RuntimeException {
        LOG.debug("Executing quantum algorithm implementation with Id: {} and name: {}", implementation.getId(), implementation.getName());

        // get suited executor plugin
        IExecutor selectedExecutor = executorList.stream()
                .filter(executor -> executor.supportedProgrammingLanguages().contains(implementation.getProgrammingLanguage()))
                .filter(executor -> executor.supportedSdks().contains(implementation.getSdk().getName()))
                .findFirst().orElse(null);
        if (Objects.isNull(selectedExecutor)) {
            LOG.error("Unable to find executor plugin for programming language {} and sdk name {}.",
                    implementation.getProgrammingLanguage(), implementation.getSdk().getName());
            throw new RuntimeException("Unable to find executor plugin for programming language "
                    + implementation.getProgrammingLanguage() + " and sdk name " + implementation.getSdk().getName());
        }

        // TODO: retrieve file of the implementation

        // execute implementation
        return selectedExecutor.executeQuantumAlgorithm(null, inputParameters);
    }

    // TODO: add selection functionality
}
