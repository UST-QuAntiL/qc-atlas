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

import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class AlgorithmRelationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private AlgorithmRelationTypeService algorithmRelationTypeService;
    @Autowired
    private AlgorithmRelationService algorithmRelationService;
    @Autowired
    private AlgorithmService algorithmService;

    @Test
    void createAlgorithmRelation() {

    }

    @Test
    void createAlgorithmRelation_TypeNotFound() {

    }
    
    @Test
    void createAlgorithmRelation_AlgorithmNotFound() {

    }

    @Test
    void findAlgorithmRelationById_ElementFound() {

    }

    @Test
    void findAlgorithmRelationById_ElementNotFound() {

    }

    @Test
    void updateAlgorithmRelation_ElementFound() {

    }

    @Test
    void updateAlgorithmRelation_ElementNotFound() {

    }

    @Test
    void deleteAlgorithmRelation_ElementFound() {

    }

    @Test
    void deleteAlgorithmRelation_ElementNotFound() {

    }
}
