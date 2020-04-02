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

package org.planqk.atlas.nisq.analyzer.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Class to evaluate mathematical formulas.
 */
@Service
public class FormulaEvaluator {

    /**
     * Evaluate the given formula and return the result.
     *
     * @param formula the mathematical formula
     * @param params  the parameters that are needed for the evaluation
     * @return the result of the calculation
     */
    public float evaluateFormula(String formula, Map<String, String> params) {
        // TODO
        return 0;
    }

    /**
     * Get all required parameters for the given formula
     *
     * @param formula the mathematical formula
     * @return the list with all required parameters
     */
    public List<String> getRequiredParameters(String formula) {
        // TODO
        return new ArrayList<>();
    }
}
