package org.planqk.atlas.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@Slf4j
public class LoggingConfiguration {
    @Bean
    public CommonsRequestLoggingFilter loggingFilter() {
        log.info("Loading Request Logging Filter..");
        var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        return filter;
    }
}
