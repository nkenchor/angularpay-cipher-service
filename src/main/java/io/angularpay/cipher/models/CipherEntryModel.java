
package io.angularpay.cipher.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CipherEntryModel {

    @JsonProperty("private_key")
    private String privateKey;
    @JsonProperty("public_key")
    private String publicKey;
}
