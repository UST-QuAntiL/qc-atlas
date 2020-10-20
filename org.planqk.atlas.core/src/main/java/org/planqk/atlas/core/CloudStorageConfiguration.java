package org.planqk.atlas.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;

@Profile("!test")
@Configuration
public class CloudStorageConfiguration {
    @Bean
    public Storage storage(){
        return StorageOptions.getDefaultInstance().getService();
    }
}
