package org.planqk.atlas.web.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Special implementation of RequestMappingHandlerMapping that optionally adds version suffixes to
 * controller URLs.
 */
public class VersionedRequestHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method);
        if (info != null) {
            ApiVersion methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, ApiVersion.class);
            if (methodAnnotation != null) {
                // Prepend our version mapping to the real method mapping.
                info = createApiVersionInfo(methodAnnotation).combine(info);
            }

            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                ApiVersion typeAnnotation = AnnotatedElementUtils.findMergedAnnotation(handlerType, ApiVersion.class);
                if (methodAnnotation == null && typeAnnotation != null) {
                    // Append our version mapping to the real controller mapping.
                    typeInfo = typeInfo.combine(createApiVersionInfo(typeAnnotation));
                }
                info = typeInfo.combine(info);
            }
        }
        return info;
    }

    @Nullable
    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = (element instanceof Class ?
                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null);
    }

    private RequestMappingInfo createApiVersionInfo(ApiVersion annotation) {
        return new RequestMappingInfo(new PatternsRequestCondition(annotation.value(), getUrlPathHelper(), getPathMatcher(),
                useSuffixPatternMatch(), useTrailingSlashMatch(), getFileExtensions()),
                null, null, null, null, null, null);
    }
}
