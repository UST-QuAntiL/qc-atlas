/*******************************************************************************
 * Copyright (c) 2020-2023 the qc-atlas contributors.
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

package org.planqk.atlas.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.minio.MinioClient;

@Profile({"!google-cloud & !test & minio"})
@Configuration
public class MinioConfiguration {

    private final String accessKey;

    private final String accessSecret;

    private final String minioUrl;

    public MinioConfiguration(
            @Value("${minio.url}") String minioUrl,
            @Value("${minio.access.name}") String accessKey,
            @Value("${minio.access.secret}") String accessSecret
    ) {
        this.minioUrl = minioUrl;
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
    }

    @Bean
    public MinioClient generateMinioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, accessSecret)
                .build();
    }
}

