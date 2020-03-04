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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.planqk.quality.execution.IExecutor;
import org.planqk.quality.knowledge.prolog.PrologQueryUtility;
import org.planqk.quality.model.Algorithm;
import org.planqk.quality.model.Implementation;
import org.planqk.quality.model.Qpu;
import org.planqk.quality.repository.ImplementationRepository;
import org.planqk.quality.repository.QpuRepository;
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

    final private FormulaEvaluator formulaEvaluator;

    final private ImplementationRepository implementationRepository;

    final private QpuRepository qpuRepository;

    public QualityControlService(List<IExecutor> executorList, FormulaEvaluator formulaEvaluator, ImplementationRepository implementationRepository, QpuRepository qpuRepository) {
        this.executorList = executorList;
        this.formulaEvaluator = formulaEvaluator;
        this.implementationRepository = implementationRepository;
        this.qpuRepository = qpuRepository;
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

    /**
     * Perform the selection of suitable implementations and corresponding QPUs for the given algorithm and the provided
     * set of input parameters
     *
     * @param algorithm       the algorithm for which an implementation and corresponding QPU should be selected
     * @param inputParameters the set of input parameters required for the selection
     * @return a map with all possible implementations and the corresponding list of QPUs that are suitable to execute
     * them
     */
    public Map<Implementation, List<Qpu>> performSelection(Algorithm algorithm, Map<String, String> inputParameters) {
        LOG.debug("Performing implementation and QPU selection for algorithm with Id: {}", algorithm.getId());
        Map<Implementation, List<Qpu>> resultPairs = new HashMap<>();

        // check all implementation if they can handle the given set of input parameters
        List<Implementation> implementations = implementationRepository.findByImplementedAlgorithm(algorithm);
        LOG.debug("Found {} implementations for the algorithm.", implementations.size());
        List<Implementation> executableImplementations = implementations.stream().filter(implementation -> PrologQueryUtility.checkExecutability()).collect(Collectors.toList());
        LOG.debug("{} implementations are executable for the given input parameters.", executableImplementations.size());

        // determine all suitable QPUs for the executable implementations
        for (Implementation execImplementation : executableImplementations) {
            int requiredQubits = (int) Math.ceil(formulaEvaluator.evaluateFormula(execImplementation.getRequiredQubits(), inputParameters));
            int circuitDepth = 0; // TODO: calculate from formula
            try {
                List<Long> suitableQpuIds = PrologQueryUtility.getSuitableQpus(execImplementation.getId(), requiredQubits, circuitDepth);
                LOG.debug("Found {} suitable QPUs for implementation with Id: {}", suitableQpuIds.size(), execImplementation.getId());

                List<Qpu> suitableQpus = suitableQpuIds.stream().map(qpuRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());

                // add to result map if at least one suitable QPU is available
                if (!suitableQpus.isEmpty()) {
                    resultPairs.put(execImplementation, suitableQpus);
                }
            } catch (IOException e) {
                LOG.error("IOException while evaluating suitable QPUs: {}", e.getMessage());
            }
        }

        return resultPairs;
    }
}
