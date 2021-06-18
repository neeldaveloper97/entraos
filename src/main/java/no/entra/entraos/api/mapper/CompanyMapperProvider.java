package no.entra.entraos.api.mapper;

import java.util.Optional;

import javax.annotation.Priority;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.spi.DbMapperProvider;
import no.entra.entraos.api.model.Company;

@Priority(1000)
public class CompanyMapperProvider implements DbMapperProvider {
    private static final CompanyMapper MAPPER = new CompanyMapper();

    @Override
    public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
        if (type.equals(Company.class)) {
            return Optional.of((DbMapper<T>) MAPPER);
        }
        return Optional.empty();
    }
}
