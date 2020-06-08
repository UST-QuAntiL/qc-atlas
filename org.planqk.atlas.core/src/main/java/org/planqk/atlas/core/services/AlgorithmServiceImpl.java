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

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class AlgorithmServiceImpl implements AlgorithmService {

    private final static Logger LOG = LoggerFactory.getLogger(AlgorithmServiceImpl.class);

    private AlgorithmRepository algorithmRepository;
    private AlgorithmRelationRepository algorithmRelationRepository;
    private AlgoRelationTypeService relationTypeService;

    private TagService tagService;
    private ProblemTypeService problemTypeService;
    private AlgoRelationTypeService algoRelationTypeService;

    @Override
    public Algorithm save(Algorithm algorithm) {
        // Persist Tags separately
        algorithm.setTags(tagService.createOrUpdateAll(algorithm.getTags()));
        // Persist ProblemTypes separately
        algorithm.setProblemTypes(problemTypeService.createOrUpdateAll(algorithm.getProblemTypes()));

        algorithm = processAlgorithmRelations(algorithm);

        return algorithmRepository.save(algorithm);
    }

    private Algorithm processAlgorithmRelations(Algorithm algorithm) {
        // save relations to append after persisting algorithm
        Set<AlgorithmRelation> relations = algorithm.getAlgorithmRelations();

        // remove algorithm relations if source algorithm does not yet exist
        if (!algorithmAlreadyPersisted(algorithm.getId())) {
            // remove invalid relations
            algorithm.setAlgorithmRelations(new HashSet<>());
            // persist algorithm
            algorithm = algorithmRepository.save(algorithm);

            for (AlgorithmRelation relation : relations) {
                // set correct source algorithm
                relation.setSourceAlgorithm(algorithm);
                if (Optional.ofNullable(relation.getTargetAlgorithm()).isPresent()
                        && validateTargetAlgorithmAndRelationType(algorithm, relation)) {
                    algorithm.addAlgorithmRelation(relation);
                }
            }
        } else {
            for (AlgorithmRelation relation : relations) {
                if (algorithm.getId() != relation.getSourceAlgorithm().getId()) {
                    relation.setSourceAlgorithm(algorithm);
                } else {
                    // TODO decide if varying IDs cause error or get corrected without further
                    // notice
                }
                if (Optional.ofNullable(relation.getTargetAlgorithm()).isPresent()
                        && validateTargetAlgorithmAndRelationType(algorithm, relation)) {
                    algorithm.addAlgorithmRelation(relation);
                }
            }
        }
        return algorithm;
    }

    private boolean algorithmAlreadyPersisted(UUID algorithmId) {
        return findOptionalById(algorithmId).isPresent();
    }

    private boolean validateTargetAlgorithmAndRelationType(Algorithm algorithm, AlgorithmRelation relation) {
        if (findOptionalById(relation.getTargetAlgorithm().getId()).isPresent()) {

            // TODO decide if missing AlgoRelationType causes exception or if
            // AlgoRelationType gets created on the fly
            AlgoRelationType relationType = algoRelationTypeService
                    .findOptionalById(relation.getAlgoRelationType().getId()).isPresent()
                            ? relation.getAlgoRelationType()
                            : algoRelationTypeService.save(relation.getAlgoRelationType());
            relation.setAlgoRelationType(relationType);
            return true;
        } else {
            // TODO decide if invalid relation due to missing target algorithm causes
            // exception or if relation gets dropped
            // throw new NoSuchElementException("Target algorithm '" +
            // relation.getTargetAlgorithm().getId().toString() + "' does not exist");
        }
        return false;
    }

    @Override
    public Algorithm update(UUID id, Algorithm algorithm) {
    	LOG.info("Trying to update algorithm");
        Algorithm persistedAlg = algorithmRepository.findById(id).orElseThrow(NoSuchElementException::new);

        persistedAlg.setName(algorithm.getName());
        persistedAlg.setAcronym(algorithm.getAcronym());
        // persistedAlg.setPublications(algorithm.getPublications());
        persistedAlg.setIntent(algorithm.getIntent());
        persistedAlg.setProblem(algorithm.getProblem());
        // persistedAlg.setAlgorithmRelations(algorithm.getAlgorithmRelations());
        persistedAlg.setInputFormat(algorithm.getInputFormat());
        persistedAlg.setAlgoParameter(algorithm.getAlgoParameter());
        persistedAlg.setOutputFormat(algorithm.getOutputFormat());
        persistedAlg.setSketch(algorithm.getSketch());
        persistedAlg.setSolution(algorithm.getSolution());
        persistedAlg.setAssumptions(algorithm.getAssumptions());
        persistedAlg.setComputationModel(algorithm.getComputationModel());
        // persistedAlg.setRelatedPatterns(algorithm.getRelatedPatterns());
        persistedAlg.setProblemTypes(algorithm.getProblemTypes());
        persistedAlg.setApplicationAreas(algorithm.getApplicationAreas());
        persistedAlg.setTags(algorithm.getTags());

        // If QuantumAlgorithm adjust Quantum fields
        if (algorithm instanceof QuantumAlgorithm) {
        	QuantumAlgorithm quantumAlgorithm = (QuantumAlgorithm) algorithm;
        	QuantumAlgorithm persistedQuantumAlg = (QuantumAlgorithm) persistedAlg;
        	
        	persistedQuantumAlg.setNisqReady(quantumAlgorithm.isNisqReady());
        	persistedQuantumAlg.setQuantumComputationModel(quantumAlgorithm.getQuantumComputationModel());
        	// persistedQuantumAlg.setRequiredQuantumResources(quantumAlgorithm.getRequiredQuantumResources());
        	persistedQuantumAlg.setSpeedUp(quantumAlgorithm.getSpeedUp());
        	
        	return algorithmRepository.save(persistedQuantumAlg);
        } else {
        	// Else if ClassicAlgorithm no more fields to adjust
        	return algorithmRepository.save(persistedAlg);
        }
    }

    @Override
    public void delete(UUID id) {
        Set<AlgorithmRelation> linkedAsTargetRelations = algorithmRelationRepository.findByTargetAlgorithmId(id);
        for (AlgorithmRelation relation : linkedAsTargetRelations) {
            deleteAlgorithmRelation(relation.getSourceAlgorithm().getId(), relation.getId());
        }

        algorithmRepository.deleteById(id);
    }

    @Override
    public Page<Algorithm> findAll(Pageable pageable) {
        return algorithmRepository.findAll(pageable);
    }

    @Override
    public Algorithm findById(UUID algoId) {
        return findOptionalById(algoId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<Algorithm> findOptionalById(UUID algoId) {
        return algorithmRepository.findById(algoId);
    }

    @Override
    public AlgorithmRelation addOrUpdateAlgorithmRelation(UUID sourceAlgorithmId, AlgorithmRelation relation) {
        // Read involved Algorithms from database
        Algorithm sourceAlgorithm = findById(sourceAlgorithmId);
        Algorithm targetAlgorithm = findById(relation.getTargetAlgorithm().getId());
        Optional<AlgoRelationType> relationTypeOpt = relationTypeService
                .findOptionalById(relation.getAlgoRelationType().getId());

        // Create relation type if not exists
        AlgoRelationType relationType = relationTypeOpt.isEmpty()
                ? algoRelationTypeService.save(relation.getAlgoRelationType())
                : relationTypeOpt.get();

        // Check if relation with those two algorithms and the relation type already
        // exists
        Optional<AlgorithmRelation> persistedRelationOpt = algorithmRelationRepository
                .findBySourceAlgorithmIdAndTargetAlgorithmIdAndAlgoRelationTypeId(sourceAlgorithm.getId(),
                        targetAlgorithm.getId(), relationType.getId());

        // If relation between the two algorithms already exists, update it
        if (persistedRelationOpt.isPresent()) {
            AlgorithmRelation persistedRelation = persistedRelationOpt.get();
            persistedRelation.setDescription(relation.getDescription());
            // Return updated relation
            return save(persistedRelation);
        }

        // Set Relation Objects with referenced database objects
        relation.setId(null);
        relation.setSourceAlgorithm(sourceAlgorithm);
        relation.setTargetAlgorithm(targetAlgorithm);
        relation.setAlgoRelationType(relationType);

        sourceAlgorithm.addAlgorithmRelation(relation);
        // Save updated Algorithm -> CASCADE will save Relation
        sourceAlgorithm = algorithmRepository.save(sourceAlgorithm);

        return relation;
    }

    private AlgorithmRelation save(AlgorithmRelation current) {
        return algorithmRelationRepository.save(current);
    }

    @Override
    public void deleteAlgorithmRelation(UUID algoId, UUID relationId) {
        // Get involved Objects from database
        Algorithm sourceAlgorithm = algorithmRepository.findById(algoId)
                .orElseThrow(() -> new NoSuchElementException("Algorithm does not exist!"));
        AlgorithmRelation relation = algorithmRelationRepository.findById(relationId)
                .orElseThrow(() -> new NoSuchElementException("Relation does not exist!"));

        Set<AlgorithmRelation> algorithmRelations = sourceAlgorithm.getAlgorithmRelations();
        algorithmRelations.remove(relation);
        algorithmRepository.save(sourceAlgorithm);
    }

    @Override
    public Set<AlgorithmRelation> getAlgorithmRelations(UUID sourceAlgorithmId) {
        return algorithmRelationRepository.findBySourceAlgorithmId(sourceAlgorithmId);
    }
}
