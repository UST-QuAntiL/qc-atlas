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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import javax.transaction.Transactional;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.repository.ProblemTypeRepository;
import org.planqk.atlas.core.util.Constants;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ProblemTypeServiceImpl implements ProblemTypeService {

    private final ProblemTypeRepository problemTypeRepository;

    @Override
    @Transactional
    public ProblemType create(@NonNull ProblemType problemType) {
        return problemTypeRepository.save(problemType);
    }

    @Override
    public Page<ProblemType> findAll(@NonNull Pageable pageable, String search) {
        if (!Objects.isNull(search) && !search.isEmpty()) {
            return problemTypeRepository.findAll(search, pageable);
        }
        String propertyOfSort = null;
        Boolean orderOfSort = null;
        for (Sort.Order order : pageable.getSort()) {
            propertyOfSort = order.getProperty();
            orderOfSort = order.isAscending();
        }
        if (propertyOfSort != null && propertyOfSort.equals(Constants.PROPERTY_SORT)) {
            final List<ProblemType> sortedlistOfProblemTypes = getParentSortedProblemTypes();
            if (!orderOfSort)
                Collections.reverse(sortedlistOfProblemTypes);
            return convertListToPage(pageable, sortedlistOfProblemTypes);
        }
        return problemTypeRepository.findAll(pageable);
    }

    public List<ProblemType> getParentSortedProblemTypes() {
        final List<ProblemType> unSortedlistOfProblemTypes = problemTypeRepository.findAll();
        final Map<String, List<ProblemType>> parentProblemTypeMap =
                new TreeMap<String, List<ProblemType>>(
                        Comparator.nullsFirst(Comparator.naturalOrder()));
        for (ProblemType problemType : unSortedlistOfProblemTypes) {
            String name = null;
            if (getParent(problemType) != null) {
                name = getParent(problemType).getName();
            }
            if (parentProblemTypeMap.containsKey(name)) {
                parentProblemTypeMap.get(name).add(problemType);
            }
            else if (!parentProblemTypeMap.containsKey(name)) {
                parentProblemTypeMap.put(name, new ArrayList<>(
                        Collections.singletonList(problemType)));
            }
        }
        final List<ProblemType> sortedlistOfProblemTypes = new ArrayList<>();
        for (Map.Entry<String, List<ProblemType>> entry : parentProblemTypeMap.entrySet()) {
            sortedlistOfProblemTypes.addAll(entry.getValue());
        }
        return sortedlistOfProblemTypes;
    }

    public Page<ProblemType> convertListToPage(Pageable pageable, List<ProblemType> sortedlistOfProblemTypes) {
        final int start = (pageable.getPageNumber()) * pageable.getPageSize();
        final int end = (start + pageable.getPageSize()) >
                sortedlistOfProblemTypes.size() ? sortedlistOfProblemTypes.size() : (pageable.getPageSize() * (pageable.getPageNumber() + 1));
        return new PageImpl<ProblemType>(sortedlistOfProblemTypes.subList(start, end), pageable,
                sortedlistOfProblemTypes.size());
    }

    @Override
    public ProblemType findById(@NonNull UUID problemTypeId) {
        return ServiceUtils.findById(problemTypeId, ProblemType.class, problemTypeRepository);
    }

    @Override
    @Transactional
    public ProblemType update(@NonNull ProblemType problemType) {
        final ProblemType persistedProblemType = findById(problemType.getId());

        persistedProblemType.setName(problemType.getName());
        persistedProblemType.setParentProblemType(problemType.getParentProblemType());

        return create(persistedProblemType);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID problemTypeId) {
        final ProblemType problemType = findById(problemTypeId);

        if (problemType.getAlgorithms().size() > 0) {
            throw new EntityReferenceConstraintViolationException("Cannot delete ProblemType with ID \"" + problemTypeId +
                    "\". It is used by existing algorithms!");
        }

        removeReferences(problemType);

        problemTypeRepository.deleteById(problemTypeId);
    }

    private void removeReferences(@NonNull ProblemType problemType) {
        removeAsParentFromProblemTypes(problemType);
        // problemType.getAlgorithms().forEach(algorithm -> algorithm.removeProblemType(problemType));
    }

    private void removeAsParentFromProblemTypes(@NonNull ProblemType problemType) {
        final List<ProblemType> persistedProblemTypes = problemTypeRepository.findProblemTypesByParentProblemType(problemType.getId());
        for (ProblemType persistedProblemType : persistedProblemTypes) {
            persistedProblemType.setParentProblemType(null);
            problemTypeRepository.save(persistedProblemType);
        }
    }

    @Override
    public List<ProblemType> getParentList(@NonNull UUID problemTypeId) {
        final ProblemType requestedProblemType = findById(problemTypeId);

        final List<ProblemType> parentTree = new ArrayList<>();
        parentTree.add(requestedProblemType);

        ProblemType parentProblemType = getParent(requestedProblemType);
        while (parentProblemType != null) {
            parentTree.add(parentProblemType);
            parentProblemType = getParent(parentProblemType);
        }
        return parentTree;
    }

    private ProblemType getParent(@NonNull ProblemType child) {
        try {
            if (child.getParentProblemType() != null) {
                return findById(child.getParentProblemType());
            }
        } catch (NoSuchElementException ignored) {
        }
        return null;
    }
}
