package no.entra.entraos.api.security;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.helidon.config.Config;
import io.helidon.config.Config.Type;
import lombok.Data;

@Data
public class StubConfig {
	
	public static final Logger logger = LoggerFactory.getLogger(StubConfig.class);
	
     private Map<String, RouteConfig> routes;

     public StubConfig(Config appConfig) {
         
         this.routes = Collections.unmodifiableMap(
             appConfig
                 .get("routes")
                 .traverse(c -> !c.isLeaf() && c.type() != Type.LIST)
                 .peek(c -> logger.debug("Request mapping for path: {}", c.name()))
                 .collect(Collectors.toMap(Config::name, RouteConfig::new))
         );
     }

}
