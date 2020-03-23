/*******************************************************************************
 * Copyright (c) 2020 University of Stuttgart
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.planqk.atlas.nisq.analyzer.knowledge.prolog;

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

        String prologContent = createImplementationFacts(id, usedSdk.toLowerCase(), implementedAlgoId, selectionRule);
        try {
            PrologKnowledgeBaseHandler.persistPrologFile(prologContent, id.toString());
            PrologKnowledgeBaseHandler.activatePrologFile(id.toString());
        } catch (IOException e) {
            LOG.error("Unable to store prolog file to add new facts after implementation insertion: {}", e.getMessage());
        }
    }

    /**
     * Update the Prolog knowledge base with the required facts for a newly added QPU.
     *
     * @param id            the id of the QPU that is added to the repository
     * @param qubitCount    the number of provided qubits of the QPU that is added to the repository
     * @param t1Time        the T1 time for the given QPU
     * @param maxGateTime   the maximum gate time for the given QPU
     * @param supportedSdks the list of supported SDKs of the QPU that is added to the repository
     */
    public static void handleQpuInsertion(Long id, int qubitCount, List<String> supportedSdks, float t1Time, float maxGateTime) {
        LOG.debug("Handling insertion of QPU with Id {} in Prolog knowledge base.", id);

        String prologContent = createQpuFacts(id, qubitCount, supportedSdks, t1Time, maxGateTime);
        try {
            PrologKnowledgeBaseHandler.persistPrologFile(prologContent, id.toString());
            PrologKnowledgeBaseHandler.activatePrologFile(id.toString());
        } catch (IOException e) {
            LOG.error("Unable to store prolog file to add new facts after QPU insertion: {}", e.getMessage());
        }
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
        String prologContent = createImplementationFacts(id, usedSdk.toLowerCase(), implementedAlgoId, selectionRule);
        try {
            PrologKnowledgeBaseHandler.persistPrologFile(prologContent, id.toString());
            PrologKnowledgeBaseHandler.activatePrologFile(id.toString());
        } catch (IOException e) {
            LOG.error("Unable to store prolog file to add new facts after implementation update: {}", e.getMessage());
        }
    }

    /**
     * Update the Prolog knowledge base with the required facts for an updated QPU and delete the outdated facts.
     *
     * @param id            the id of the QPU that is updated in the repository
     * @param qubitCount    the number of provided qubits of the QPU that is updated in the repository
     * @param t1Time        the T1 time for the given QPU
     * @param maxGateTime   the maximum gate time for the given QPU
     * @param supportedSdks the list of supported SDKs of the QPU that is updated in the repository
     */
    public static void handleQpuUpdate(Long id, int qubitCount, List<String> supportedSdks, float t1Time, float maxGateTime) {
        LOG.debug("Handling update of QPU with Id {} in Prolog knowledge base.", id);

        // deactivate and delete the Prolog file with the old facts
        PrologKnowledgeBaseHandler.deletePrologFile(id.toString());

        // create and activate the Prolog file with the new facts
        String prologContent = createQpuFacts(id, qubitCount, supportedSdks, t1Time, maxGateTime);
        try {
            PrologKnowledgeBaseHandler.persistPrologFile(prologContent, id.toString());
            PrologKnowledgeBaseHandler.activatePrologFile(id.toString());
        } catch (IOException e) {
            LOG.error("Unable to store prolog file to add new facts after QPU update: {}", e.getMessage());
        }
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
     * Create a string containing all required prolog fact for an implementation.
     */
    private static String createImplementationFacts(Long implId, String usedSdk, Long implementedAlgoId, String selectionRule) {
        // the following three lines are required to define the same predicate in multiple files
        String prologContent = ":- multifile implements/2." + newline;
        prologContent += ":- multifile requiredSdk/2." + newline;
        prologContent += ":- multifile " + getNameOfPredicate(selectionRule) + "/" + PrologUtility.getNumberOfParameters(selectionRule) + "." + newline;

        prologContent += createImplementsFact(implId, implementedAlgoId) + newline;
        prologContent += createRequiredSdkFact(implId, usedSdk) + newline;
        prologContent += selectionRule + newline;
        return prologContent;
    }

    /**
     * Create a string containing all required prolog fact for an QPU.
     */
    private static String createQpuFacts(Long qpuId, int qubitCount, List<String> supportedSdks, float t1Time, float maxGateTime) {
        // the following two lines are required to define the same predicate in multiple files
        String prologContent = ":- multifile providesQubits/2." + newline;
        prologContent += ":- multifile usesSdk/2." + newline;
        prologContent += ":- multifile t1Time/2." + newline;
        prologContent += ":- multifile maxGateTime/2." + newline;

        prologContent += createProvidesQubitFact(qpuId, qubitCount) + newline;
        prologContent += createUsesSdkFacts(qpuId, supportedSdks) + newline;
        prologContent += createT1TimeFact(qpuId, t1Time) + newline;
        prologContent += createMaxGateTimeFact(qpuId, maxGateTime) + newline;
        return prologContent;
    }

    /**
     * Create a list of facts that the given QPU supports the given list of SDKs
     *
     * @param qpuId         the id of the QPU
     * @param supportedSdks the list of SDKs that are supported by the QPU
     * @return the Prolog facts
     */
    private static String createUsesSdkFacts(Long qpuId, List<String> supportedSdks) {
        String prologContent = "";
        for (String supportedSdk : supportedSdks) {
            prologContent += "usesSdk(" + qpuId + "," + supportedSdk.toLowerCase() + ")." + newline;
        }
        return prologContent;
    }

    /**
     * Create a fact that the given QPU provides the given number of Qubits
     *
     * @param qpuId      the id of the QPU
     * @param qubitCount the number of Qubits that are provided by the QPU
     * @return the Prolog fact
     */
    private static String createProvidesQubitFact(Long qpuId, int qubitCount) {
        return "providesQubits(" + qpuId + "," + qubitCount + ").";
    }

    /**
     * Create a fact that the given QPU has the given T1 time
     *
     * @param qpuId  the id of the QPU
     * @param t1Time the T1 time for the given QPU
     * @return the Prolog fact
     */
    private static String createT1TimeFact(Long qpuId, float t1Time) {
        return "t1Time(" + qpuId + "," + t1Time + ").";
    }

    /**
     * Create a fact that the given QPU has the given execution time for the slowest gate
     *
     * @param qpuId       the id of the QPU
     * @param maxGateTime the time of the slowest gate of the QPU
     * @return the Prolog fact
     */
    private static String createMaxGateTimeFact(Long qpuId, float maxGateTime) {
        return "maxGateTime(" + qpuId + "," + maxGateTime + ").";
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

    /**
     * Get the predicate name for the given Prolog rule. E.g. "executable(N, shor-15-qiskit)" --> "executable"
     *
     * @param rule the rule to retrieve the predicate name from
     * @return the name of the predicate
     */
    private static String getNameOfPredicate(String rule) {
        return rule.split("\\(")[0];
    }
}
