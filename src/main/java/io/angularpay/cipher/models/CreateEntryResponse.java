
package io.angularpay.cipher.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CreateEntryResponse {

    private final String reference;
    @JsonProperty("public_key")
    private final String publicKey;
}
