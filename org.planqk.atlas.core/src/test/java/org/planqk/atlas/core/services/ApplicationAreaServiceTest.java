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
import java.util.UUID;

import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.repository.ApplicationAreaRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationAreaServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ApplicationAreaService applicationAreaService;

    @Autowired
    private ApplicationAreaRepository applicationAreaRepository;

    @Test
    void getFullApplicationArea() {
        var createdArea = getFullApplicationArea("my area");

        var storedArea = applicationAreaRepository.findById(createdArea.getId()).get();

        assertThat(storedArea.getId()).isEqualTo(createdArea.getId());
        assertThat(storedArea.getName()).isEqualTo(createdArea.getName());
    }

    @Test
    void findApplicationAreaById_ElementFound() {
        var createdArea = getFullApplicationArea("my area");

        var storedArea = applicationAreaService.findById(createdArea.getId());

        assertThat(storedArea.getId()).isEqualTo(createdArea.getId());
        assertThat(storedArea.getName()).isEqualTo(createdArea.getName());
    }

    @Test
    void findApplicationAreaById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> applicationAreaService.findById(UUID.randomUUID()));
    }

    @Test
    void findApplicationAreaById_IdNull() {
        assertThrows(NullPointerException.class, () -> applicationAreaService.findById(null));
    }

    @Test
    void updateApplicationArea_ElementFound() {
        var area = getFullApplicationArea("test");

        area.setName("My Test");

        applicationAreaService.update(area);

        assertThat(applicationAreaRepository.findById(area.getId()).get().getName()).isEqualTo(area.getName());
    }

    @Test
    void updateApplicationArea_ElementNotFound() {
        ApplicationArea area = new ApplicationArea();
        area.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () -> applicationAreaService.update(area));
    }

    @Test
    void updateApplicationArea_IdNull() {
        ApplicationArea area = new ApplicationArea();
        assertThrows(NullPointerException.class, () -> applicationAreaService.update(area));
    }

    @Test
    void deleteApplicationArea_ElementFound() {
        var area = getFullApplicationArea("TEST");

        applicationAreaService.delete(area.getId());

        assertThat(applicationAreaRepository.findAll()).isEmpty();
    }

    @Test
    void deleteApplicationArea_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> applicationAreaService.delete(UUID.randomUUID()));
    }

    @Test
    void deleteApplicationArea_IdNull() {
        assertThrows(NullPointerException.class, () -> applicationAreaService.delete(null));
    }

    @Test
    void findAllApplicationAreas_MultipleElements() {
        var areas = new HashSet<ApplicationArea>();
        for (int i = 0; i < 10; i++) {
            areas.add(getFullApplicationArea("Area " + i));
        }

        var elements = applicationAreaService.findAll(Pageable.unpaged(), "");
        assertThat(elements.getTotalElements()).isEqualTo(10);
        assertThat(elements.getContent().stream().filter(areas::contains).count()).isEqualTo(10);
    }

    @Test
    void findAllApplicationArea_Search() {
        for (int i = 0; i < 10; i++) {
            getFullApplicationArea("Area " + i);
        }

        var elements = applicationAreaService.findAll(Pageable.unpaged(), "3");
        assertThat(elements.getTotalElements()).isEqualTo(1);
        assertThat(elements.getContent().get(0).getName()).isEqualTo("Area 3");
    }

    private ApplicationArea getFullApplicationArea(String name) {
        var applicationArea = new ApplicationArea();
        applicationArea.setName(name);
        return applicationAreaService.create(applicationArea);
    }
}
