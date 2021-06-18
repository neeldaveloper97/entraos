package no.entra.entraos.api.mapper;

import java.util.Optional;

import javax.annotation.Priority;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.spi.DbMapperProvider;
import no.entra.entraos.api.model.ContractCategory;

@Priority(1000)
public class ContractCategoryMapperProvider implements DbMapperProvider {
    private static final ContractCategoryMapper MAPPER = new ContractCategoryMapper();

    @Override
    public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
        if (type.equals(ContractCategory.class)) {
            return Optional.of((DbMapper<T>) MAPPER);
        }
        return Optional.empty();
    }
}
