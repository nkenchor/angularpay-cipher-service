package io.angularpay.cipher.ports.inbound;

import io.angularpay.cipher.models.*;

import java.util.Map;

public interface RestApiPort {
    void generateKeys(int count);
    CreateEntryResponse create(Map<String, String> headers);
    void recycle(String cipherReference, Map<String, String> headers);
    EncryptionResponseModel encrypt(String cipherReference, String plaintext, Map<String, String> headers);
    DecryptionResponseModel decrypt(String cipherReference, String encryptedString, Map<String, String> headers);
    SignatureResponseModel sign(String cipherReference, String plaintext, Map<String, String> headers);
    VerifySignatureResponseModel verifySignature(String cipherReference, String plaintext, Map<String, String> headers);
}
