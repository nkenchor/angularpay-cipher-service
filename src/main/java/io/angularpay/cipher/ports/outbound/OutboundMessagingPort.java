package io.angularpay.cipher.ports.outbound;

import java.util.Map;

public interface OutboundMessagingPort {
    void push(String message);
    String pop();
    Long size();
    Map<String, String> getPlatformConfigurations(String hashName);
}
