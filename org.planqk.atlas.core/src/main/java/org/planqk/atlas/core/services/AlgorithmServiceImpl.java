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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.Sketch;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.AlgoRelationTypeRepository;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ApplicationAreaRepository;
import org.planqk.atlas.core.repository.ComputeResourcePropertyRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.repository.PatternRelationRepository;
import org.planqk.atlas.core.repository.ProblemTypeRepository;
import org.planqk.atlas.core.repository.PublicationRepository;
import org.planqk.atlas.core.repository.SketchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AlgorithmServiceImpl implements AlgorithmService {

    private final AlgorithmRepository algorithmRepository;

    private final SketchRepository sketchRepository;

    private final AlgorithmRelationRepository algorithmRelationRepository;

    private final AlgoRelationService algoRelationService;
    private final AlgoRelationTypeService algoRelationTypeService;
    private final AlgoRelationTypeRepository algoRelationTypeRepository;

    private final ImplementationRepository implementationRepository;

    private final PublicationService publicationService;
    private final PublicationRepository publicationRepository;

    private final ProblemTypeService problemTypeService;
    private final ProblemTypeRepository problemTypeRepository;

    private final ApplicationAreaService applicationAreaService;
    private final ApplicationAreaRepository applicationAreaRepository;

    private final PatternRelationService patternRelationService;
    private final PatternRelationRepository patternRelationRepository;

    private final ComputeResourcePropertyService computeResourcePropertyService;
    private final ComputeResourcePropertyRepository computeResourcePropertyRepository;

    @Transactional
    @Override
    public Algorithm save(Algorithm algorithm) {
        return algorithmRepository.save(algorithm);
    }

    @Override
    public Page<Algorithm> findAll(Pageable pageable, String search) {
        if (!Objects.isNull(search) && !search.isEmpty()) {
            return algorithmRepository.findAll(search, pageable);
        }
        return algorithmRepository.findAll(pageable);
    }

    @Override
    public Algorithm findById(UUID algoId) {
        return algorithmRepository.findById(algoId).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    @Override
    public Algorithm update(UUID id, Algorithm algorithm) {
        Algorithm persistedAlgorithm = this.findById(id);

        // remove all attached sketches
        persistedAlgorithm.removeSketches(persistedAlgorithm.getSketches());
        if (algorithm.getSketches() != null) {
            algorithm.getSketches().forEach(sketch -> {
                if (sketch.getId() == null) {
                    // add sketches one by one
                    final Sketch savedSketch = sketchRepository.save(sketch);
                    persistedAlgorithm.addSketch(savedSketch);
                }
            });
        }

        persistedAlgorithm.setName(algorithm.getName());
        persistedAlgorithm.setAcronym(algorithm.getAcronym());
        persistedAlgorithm.setIntent(algorithm.getIntent());
        persistedAlgorithm.setProblem(algorithm.getProblem());
        persistedAlgorithm.setInputFormat(algorithm.getInputFormat());
        persistedAlgorithm.setAlgoParameter(algorithm.getAlgoParameter());
        persistedAlgorithm.setOutputFormat(algorithm.getOutputFormat());
        persistedAlgorithm.setSolution(algorithm.getSolution());
        persistedAlgorithm.setAssumptions(algorithm.getAssumptions());
        persistedAlgorithm.setComputationModel(algorithm.getComputationModel());

        // If QuantumAlgorithm adjust Quantum fields
        if (algorithm instanceof QuantumAlgorithm) {
            QuantumAlgorithm quantumAlgorithm = (QuantumAlgorithm) algorithm;
            QuantumAlgorithm persistedQuantumAlgorithm = (QuantumAlgorithm) persistedAlgorithm;

            persistedQuantumAlgorithm.setNisqReady(quantumAlgorithm.isNisqReady());
            persistedQuantumAlgorithm.setQuantumComputationModel(quantumAlgorithm.getQuantumComputationModel());
            persistedQuantumAlgorithm.setSpeedUp(quantumAlgorithm.getSpeedUp());

            return algorithmRepository.save(persistedQuantumAlgorithm);
        } else {
            // Else if ClassicAlgorithm no more fields to adjust
            return algorithmRepository.save(persistedAlgorithm);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Algorithm algorithm = findById(id);

        removeReferences(algorithm);

        algorithmRepository.deleteById(id);
    }

    private void removeReferences(Algorithm algorithm) {
        // delete related implementations
        implementationRepository.findByImplementedAlgorithm(algorithm).forEach(implementationRepository::delete);

        // delete algorithm relations
        getAlgorithmRelations(algorithm.getId(), Pageable.unpaged())
                .forEach(algorithmRelationRepository::delete);

        // delete related pattern relations
        patternRelationService.findByAlgorithmId(algorithm.getId()).forEach(
                patternRelation -> patternRelationService.deleteById(patternRelation.getId()));

        // delete all references to publications
        algorithm.getPublications().forEach(
                publication -> publication.removeAlgorithm(algorithm));
    }

    @Override
    public Page<Publication> findPublications(UUID algoId, Pageable pageable) {
        if (algorithmRepository.existsAlgorithmById(algoId)) {
            throw new NoSuchElementException();
        }

        return publicationRepository.findPublicationsByAlgorithmId(algoId, pageable);
    }

    @Override
    @Transactional
    public void addPublicationReference(UUID algoId, UUID publicationId) {
        Algorithm algorithm = findById(algoId);
        Publication publication = publicationService.findById(publicationId);

        if (algorithm.getPublications().contains(publication)) {
            throw new ConsistencyException("Publication and algorithm are already linked");
        }

        algorithm.addPublication(publication);
    }

    @Override
    @Transactional
    public void deletePublicationReference(UUID algoId, UUID publicationId) {
        Algorithm algorithm = findById(algoId);
        Publication publication = publicationService.findById(publicationId);

        if (!algorithm.getPublications().contains(publication)) {
            throw new ConsistencyException("Publication and algorithm are not linked");
        }

        algorithm.removePublication(publication);
    }

    @Override
    public Page<ProblemType> findProblemTypes(UUID algoId, Pageable pageable) {
        if (algorithmRepository.existsAlgorithmById(algoId)) {
            throw new NoSuchElementException();
        }

        return problemTypeRepository.findProblemTypesByAlgorithmId(algoId, pageable);
    }

    @Override
    @Transactional
    public void addProblemTypeReference(UUID algoId, UUID problemTypeId) {
        Algorithm algorithm = findById(algoId);
        ProblemType problemType = problemTypeService.findById(problemTypeId);

        if (algorithm.getProblemTypes().contains(problemType)) {
            throw new ConsistencyException("Problem type and algorithm are already linked");
        }

        algorithm.addProblemType(problemType);
    }

    @Override
    @Transactional
    public void deleteProblemTypeReference(UUID algoId, UUID problemTypeId) {
        Algorithm algorithm = findById(algoId);
        ProblemType problemType = problemTypeService.findById(problemTypeId);

        if (!algorithm.getProblemTypes().contains(problemType)) {
            throw new ConsistencyException("Problem type and algorithm are not linked");
        }

        algorithm.removeProblemType(problemType);
    }

    @Override
    public Page<ApplicationArea> findApplicationAreas(UUID algoId, Pageable pageable) {
        if (algorithmRepository.existsAlgorithmById(algoId)) {
            throw new NoSuchElementException();
        }

        return applicationAreaRepository.findApplicationAreasByAlgorithmId(algoId, pageable);
    }

    @Override
    @Transactional
    public void addApplicationAreaReference(UUID algoId, UUID applicationAreaId) {
        Algorithm algorithm = findById(algoId);
        ApplicationArea applicationArea = applicationAreaService.findById(applicationAreaId);

        if (algorithm.getApplicationAreas().contains(applicationArea)) {
            throw new ConsistencyException("Application area and algorithm are already linked");
        }

        algorithm.addApplicationArea(applicationArea);
    }

    @Override
    @Transactional
    public void deleteApplicationAreaReference(UUID algoId, UUID applicationAreaId) {
        Algorithm algorithm = findById(algoId);
        ApplicationArea applicationArea = applicationAreaService.findById(applicationAreaId);

        if (!algorithm.getApplicationAreas().contains(applicationArea)) {
            throw new ConsistencyException("Application area and algorithm are not linked");
        }

        algorithm.removeApplicationArea(applicationArea);
    }

    @Override
    public Page<PatternRelation> findPatternRelations(UUID algoId, Pageable pageable) {
        if (algorithmRepository.existsAlgorithmById(algoId)) {
            throw new NoSuchElementException();
        }

        return patternRelationRepository.findByAlgorithmId(algoId, pageable);
    }

    @Override
    @Transactional
    public void addPatternRelationReference(UUID algoId, UUID patternRelationId) {
        Algorithm algorithm = findById(algoId);
        PatternRelation patternRelation = patternRelationService.findById(patternRelationId);

        if (algorithm.getRelatedPatterns().contains(patternRelation)) {
            throw new ConsistencyException("Pattern relation and algorithm are already linked");
        }

        algorithm.getRelatedPatterns().add(patternRelation);
    }

    @Override
    @Transactional
    public void deletePatternRelationReference(UUID algoId, UUID patternRelationId) {
        Algorithm algorithm = findById(algoId);
        PatternRelation patternRelation = patternRelationService.findById(patternRelationId);

        if (!algorithm.getRelatedPatterns().contains(patternRelation)) {
            throw new ConsistencyException("Pattern relation and algorithm are not linked");
        }

        algorithm.getRelatedPatterns().remove(patternRelation);
    }

    @Override
    public Page<AlgorithmRelation> findAlgorithmRelations(UUID algoId, Pageable pageable) {
        if (algorithmRepository.existsAlgorithmById(algoId)) {
            throw new NoSuchElementException();
        }

        return getAlgorithmRelations(algoId, pageable);
    }

    @Override
    public Page<ComputeResourceProperty> findComputeResourceProperties(UUID algoId, Pageable pageable) {
        if (algorithmRepository.existsAlgorithmById(algoId)) {
            throw new NoSuchElementException();
        }

        return computeResourcePropertyRepository.findAllByAlgorithm_Id(algoId, pageable);
    }

    @Override
    public ComputeResourceProperty createComputeResourceProperty(UUID algoId, ComputeResourceProperty computeResourceProperty) {
        Algorithm algorithm = findById(algoId);

        var createdProperty = computeResourcePropertyService.saveComputeResourceProperty(computeResourceProperty);
        algorithm.addComputeResourceProperty(createdProperty);

        return createdProperty;
    }

    @Override
    public void deleteComputeResourceProperty(UUID algoId, UUID computeResourcePropertyId) {
        Algorithm algorithm = findById(algoId);
        var computeResourceProperty = computeResourcePropertyService.findComputeResourcePropertyById(computeResourcePropertyId);

        if (!algorithm.getRequiredComputeResourceProperties().contains(computeResourceProperty)) {
            throw new ConsistencyException("Compute resource property to delete is not part of given algorithm");
        }

        computeResourcePropertyService.deleteComputeResourceProperty(computeResourcePropertyId);
    }

//    private AlgoRelationType getPersistedAlgoRelationType(AlgorithmRelation relation) {
//        if (algoRelationTypeRepository.existsById(relation.getAlgoRelationType().getId())) {
//            return algoRelationTypeService.findById(relation.getAlgoRelationType().getId());
//        } else {
//            return algoRelationTypeService.save(relation.getAlgoRelationType());
//        }
//    }
//
//    @Transactional
//    @Override
//    public AlgorithmRelation addOrUpdateAlgorithmRelation(UUID sourceAlgorithmId, AlgorithmRelation relation) {
//        // Read involved Algorithms from database
//        Algorithm sourceAlgorithm = findById(sourceAlgorithmId);
//        Algorithm targetAlgorithm = findById(relation.getTargetAlgorithm().getId());
//
//        if (relation.getAlgoRelationType().getId() == null) {
//            algoRelationTypeService.save(relation.getAlgoRelationType());
//        }
//
//        AlgoRelationType relationType = getPersistedAlgoRelationType(relation);
//
//        // Check if relation with those two algorithms and the relation type already
//        // exists
//        Optional<AlgorithmRelation> persistedRelationOpt = algorithmRelationRepository
//                .findBySourceAlgorithmIdAndTargetAlgorithmIdAndAlgoRelationTypeId(sourceAlgorithm.getId(),
//                        targetAlgorithm.getId(), relationType.getId());
//
//        // If relation between the two algorithms already exists, update it
//        if (persistedRelationOpt.isPresent()) {
//            AlgorithmRelation persistedRelation = persistedRelationOpt.get();
//            persistedRelation.setDescription(relation.getDescription());
//            // Return updated relation
//            return algorithmRelationRepository.save(persistedRelation);
//        }
//
//        // Set Relation Objects with referenced database objects
//        relation.setSourceAlgorithm(sourceAlgorithm);
//        relation.setTargetAlgorithm(targetAlgorithm);
//        relation.setAlgoRelationType(relationType);
//
//        sourceAlgorithm.addAlgorithmRelation(relation);
//
//        // Save updated Algorithm -> CASCADE will save Relation
//        this.algorithmRepository.save(sourceAlgorithm);
//        persistedRelationOpt = algorithmRelationRepository
//                .findBySourceAlgorithmIdAndTargetAlgorithmIdAndAlgoRelationTypeId(sourceAlgorithm.getId(),
//                        targetAlgorithm.getId(), relationType.getId());
//
//        return persistedRelationOpt.get();
//    }
//
//    @Override
//    public void deleteAlgorithmRelation(UUID algoId, UUID relationId) {
//        // Get involved Objects from database
//        Algorithm sourceAlgorithm = algorithmRepository.findById(algoId)
//                .orElseThrow(() -> new NoSuchElementException("Algorithm does not exist!"));
//        AlgorithmRelation relation = algorithmRelationRepository.findById(relationId)
//                .orElseThrow(() -> new NoSuchElementException("Relation does not exist!"));
//
//        Set<AlgorithmRelation> algorithmRelations = sourceAlgorithm.getAlgorithmRelations();
//        algorithmRelations.remove(relation);
//        algorithmRepository.save(sourceAlgorithm);
//    }

    private Page<AlgorithmRelation> getAlgorithmRelations(UUID algorithmId, Pageable pageable) {
        return algorithmRelationRepository.findBySourceAlgorithmIdOrTargetAlgorithmId(algorithmId, algorithmId, pageable);
    }
}
