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

package org.planqk.atlas.core.model.listener;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.planqk.atlas.core.model.HasId;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Qpu;
import org.planqk.atlas.core.model.Sdk;
import org.planqk.atlas.nisq.analyzer.knowledge.prolog.PrologFactUpdater;

/**
 * Listener to react to changes in the Quality repositories by updating corresponding prolog facts.
 */
public class DatabaseListener {

    @PostPersist
    private void onInsert(HasId hasId) {

        // forward insertions of implementations and qpus, ad these have to be reflected in the knowledge base
        if (hasId.getClass().equals(Implementation.class)) {
            Implementation impl = (Implementation) hasId;
            PrologFactUpdater.handleImplementationInsertion(impl.getId(), impl.getSdk().getName(),
                    impl.getImplementedAlgorithm().getId(), impl.getSelectionRule());
        }

        if (hasId.getClass().equals(Qpu.class)) {
            Qpu qpu = (Qpu) hasId;
            List<String> sdkNames = qpu.getSupportedSdks().stream().map(Sdk::getName).collect(Collectors.toList());
            PrologFactUpdater.handleQpuInsertion(qpu.getId(), qpu.getQubitCount(), sdkNames, qpu.getT1(), qpu.getMaxGateTime());
        }
    }

    @PostUpdate
    private void onUpdate(HasId hasId) {

        // forward updates of implementations and qpus, ad these have to be reflected in the knowledge base
        if (hasId.getClass().equals(Implementation.class)) {
            Implementation impl = (Implementation) hasId;
            PrologFactUpdater.handleImplementationUpdate(impl.getId(), impl.getSdk().getName(),
                    impl.getImplementedAlgorithm().getId(), impl.getSelectionRule());
        }

        if (hasId.getClass().equals(Qpu.class)) {
            Qpu qpu = (Qpu) hasId;
            List<String> sdkNames = qpu.getSupportedSdks().stream().map(Sdk::getName).collect(Collectors.toList());
            PrologFactUpdater.handleQpuUpdate(qpu.getId(), qpu.getQubitCount(), sdkNames, qpu.getT1(), qpu.getMaxGateTime());
        }
    }

    @PostRemove
    private void onRemove(HasId hasId) {

        // forward deletions of implementations and qpus, ad these have to be reflected in the knowledge base
        if (hasId.getClass().equals(Implementation.class)) {
            PrologFactUpdater.handleImplementationDeletion(hasId.getId());
        }

        if (hasId.getClass().equals(Qpu.class)) {
            PrologFactUpdater.handleQpuDeletion(hasId.getId());
        }
    }
}
