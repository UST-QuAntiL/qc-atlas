/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import java.util.Map;

import org.planqk.atlas.web.Constants;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListParametersMethodArgumentResolver extends PageableHandlerMethodArgumentResolverSupport
    implements HandlerMethodArgumentResolver, UriComponentsContributor {
    private final HateoasSortHandlerMethodArgumentResolver sortResolver = new HateoasSortHandlerMethodArgumentResolver();

    @Override
    public Object resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        final String page = webRequest.getParameter(getParameterNameToUse(Constants.PAGE, methodParameter));
        final String pageSize = webRequest.getParameter(getParameterNameToUse(Constants.SIZE, methodParameter));
        final String searchQuery = webRequest.getParameter(getParameterNameToUse(Constants.SEARCH, methodParameter));

        final Sort sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
        Pageable pageable = getPageable(methodParameter, page, pageSize);

        if ((page != null && pageSize != null) && (page.equals("-1") && pageSize.equals("-1"))) {
            pageable = Pageable.unpaged();
        }

        if (sort.isSorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }
        return new ListParameters(pageable, searchQuery);
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(ListParameters.class);
    }

    @Override
    public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables,
                                         ConversionService conversionService) {
        final var listParams = (ListParameters) value;
        final var pageable = listParams.getPageable();

        final var pagePropertyName = getParameterNameToUse(Constants.PAGE, parameter);
        final var sizePropertyName = getParameterNameToUse(Constants.SIZE, parameter);
        if (!pageable.isUnpaged()) {
            final var pageNumber = pageable.getPageNumber();
            builder.replaceQueryParam(pagePropertyName, isOneIndexedParameters() ? pageNumber + 1 : pageNumber);
            builder.replaceQueryParam(sizePropertyName,
                pageable.getPageSize() <= getMaxPageSize() ? pageable.getPageSize() : getMaxPageSize());
        } else {
            builder.replaceQueryParam(pagePropertyName, "-1");
            builder.replaceQueryParam(sizePropertyName, "-1");
        }

        if (pageable.getSort().isSorted()) {
            sortResolver.enhance(builder, parameter, pageable.getSort());
        }

        if (listParams.getSearch() != null && !listParams.getSearch().isEmpty()) {
            builder.replaceQueryParam(getParameterNameToUse(Constants.SEARCH, parameter), listParams.getSearch());
        }
    }
}
