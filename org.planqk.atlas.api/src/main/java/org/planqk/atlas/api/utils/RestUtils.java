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

package org.planqk.atlas.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.planqk.atlas.api.controller.AlgorithmController;
import org.planqk.atlas.api.dtos.entities.ParameterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for the REST API functionality
 */
public class RestUtils {

    final private static Logger LOG = LoggerFactory.getLogger(AlgorithmController.class);

    /**
     * Check if the given lists of input and output parameters are consistent and contain the required attributes to
     * store them in the repository
     *
     * @param inputParameters  the list of input parameters
     * @param outputParameters the list of output parameters
     * @return <code>true</code> if all parameters are consistens, <code>false</code> otherwise
     */
    public static boolean parameterConsistent(List<ParameterDto> inputParameters, List<ParameterDto> outputParameters) {
        // avoid changing the potential live lists that are passed
        List<ParameterDto> parameters = new ArrayList<>();
        parameters.addAll(inputParameters);
        parameters.addAll(outputParameters);

        for (ParameterDto param : parameters) {
            if (Objects.isNull(param.getName()) || Objects.isNull(param.getType())) {
                LOG.error("Invalid parameter: {}", param.toString());
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all required parameters are contained in the provided parameters
     *
     * @param requiredParameters the set of required parameters
     * @param providedParameters the map with the provided parameters
     * @return <code>true</code> if all required parameters are contained in the provided parameters, <code>false</code>
     * otherwise
     */
    public static boolean parametersAvailable(Set<String> requiredParameters, Map<String, String> providedParameters) {
        return requiredParameters.stream().filter(param -> !providedParameters.containsKey(param)).count() == 0;
    }
}
