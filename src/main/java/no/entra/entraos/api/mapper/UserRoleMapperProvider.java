package no.entra.entraos.api.mapper;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.spi.DbMapperProvider;
import no.entra.entraos.api.model.Template;
import no.entra.entraos.api.model.UserRole;

import javax.annotation.Priority;
import java.util.Optional;

@Priority(1000)
public class UserRoleMapperProvider implements DbMapperProvider {
    private static final UserRoleMapper MAPPER = new UserRoleMapper();

    @Override
    public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
        if (type.equals(UserRole.class)) {
            return Optional.of((DbMapper<T>) MAPPER);
        }
        return Optional.empty();
    }
}
