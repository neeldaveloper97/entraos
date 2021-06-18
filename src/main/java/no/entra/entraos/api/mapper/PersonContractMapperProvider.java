package no.entra.entraos.api.mapper;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.spi.DbMapperProvider;
import no.entra.entraos.api.model.PersonContract;
import no.entra.entraos.api.model.Template;
import no.entra.entraos.api.model.UserRole;

import javax.annotation.Priority;
import java.util.Optional;

@Priority(1000)
public class PersonContractMapperProvider implements DbMapperProvider {
    private static final PersonContractMapper MAPPER = new PersonContractMapper();

    @Override
    public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
        if (type.equals(PersonContract.class)) {
            return Optional.of((DbMapper<T>) MAPPER);
        }
        return Optional.empty();
    }
}
