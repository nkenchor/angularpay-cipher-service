package io.angularpay.cipher.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class CreateRequestCommandRequest extends AccessControl {

    CreateRequestCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
