package no.entra.entraos.api.resource;

import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.cantara.config.ApplicationProperties;
import no.entra.entraos.api.Main;
import no.entra.entraos.api.MainProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.Properties;


public class HealthResource implements Service {
	public static final Logger log = LoggerFactory.getLogger(HealthResource.class);
	
	private boolean ok = true;
	private static String runningSince;
	private String myVersion;
	private String myIp;
	private Config config;
	
	public HealthResource(Config config){
		this.config = config;
	}

	/**
	 * A service registers itself by updating the routine rules.
	 *
	 * @param rules the routing rules.
	 */
	@Override
	public void update(Routing.Rules rules) {
		rules.get("/health", this::showEnvironment);
		runningSince = getRunningSince();
	}

	
	@SuppressWarnings("checkstyle:designforextension")
	public synchronized void showEnvironment(final ServerRequest request, final ServerResponse response) {
		String msg = getHealthTextJson();
		response.status(200).send(msg);
	}


	public String getHealthTextJson() {
		
		if (myVersion==null) {
			myVersion = getVersion();
		}
		if (myIp==null) {
			myIp = getMyIPAddresssString();
		}

		return "{\n" +
				"  \"Status\": \"" + ok + "\",\n" +
				"  \"name\": \"" + Main.APPLICATION_NAME + "\",\n" +
				"  \"oauth2_issuer\": \"" + ApplicationProperties.getInstance().get(MainProperties.WHYDAH_OAUTH2_URI) + "\",\n" +
				"  \"port\": \"" + ApplicationProperties.getInstance().get(MainProperties.SERVER_PORT) + "\",\n" +
				"  \"ip\": \"" + myIp + "\",\n" +
				"  \"version\": \"" + myVersion + "\",\n" +
				"  \"now\": \"" + Instant.now() + "\",\n" +
				"  \"running since\": \"" + runningSince + "\"\n\n" +

				"}\n";
	}

	public static String getRunningSince() {
		long uptimeInMillis = ManagementFactory.getRuntimeMXBean().getUptime();
		return Instant.now().minus(uptimeInMillis, ChronoUnit.MILLIS).toString();
	}

	private static String getVersion() {
		Properties mavenProperties = new Properties();
		String resourcePath = "/META-INF/maven/no.entra.entraos/" + Main.APPLICATION_NAME + "/pom.properties";
		URL mavenVersionResource = HealthResource.class.getResource(resourcePath);
		if (mavenVersionResource != null) {
			try {
				mavenProperties.load(mavenVersionResource.openStream());
				return mavenProperties.getProperty("version", "missing version info in " + resourcePath);
			} catch (IOException e) {
				log.warn("Problem reading version resource from classpath: ", e);
			}
		}
		return "(DEV VERSION)" + " [" + Main.APPLICATION_NAME + " - " + getMyIPAddresssString() + "]";
	}

	public static String getMyIPAddresssesString() {

		String ipAdresses = "";

		try {
			ipAdresses = InetAddress.getLocalHost().getHostAddress();
			Enumeration n = NetworkInterface.getNetworkInterfaces();

			while (n.hasMoreElements()) {
				NetworkInterface e = (NetworkInterface) n.nextElement();

				InetAddress addr;
				for (Enumeration a = e.getInetAddresses(); a.hasMoreElements(); ipAdresses = ipAdresses + "  " + addr.getHostAddress()) {
					addr = (InetAddress) a.nextElement();
				}
			}
		} catch (Exception e) {
			return null;
		}

		return ipAdresses;
	}

	public static String getMyIPAddresssString() {

		String fullString = getMyIPAddresssesString();
		return fullString.substring(0, fullString.indexOf(" "));
	}

}
