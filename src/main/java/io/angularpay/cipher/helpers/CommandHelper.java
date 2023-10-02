package io.angularpay.cipher.helpers;

import io.angularpay.cipher.adapters.outbound.MongoAdapter;
import io.angularpay.cipher.domain.CipherRequest;
import io.angularpay.cipher.exceptions.CommandException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.angularpay.cipher.exceptions.ErrorCode.REQUEST_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommandHelper {

    private final MongoAdapter mongoAdapter;

    public String getRequestOwner(String requestReference) {
        CipherRequest found = this.mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                CommandHelper::commandException
        );
        return found.getUserReference();
    }

    private static CommandException commandException() {
        return CommandException.builder()
                .status(HttpStatus.NOT_FOUND)
                .errorCode(REQUEST_NOT_FOUND)
                .message(REQUEST_NOT_FOUND.getDefaultMessage())
                .build();
    }

    public static CipherRequest getRequestByReferenceOrThrow(MongoAdapter mongoAdapter, String requestReference) {
        return mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                CommandHelper::commandException
        );
    }
}
