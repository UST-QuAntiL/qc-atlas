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

package org.planqk.atlas.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = "org.planqk.atlas.*")
@EnableJpaRepositories("org.planqk.atlas.*")
@EntityScan("org.planqk.atlas.*")
@OpenAPIDefinition(
        info = @Info(
                title = "atlas", version = "1.0",
                description = "Platform for Sharing Quantum Software",
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                ),
                contact = @Contact(
                        url = "https://github.com/PlanQK/qc-atlas",
                        name = "GitHub Repository"
                )
        )
)
public class Application extends SpringBootServletInitializer {

    final private static Logger LOG = LoggerFactory.getLogger(Application.class);

    public Application() {
        logReadyMessage();
    }

    private static void logReadyMessage() {
        if (LOG.isInfoEnabled()) {
            final String readyMessage = "\n===================================================\n" +
                    "ATLAS IS READY TO USE!\n" +
                    "===================================================";
            LOG.info(readyMessage);
        }
    }
}
