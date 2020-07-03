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

package org.planqk.atlas.web.utils;

import org.planqk.atlas.web.Constants;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility class for the REST API functionality
 */
public class RestUtils {

    /**
     * Return a (default) pageable from the provided Requestparams for an endpoint that can be used with pagination.
     *
     * @param page
     * @param size
     * @return pageable
     */
    public static Pageable getPageableFromRequestParams(Integer page, Integer size) {
        if (page == null || size == null)
            return getAllPageable();

        return PageRequest.of(page, size);
    }

    /**
     * Return a (default) pageable from the provided Requestparams for an endpoint that can be used with pagination.
     *
     * @param page
     * @param size
     * @param sortType
     * @param sortVariable
     * @return pageable
     */
    public static Pageable getPageableFromRequestParams(Integer page, Integer size, String sortType, String sortVariable) {
        if (sortVariable == null || sortVariable.isEmpty() || sortType == null || sortType.isEmpty())
            return getPageableFromRequestParams(page, size);

        // Add sorting to Pageable object if necessary information available
        if (sortType.equals("asc")) {
            return PageRequest.of(page, size, Sort.by(sortVariable).ascending());
        }
        if (sortType.equals("desc")) {
            return PageRequest.of(page, size, Sort.by(sortVariable).descending());
        }

        return PageRequest.of(page, size);
    }

    /**
     * Returns unpaged Paginationparams
     */
    public static Pageable getAllPageable() {
        return Pageable.unpaged();
    }

    /**
     * Returns default Paginationparams
     */
    public static Pageable getDefaultPageable() {
        return PageRequest.of(Constants.DEFAULT_PAGE_NUMBER, Constants.DEFAULT_PAGE_SIZE);
    }
}
