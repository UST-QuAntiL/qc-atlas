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

package org.planqk.atlas.nisq.analyzer.knowledge.prolog;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for the handling of prolog facts and rules
 */
public class PrologUtility {

    final private static Logger LOG = LoggerFactory.getLogger(PrologUtility.class);

    /**
     * Get the set of variables that are required to evaluate the given rule
     *
     * @param rule the rule to retrieve the variables from
     * @return the set of required variables to evaluate the rule
     */
    public static Set<String> getVariablesForRule(String rule) {
        return getParametersForRule(rule).stream().filter(PrologUtility::isVariable).collect(Collectors.toSet());
    }

    /**
     * Check if the given parameter of a prolog rule is a variable
     *
     * @param parameter the parameter to check
     * @return <code>true</code> if the parameter is a variable, <code>false</code> otherwise
     */
    public static boolean isVariable(String parameter) {
            String paramName = parameter.replaceAll("\\s", "");
            LOG.debug("Checking if param {} is variable.", paramName);

            // variables have to start with an uppercase letter or an underscore
            return Character.isUpperCase(paramName.charAt(0)) || paramName.startsWith("_");
    }

    /**
     * Get the set of parameters of the given rule
     *
     * @param rule the rule to retrieve the parameters from
     * @return the set of parameters
     */
    public static Set<String> getParametersForRule(String rule) {
        LOG.debug("Getting parameters for rule: {}", rule);

        // get String part between the brackets
        String[] ruleParts = rule.split("\\(");

        HashSet<String> parameterSet = new HashSet<>();

        // rule is invalid as it does not contain brackets for the parameters
        if (ruleParts.length < 2) {
            return parameterSet;
        }

        // get the set of parameters
        String parametersPart = ruleParts[1].split("\\)")[0];
        String[] params = parametersPart.split(",");
        LOG.debug("Rule contains {} parameters.", params.length);

        return new HashSet<>(Arrays.asList(params));
    }

    /**
     * Get the number of parameters that are used for a given rule.
     *
     * @param rule the rule to get the parameter count from
     * @return the number of available parameters
     */
    public static int getNumberOfParameters(String rule) {
        // get String part between the brackets
        String[] ruleParts = rule.split("\\(");

        // rule is invalid as it does not contain brackets for the parameters
        if (ruleParts.length < 2) {
            return 0;
        }

        String parametersPart = rule.split("\\)")[0];
        return parametersPart.split(",").length;
    }

    /**
     * Retrieve the signature of the given prolog rule
     *
     * @param rule the rule to get the signature from
     * @return the signature (left part) of the rule
     */
    public static String getSignatureOfRule(String rule) {
        return rule.split(":")[0].replaceAll("\\s", "");
    }
}
