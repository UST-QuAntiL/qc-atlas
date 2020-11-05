package org.planqk.atlas.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Profile({"google-cloud & !test"})
@Primary
@Configuration
public class CloudStorageConfiguration {
    @Bean
    public Storage storage() {
        return StorageOptions.getDefaultInstance().getService();
    }
}
