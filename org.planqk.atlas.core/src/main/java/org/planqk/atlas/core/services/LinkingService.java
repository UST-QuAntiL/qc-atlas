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

package org.planqk.atlas.core.services;

import java.util.UUID;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.LearningMethod;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for operations related to linking existing objects in the database.
 */
public interface LinkingService {

    /**
     * Links an existing {@link org.planqk.atlas.core.model.Algorithm} and an existing {@link org.planqk.atlas.core.model.Publication}.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.Algorithm} or the {@link org.planqk.atlas.core.model.Publication} with given IDs could not be
     * found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are already linked this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param algorithmId   The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to link
     * @param publicationId The ID of the {@link org.planqk.atlas.core.model.Publication} we want to link
     */
    @Transactional
    void linkAlgorithmAndPublication(UUID algorithmId, UUID publicationId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.Algorithm} and an existing {@link org.planqk.atlas.core.model.Publication} that have been
     * linked before.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.Algorithm} or the {@link org.planqk.atlas.core.model.Publication} with given IDs could not be
     * found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are not linked beforehand this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param algorithmId   The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to unlink
     * @param publicationId The ID of the {@link org.planqk.atlas.core.model.Publication} we want to unlink
     */
    @Transactional
    void unlinkAlgorithmAndPublication(UUID algorithmId, UUID publicationId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.Algorithm} and an existing {@link org.planqk.atlas.core.model.ProblemType}.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.Algorithm} or the {@link org.planqk.atlas.core.model.ProblemType} with given IDs could not be
     * found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are already linked this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param algorithmId   The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to link
     * @param problemTypeId The ID of the {@link org.planqk.atlas.core.model.ProblemType} we want to link
     */
    @Transactional
    void linkAlgorithmAndProblemType(UUID algorithmId, UUID problemTypeId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.Algorithm} and an existing {@link org.planqk.atlas.core.model.ProblemType} that have been
     * linked before.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.Algorithm} or the {@link org.planqk.atlas.core.model.ProblemType} with given IDs could not be
     * found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are not linked beforehand this method will throw an {
     *
     * @param algorithmId   The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to unlink
     * @param problemTypeId The ID of the {@link org.planqk.atlas.core.model.ProblemType} we want to unlink
     * @link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     */
    @Transactional
    void unlinkAlgorithmAndProblemType(UUID algorithmId, UUID problemTypeId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.Algorithm} and an existing {@link org.planqk.atlas.core.model.ApplicationArea}.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.Algorithm} or the {@link org.planqk.atlas.core.model.ApplicationArea} with given IDs could not
     * be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are already linked this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param algorithmId       The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to link
     * @param applicationAreaId The ID of the {@link org.planqk.atlas.core.model.ApplicationArea} we want to link
     */
    @Transactional
    void linkAlgorithmAndApplicationArea(UUID algorithmId, UUID applicationAreaId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.Algorithm} and an existing {@link org.planqk.atlas.core.model.ApplicationArea} that have
     * been linked before.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.Algorithm} or the {@link org.planqk.atlas.core.model.ApplicationArea} with given IDs could not
     * be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are not linked beforehand this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param algorithmId       The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to unlink
     * @param applicationAreaId The ID of the {@link org.planqk.atlas.core.model.ApplicationArea} we want to unlink
     */
    @Transactional
    void unlinkAlgorithmAndApplicationArea(UUID algorithmId, UUID applicationAreaId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.Implementation} and an existing {@link org.planqk.atlas.core.model.Publication}.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.Implementation} or the {@link org.planqk.atlas.core.model.Publication} with given IDs could
     * not be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are already linked this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param implementationId The ID of the {@link org.planqk.atlas.core.model.Implementation} we want to link
     * @param publicationId    The ID of the {@link org.planqk.atlas.core.model.Publication} we want to link
     */
    @Transactional
    void linkImplementationAndPublication(UUID implementationId, UUID publicationId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.Implementation} and an existing {@link org.planqk.atlas.core.model.Publication} that have
     * been linked before.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.Implementation} or the {@link org.planqk.atlas.core.model.Publication} with given IDs could
     * not be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are not linked beforehand this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param implementationId The ID of the {@link org.planqk.atlas.core.model.Implementation} we want to unlink
     * @param publicationId    The ID of the {@link org.planqk.atlas.core.model.Publication} we want to unlink
     */
    @Transactional
    void unlinkImplementationAndPublication(UUID implementationId, UUID publicationId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.Implementation} and an existing {@link org.planqk.atlas.core.model.SoftwarePlatform}.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.Implementation} or the {@link org.planqk.atlas.core.model.SoftwarePlatform} with given IDs
     * could not be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are already linked this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param implementationId   The ID of the {@link org.planqk.atlas.core.model.Implementation} we want to link
     * @param softwarePlatformId The ID of the {@link org.planqk.atlas.core.model.SoftwarePlatform} we want to link
     */
    @Transactional
    void linkImplementationAndSoftwarePlatform(UUID implementationId, UUID softwarePlatformId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.Implementation} and an existing {@link org.planqk.atlas.core.model.SoftwarePlatform} that
     * have been linked before.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.Implementation} or the {@link org.planqk.atlas.core.model.SoftwarePlatform} with given IDs
     * could not be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are not linked beforehand this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param implementationId   The ID of the {@link org.planqk.atlas.core.model.Implementation} we want to unlink
     * @param softwarePlatformId The ID of the {@link org.planqk.atlas.core.model.SoftwarePlatform} we want to unlink
     */
    @Transactional
    void unlinkImplementationAndSoftwarePlatform(UUID implementationId, UUID softwarePlatformId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.SoftwarePlatform} and an existing {@link org.planqk.atlas.core.model.CloudService}.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.SoftwarePlatform} or the {@link org.planqk.atlas.core.model.CloudService} with given IDs could
     * not be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are already linked this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param softwarePlatformId The ID of the {@link org.planqk.atlas.core.model.SoftwarePlatform} we want to link
     * @param cloudServiceId     The ID of the {@link org.planqk.atlas.core.model.CloudService} we want to link
     */
    @Transactional
    void linkSoftwarePlatformAndCloudService(UUID softwarePlatformId, UUID cloudServiceId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.SoftwarePlatform} and an existing {@link org.planqk.atlas.core.model.CloudService} that
     * have been linked before.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.SoftwarePlatform} or the {@link org.planqk.atlas.core.model.CloudService} with given IDs could
     * not be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are not linked beforehand this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param softwarePlatformId The ID of the {@link org.planqk.atlas.core.model.SoftwarePlatform} we want to unlink
     * @param cloudServiceId     The ID of the {@link org.planqk.atlas.core.model.CloudService} we want to unlink
     */
    @Transactional
    void unlinkSoftwarePlatformAndCloudService(UUID softwarePlatformId, UUID cloudServiceId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.SoftwarePlatform} and an existing {@link org.planqk.atlas.core.model.ComputeResource}.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.SoftwarePlatform} or the {@link org.planqk.atlas.core.model.ComputeResource} with given IDs
     * could not be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are already linked this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param softwarePlatformId The ID of the {@link org.planqk.atlas.core.model.SoftwarePlatform} we want to link
     * @param computeResourceId  The ID of the {@link org.planqk.atlas.core.model.ComputeResource} we want to link
     */
    @Transactional
    void linkSoftwarePlatformAndComputeResource(UUID softwarePlatformId, UUID computeResourceId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.SoftwarePlatform} and an existing {@link org.planqk.atlas.core.model.ComputeResource} that
     * have been linked before.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.SoftwarePlatform} or the {@link org.planqk.atlas.core.model.ComputeResource} with given IDs
     * could not be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are not linked beforehand this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param softwarePlatformId The ID of the {@link org.planqk.atlas.core.model.SoftwarePlatform} we want to unlink
     * @param computeResourceId  The ID of the {@link org.planqk.atlas.core.model.ComputeResource} we want to unlink
     */
    @Transactional
    void unlinkSoftwarePlatformAndComputeResource(UUID softwarePlatformId, UUID computeResourceId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.CloudService} and an existing {@link org.planqk.atlas.core.model.ComputeResource}.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.CloudService} or the {@link org.planqk.atlas.core.model.ComputeResource} with given IDs could
     * not be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are already linked this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param cloudServiceId    The ID of the {@link org.planqk.atlas.core.model.CloudService} we want to link
     * @param computeResourceId The ID of the {@link org.planqk.atlas.core.model.ComputeResource} we want to link
     */
    @Transactional
    void linkCloudServiceAndComputeResource(UUID cloudServiceId, UUID computeResourceId);

    /**
     * Links an existing {@link org.planqk.atlas.core.model.CloudService} and an existing {@link org.planqk.atlas.core.model.ComputeResource} that
     * have been linked before.
     * <p>
     * If either the {@link org.planqk.atlas.core.model.CloudService} or the {@link org.planqk.atlas.core.model.ComputeResource} with given IDs could
     * not be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are not linked beforehand this method will throw an
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}.
     *
     * @param cloudServiceId    The ID of the {@link org.planqk.atlas.core.model.CloudService} we want to unlink
     * @param computeResourceId The ID of the {@link org.planqk.atlas.core.model.ComputeResource} we want to unlink
     */
    @Transactional
    void unlinkCloudServiceAndComputeResource(UUID cloudServiceId, UUID computeResourceId);

    /**
     * Links an existing {@link Algorithm} and an existing
     * {@link LearningMethod}.
     * <p>
     * If either the {@link Algorithm} or the {@link LearningMethod}
     * with given IDs could not be
     * found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are already linked this method will throw an
     * {@link EntityReferenceConstraintViolationException}.
     *
     * @param algorithmId      The ID of the {@link Algorithm} we want to link
     * @param learningMethodId The ID of the {@link LearningMethod} we want to link
     */
    @Transactional
    void linkAlgorithmAndLearningMethod(UUID algorithmId, UUID learningMethodId);

    /**
     * Links an existing {@link Algorithm} and an existing
     * {@link LearningMethod} that have been linked before.
     * <p>
     * If either the {@link Algorithm} or the {@link LearningMethod} with given
     * IDs could not be
     * found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * If both entities exist but they are not linked beforehand this method will throw an {
     *
     * @param algorithmId      The ID of the {@link Algorithm} we want to unlink
     * @param learningMethodId The ID of the {@link LearningMethod} we want to unlink
     * @link de.stoneone.planqk.qccatalog.exception.EntityReferenceConstraintViolationException}.
     */
    @Transactional
    void unlinkAlgorithmAndLearningMethod(UUID algorithmId, UUID learningMethodId);
}
