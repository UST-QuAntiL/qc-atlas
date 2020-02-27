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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Class to update the local Prolog knowledge base depending on changes in the repositories.
 */
@Service
public class PrologFactUpdater {

    final private static Logger LOG = LoggerFactory.getLogger(PrologFactUpdater.class);

    final private static String newline = System.getProperty("line.separator");

    /**
     * Update the Prolog knowledge base with the required facts for a newly added implementation.
     *
     * @param id                the id of the implementation that is updated in the repository
     * @param usedSdk           the used SDK in the updated implementation
     * @param implementedAlgoId the id of the implemented algorithm
     * @param selectionRule     the selection rule defined in the updated implementation
     */
    public static void handleImplementationInsertion(Long id, String usedSdk, Long implementedAlgoId, String selectionRule) {
        LOG.debug("Handling insertion of implementation with Id {} in Prolog knowledge base.", id);

        String prologContent = createImplementationFacts(id, usedSdk, implementedAlgoId, selectionRule);
        try {
            PrologKnowledgeBaseHandler.persistPrologFile(prologContent, id.toString());
        } catch (IOException e) {
            LOG.error("Unable to store prolog file to add new facts after implementation insertion: {}", e.getMessage());
        }
        // TODO: activate rules in Prolog
    }

    /**
     * Update the Prolog knowledge base with the required facts for a newly added QPU.
     *
     * @param id            the id of the QPU that is added to the repository
     * @param qubitCount    the number of provided qubits of the QPU that is added to the repository
     * @param supportedSdks the list of supported SDKs of the QPU that is added to the repository
     */
    public static void handleQpuInsertion(Long id, int qubitCount, List<String> supportedSdks) {
        LOG.debug("Handling insertion of QPU with Id {} in Prolog knowledge base.", id);
        // TODO
    }

    /**
     * Update the Prolog knowledge base with the required facts for an updated implementation and delete the outdated
     * facts.
     *
     * @param id                the id of the implementation that is updated in the repository
     * @param usedSdk           the used SDK in the updated implementation
     * @param implementedAlgoId the id of the implemented algorithm
     * @param selectionRule     the selection rule defined in the updated implementation
     */
    public static void handleImplementationUpdate(Long id, String usedSdk, Long implementedAlgoId, String selectionRule) {
        LOG.debug("Handling update of implementation with Id {} in Prolog knowledge base.", id);

        // deactivate and delete the Prolog file with the old facts
        PrologKnowledgeBaseHandler.deletePrologFile(id.toString());

        // create and activate the Prolog file with the new facts
        String prologContent = createImplementationFacts(id, usedSdk, implementedAlgoId, selectionRule);
        try {
            PrologKnowledgeBaseHandler.persistPrologFile(prologContent, id.toString());
        } catch (IOException e) {
            LOG.error("Unable to store prolog file to add new facts after implementation insertion: {}", e.getMessage());
        }
        // TODO: activate facts in Prolog
    }

    /**
     * Update the Prolog knowledge base with the required facts for an updated QPU and delete the outdated facts.
     *
     * @param id            the id of the QPU that is updated in the repository
     * @param qubitCount    the number of provided qubits of the QPU that is updated in the repository
     * @param supportedSdks the list of supported SDKs of the QPU that is updated in the repository
     */
    public static void handleQpuUpdate(Long id, int qubitCount, List<String> supportedSdks) {
        LOG.debug("Handling update of QPU with Id {} in Prolog knowledge base.", id);

        // deactivate and delete the Prolog file with the old facts
        PrologKnowledgeBaseHandler.deletePrologFile(id.toString());

        // TODO
    }

    /**
     * Delete the facts in the knowledge base about the implementation that is deleted from the repository.
     *
     * @param id the id of the implementation that is deleted from the repository
     */
    public static void handleImplementationDeletion(Long id) {
        LOG.debug("Handling deletion of implementation with Id {} in Prolog knowledge base.", id);
        PrologKnowledgeBaseHandler.deletePrologFile(id.toString());
    }

    /**
     * Delete the facts in the knowledge base about the QPU that is deleted from the repository.
     *
     * @param id the id of the QPU that is deleted from the repository
     */
    public static void handleQpuDeletion(Long id) {
        LOG.debug("Handling deletion of QPU with Id {} in Prolog knowledge base.", id);
        PrologKnowledgeBaseHandler.deletePrologFile(id.toString());
    }

    /**
     * Create a string containing all required prologs fact for an implementation.
     */
    private static String createImplementationFacts(Long implId, String usedSdk, Long implementedAlgoId, String selectionRule) {
        String prologContent = createImplementsFact(implId, implementedAlgoId) + newline;
        prologContent += createRequiredSdkFact(implId, usedSdk) + newline;
        prologContent += selectionRule + newline;
        return prologContent;
    }

    /**
     * Create a fact that the given implementation implements the given algorithm
     *
     * @param implId the id of the implementation
     * @param algoId the id of the algorithm
     * @return the Prolog fact
     */
    private static String createImplementsFact(Long implId, Long algoId) {
        return "implements(" + implId + "," + algoId + ").";
    }

    /**
     * Create a fact that the given implementation requires the given SDK
     *
     * @param implId  the id of the implementation
     * @param sdkName the name of the SDK
     * @return the Prolog fact
     */
    private static String createRequiredSdkFact(Long implId, String sdkName) {
        return "requiredSdk(" + implId + "," + sdkName + ").";
    }
}
