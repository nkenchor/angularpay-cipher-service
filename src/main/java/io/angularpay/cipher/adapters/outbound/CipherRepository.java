package io.angularpay.cipher.adapters.outbound;

import io.angularpay.cipher.domain.CipherRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.Optional;

public interface CipherRepository extends MongoRepository<CipherRequest, String> {

    Optional<CipherRequest> findByReference(String reference);
    Page<CipherRequest> findAll(Pageable pageable);
    void deleteByReference(String reference);
    Collection<CipherRequest> findByUserReference(String userReference);
    void deleteByUserReference(String userReference);
}
