/*******************************************************************************
 * Copyright (c) 2022 the qc-atlas contributors.
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

package org.planqk.atlas.core;

import java.util.InputMismatchException;

import org.planqk.atlas.core.model.ToscaApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WineryService {

    // API Endpoints
    private final String baseAPIEndpoint;

    private final ObjectMapper mapper = new ObjectMapper();

    public WineryService(
            @Value("${org.planqk.atlas.winery.protocol}") String protocol,
            @Value("${org.planqk.atlas.winery.hostname}") String hostname,
            @Value("${org.planqk.atlas.winery.port}") String port
    ) {
        if ("".equals(protocol)) {
            this.baseAPIEndpoint = String.format("http://%s:%s/", hostname, port);
        } else {
            this.baseAPIEndpoint = String.format("%s://%s:%s/", protocol, hostname, port);
        }
    }

    public String get(@NonNull String route) {
        return this.get(route, String.class);
    }

    public <T> T get(String route, Class<T> responseType) {

        final RestTemplate restTemplate = new RestTemplate();
        try {
            final ResponseEntity<T> response = restTemplate.getForEntity(this.baseAPIEndpoint + route, responseType);
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                throw new ResponseStatusException(response.getStatusCode());
            }
            return response.getBody();
        } catch (HttpClientErrorException.NotFound notFound) {
            throw new ResponseStatusException(notFound.getStatusCode());
        }
    }

    public ToscaApplication uploadCsar(@NonNull Resource file, String name) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);
        body.add("name", name);
        final HttpEntity<MultiValueMap<String, Object>> postRequestEntity = new HttpEntity<>(body, headers);

        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<String> response = restTemplate
                .postForEntity(this.baseAPIEndpoint + "/winery/", postRequestEntity, String.class);
        if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
            throw new ResponseStatusException(response.getStatusCode());
        }
        if (response.getHeaders().getLocation() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        final String path = response.getHeaders().getLocation().getPath();
        log.info(path);
        final String jsonResponse = restTemplate.getForObject(this.baseAPIEndpoint + path, String.class);
        final JsonNode node;
        try {
            node = this.mapper.readTree(jsonResponse).get("serviceTemplateOrNodeTypeOrNodeTypeImplementation");
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
            return toscaApplication;
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void delete(@NonNull ToscaApplication toscaApplication) {
        final String path = toscaApplication.getWineryLocation();
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(this.baseAPIEndpoint + path);
    }
}

