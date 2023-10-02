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
import io.angularpay.cipher.models.GenerateSignatureCommandRequest;
import io.angularpay.cipher.models.SignatureResponseModel;
import io.angularpay.cipher.validation.DefaultConstraintValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import static io.angularpay.cipher.exceptions.ErrorCode.CIPHER_ERROR;
import static io.angularpay.cipher.helpers.CommandHelper.getRequestByReferenceOrThrow;

@Service
public class GenerateSignatureCommand extends AbstractCommand<GenerateSignatureCommandRequest, SignatureResponseModel> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CipherService cipherService;
    private final CommandHelper commandHelper;

    public GenerateSignatureCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CipherService cipherService,
            CommandHelper commandHelper) {
        super("GenerateSignatureCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.cipherService = cipherService;
        this.commandHelper = commandHelper;
    }

    @Override
    protected String getResourceOwner(GenerateSignatureCommandRequest request) {
        return this.commandHelper.getRequestOwner(request.getCipherReference());
    }

    @Override
    protected SignatureResponseModel handle(GenerateSignatureCommandRequest request) {
        try {
            CipherRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getCipherReference());
            CipherEntryModel cipherEntryModel = CipherEntryModel.builder()
                    .privateKey(found.getPrivateKey())
                    .publicKey(found.getPublicKey())
                    .build();
            String signature = this.cipherService.sign(cipherEntryModel, request.getPlaintext());
            return new SignatureResponseModel(signature);
        } catch (GeneralSecurityException ignored) {
            throw CommandException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .errorCode(CIPHER_ERROR)
                    .message(CIPHER_ERROR.getDefaultMessage())
                    .build();
        }
    }

    @Override
    protected List<ErrorObject> validate(GenerateSignatureCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
