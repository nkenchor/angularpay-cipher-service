package io.angularpay.cipher.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.cipher.adapters.outbound.MongoAdapter;
import io.angularpay.cipher.domain.CipherRequest;
import io.angularpay.cipher.domain.Role;
import io.angularpay.cipher.domain.service.CipherService;
import io.angularpay.cipher.exceptions.CommandException;
import io.angularpay.cipher.exceptions.ErrorObject;
import io.angularpay.cipher.helpers.CommandHelper;
import io.angularpay.cipher.models.CipherEntryModel;
import io.angularpay.cipher.models.DecryptDataCommandRequest;
import io.angularpay.cipher.models.DecryptionResponseModel;
import io.angularpay.cipher.validation.DefaultConstraintValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import static io.angularpay.cipher.exceptions.ErrorCode.CIPHER_ERROR;
import static io.angularpay.cipher.helpers.CommandHelper.getRequestByReferenceOrThrow;

@Service
public class DecryptDataCommand extends AbstractCommand<DecryptDataCommandRequest, DecryptionResponseModel> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CipherService cipherService;
    private final CommandHelper commandHelper;

    public DecryptDataCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CipherService cipherService,
            CommandHelper commandHelper) {
        super("DecryptDataCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.cipherService = cipherService;
        this.commandHelper = commandHelper;
    }

    @Override
    protected String getResourceOwner(DecryptDataCommandRequest request) {
        return this.commandHelper.getRequestOwner(request.getCipherReference());
    }

    @Override
    protected DecryptionResponseModel handle(DecryptDataCommandRequest request) {
        try {
            CipherRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getCipherReference());
            CipherEntryModel cipherEntryModel = CipherEntryModel.builder()
                    .privateKey(found.getPrivateKey())
                    .publicKey(found.getPublicKey())
                    .build();
            String decryptedString = this.cipherService.decrypt(cipherEntryModel, request.getEncryptedString());
            return new DecryptionResponseModel(decryptedString);
        } catch (GeneralSecurityException ignored) {
            throw CommandException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .errorCode(CIPHER_ERROR)
                    .message(CIPHER_ERROR.getDefaultMessage())
                    .build();
        }
    }

    @Override
    protected List<ErrorObject> validate(DecryptDataCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
