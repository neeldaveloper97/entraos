package no.entra.entraos.api.mapper;

import java.util.Optional;

import javax.annotation.Priority;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.spi.DbMapperProvider;
import no.entra.entraos.api.model.CompanyContract;

@Priority(1000)
public class CompanyContractMapperProvider implements DbMapperProvider {
    private static final CompanyContractMapper MAPPER = new CompanyContractMapper();

    @Override
    public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
        if (type.equals(CompanyContract.class)) {
            return Optional.of((DbMapper<T>) MAPPER);
        }
        return Optional.empty();
    }
}
