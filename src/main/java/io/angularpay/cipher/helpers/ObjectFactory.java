package io.angularpay.cipher.helpers;

import io.angularpay.cipher.domain.CipherRequest;

import java.util.UUID;

public class ObjectFactory {

    public static CipherRequest pmtRequestWithDefaults() {
        return CipherRequest.builder()
                .reference(UUID.randomUUID().toString())
                .build();
    }
}