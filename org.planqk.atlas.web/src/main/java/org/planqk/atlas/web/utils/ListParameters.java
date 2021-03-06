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

package org.planqk.atlas.web.utils;

import org.planqk.atlas.web.Constants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Core parameters for a generic data list
 */
@RequiredArgsConstructor
@Data
public class ListParameters {
    @NonNull
    private final Pageable pageable;

    @Nullable
    private final String search;

    public static ListParameters getDefault() {
        return new ListParameters(PageRequest.of(Constants.DEFAULT_PAGE_NUMBER, Constants.DEFAULT_PAGE_SIZE), "");
    }
}
