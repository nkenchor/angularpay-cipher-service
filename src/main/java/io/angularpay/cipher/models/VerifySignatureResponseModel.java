
package io.angularpay.cipher.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class VerifySignatureResponseModel {

    @JsonProperty("is_valid")
    private final boolean valid;

}
