package io.angularpay.cipher.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.cipher.adapters.outbound.MongoAdapter;
import io.angularpay.cipher.adapters.outbound.RedisAdapter;
import io.angularpay.cipher.domain.CipherRequest;
import io.angularpay.cipher.domain.Role;
import io.angularpay.cipher.exceptions.CommandException;
import io.angularpay.cipher.exceptions.ErrorObject;
import io.angularpay.cipher.models.CipherEntryModel;
import io.angularpay.cipher.models.CreateEntryResponse;
import io.angularpay.cipher.models.CreateRequestCommandRequest;
import io.angularpay.cipher.models.RecycleCipherCommandRequest;
import io.angularpay.cipher.validation.DefaultConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static io.angularpay.cipher.exceptions.ErrorCode.INVALID_JSON;
import static io.angularpay.cipher.helpers.ObjectFactory.pmtRequestWithDefaults;
import static io.angularpay.cipher.models.RecycleBy.USER_REFERENCE;

@Slf4j
@Service
public class CreateRequestCommand extends AbstractCommand<CreateRequestCommandRequest, CreateEntryResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final RedisAdapter redisAdapter;
    private final RecycleCipherCommand recycleCipherCommand;

    public CreateRequestCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            RedisAdapter redisAdapter,
            RecycleCipherCommand recycleCipherCommand) {
        super("CreateRequestCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.redisAdapter = redisAdapter;
        this.recycleCipherCommand = recycleCipherCommand;
    }

    @Override
    protected String getResourceOwner(CreateRequestCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected CreateEntryResponse handle(CreateRequestCommandRequest request) {
        try {
            this.recyclePreviousCiphersForUser(request);
            String message = this.redisAdapter.pop();
            CipherEntryModel cipherEntryModel = this.mapper.readValue(message, CipherEntryModel.class);
            CipherRequest cipherRequestWithDefaults = pmtRequestWithDefaults();
            CipherRequest withOtherDetails = cipherRequestWithDefaults.toBuilder()
                    .userReference(request.getAuthenticatedUser().getUserReference())
                    .deviceId(request.getAuthenticatedUser().getDeviceId())
                    .privateKey(cipherEntryModel.getPrivateKey())
                    .publicKey(cipherEntryModel.getPublicKey())
                    .build();
            CipherRequest response = this.mongoAdapter.createRequest(withOtherDetails);
            return CreateEntryResponse.builder().reference(response.getReference()).publicKey(response.getPublicKey()).build();
        } catch (JsonProcessingException ignored) {
            throw CommandException.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(INVALID_JSON)
                    .message(INVALID_JSON.getDefaultMessage())
                    .build();
        }
    }

    private void recyclePreviousCiphersForUser(CreateRequestCommandRequest request) {
        RecycleCipherCommandRequest recycleCipherCommandRequest = RecycleCipherCommandRequest.builder()
                .reference(request.getAuthenticatedUser().getUserReference())
                .recycleBy(USER_REFERENCE)
                .authenticatedUser(request.getAuthenticatedUser())
                .build();
        this.recycleCipherCommand.execute(recycleCipherCommandRequest);
    }

    @Override
    protected List<ErrorObject> validate(CreateRequestCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Arrays.asList(Role.ROLE_UNVERIFIED_USER, Role.ROLE_VERIFIED_USER, Role.ROLE_PLATFORM_ADMIN);
    }
}
