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

package org.planqk.atlas.core.util;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;

@Slf4j
public class DatabaseExtension implements ExecutionCondition {

    private static Properties applicationProps;
    private static boolean enforceSkip = false;

    static {
        try {
            var input = DatabaseExtension.class.getResourceAsStream("/db-test.properties");
            applicationProps = new Properties();
            applicationProps.load(input);
        } catch (Exception e) {
            log.warn("Failed to load property file for database tests", e);
            enforceSkip = true;
        }
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        if (enforceSkip) {
            return disabled("Failed to read properties file.");
        }
        var driverClass = applicationProps.getProperty("spring.datasource.driverClassName");
        var dbUrl = applicationProps.getProperty("spring.datasource.url");
        var username = applicationProps.getProperty("spring.datasource.username");
        var password = applicationProps.getProperty("spring.datasource.password");

        Driver driverInstance = null;
        try {
            driverInstance = (Driver) Class.forName(driverClass).getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(driverInstance);

            var conn = DriverManager.getConnection(dbUrl, username, password);
            System.out.println(conn.getSchema());
            conn.close();
        } catch (Exception e) {
            return disabled("Database not available");
        } finally {
            try {
                DriverManager.deregisterDriver(driverInstance);
            } catch (Exception e) {
                log.warn("Removing db driver failed", e);
            }
        }
        return enabled("Database available");
    }
}
