package org.planqk.atlas.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;

@Profile("test")
@Configuration
public class CloudStorageTestConfiguration {

    @Bean
    public Storage storage(){
        Storage storage = LocalStorageHelper.getOptions().getService();
        return storage;
    }
}
