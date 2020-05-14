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

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.repository.AlgorithmRepository;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class AlgorithmServiceImpl implements AlgorithmService {

	private AlgorithmRepository algorithmRepository;

	private TagService tagService;
	private ProblemTypeService problemTypeService;

	@Override
	public Algorithm save(Algorithm algorithm) {

		// Persist Tags separately
		Set<Tag> tags = algorithm.getTags();
		for (Tag algorithmTag : algorithm.getTags()) {
			Optional<Tag> storedTagOptional = tagService.getTagById(algorithmTag.getId());
			if (!storedTagOptional.isPresent()) {
				tags.remove(algorithmTag);
				tags.add(tagService.save(algorithmTag));
			}
		}
		// Persist ProblemTypes separately
		algorithm.setProblemTypes(problemTypeService.createOrUpdateAll(algorithm.getProblemTypes()));

		algorithm.setTags(tags);

		return algorithmRepository.save(algorithm);
	}

	@Override
	public Algorithm update(Long id, Algorithm algorithm) {
		Optional<Algorithm> persistedAlgOpt = findById(id);
		if (persistedAlgOpt.isPresent()) {
			Algorithm persistedAlg = persistedAlgOpt.get();
			persistedAlg.setName(algorithm.getName());
			persistedAlg.setInputFormat(algorithm.getInputFormat());
			persistedAlg.setOutputFormat(algorithm.getOutputFormat());
			persistedAlg.setProblemTypes(algorithm.getProblemTypes());
			persistedAlg.setTags(algorithm.getTags());
			return algorithmRepository.save(persistedAlg);
		}
		// TODO: Impl exception handling
		return null;
	}

	@Override
	public void delete(Long id) {
		algorithmRepository.deleteById(id);
	}

	@Override
	public Page<Algorithm> findAll(Pageable pageable) {
		return algorithmRepository.findAll(pageable);
	}

	@Override
	public Optional<Algorithm> findById(Long algoId) {
		return Objects.isNull(algoId) ? Optional.empty() : algorithmRepository.findById(algoId);
	}
}
