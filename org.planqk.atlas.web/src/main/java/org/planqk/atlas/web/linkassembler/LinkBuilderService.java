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
package org.planqk.atlas.web.linkassembler;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.hateoas.server.core.LastInvocationAware;
import org.springframework.hateoas.server.core.MethodInvocation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Custom HATEOAS link builder that resolves paths using the container's {@link RequestMappingInfoHandlerMapping}
 * instance.
 */
@Component
@RequiredArgsConstructor
public class LinkBuilderService {
    private static final CompositeUriComponentsContributor contributor;
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final RequestMappingHandlerMapping mappings;

    static {
        contributor = new CompositeUriComponentsContributor(
                new PathVariableMethodArgumentResolver(), new RequestParamMethodArgumentResolver(false));
    }

    /**
     * Special version of {@link WebMvcLinkBuilder#linkTo(Object)} that resolves paths via {@link
     * RequestMappingInfoHandlerMapping}.
     */
    public LinkBuilder linkTo(Object invocationValue) {
        Assert.isInstanceOf(LastInvocationAware.class, invocationValue);

        var invocations = DummyInvocationUtils.getLastInvocationAware(invocationValue);
        if (invocations == null) {
            throw new IllegalStateException(String.format("Could not obtain previous invocation from %s!", invocationValue));
        }

        var invocation = invocations.getLastInvocation();
        Assert.notNull(invocation, "No invocation present");

        var mappingInfo = resolveInvocation(invocation);
        if (mappingInfo == null) {
            // In case there's no mapping, using just the annotations is our only option!
            return new LinkBuilder(WebMvcLinkBuilder.linkTo(invocationValue).toUriComponentsBuilder().build());
        }

        UriComponentsBuilder builder;
        // This is required for tests, which don't necessarily have a current request
        // (for example, when using this method to build an URL in order to perform a request).
        // If we supply a null builder in such a case, fromMethodCall will throw an InvalidStateException,
        // as it cannot figure out the base URL to use!
        if (RequestContextHolder.getRequestAttributes() != null)
            builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
        else
            builder = UriComponentsBuilder.newInstance();
        return new LinkBuilder(appendMappingParameters(appendMappingPath(builder, mappingInfo), invocation).build());
    }

    private RequestMappingInfo resolveInvocation(MethodInvocation invocation) {
        for (var entry : this.mappings.getHandlerMethods().entrySet()) {
            if (entry.getValue().getMethod().equals(invocation.getMethod()))
                return entry.getKey();
        }
        return null;
    }

    private UriComponentsBuilder appendMappingPath(UriComponentsBuilder builder, RequestMappingInfo mapping) {
        var patternSet = mapping.getPatternsCondition().getPatterns();
        Assert.notEmpty(patternSet, "Need at least one URL mapping");
        builder.path(patternSet.stream().sorted().findFirst().orElse(""));
        return builder;
    }

    private UriComponentsBuilder appendMappingParameters(UriComponentsBuilder builder, MethodInvocation invocation) {
        int paramCount = invocation.getMethod().getParameterCount();
        int argCount = invocation.getArguments().length;
        if (paramCount != argCount) {
            throw new IllegalArgumentException("Number of method parameters " + paramCount +
                    " does not match number of argument values " + argCount);
        }

        final Map<String, Object> uriVars = new HashMap<>();
        for (int i = 0; i < paramCount; i++) {
            MethodParameter param = new SynthesizingMethodParameter(invocation.getMethod(), i);
            param.initParameterNameDiscovery(parameterNameDiscoverer);
            contributor.contributeMethodArgument(param, invocation.getArguments()[i], builder, uriVars);
        }

        // This may not be all the URI variables, supply what we have so far..
        return builder.uriVariables(uriVars);
    }
}
