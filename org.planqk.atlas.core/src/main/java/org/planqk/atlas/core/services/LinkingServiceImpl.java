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

package org.planqk.atlas.core.services;

import java.util.UUID;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.SoftwarePlatform;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class LinkingServiceImpl implements LinkingService {

    private final AlgorithmService algorithmService;

    private final ImplementationService implementationService;

    private final PublicationService publicationService;

    private final ProblemTypeService problemTypeService;

    private final ApplicationAreaService applicationAreaService;

    private final PatternRelationService patternRelationService;

    private final SoftwarePlatformService softwarePlatformService;

    private final CloudServiceService cloudServiceService;

    private final ComputeResourceService computeResourceService;

    @Override
    @Transactional
    public void linkAlgorithmAndPublication(@NonNull UUID algorithmId, @NonNull UUID publicationId) {
        Algorithm algorithm = algorithmService.findById(algorithmId);
        Publication publication = publicationService.findById(publicationId);

        if (algorithm.getPublications().contains(publication)) {
            throw new EntityReferenceConstraintViolationException("Algorithm and Publication are already linked");
        }

        algorithm.addPublication(publication);
    }

    @Override
    @Transactional
    public void unlinkAlgorithmAndPublication(@NonNull UUID algorithmId, @NonNull UUID publicationId) {
        Algorithm algorithm = algorithmService.findById(algorithmId);
        Publication publication = publicationService.findById(publicationId);

        if (!algorithm.getPublications().contains(publication)) {
            throw new EntityReferenceConstraintViolationException("Algorithm and Publication are not linked");
        }

        algorithm.removePublication(publication);
    }

    @Override
    @Transactional
    public void linkAlgorithmAndProblemType(@NonNull UUID algorithmId, @NonNull UUID problemTypeId) {
        Algorithm algorithm = algorithmService.findById(algorithmId);
        ProblemType problemType = problemTypeService.findById(problemTypeId);

        if (algorithm.getProblemTypes().contains(problemType)) {
            throw new EntityReferenceConstraintViolationException("Algorithm and Problem type are already linked");
        }

        algorithm.addProblemType(problemType);
    }

    @Override
    @Transactional
    public void unlinkAlgorithmAndProblemType(@NonNull UUID algorithmId, @NonNull UUID problemTypeId) {
        Algorithm algorithm = algorithmService.findById(algorithmId);
        ProblemType problemType = problemTypeService.findById(problemTypeId);

        if (!algorithm.getProblemTypes().contains(problemType)) {
            throw new EntityReferenceConstraintViolationException("Algorithm and Problem type are not linked");
        }

        algorithm.removeProblemType(problemType);
    }

    @Override
    @Transactional
    public void linkAlgorithmAndApplicationArea(@NonNull UUID algorithmId, @NonNull UUID applicationAreaId) {
        Algorithm algorithm = algorithmService.findById(algorithmId);
        ApplicationArea applicationArea = applicationAreaService.findById(applicationAreaId);

        if (algorithm.getApplicationAreas().contains(applicationArea)) {
            throw new EntityReferenceConstraintViolationException("Algorithm and Application area are already linked");
        }

        algorithm.addApplicationArea(applicationArea);
    }

    @Override
    @Transactional
    public void unlinkAlgorithmAndApplicationArea(@NonNull UUID algorithmId, @NonNull UUID applicationAreaId) {
        Algorithm algorithm = algorithmService.findById(algorithmId);
        ApplicationArea applicationArea = applicationAreaService.findById(applicationAreaId);

        if (!algorithm.getApplicationAreas().contains(applicationArea)) {
            throw new EntityReferenceConstraintViolationException("Algorithm and Application area are not linked");
        }

        algorithm.removeApplicationArea(applicationArea);
    }

    @Override
    @Transactional
    public void linkAlgorithmAndPatternRelation(@NonNull UUID algorithmId, @NonNull UUID patternRelationId) {
        Algorithm algorithm = algorithmService.findById(algorithmId);
        PatternRelation patternRelation = patternRelationService.findById(patternRelationId);

        if (algorithm.getRelatedPatterns().contains(patternRelation)) {
            throw new EntityReferenceConstraintViolationException("Algorithm and Pattern relation are already linked");
        }

        algorithm.getRelatedPatterns().add(patternRelation);
    }

    @Override
    @Transactional
    public void unlinkAlgorithmAndPatternRelation(@NonNull UUID algorithmId, @NonNull UUID patternRelationId) {
        Algorithm algorithm = algorithmService.findById(algorithmId);
        PatternRelation patternRelation = patternRelationService.findById(patternRelationId);

        if (!algorithm.getRelatedPatterns().contains(patternRelation)) {
            throw new EntityReferenceConstraintViolationException("Algorithm and Pattern relation are not linked");
        }

        algorithm.getRelatedPatterns().remove(patternRelation);
    }

    @Override
    @Transactional
    public void linkImplementationAndPublication(@NonNull UUID implementationId, @NonNull UUID publicationId) {
        Implementation implementation = implementationService.findById(implementationId);
        Publication publication = publicationService.findById(publicationId);

        if (implementation.getPublications().contains(publication)) {
            throw new EntityReferenceConstraintViolationException("Implementation and Publication are already linked");
        }

        implementation.addPublication(publication);
    }

    @Override
    @Transactional
    public void unlinkImplementationAndPublication(@NonNull UUID implementationId, @NonNull UUID publicationId) {
        Implementation implementation = implementationService.findById(implementationId);
        Publication publication = publicationService.findById(publicationId);

        if (!implementation.getPublications().contains(publication)) {
            throw new EntityReferenceConstraintViolationException("Implementation and Publication are not linked");
        }

        implementation.removePublication(publication);
    }

    @Override
    @Transactional
    public void linkImplementationAndSoftwarePlatform(@NonNull UUID implementationId, @NonNull UUID softwarePlatformId) {
        Implementation implementation = implementationService.findById(implementationId);
        SoftwarePlatform softwarePlatform = softwarePlatformService.findById(softwarePlatformId);

        if (implementation.getSoftwarePlatforms().contains(softwarePlatform)) {
            throw new EntityReferenceConstraintViolationException("Implementation and Software platform are already linked");
        }

        implementation.addSoftwarePlatform(softwarePlatform);
    }

    @Override
    @Transactional
    public void unlinkImplementationAndSoftwarePlatform(@NonNull UUID implementationId, @NonNull UUID softwarePlatformId) {
        Implementation implementation = implementationService.findById(implementationId);
        SoftwarePlatform softwarePlatform = softwarePlatformService.findById(softwarePlatformId);

        if (!implementation.getSoftwarePlatforms().contains(softwarePlatform)) {
            throw new EntityReferenceConstraintViolationException("Implementation and Software platform are not linked");
        }

        implementation.removeSoftwarePlatform(softwarePlatform);
    }

    @Override
    @Transactional
    public void linkSoftwarePlatformAndCloudService(@NonNull UUID softwarePlatformId, @NonNull UUID cloudServiceId) {
        SoftwarePlatform softwarePlatform = softwarePlatformService.findById(softwarePlatformId);
        CloudService cloudService = cloudServiceService.findById(cloudServiceId);

        if (softwarePlatform.getSupportedCloudServices().contains(cloudService)) {
            throw new EntityReferenceConstraintViolationException("Software platform and Cloud service are already linked");
        }

        softwarePlatform.addCloudService(cloudService);
    }

    @Override
    @Transactional
    public void unlinkSoftwarePlatformAndCloudService(@NonNull UUID softwarePlatformId, @NonNull UUID cloudServiceId) {
        SoftwarePlatform softwarePlatform = softwarePlatformService.findById(softwarePlatformId);
        CloudService cloudService = cloudServiceService.findById(cloudServiceId);

        if (!softwarePlatform.getSupportedCloudServices().contains(cloudService)) {
            throw new EntityReferenceConstraintViolationException("Software platform and Cloud service are not linked");
        }

        softwarePlatform.removeCloudService(cloudService);
    }

    @Override
    @Transactional
    public void linkSoftwarePlatformAndComputeResource(@NonNull UUID softwarePlatformId, @NonNull UUID computeResourceId) {
        SoftwarePlatform softwarePlatform = softwarePlatformService.findById(softwarePlatformId);
        ComputeResource computeResource = computeResourceService.findById(computeResourceId);

        if (softwarePlatform.getSupportedComputeResources().contains(computeResource)) {
            throw new EntityReferenceConstraintViolationException("Software platform and Compute resource are already linked");
        }

        softwarePlatform.addComputeResource(computeResource);
    }

    @Override
    @Transactional
    public void unlinkSoftwarePlatformAndComputeResource(@NonNull UUID softwarePlatformId, @NonNull UUID computeResourceId) {
        SoftwarePlatform softwarePlatform = softwarePlatformService.findById(softwarePlatformId);
        ComputeResource computeResource = computeResourceService.findById(computeResourceId);

        if (!softwarePlatform.getSupportedComputeResources().contains(computeResource)) {
            throw new EntityReferenceConstraintViolationException("Software platform and Compute resource are not linked");
        }

        softwarePlatform.removeComputeResource(computeResource);
    }

    @Override
    @Transactional
    public void linkCloudServiceAndComputeResource(@NonNull UUID cloudServiceId, @NonNull UUID computeResourceId) {
        var cloudService = cloudServiceService.findById(cloudServiceId);
        var computeResource = computeResourceService.findById(computeResourceId);

        if (cloudService.getProvidedComputeResources().contains(computeResource)) {
            throw new EntityReferenceConstraintViolationException("Cloud service and Compute Resource are already linked");
        }

        cloudService.addComputeResource(computeResource);
    }

    @Override
    @Transactional
    public void unlinkCloudServiceAndComputeResource(@NonNull UUID cloudServiceId, @NonNull UUID computeResourceId) {
        var cloudService = cloudServiceService.findById(cloudServiceId);
        var computeResource = computeResourceService.findById(computeResourceId);

        if (!cloudService.getProvidedComputeResources().contains(computeResource)) {
            throw new EntityReferenceConstraintViolationException("Cloud service and Compute Resource are not linked");
        }

        cloudService.removeComputeResource(computeResource);
    }
}
