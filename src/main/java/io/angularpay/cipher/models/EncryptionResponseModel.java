
package io.angularpay.cipher.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EncryptionResponseModel {

    @JsonProperty("encrypted_string")
    private final String encryptedString;

}
