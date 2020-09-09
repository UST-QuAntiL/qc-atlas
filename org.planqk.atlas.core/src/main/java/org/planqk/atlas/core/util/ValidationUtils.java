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

package org.planqk.atlas.core.util;

import org.planqk.atlas.core.exceptions.InvalidResourceTypeValueException;
import org.planqk.atlas.core.model.ComputeResourceProperty;

public class ValidationUtils {

    public static void validateComputeResourceProperty(ComputeResourceProperty resource) {
        if (!resource.getComputeResourcePropertyType().getDatatype().isValid(resource.getValue())) {
            throw new InvalidResourceTypeValueException("The value \"" + resource.getValue() +
                    "\" is not valid for the Type " + resource.getComputeResourcePropertyType().getDatatype().name());
        }
    }
}