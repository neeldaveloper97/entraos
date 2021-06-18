package no.entra.entraos.api.persistence;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseMigrationHelper {
    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationHelper.class);

    private final Flyway flyway;
    private final String dbUrl;

    public DatabaseMigrationHelper(String url, String username, String password) {
       
        this.dbUrl = url;
        String location ="db/migration/h2";
        if(!this.dbUrl.startsWith("jdbc:h2")) {
        	location = "db/migration/posgres";
        }
        
        flyway = Flyway.configure()
                .baselineOnMigrate(true)
                .locations(location)
                .table("schema_version")
                .dataSource(url, username, password).load();

    }

    public void upgradeDatabase() {
        log.info("Upgrading database with url={} using migration files from {}", dbUrl, flyway.getConfiguration().getLocations());
        try {
            flyway.migrate();
        } catch (FlywayException e) {
            log.error("Database upgrade failed using " + dbUrl, e);
        }
    }

    //used by tests
    public void cleanDatabase() {
        try {
            flyway.clean();
        } catch (FlywayException e) {
            throw new RuntimeException("Database cleaning failed.", e);
        }
    }
}