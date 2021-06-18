package no.entra.entraos.api.mapper;

import java.util.Optional;

import javax.annotation.Priority;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.spi.DbMapperProvider;
import no.entra.entraos.api.model.ContractProperty;

@Priority(1000)
public class ContractPropertyMapperProvider implements DbMapperProvider {
    private static final ContractPropertyMapper MAPPER = new ContractPropertyMapper();

    @Override
    public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
        if (type.equals(ContractProperty.class)) {
            return Optional.of((DbMapper<T>) MAPPER);
        }
        return Optional.empty();
    }
}
