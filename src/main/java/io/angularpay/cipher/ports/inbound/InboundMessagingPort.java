package io.angularpay.cipher.ports.inbound;

import io.angularpay.cipher.models.platform.PlatformConfigurationIdentifier;

public interface InboundMessagingPort {
    void onMessage(String message, PlatformConfigurationIdentifier identifier);
}
