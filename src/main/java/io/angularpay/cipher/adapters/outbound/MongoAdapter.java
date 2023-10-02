package io.angularpay.cipher.adapters.outbound;

import io.angularpay.cipher.domain.CipherRequest;
import io.angularpay.cipher.ports.outbound.PersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MongoAdapter implements PersistencePort {

    private final CipherRepository cipherRepository;

    @Override
    public CipherRequest createRequest(CipherRequest request) {
        request.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return this.cipherRepository.save(request);
    }

    @Override
    public CipherRequest updateRequest(CipherRequest request) {
        return this.cipherRepository.save(request);
    }

    @Override
    public Optional<CipherRequest> findRequestByReference(String reference) {
        return this.cipherRepository.findByReference(reference);
    }

    @Override
    public void deleteRequestByReference(String reference) {
        this.cipherRepository.deleteByReference(reference);
    }

    @Override
    public Collection<CipherRequest> findRequestByUserReference(String userReference) {
        return this.cipherRepository.findByUserReference(userReference);
    }

    @Override
    public void deleteRequestByUserReference(String userReference) {
        this.cipherRepository.deleteByUserReference(userReference);
    }
}
