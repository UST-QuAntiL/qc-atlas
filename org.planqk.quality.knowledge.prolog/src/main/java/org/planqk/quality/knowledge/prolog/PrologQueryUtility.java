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

package org.planqk.quality.knowledge.prolog;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jpl7.PrologException;
import org.jpl7.Query;
import org.jpl7.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static org.planqk.quality.knowledge.prolog.PrologKnowledgeBaseHandler.activatePrologFile;
import static org.planqk.quality.knowledge.prolog.PrologKnowledgeBaseHandler.doesPrologFileExist;
import static org.planqk.quality.knowledge.prolog.PrologKnowledgeBaseHandler.persistPrologFile;

/**
 * Class to execute different kinds of required prolog queries.
 */
@Service
public class PrologQueryUtility {

    final private static Logger LOG = LoggerFactory.getLogger(PrologQueryUtility.class);

    /**
     * Execute a prolog query with variables and return all possible solutions
     *
     * @param queryContent the content of the query
     * @return an array with a map for each solution containing the values of the query variables, or <code>null</code>
     * if an error occurred
     */
    private static Map<String, Term>[] getSolutions(String queryContent) {
        LOG.debug("Executing query with the following content to retrieve solutions: {}", queryContent);
        try {
            Map<String, Term>[] solutions = Query.allSolutions(queryContent);
            LOG.debug("Number of solutions: {}", solutions.length);
            return solutions;
        } catch (PrologException e) {
            LOG.warn("Prolog error while executing query. Procedure may not exist in knowledge base...");
            return null;
        }
    }

    /**
     * Execute a prolog query and return the evaluation result as boolean
     *
     * @param queryContent the content of the query
     * @return <code>true</code> if there is a solution for the query, <code>false</code> otherwise
     */
    protected static boolean hasSolution(String queryContent) {
        LOG.debug("Checking if solution for query with the following content exists: {}", queryContent);
        try {
            return Query.hasSolution(queryContent);
        } catch (PrologException e) {
            LOG.warn("Prolog error while executing query. Procedure may not exist in knowledge base...");
            return false;
        }
    }

    /**
     * Check the prolog knowledge base for QPUs that can handle the given implementation and return them
     *
     * @param implementationId the id of the implementation for which
     * @param requiredQubits   the number of qubits that are required for the execution
     * @param circuitDepth     the depth of the circuit representation of the implementation
     * @return a list with an Id for each QPU that can execute the given implementation
     * @throws IOException is thrown if the required prolog files can not be created
     */
    public List<Long> getSuitableQpus(Long implementationId, int requiredQubits, int circuitDepth) throws IOException {
        // check if file with required rule exists and create otherwise
        if (!doesPrologFileExist(Constants.CPU_RULE_NAME)) {
            persistPrologFile(Constants.CPU_RULE_CONTENT, Constants.CPU_RULE_NAME);
        }
        activatePrologFile(Constants.CPU_RULE_NAME);

        // TODO: perform query and return results
        String query = "executableOnQpu(" + implementationId + "," + "TODO" + ")";
        Map<String, Term>[] solutions = getSolutions(query);
        // TODO
        return null;
    }
}
