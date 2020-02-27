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

package org.planqk.quality.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.planqk.quality.api.controller.AlgorithmController;
import org.planqk.quality.api.dtos.entities.ParameterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestUtils {

    final private static Logger LOG = LoggerFactory.getLogger(AlgorithmController.class);

    public static boolean parameterConsistent(List<ParameterDto> inputParameters, List<ParameterDto> outputParameters){
        // avoid changing the potential live lists that are passed
        List<ParameterDto> parameters = new ArrayList<>();
        parameters.addAll(inputParameters);
        parameters.addAll(outputParameters);

        for(ParameterDto param : parameters){
            if(Objects.isNull(param.getName()) || Objects.isNull(param.getType())){
                LOG.error("Invalid parameter: {}", param.toString());
                return false;
            }
        }
        return true;
    }
}
