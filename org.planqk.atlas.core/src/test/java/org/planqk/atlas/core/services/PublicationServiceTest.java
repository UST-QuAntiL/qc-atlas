/********************************************************************************
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

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.PublicationRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class PublicationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublicationRepository publicationRepository;
    @Autowired
    private AlgorithmRepository algorithmRepository;

    @Test
    void testAddPublication() {

    }

    @Test
    void testUpdatePublication_ElementNotFound() {

    }

    @Test
    void testUpdatePublication_ElementFound() {

    }

    @Test
    void testFindPublicationById_ElementNotFound() {

    }

    @Test
    void testFindPublicationById_ElementFound() {

    }

    @Test
    void testFindPublicationAlgorithms() {

    }

    @Test
    void testDeletePublication() {

    }

    @Test
    void testDeletePublications() {

    }

}
