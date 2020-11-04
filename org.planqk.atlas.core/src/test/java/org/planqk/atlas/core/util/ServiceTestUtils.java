/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.SoftwarePlatform;

public class ServiceTestUtils {

    public static void assertCollectionEquality(Collection<?> collection1, Collection<?> collection2) {
        assertThat(collection1.containsAll(collection2)).isTrue();
        assertThat(collection2.containsAll(collection1)).isTrue();
    }

    public static void assertAlgorithmEquality(Algorithm algorithm1, Algorithm algorithm2) {
        assertThat(algorithm1.getName()).isEqualTo(algorithm2.getName());
        assertThat(algorithm1.getAcronym()).isEqualTo(algorithm2.getAcronym());
        assertThat(algorithm1.getIntent()).isEqualTo(algorithm2.getIntent());
        assertThat(algorithm1.getProblem()).isEqualTo(algorithm2.getProblem());
        assertThat(algorithm1.getInputFormat()).isEqualTo(algorithm2.getInputFormat());
        assertThat(algorithm1.getAlgoParameter()).isEqualTo(algorithm2.getAlgoParameter());
        assertThat(algorithm1.getOutputFormat()).isEqualTo(algorithm2.getOutputFormat());
        assertThat(algorithm1.getSolution()).isEqualTo(algorithm2.getSolution());
        assertThat(algorithm1.getAssumptions()).isEqualTo(algorithm2.getAssumptions());
        assertThat(algorithm1.getComputationModel()).isEqualTo(algorithm2.getComputationModel());

        if (algorithm1.getComputationModel() == ComputationModel.QUANTUM
            || algorithm1.getComputationModel() == ComputationModel.HYBRID) {
            var quantumAlgorithm1 = (QuantumAlgorithm) algorithm1;
            var quantumAlgorithm2 = (QuantumAlgorithm) algorithm2;

            assertThat(quantumAlgorithm1.getQuantumComputationModel())
                .isEqualTo(quantumAlgorithm2.getQuantumComputationModel());
            assertThat(quantumAlgorithm1.getSpeedUp()).isEqualTo(quantumAlgorithm2.getSpeedUp());
            assertThat(quantumAlgorithm1.isNisqReady()).isEqualTo(quantumAlgorithm2.isNisqReady());
        }

        assertCollectionEquality(algorithm1.getSketches(), algorithm2.getSketches());
    }

    public static void assertAlgorithmRelationEquality(
        AlgorithmRelation algorithmRelation1, AlgorithmRelation algorithmRelation2) {
        assertThat(algorithmRelation1.getDescription()).isEqualTo(algorithmRelation2.getDescription());
        assertThat(algorithmRelation1.getAlgorithmRelationType().getId())
            .isEqualTo(algorithmRelation2.getAlgorithmRelationType().getId());
        assertThat(algorithmRelation1.getAlgorithmRelationType().getName())
            .isEqualTo(algorithmRelation2.getAlgorithmRelationType().getName());
        assertThat(algorithmRelation1.getSourceAlgorithm().getId()).isEqualTo(algorithmRelation2.getSourceAlgorithm().getId());
        assertThat(algorithmRelation1.getTargetAlgorithm().getId()).isEqualTo(algorithmRelation2.getTargetAlgorithm().getId());
    }

    public static void assertImplementationEquality(Implementation implementation1, Implementation implementation2) {
        assertThat(implementation1.getName()).isEqualTo(implementation2.getName());
        assertThat(implementation1.getDescription()).isEqualTo(implementation2.getDescription());
        assertThat(implementation1.getContributors()).isEqualTo(implementation2.getContributors());
        assertThat(implementation1.getAssumptions()).isEqualTo(implementation2.getAssumptions());
        assertThat(implementation1.getParameter()).isEqualTo(implementation2.getParameter());
        assertThat(implementation1.getLink()).isEqualTo(implementation2.getLink());
        assertThat(implementation1.getDependencies()).isEqualTo(implementation2.getDependencies());
        assertThat(implementation1.getImplementedAlgorithm().getId())
            .isEqualTo(implementation2.getImplementedAlgorithm().getId());
        assertAlgorithmEquality(implementation1.getImplementedAlgorithm(), implementation2.getImplementedAlgorithm());
    }

    public static void assertPublicationEquality(Publication publication1, Publication publication2) {
        assertThat(publication1.getTitle()).isEqualTo(publication2.getTitle());
        assertThat(publication1.getUrl()).isEqualTo(publication2.getUrl());
        assertThat(publication1.getDoi()).isEqualTo(publication2.getDoi());

        assertCollectionEquality(publication1.getAuthors(), publication2.getAuthors());
    }

    public static void assertSoftwarePlatformEquality(SoftwarePlatform softwarePlatform1, SoftwarePlatform softwarePlatform2) {
        assertThat(softwarePlatform1.getName()).isEqualTo(softwarePlatform2.getName());
        assertThat(softwarePlatform1.getLink()).isEqualTo(softwarePlatform2.getLink());
        assertThat(softwarePlatform1.getVersion()).isEqualTo(softwarePlatform2.getVersion());
        assertThat(softwarePlatform1.getLicence()).isEqualTo(softwarePlatform2.getLicence());
    }

    public static void assertCloudServiceEquality(CloudService cloudService1, CloudService cloudService2) {
        assertThat(cloudService1.getName()).isEqualTo(cloudService2.getName());
        assertThat(cloudService1.getProvider()).isEqualTo(cloudService2.getProvider());
        assertThat(cloudService1.getUrl()).isEqualTo(cloudService2.getUrl());
        assertThat(cloudService1.getCostModel()).isEqualTo(cloudService2.getCostModel());
    }

    public static void assertComputeResourceEquality(ComputeResource computeResource1, ComputeResource computeResource2) {
        assertThat(computeResource1.getName()).isEqualTo(computeResource2.getName());
        assertThat(computeResource1.getQuantumComputationModel()).isEqualTo(computeResource2.getQuantumComputationModel());
        assertThat(computeResource1.getTechnology()).isEqualTo(computeResource2.getTechnology());
        assertThat(computeResource1.getVendor()).isEqualTo(computeResource2.getVendor());
    }

    public static void assertComputeResourcePropertyEquality(
        ComputeResourceProperty computeResourceProperty1, ComputeResourceProperty computeResourceProperty2) {
        assertThat(computeResourceProperty1.getValue()).isEqualTo(computeResourceProperty2.getValue());
        assertThat(computeResourceProperty1.getComputeResourcePropertyType().getId())
            .isEqualTo(computeResourceProperty2.getComputeResourcePropertyType().getId());
        assertThat(computeResourceProperty1.getComputeResourcePropertyType().getName())
            .isEqualTo(computeResourceProperty2.getComputeResourcePropertyType().getName());
        assertThat(computeResourceProperty1.getComputeResourcePropertyType().getDescription())
            .isEqualTo(computeResourceProperty2.getComputeResourcePropertyType().getDescription());
        assertThat(computeResourceProperty1.getComputeResourcePropertyType().getDatatype())
            .isEqualTo(computeResourceProperty2.getComputeResourcePropertyType().getDatatype());
    }
}
