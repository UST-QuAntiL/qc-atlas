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
package org.planqk.atlas.web.controller.mixin;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.ModelWithPublications;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.dtos.PublicationDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublicationMixin {
    private final PublicationService publicationService;

    public Publication getPublication(ModelWithPublications model, UUID publicationId) {
        final var publications = model.getPublications();
        // Only consider publications that are part of this model.
        // This also saves us one query.
        return publications.stream().filter(pub -> pub.getId().equals(publicationId))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    public void addPublication(ModelWithPublications model, PublicationDto dto) {
        var publication = publicationService.findById(dto.getId());
        if (!model.getPublications().contains(publication)) {
            var publications = model.getPublications();
            publications.add(publication);
            model.setPublications(publications);
        }
    }

    public void unlinkPublication(ModelWithPublications model, UUID publicationId) {
        var publications = model.getPublications();
        if (!publications.removeIf(publication -> publication.getId().equals(publicationId))) {
            throw new NoSuchElementException();
        }
        model.setPublications(publications);
    }
}
