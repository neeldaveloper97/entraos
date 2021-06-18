package no.entra.entraos.api;

import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.session.WhydahApplicationSession2;
import no.cantara.config.ApplicationProperties;
import no.cantara.config.testsupport.ApplicationPropertiesTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @BeforeAll
    public static void startTheServer() {
        ApplicationPropertiesTestHelper.enableMutableSingleton();
        ApplicationProperties.builder().testDefaults().buildAndSetStaticSingleton();
    }

    @Test
    void testGetProperties() {

    }

    @Test
    void testGetSSOSession() throws Exception {
        String ststURL = ApplicationProperties.getInstance().get(MainProperties.WHYDAH_TOKENSERVICE_URI);
        String appId = ApplicationProperties.getInstance().get(MainProperties.WHYDAH_APPLICATION_ID);
        String appName = ApplicationProperties.getInstance().get(MainProperties.WHYDAH_APPLICATION_NAME);
        String appSecret = ApplicationProperties.getInstance().get(MainProperties.WHYDAH_APPLICATION_SECRET);
        ApplicationCredential appCred = new ApplicationCredential(appId, appName, appSecret);
        WhydahApplicationSession2 was = WhydahApplicationSession2.getInstance(ststURL, appCred);
        for (int n = 0; n < 5; n++) {
            Thread.sleep(2000);
            String s = new CommandLogonApplication(URI.create(ststURL), appCred).execute();
            System.out.println(n + " - Got ApplicationTokenId(WAS): " + was.getActiveApplicationTokenId());
            System.out.println(n + " - Got ApplicationTokenId(Command): " + ApplicationTokenMapper.fromXml(s).getApplicationTokenId());

        }

        assertEquals(true, was.getActiveApplicationTokenId() != null);

    }

}