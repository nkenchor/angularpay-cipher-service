package io.angularpay.cipher.adapters.inbound;

import io.angularpay.cipher.configurations.AngularPayConfiguration;
import io.angularpay.cipher.domain.commands.*;
import io.angularpay.cipher.models.*;
import io.angularpay.cipher.ports.inbound.RestApiPort;
import io.angularpay.cipher.util.CipherGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static io.angularpay.cipher.helpers.Helper.fromHeaders;
import static io.angularpay.cipher.models.RecycleBy.CIPHER_REFERENCE;

@RestController
@RequestMapping("/cipher/entries")
@RequiredArgsConstructor
public class RestApiAdapter implements RestApiPort {

    private final CreateRequestCommand createRequestCommand;
    private final RecycleCipherCommand recycleCipherCommand;
    private final EncryptDataCommand encryptDataCommand;
    private final DecryptDataCommand decryptDataCommand;
    private final GenerateSignatureCommand generateSignatureCommand;
    private final VerifySignatureCommand verifySignatureCommand;

    private final CipherGenerator cipherGenerator;
    private final AngularPayConfiguration configuration;

    @PostMapping("/generate-keys/count/{count}")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public void generateKeys(@PathVariable int count) {
        this.cipherGenerator.generateAndPopulateQueue(count > 0? count : configuration.getNumberOfKeysToGenerate());
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public CreateEntryResponse create(
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        CreateRequestCommandRequest createRequestCommandRequest = CreateRequestCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .build();
        return this.createRequestCommand.execute(createRequestCommandRequest);
    }

    @DeleteMapping("/{cipherReference}/recycle")
    @Override
    public void recycle(
            @PathVariable String cipherReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        RecycleCipherCommandRequest recycleCipherCommandRequest = RecycleCipherCommandRequest.builder()
                .reference(cipherReference)
                .recycleBy(CIPHER_REFERENCE)
                .authenticatedUser(authenticatedUser)
                .build();
        this.recycleCipherCommand.execute(recycleCipherCommandRequest);
    }

    @PostMapping("/{cipherReference}/encrypt")
    @Override
    public EncryptionResponseModel encrypt(
            @PathVariable String cipherReference,
            @RequestBody String plaintext,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        EncryptDataCommandRequest encryptDataCommandRequest = EncryptDataCommandRequest.builder()
                .cipherReference(cipherReference)
                .plaintext(plaintext)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.encryptDataCommand.execute(encryptDataCommandRequest);
    }

    @PostMapping("/{cipherReference}/decrypt")
    @Override
    public DecryptionResponseModel decrypt(
            @PathVariable String cipherReference,
            @RequestBody String encryptedString,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        DecryptDataCommandRequest decryptDataCommandRequest = DecryptDataCommandRequest.builder()
                .cipherReference(cipherReference)
                .encryptedString(encryptedString)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.decryptDataCommand.execute(decryptDataCommandRequest);
    }

    @PostMapping("/{cipherReference}/sign")
    @Override
    public SignatureResponseModel sign(
            @PathVariable String cipherReference,
            @RequestBody String plaintext,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenerateSignatureCommandRequest generateSignatureCommandRequest = GenerateSignatureCommandRequest.builder()
                .cipherReference(cipherReference)
                .plaintext(plaintext)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.generateSignatureCommand.execute(generateSignatureCommandRequest);
    }

    @PostMapping("/{cipherReference}/verify")
    @Override
    public VerifySignatureResponseModel verifySignature(
            @PathVariable String cipherReference,
            @RequestBody String plaintext,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        VerifySignatureCommandRequest verifySignatureCommandRequest = VerifySignatureCommandRequest.builder()
                .cipherReference(cipherReference)
                .plaintext(plaintext)
                .signature(headers.get("x-angularpay-cipher-signature"))
                .authenticatedUser(authenticatedUser)
                .build();
        return this.verifySignatureCommand.execute(verifySignatureCommandRequest);
    }
}
