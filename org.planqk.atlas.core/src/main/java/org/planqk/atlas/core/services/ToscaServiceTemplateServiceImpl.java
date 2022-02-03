/*******************************************************************************
 * Copyright (c) 2020-2022 the qc-atlas contributors.
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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@AllArgsConstructor
public class ToscaServiceTemplateServiceImpl implements ToscaServiceTemplateService {

    private final String serverUrl = "http://localhost:8091";

    private <T> T getToWinery(String route, Class<T> responseType) {
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(serverUrl + route, responseType);
    }

    private String getToWinery(String route) {
        return getToWinery(route, String.class);
    }

    @Override
    public String getAll() {
        return getToWinery("/winery/servicetemplates");
    }

    @Override
    public String get(String namespace, String name) {
        return getToWinery(String.format("/winery/servicetemplates/%s/%s", namespace, name));
    }

    @Override
    public String getSelfServicePortal(String namespace, String name) {
        return getToWinery(String.format("/winery/servicetemplates/%s/%s/selfserviceportal", namespace, name));
    }

    @Override
    public byte[] getSelfServicePortalIcon(String namespace, String name) {
        return getToWinery(String.format("/winery/servicetemplates/%s/%s/selfserviceportal/icon.jpg", namespace, name), byte[].class);
    }

    @Override
    public byte[] getSelfServicePortalImage(String namespace, String name) {
        return getToWinery(String.format("/winery/servicetemplates/%s/%s/selfserviceportal/image.jpg", namespace, name), byte[].class);
    }
}
