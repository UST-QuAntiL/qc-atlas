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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
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
    private PublicationService publicationService;

    @Override
    public Algorithm save(Algorithm algorithm) {
        // Persist Tags separately
        algorithm.setTags(tagService.createOrUpdateAll(algorithm.getTags()));
        // Persist ProblemTypes separately
        algorithm.setProblemTypes(problemTypeService.createOrUpdateAll(algorithm.getProblemTypes()));
        // Persist Publications separately
        algorithm.setPublications(publicationService.createOrUpdateAll(algorithm.getPublications()));
        
        return algorithmRepository.save(algorithm);
    }

    @Override
    public Algorithm update(UUID id, Algorithm algorithm) {
        LOG.info("Trying to update algorithm");
        Algorithm persistedAlg = algorithmRepository.findById(id).orElseThrow(NoSuchElementException::new);

        persistedAlg.setName(algorithm.getName());
        persistedAlg.setInputFormat(algorithm.getInputFormat());
        persistedAlg.setOutputFormat(algorithm.getOutputFormat());
        persistedAlg.setProblemTypes(algorithm.getProblemTypes());
        persistedAlg.setTags(algorithm.getTags());
        return algorithmRepository.save(persistedAlg);
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
        sourceAlgorithm = save(sourceAlgorithm);

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
