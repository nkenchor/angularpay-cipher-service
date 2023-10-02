package io.angularpay.cipher.domain.service;

import io.angularpay.cipher.models.CipherEntryModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifySignatureRequest {
    private final CipherEntryModel cipherEntry;
    private final String plaintext;
    private final String signatureString;
}
