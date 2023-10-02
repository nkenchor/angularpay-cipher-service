package io.angularpay.cipher.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VerifySignatureCommandRequest extends AccessControl {

    @NotEmpty
    private String cipherReference;

    @NotEmpty
    private String plaintext;

    @NotEmpty
    private String signature;

    VerifySignatureCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
