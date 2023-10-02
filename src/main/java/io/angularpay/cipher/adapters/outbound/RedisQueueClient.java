package io.angularpay.cipher.adapters.outbound;

import io.angularpay.cipher.configurations.AngularPayConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

import static io.angularpay.cipher.common.Constants.CIPHER_QUEUE;

@Service
@RequiredArgsConstructor
public class RedisQueueClient {

    private final AngularPayConfiguration configuration;

    public void push(String message) {
        try (Jedis jedis = jedisInstance()) {
            jedis.rpush(CIPHER_QUEUE, message);
        }
    }

    public String pop() {
        try (Jedis jedis = jedisInstance()) {
            List<String> messages = jedis.blpop(0, CIPHER_QUEUE);
            return messages.get(1);
        }
    }

    public Long size() {
        try (Jedis jedis = jedisInstance()) {
            return jedis.llen(CIPHER_QUEUE);
        }
    }

    private Jedis jedisInstance() {
        return new Jedis(
                configuration.getRedis().getHost(),
                configuration.getRedis().getPort(),
                configuration.getRedis().getTimeout()
        );
    }
}
