
package io.angularpay.cipher.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SignatureResponseModel {

    private final String signature;

}
