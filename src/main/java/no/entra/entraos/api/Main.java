package no.entra.entraos.api;

import io.helidon.common.configurable.Resource;
import io.helidon.config.Config;
import io.helidon.config.MapConfigSource;
import io.helidon.dbclient.DbClient;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.openapi.OpenAPISupport;
import io.helidon.security.Security;
import io.helidon.security.integration.webserver.WebSecurity;
import io.helidon.security.providers.jwt.JwtProvider;
import io.helidon.security.providers.jwt.JwtProvider.Builder;
import io.helidon.webserver.Routing;
import io.helidon.webserver.Service;
import io.helidon.webserver.StaticContentSupport;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.cors.CorsSupport;
import io.helidon.webserver.cors.CrossOriginConfig;
import no.cantara.config.ApplicationProperties;
import no.entra.entraos.api.exception.GlobalExceptionHandler;
import no.entra.entraos.api.persistence.DataInitializer;
import no.entra.entraos.api.persistence.DatabaseMigrationHelper;
import no.entra.entraos.api.repository.*;
import no.entra.entraos.api.resource.*;
import no.entra.entraos.api.security.SecurityFilter;
import no.entra.entraos.api.whydah.WhydahService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static io.helidon.config.ConfigSources.classpath;
import static io.helidon.config.ConfigSources.file;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static final String CONTEXT = "/";
    public static final String APPLICATION_NAME = "contract-self-administration";
    public static final String LOCAL_OVERRIDE_PROPERTIES = "local_override.properties";
    public static WhydahService whydahservice;
    
    private Main() {
    }

    public static void main(final String[] args) throws IOException {
        setupLogging();
        startServer();
    }

    private static void startServer() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        ApplicationProperties.builder().defaults()
                .expectedProperties(MainProperties.class)
                .buildAndSetStaticSingleton();

        Config config = buildConfig();
        Config dbConfig = config.get("db");
        DbClient dbClient = DbClient.builder(dbConfig).build();

        WebServer server = WebServer.builder(buildRouting(config, dbClient))
                .config(config)
                .port(config.get("server.port").asInt().get())
                .addMediaSupport(JsonpSupport.create())
                .build();

        server.start()
                .thenApply(webServer -> {


                            String endpoint = "http://localhost:" + webServer.port();
                            log.info("\n\n Visit Application at: " + endpoint);
                            log.info(" - Health checks available on: " + endpoint + "/health\n\n");
                            return null;
                        }
                );

        //setup DB
        setUpAndMigrateDb(config);

        //initialize data
        initializeData(dbClient);
    }


    private static Config buildConfig() {
        return Config.builder()
                .disableEnvironmentVariablesSource()
                .disableSystemPropertiesSource()
                .sources(
                        MapConfigSource.builder().map(ApplicationProperties.getInstance().map()),
                        classpath("application.yaml")        
                		)
                .build();
    }

    private static void initializeData(DbClient dbClient) {
        DataInitializer.init(dbClient);
    }

    public static void setUpAndMigrateDb(Config config) {
        try {
            String url = ApplicationProperties.getInstance().get(MainProperties.DB_CONNECTION_URL);
            String username = ApplicationProperties.getInstance().get(MainProperties.DB_CONNECTION_USERNAME);
            String pwd = ApplicationProperties.getInstance().get(MainProperties.DB_CONNECTION_PASSWORD);
            DatabaseMigrationHelper databaseMigrationHelper = new DatabaseMigrationHelper(url, username, pwd);
            databaseMigrationHelper.upgradeDatabase();
        } catch (Exception e) {
            log.error("Unable to create and migrate database", e);
        }
    }

    private static Routing buildRouting(Config config, DbClient client) {
    	
    	CorsSupport corsSupport = CorsSupport.builder()  
                .addCrossOrigin(CrossOriginConfig.builder() 
                            .allowOrigins("http://localhost:3000", "http://localhost:8087", "https://contract-devtest.entraos.io") 
                            .allowMethods("*")
                            .build()) 
                .addCrossOrigin(CrossOriginConfig.create()) 
                .build(); 
    	
    	whydahservice = new WhydahService();
    	
        MetricsSupport metrics = MetricsSupport.create();
        HealthResource healthResource = new HealthResource(config);
       

        PersonContractRepository personcontract_repo = new PersonContractRepository(client);
        PersonCompanyRepository personcompany_repo = new PersonCompanyRepository(client);
        ContractRepository contract_repo = new ContractRepository(client);
        CompanyRepository company_repo = new CompanyRepository(client);
        ContractPropertyRepository contract_property_repo = new ContractPropertyRepository(client);
        ContractCategoryRepository contract_category_repo = new ContractCategoryRepository(client);
        ContractTypeRepository contract_type_repo = new ContractTypeRepository(client);
        TemplateRepository template_repo = new TemplateRepository(client);
        UserRoleRepository userrole_rpo = new UserRoleRepository(client);
        
        Service company_resource = new CompanyResource(company_repo, whydahservice);
        Service contractproperty_resource = new ContractPropertyResource(contract_property_repo);
        Service contractcategory_resource = new ContractCategoryResource(contract_category_repo);
        Service contracttype_resource = new ContractTypeResource(contract_type_repo);
        Service template_resource = new TemplateResource(template_repo);
        Service userrole_resource = new UserRoleResource(userrole_rpo);
        Service personcontract_resource = new PersonContractResource(personcontract_repo, company_repo, personcompany_repo, contract_repo, whydahservice, contract_type_repo);
        Service contract_resource = new CompanyContractResource(contract_repo, personcontract_repo, personcompany_repo, company_repo, contract_property_repo, contract_type_repo, whydahservice);
        Service personcompany_resource = new PersonCompanyResource(personcompany_repo, company_repo, contract_repo, whydahservice, contract_category_repo, contract_property_repo, contract_type_repo, personcontract_repo);
        
        SecurityFilter sf = new SecurityFilter(config.get("app"), userrole_rpo, company_repo, whydahservice);
        
        io.helidon.webserver.Routing.Builder builder = Routing.builder();
		builder.register(WebSecurity.create(getSecurityConfig(config)));
		builder.any(sf);
	

        builder
                .register(OpenAPISupport.create(Config.create()))
                .register(healthResource)
                .register(metrics)
                .register(corsSupport)
                .register(company_resource)
                .register(contract_resource)
                .register(contractproperty_resource)
                .register(template_resource)
                .register(contracttype_resource)
                .register(contractcategory_resource)
                .register(userrole_resource)
                .register(personcompany_resource)
                .register(personcontract_resource)
                .error(Throwable.class, GlobalExceptionHandler.handleErrors(config.get("app.errorlevel").asString().get()));
        return registerStaticPaths(builder).build();

    }


    private static Security getSecurityConfig(Config config) {
        Builder provider = JwtProvider.builder().verifyJwk(Resource.create(URI.create(ApplicationProperties.getInstance().get(MainProperties.WHYDAH_OAUTH2_URI) + "/.well-known/jwks.json")));
        return Security.builder().addProvider(provider).build();
    }

    private static io.helidon.webserver.Routing.Builder registerStaticPaths(io.helidon.webserver.Routing.Builder b) {
        return b.register("/", StaticContentSupport.builder("/public/index.html")  		
        				.build())
                .register("/ui", StaticContentSupport.builder("/public/ui")
                		.welcomeFileName("index.html")
                        .build())   
                .register("/favicon.ico", StaticContentSupport.builder("/public/favicon.ico")
                        .build())
                .register("/_nuxt", StaticContentSupport.builder("/public/_nuxt")
                        .build())
                .register("/ui/favicon.ico", StaticContentSupport.builder("/public/favicon.ico")
                        .build())
                .register("/ui/_nuxt", StaticContentSupport.builder("/public/_nuxt")
                        .build())
                .register("/login", StaticContentSupport.builder("/public/login/index.html")
                        .build()
                      )
                .register("/welcome", StaticContentSupport.builder("/app")
                		.welcomeFileName("index.html")
                        .build()
                      )
                .register("/swagger", StaticContentSupport.builder("/swagger")
                		.welcomeFileName("index.html")
                        .build()
                      )
                ;

    }

    private static void setupLogging() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private static Config buildConfig_Old() {
        Config config = null;
        boolean hasLocalConfigFile = hasLocalConfigFile();
        if (hasLocalConfigFile) {
            config = Config.builder()
                    .sources(
                            file(LOCAL_OVERRIDE_PROPERTIES),
                            classpath("application.yaml"),
                            classpath("META-INF/microprofile-config.properties")
                    )
                    .build();
        } else {
            config = Config.builder()
                    .sources(
                            classpath("application.yaml"),
                            classpath("META-INF/microprofile-config.properties")
                    )
                    .build();
        }
        return config;
    }

    protected static boolean hasLocalConfigFile() {
        boolean hasLocalConfigFile = false;
        try {
            File f = new File(LOCAL_OVERRIDE_PROPERTIES);
            if (f.exists()) {
                hasLocalConfigFile = true;
            }
        } catch (Exception e) {
            log.trace("local_config.properties is not found on path");
        }
        return hasLocalConfigFile;
    }
    


}
