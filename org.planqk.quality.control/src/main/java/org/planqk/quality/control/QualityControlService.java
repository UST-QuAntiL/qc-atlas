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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.planqk.quality.model.DataType;
import org.planqk.quality.model.Implementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Control service that handles all internal control flow and invokes the required functionality on behalf of the API.
 */
public class QualityControlService {

    final private static Logger LOG = LoggerFactory.getLogger(QualityControlService.class);

    /**
     * Execute the given quantum algorithm implementation with the given input parameters and return the corresponding
     * output of the execution.
     *
     * @param implementation  the quantum algorithm implementation that shall be executed
     * @param inputParameters the input parameters for the execution as key/value pairs
     * @return the output parameters of the execution as key/value pairs
     * @throws RuntimeException is thrown in case the execution of the algorithm implementation fails
     */
    public Map<DataType,String> executeQuantumAlgorithm(Implementation implementation, Map<DataType,String> inputParameters) throws RuntimeException {
        LOG.debug("Executing quantum algorithm implementation with Id: {} and name: {}", implementation.getId(), implementation.getName());

        // TODO: retrieve file of the implementation
        // TODO: select plug-in for the execution and pass file and input parameters

        Map<DataType,String> outputParameters = new HashMap<>();
        // TODO
        return outputParameters;
    }

    // TODO: add selection functionality
}
