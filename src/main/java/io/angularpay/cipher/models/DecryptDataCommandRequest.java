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
public class DecryptDataCommandRequest extends AccessControl {

    @NotEmpty
    private String cipherReference;

    @NotEmpty
    private String encryptedString;

    DecryptDataCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
