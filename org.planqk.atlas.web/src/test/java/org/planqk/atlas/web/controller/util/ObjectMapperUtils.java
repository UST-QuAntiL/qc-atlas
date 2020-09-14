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

package org.planqk.atlas.web.controller.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.json.JSONObject;

public class ObjectMapperUtils {

    public static final String EMBEDDED_RESOURCES_JSON_KEY = "_embedded";
    public static final String PAGED_MODEL_PAGE_INFO_JSON_KEY = "page";

    /**
     * Returns all Unmarshalled objects of a Paged Model Response
     *
     * @param response  the application/json+hal response body returned from the request
     * @param key       the key for the resources within the '_embedded' child object
     * @param mapper    The object mapper used to perform the mapping operations. Must not be null.
     * @param className the Class object of the elements to unmarshall e.g. AlgorithmDto.class. Must be a child o <T>
     * @param <T>       Generic type that the unmarshalled objects will be returned as
     * @return an (array) list of all objects in the response under the given key. The ordering shuld remain identical
     * to before
     * @throws Exception generic errors may occur since this method does not handle errors . It is only intended to be
     *                   used in tests.
     */
    public static <T> List<T> mapResponseToList(String response, String key, Class<? extends T> className,
                                                @NonNull ObjectMapper mapper) throws Exception {
        var rootObject = new JSONObject(response);
        if (!rootObject.has(EMBEDDED_RESOURCES_JSON_KEY)) {
            return Collections.emptyList();
        }
        var objects = rootObject.getJSONObject(EMBEDDED_RESOURCES_JSON_KEY).getJSONArray(key);

        var contents = new ArrayList<T>();

        for (int i = 0; i < objects.length(); i++) {
            JSONObject elementJson = objects.getJSONObject(i);

            T element = mapper.readValue(elementJson.toString(), className);
            contents.add(element);
        }

        return contents;
    }

    /**
     * Returns all Unmarshalled objects of a Paged Model Response
     *
     * @param response  the application/json+hal response body returned from the request
     * @param key       the key for the resources within the '_embedded' child object
     * @param className the Class object of the elements to unmarshall e.g. AlgorithmDto.class. Must be a child o <T>
     * @param <T>       Generic type that the unmarshalled objects will be returned as
     * @return an (array) list of all objects in the response under the given key. The ordering shuld remain identical
     * to before
     * @throws Exception generic errors may occur since this method does not handle errors . It is only intended to be
     *                   used in tests.
     */
    public static <T> List<T> mapResponseToList(String response, String key, Class<? extends T> className)
            throws Exception {
        return mapResponseToList(response, key, className, newTestMapper());
    }

    /**
     * Returns the page information for the given PagedModel response
     *
     * @param response the json+hal response as string
     * @return the Unmarshalled page information
     * @throws Exception generic errors may occur since this method does not handle errors . It is only intended to be *
     *                   used in tests.
     */
    public static PageInfo getPageInfo(String response) throws Exception {
        var pageContent = new JSONObject(response).getJSONObject(PAGED_MODEL_PAGE_INFO_JSON_KEY).toString();
        return newTestMapper().readValue(pageContent, PageInfo.class);
    }

    public static ObjectMapper newTestMapper() {
        var mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        return mapper;
    }

    @Data
    @NoArgsConstructor
    public static class PageInfo {
        private int number;
        private int size;
        private int totalPages;
        private int totalElements;
    }
}
