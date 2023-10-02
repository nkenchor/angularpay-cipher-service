
package io.angularpay.cipher.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document("cipher_requests")
public class CipherRequest {

    @Id
    private String id;
    @Version
    private int version;
    private String reference;
    @JsonProperty("created_on")
    private String createdOn;
    @JsonProperty("user_reference")
    private String userReference;
    @JsonProperty("device_id")
    private String deviceId;
    @JsonProperty("private_key")
    private String privateKey;
    @JsonProperty("public_key")
    private String publicKey;
}
