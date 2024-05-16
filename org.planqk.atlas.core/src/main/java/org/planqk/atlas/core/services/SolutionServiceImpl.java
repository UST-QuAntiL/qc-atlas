/*******************************************************************************
 * Copyright (c) 2020-2021 the qc-atlas contributors.
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

import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.Solution;
import org.planqk.atlas.core.repository.SolutionRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SolutionServiceImpl implements SolutionService {

    private final SolutionRepository solutionRepository;

    private final FileService fileService;

    @Override
    @Transactional
    public Solution create(Solution solution) {
        return solutionRepository.save(solution);
    }

    @Override
    public Page<Solution> findAll(@NonNull Pageable pageable) {
        return solutionRepository.findAll(pageable);
    }

    @Override
    public Solution findById(@NonNull UUID solutionId) {
        return ServiceUtils.findById(solutionId, Solution.class, solutionRepository);
    }

    @Override
    @Transactional
    public Solution update(@NonNull Solution solution) {
        final Solution persistedAlgorithm = findById(solution.getId());
        persistedAlgorithm.setPatternId(solution.getPatternId());

        return solutionRepository.save(persistedAlgorithm);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID solutionId) {
        final Solution algorithm = findById(solutionId);

        if (algorithm.getFile() != null) {
            fileService.delete(algorithm.getFile().getId());
        }

        solutionRepository.deleteById(solutionId);
    }

    @Override
    public File addFileToSolution(UUID solutionId, MultipartFile multipartFile) {
        final Solution solution =
                ServiceUtils.findById(solutionId, Solution.class, solutionRepository);
        final File file = fileService.create(multipartFile);
        solution.setFile(file);
        solutionRepository.save(solution);

        return file;
    }
}
