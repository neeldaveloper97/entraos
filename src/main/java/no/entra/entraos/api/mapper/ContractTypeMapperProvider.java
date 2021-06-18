package no.entra.entraos.api.mapper;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.spi.DbMapperProvider;
import no.entra.entraos.api.model.ContractType;
import no.entra.entraos.api.model.Template;

import javax.annotation.Priority;
import java.util.Optional;

@Priority(1000)
public class ContractTypeMapperProvider implements DbMapperProvider {
    private static final ContractTypeMapper MAPPER = new ContractTypeMapper();

    @Override
    public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
        if (type.equals(ContractType.class)) {
            return Optional.of((DbMapper<T>) MAPPER);
        }
        return Optional.empty();
    }
}
