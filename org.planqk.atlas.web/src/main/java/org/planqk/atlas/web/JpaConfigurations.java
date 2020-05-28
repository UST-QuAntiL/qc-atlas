package org.planqk.atlas.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("org.planqk.atlas.*")
public class JpaConfigurations {
}
