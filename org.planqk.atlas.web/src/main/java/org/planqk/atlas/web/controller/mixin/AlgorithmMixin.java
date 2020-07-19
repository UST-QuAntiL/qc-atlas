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

package org.planqk.atlas.web.controller.mixin;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ModelWithAlgorithms;
import org.planqk.atlas.core.services.AlgorithmService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlgorithmMixin {
    private final AlgorithmService algorithmService;

    public Algorithm getAlgorithm(ModelWithAlgorithms model, UUID algorithmId) {
        final var algorithms = model.getAlgorithms();
        // Only consider publications that are part of this model.
        // This also saves us one query.
        return algorithms.stream().filter(alg -> alg.getId().equals(algorithmId))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    public void addAlgorithm(ModelWithAlgorithms model, UUID algoId) {
        var algorithm = algorithmService.findById(algoId);
        if (!model.getAlgorithms().contains(algorithm)) {
            var algorithms = model.getAlgorithms();
            algorithms.add(algorithm);
            model.setAlgorithms(algorithms);
        }
    }

    public void unlinkAlgorithm(ModelWithAlgorithms model, UUID algorithmId) {
        var algorithms = model.getAlgorithms();
        if (!algorithms.removeIf(algorithm -> algorithm.getId().equals(algorithmId))) {
            throw new NoSuchElementException();
        }
        model.setAlgorithms(algorithms);
    }
}
