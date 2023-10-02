
package io.angularpay.cipher.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DecryptionResponseModel {

    @JsonProperty("decrypted_string")
    private final String decryptedString;

}
