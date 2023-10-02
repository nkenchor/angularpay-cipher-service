package io.angularpay.cipher.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.cipher.adapters.outbound.RedisQueueClient;
import io.angularpay.cipher.configurations.AngularPayConfiguration;
import io.angularpay.cipher.domain.service.CipherService;
import io.angularpay.cipher.models.CipherEntryModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class CipherGenerator {

    private final RedisQueueClient redisQueueClient;
    private final CipherService cipherService;
    private final ObjectMapper objectMapper;
    private final AngularPayConfiguration configuration;

    public CipherGenerator(
            RedisQueueClient redisQueueClient,
            CipherService cipherService,
            ObjectMapper objectMapper,
            AngularPayConfiguration configuration) {
        this.redisQueueClient = redisQueueClient;
        this.cipherService = cipherService;
        this.objectMapper = objectMapper;
        this.configuration = configuration;

        new Timer().schedule(new TimerTask() {
            public void run() {
                Long size = redisQueueClient.size();
                if (size <= configuration.getMinimumKeyCount()) {
                    generateAndPopulateQueue(configuration.getNumberOfKeysToGenerate());
                }
            }
        }, 0, configuration.getQueueCheckIntervalInSeconds() * 1000);
    }

    public void generateAndPopulateQueue(int numberOfKeysToGenerate) {
        log.info("started generating {} keys and populating REDIS queue", numberOfKeysToGenerate);
        IntStream.range(0, numberOfKeysToGenerate).parallel().forEach(x -> {
            Executors.newSingleThreadExecutor().submit(() ->{
                try {
                    CipherEntryModel cipherEntry = cipherService.createCipherEntry();
                    String message = objectMapper.writeValueAsString(cipherEntry);
                    this.redisQueueClient.push(message);
                } catch (Exception exception) {
                    log.error("An error occurred while generating ciphers and populating REDIS queue", exception);
                }
            });
        });
        log.info("finished generating {} keys and populating REDIS queue", numberOfKeysToGenerate);
    }

    public void generateAndPopulateQueueIfNecessary() {
        Long size = redisQueueClient.size();
        if (size <= configuration.getMinimumKeyCount()) {
            Executors.newSingleThreadExecutor().submit(() ->{
                generateAndPopulateQueue(configuration.getNumberOfKeysToGenerate());
            });
        }
    }
}
