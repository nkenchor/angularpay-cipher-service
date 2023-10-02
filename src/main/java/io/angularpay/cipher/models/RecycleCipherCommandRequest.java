package io.angularpay.cipher.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RecycleCipherCommandRequest extends AccessControl {

    @NotEmpty
    private String reference;

    @NotNull
    private RecycleBy recycleBy;

    RecycleCipherCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
