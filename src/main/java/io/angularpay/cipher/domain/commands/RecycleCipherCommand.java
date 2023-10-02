package io.angularpay.cipher.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.cipher.adapters.outbound.MongoAdapter;
import io.angularpay.cipher.adapters.outbound.RedisAdapter;
import io.angularpay.cipher.domain.CipherRequest;
import io.angularpay.cipher.domain.Role;
import io.angularpay.cipher.exceptions.CommandException;
import io.angularpay.cipher.exceptions.ErrorObject;
import io.angularpay.cipher.helpers.CommandHelper;
import io.angularpay.cipher.models.CipherEntryModel;
import io.angularpay.cipher.models.RecycleBy;
import io.angularpay.cipher.models.RecycleCipherCommandRequest;
import io.angularpay.cipher.validation.DefaultConstraintValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static io.angularpay.cipher.exceptions.ErrorCode.INVALID_JSON;
import static io.angularpay.cipher.helpers.CommandHelper.getRequestByReferenceOrThrow;

@Service
public class RecycleCipherCommand extends AbstractCommand<RecycleCipherCommandRequest, Void> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final RedisAdapter redisAdapter;
    private final CommandHelper commandHelper;

    public RecycleCipherCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            RedisAdapter redisAdapter,
            CommandHelper commandHelper) {
        super("RecycleCipherCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.redisAdapter = redisAdapter;
        this.commandHelper = commandHelper;
    }

    @Override
    protected String getResourceOwner(RecycleCipherCommandRequest request) {
        if (request.getRecycleBy() == RecycleBy.USER_REFERENCE) {
            return request.getAuthenticatedUser().getUserReference();
        } else {
            return this.commandHelper.getRequestOwner(request.getReference());
        }
    }

    @Override
    protected Void handle(RecycleCipherCommandRequest request) {
        try {
            if (request.getRecycleBy() == RecycleBy.USER_REFERENCE) {
                return recycleByUserReference(request);
            } else {
                return recycleByCipherReference(request);
            }
        } catch (JsonProcessingException ignored) {
            throw CommandException.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(INVALID_JSON)
                    .message(INVALID_JSON.getDefaultMessage())
                    .build();
        }
    }

    private Void recycleByCipherReference(RecycleCipherCommandRequest request) throws JsonProcessingException {
        CipherRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getReference());
        mapAndPushToQueue(found);
        this.mongoAdapter.deleteRequestByReference(found.getReference());
        return null;
    }

    private Void recycleByUserReference(RecycleCipherCommandRequest request) throws JsonProcessingException {
        Collection<CipherRequest> collection = this.mongoAdapter.findRequestByUserReference(request.getReference());
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }
        for (CipherRequest cipherRequest : collection) {
            mapAndPushToQueue(cipherRequest);
        }
        this.mongoAdapter.deleteRequestByUserReference(request.getReference());
        return null;
    }

    private void mapAndPushToQueue(CipherRequest cipherRequest) throws JsonProcessingException {
        CipherEntryModel cipherEntryModel = CipherEntryModel.builder()
                .privateKey(cipherRequest.getPrivateKey())
                .publicKey(cipherRequest.getPublicKey())
                .build();
        String message = this.mapper.writeValueAsString(cipherEntryModel);
        this.redisAdapter.push(message);
    }

    @Override
    protected List<ErrorObject> validate(RecycleCipherCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
