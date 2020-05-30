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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
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

		return algorithmRepository.save(algorithm);
	}

	@Override
	public Algorithm update(UUID id, Algorithm algorithm) throws NotFoundException {
		Optional<Algorithm> persistedAlgOpt = findOptionalById(id);
		if (persistedAlgOpt.isEmpty()) {
			LOG.info("Trying to update non-existing algorithm.");
			throw new NotFoundException("Could not find algorithm to update.");
		}
		Algorithm persistedAlg = persistedAlgOpt.get();
		persistedAlg.setName(algorithm.getName());
		persistedAlg.setInputFormat(algorithm.getInputFormat());
		persistedAlg.setOutputFormat(algorithm.getOutputFormat());
		persistedAlg.setProblemTypes(algorithm.getProblemTypes());
		persistedAlg.setTags(algorithm.getTags());
		return algorithmRepository.save(persistedAlg);
	}

	@Override
	public void delete(UUID id) throws NotFoundException {
		Optional<Algorithm> algorithmOpt = findOptionalById(id);
		if (algorithmOpt.isEmpty()) {
			LOG.info("Trying to delete non-existing algorithm.");
			throw new NotFoundException("Could not find algorithm to delete.");
		}
		Optional<List<AlgorithmRelation>> linkedAsTargetRelations = algorithmRelationRepository.findByTargetAlgorithmId(id);
		if (linkedAsTargetRelations.isPresent()) {
			for (AlgorithmRelation relation : linkedAsTargetRelations.get()) {
				deleteAlgorithmRelation(relation.getSourceAlgorithm().getId(), relation.getId());
			}
		}
		algorithmRepository.deleteById(id);
	}

	@Override
	public Page<Algorithm> findAll(Pageable pageable) {
		return algorithmRepository.findAll(pageable);
	}

	@Override
	public Algorithm findById(UUID algoId) throws NotFoundException {
		Optional<Algorithm> algorithmOpt = findOptionalById(algoId);
		if (algorithmOpt.isEmpty()) {
			LOG.info("Could not find algorithm with id " + algoId + ".");
			throw new NotFoundException("Could not find algorithm with id " + algoId + ".");
		}
		return algorithmOpt.get();
	}

	@Override
	public Optional<Algorithm> findOptionalById(UUID algoId) {
	    return algorithmRepository.findById(algoId);
	}

	@Override
	public AlgorithmRelation addUpdateAlgorithmRelation(UUID sourceAlgorithm_id, AlgorithmRelation relation)
			throws NotFoundException {
		// Read involved Algorithms from database
		Optional<Algorithm> sourceAlgorithmOpt = findOptionalById(sourceAlgorithm_id);
		Optional<Algorithm> targetAlgorithmOpt = findOptionalById(relation.getTargetAlgorithm().getId());
		Optional<AlgoRelationType> relationTypeOpt = relationTypeService
				.findOptionalById(relation.getAlgoRelationType().getId());

		// If one of the algorithms does not exist
		if (sourceAlgorithmOpt.isEmpty()) {
			LOG.info("Trying to add algorithmRelation for non-existing source algorithm.");
			throw new NotFoundException("Could not add algorithmRelation to non-existing source algorithm.");
		} else if (targetAlgorithmOpt.isEmpty()) {
			LOG.info("Trying to add algorithmRelation for non-existing target algorithm.");
			throw new NotFoundException("Could not add algorithmRelation to non-existing target algorithm.");
		}

		// Get Algorithms
		Algorithm sourceAlgorithm = sourceAlgorithmOpt.get();
		Algorithm targetAlgorithm = targetAlgorithmOpt.get();
		AlgoRelationType relationType = relationTypeOpt.isEmpty() ? algoRelationTypeService.save(relation.getAlgoRelationType())
				: relationTypeOpt.get();

		// Check if relation with those two algorithms and the relation type already exists
		Optional<AlgorithmRelation> persistedRelationOpt = algorithmRelationRepository
				.findBySourceAlgorithmIdAndTargetAlgorithmIdAndAlgoRelationTypeId(
						sourceAlgorithm.getId(), targetAlgorithm.getId(), relationType.getId());

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
	public void deleteAlgorithmRelation(UUID algoId, UUID relationId) throws NotFoundException {
		Optional<Algorithm> optAlgorithm = algorithmRepository.findById(algoId);
		Optional<AlgorithmRelation> optRelation = algorithmRelationRepository.findById(relationId);

		if (optAlgorithm.isEmpty()) {
			LOG.info("Trying to delete algorithmRelation from non-existing source algorithm.");
			throw new NotFoundException("Could not delete algorithmRelation from non-existing source algorithm.");
		} else if (optRelation.isEmpty()) {
			LOG.info("Trying to delete non-existing algorithmRelation.");
			throw new NotFoundException("Could not delete non-existing algorithmRelation.");
		}

		// Get Objects from database
		Algorithm sourceAlgorithm = optAlgorithm.get();
		AlgorithmRelation relation = optRelation.get();

		Set<AlgorithmRelation> algorithmRelations = sourceAlgorithm.getAlgorithmRelations();
		algorithmRelations.remove(relation);
		algorithmRepository.save(sourceAlgorithm);
	}

	@Override
	public Set<AlgorithmRelation> getAlgorithmRelations(UUID sourceAlgorithm_id) throws NotFoundException {
		Optional<Set<AlgorithmRelation>> algorithmRelationsOpt =  algorithmRelationRepository.findBySourceAlgorithmId(sourceAlgorithm_id);
		if (algorithmRelationsOpt.isEmpty()) {
			LOG.info("Could not find any relations with source algorithm id " + sourceAlgorithm_id + ".");
			throw new NotFoundException("Could not find any relations with source altorithm id " + sourceAlgorithm_id + ".");
		}
		return algorithmRelationsOpt.get();
	}
}
