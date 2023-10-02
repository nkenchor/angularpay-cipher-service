package io.angularpay.cipher.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("angularpay")
@Data
public class AngularPayConfiguration {

    private int numberOfKeysToGenerate;
    private int minimumKeyCount;
    private long queueCheckIntervalInSeconds;
    private Redis redis;

    @Data
    public static class Redis {
        private String host;
        private int port;
        private int timeout;
    }
}
