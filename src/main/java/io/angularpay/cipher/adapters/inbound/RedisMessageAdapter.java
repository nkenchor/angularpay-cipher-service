package io.angularpay.cipher.adapters.inbound;

import io.angularpay.cipher.domain.commands.PlatformConfigurationsConverterCommand;
import io.angularpay.cipher.models.platform.PlatformConfigurationIdentifier;
import io.angularpay.cipher.ports.inbound.InboundMessagingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.angularpay.cipher.models.platform.PlatformConfigurationSource.TOPIC;

@Service
@RequiredArgsConstructor
public class RedisMessageAdapter implements InboundMessagingPort {

    private final PlatformConfigurationsConverterCommand converterCommand;

    @Override
    public void onMessage(String message, PlatformConfigurationIdentifier identifier) {
        this.converterCommand.execute(message, identifier, TOPIC);
    }
}
