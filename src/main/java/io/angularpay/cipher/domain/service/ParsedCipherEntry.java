package io.angularpay.cipher.domain.service;

import lombok.Builder;
import lombok.Getter;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

@Builder
@Getter
public class ParsedCipherEntry {
    private final PrivateKey privateKey;
    private final RSAPublicKey publicKey;
}