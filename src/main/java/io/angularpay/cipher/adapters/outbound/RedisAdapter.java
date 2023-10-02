package io.angularpay.cipher.adapters.outbound;

import io.angularpay.cipher.ports.outbound.OutboundMessagingPort;
import io.angularpay.cipher.util.CipherGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisAdapter implements OutboundMessagingPort {

    private final RedisQueueClient redisQueueClient;
    private final CipherGenerator cipherGenerator;
    private final RedisHashClient redisHashClient;

    @Override
    public void push(String message) {
        this.redisQueueClient.push(message);
    }

    @Override
    public String pop() {
        cipherGenerator.generateAndPopulateQueueIfNecessary();
        return this.redisQueueClient.pop();
    }

    @Override
    public Long size() {
        return this.redisQueueClient.size();
    }

    @Override
    public Map<String, String> getPlatformConfigurations(String hashName) {
        return this.redisHashClient.getPlatformConfigurations(hashName);
    }
}
