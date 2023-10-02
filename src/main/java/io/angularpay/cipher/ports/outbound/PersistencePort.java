package io.angularpay.cipher.ports.outbound;

import io.angularpay.cipher.domain.CipherRequest;

import java.util.Collection;
import java.util.Optional;

public interface PersistencePort {
    CipherRequest createRequest(CipherRequest request);
    CipherRequest updateRequest(CipherRequest request);
    Optional<CipherRequest> findRequestByReference(String reference);
    void deleteRequestByReference(String reference);
    Collection<CipherRequest> findRequestByUserReference(String userReference);
    void deleteRequestByUserReference(String reference);
}
