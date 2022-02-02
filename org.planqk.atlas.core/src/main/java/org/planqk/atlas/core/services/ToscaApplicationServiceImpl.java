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

import java.util.InputMismatchException;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.planqk.atlas.core.model.ToscaApplication;
import org.planqk.atlas.core.repository.ToscaApplicationRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class ToscaApplicationServiceImpl implements ToscaApplicationService {

    private final ToscaApplicationRepository toscaApplicationRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    private final String serverUrl = "http://localhost:8091";

    @Override
    @Transactional
    public ToscaApplication create(ToscaApplication toscaApplication) {
        return this.toscaApplicationRepository.save(toscaApplication);
    }

    @Override
    public ToscaApplication createFromFile(MultipartFile file, String name) {
        log.info("Got file " + file.getOriginalFilename() + " of size " + file.getSize());
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        body.add("name", name);
        final HttpEntity<MultiValueMap<String, Object>> postRequestEntity = new HttpEntity<>(body, headers);

        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<String> response = restTemplate
                .postForEntity(serverUrl + "/winery/", postRequestEntity, String.class);
        if (response.getStatusCode().equals(HttpStatus.CREATED) && response.getHeaders().getLocation() != null) {
            final String path = response.getHeaders().getLocation().getPath();
            log.info(path);
            final String jsonResponse = restTemplate.getForObject(serverUrl + path, String.class);
            try {
                final JsonNode node = this.mapper.readTree(jsonResponse).get("serviceTemplateOrNodeTypeOrNodeTypeImplementation");
                if (node.size() != 1) {
                    throw new InputMismatchException();
                }
                final JsonNode firstElement = node.get(0);
                final ToscaApplication toscaApplication = new ToscaApplication();
                toscaApplication.setToscaID(firstElement.get("id").asText());
                toscaApplication.setToscaNamespace(firstElement.get("targetNamespace").asText());
                toscaApplication.setToscaName(firstElement.get("name").asText());
                toscaApplication.setName(name);
                toscaApplication.setWineryLocation(path);
                return this.toscaApplicationRepository.save(toscaApplication);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return new ToscaApplication();
    }

    @Override
    public Page<ToscaApplication> findAll(@NonNull Pageable pageable) {
        return this.toscaApplicationRepository.findAll(pageable);
    }

    @Override
    public ToscaApplication findById(@NonNull UUID toscaApplicationId) {
        return ServiceUtils.findById(toscaApplicationId, ToscaApplication.class, this.toscaApplicationRepository);
    }

    @Override
    public ToscaApplication update(@NonNull ToscaApplication toscaApplication) {
        final ToscaApplication persistedToscaApplication = findById(toscaApplication.getId());

        persistedToscaApplication.setName(toscaApplication.getName());

        return this.toscaApplicationRepository.save(persistedToscaApplication);
    }

    @Override
    public void delete(@NonNull UUID toscaApplicationId) {
        final ToscaApplication persistedToscaApplication = findById(toscaApplicationId);
        final String path = persistedToscaApplication.getWineryLocation();
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(serverUrl + path);
        this.toscaApplicationRepository.deleteById(toscaApplicationId);
    }
}
