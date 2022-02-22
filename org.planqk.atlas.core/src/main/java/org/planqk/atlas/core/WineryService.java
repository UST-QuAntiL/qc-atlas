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

import java.net.URI;
import java.util.InputMismatchException;

import org.apache.http.client.utils.URIBuilder;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WineryService {

    // API Endpoints
    private final URIBuilder baseAPIEndpoint;

    private final ObjectMapper mapper = new ObjectMapper();

    private final RestTemplate restTemplate;

    public WineryService(
            @Value("${org.planqk.atlas.winery.protocol}") String protocol,
            @Value("${org.planqk.atlas.winery.hostname}") String hostname,
            @Value("${org.planqk.atlas.winery.port}") int port,
            RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseAPIEndpoint = new URIBuilder();
        this.baseAPIEndpoint.setHost(hostname).setPort(port);
        if ("".equals(protocol)) {
            this.baseAPIEndpoint.setScheme("http");
        } else {
            this.baseAPIEndpoint.setScheme(protocol);
        }
    }

    public String get(@NonNull String route) {
        return this.get(route, String.class);
    }

    @SneakyThrows
    public <T> T get(String route, Class<T> responseType) {
        try {
            final ResponseEntity<T> response = restTemplate.getForEntity(this.baseAPIEndpoint.setPath(route).build(), responseType);
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                throw new ResponseStatusException(response.getStatusCode());
            }
            return response.getBody();
        } catch (HttpClientErrorException.NotFound notFound) {
            throw new ResponseStatusException(notFound.getStatusCode());
        }
    }

    @SneakyThrows
    public ToscaApplication uploadCsar(@NonNull Resource file, String name) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);
        body.add("name", name);
        final HttpEntity<MultiValueMap<String, Object>> postRequestEntity = new HttpEntity<>(body, headers);

        final ResponseEntity<String> response = this.restTemplate
                .postForEntity(this.baseAPIEndpoint.setPath("/winery/").build(), postRequestEntity, String.class);
        if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
            throw new ResponseStatusException(response.getStatusCode());
        }
        final URI location = response.getHeaders().getLocation();
        if (location == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        final String jsonResponse = this.restTemplate.getForObject(baseAPIEndpoint.setPath(location.getPath()).build(), String.class);
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
            toscaApplication.setWineryLocation(location.getPath());
            return toscaApplication;
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SneakyThrows
    public void delete(@NonNull ToscaApplication toscaApplication) {
        final String path = toscaApplication.getWineryLocation();
        this.restTemplate.delete(this.baseAPIEndpoint.setPath(path).build());
    }


}

