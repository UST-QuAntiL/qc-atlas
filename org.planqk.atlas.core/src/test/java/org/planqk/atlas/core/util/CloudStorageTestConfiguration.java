package org.planqk.atlas.core.util;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.google.cloud.storage.Storage;

@Profile("test")
@Primary
@Configuration
public class CloudStorageTestConfiguration {
    @Bean
    public Storage storage() {
        return Mockito.mock(Storage.class);
    }
}
