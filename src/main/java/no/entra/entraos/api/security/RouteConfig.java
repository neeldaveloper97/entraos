package no.entra.entraos.api.security;

import java.util.HashSet;
import java.util.Set;

import io.helidon.config.Config;
import lombok.Data;

@Data
public class RouteConfig {

	private final boolean apply_subpaths;
    private final boolean authorize;
    private final boolean authenticate;
    private final Set<String> methods;
    private final Set<String> roles;

    public RouteConfig(Config route) {
        this.authorize = route
            .get("authorize")
            .asBoolean()
            .orElse(false);
        this.authenticate = route
            .get("authenticate")
            .asBoolean()
            .orElse(false);
        this.roles = route
            .get("roles")
            .asList(String.class)
            .map(HashSet::new)
            .orElse(new HashSet<>());
        this.methods = route
                .get("methods")
                .asList(String.class)
                .map(HashSet::new)
                .orElse(new HashSet<>());
        this.apply_subpaths = route
                .get("apply-subpaths")
                .asBoolean()
                .orElse(false);
    }

}
