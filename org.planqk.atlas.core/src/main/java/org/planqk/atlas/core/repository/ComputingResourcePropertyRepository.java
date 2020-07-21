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

package org.planqk.atlas.core.repository;

import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.ComputingResourceProperty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ComputingResourcePropertyRepository extends JpaRepository<ComputingResourceProperty, UUID> {

    Set<ComputingResourceProperty> findAllByAlgorithm_Id(UUID algoId);

    Page<ComputingResourceProperty> findAllByAlgorithm_Id(UUID algoId, Pageable p);

    Set<ComputingResourceProperty> findAllByImplementation_Id(UUID backendId);

    Page<ComputingResourceProperty> findAllByImplementation_Id(UUID backendId, Pageable p);

    Set<ComputingResourceProperty> findAllByComputeResource_Id(UUID backendId);

    Page<ComputingResourceProperty> findAllByComputeResource_Id(UUID backendId, Pageable p);
}
