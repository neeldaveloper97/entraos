package no.entra.entraos.api.whydah;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.userauth.CommandGetUserTokenByUserTokenId;
import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.util.StringConv;
import net.whydah.sso.util.StringUtil;
import no.cantara.config.ApplicationProperties;
import no.entra.entraos.api.MainProperties;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.api.util.HttpConnectionHelper;
import no.entra.entraos.api.util.Response;
import no.entra.entraos.api.util.StringUtils;
import no.entra.entraos.domain.person.Contract;
import no.entra.entraos.domain.person.Person;
import no.entra.entraos.domain.person.PersonMapper;
import no.entra.entraos.domain.person.contracts.ContractMapper;

public class WhydahService {

	public static final Logger log = LoggerFactory.getLogger(WhydahService.class);

	WhydahApplicationSession was;

	public WhydahService() {
		String ststURL = ApplicationProperties.getInstance().get(MainProperties.WHYDAH_TOKENSERVICE_URI);
		String appId = ApplicationProperties.getInstance().get(MainProperties.WHYDAH_APPLICATION_ID);
		String appName = ApplicationProperties.getInstance().get(MainProperties.WHYDAH_APPLICATION_NAME);
		String appSecret = ApplicationProperties.getInstance().get(MainProperties.WHYDAH_APPLICATION_SECRET);
		ApplicationCredential appCred = new ApplicationCredential(appId, appName, appSecret);
		was = WhydahApplicationSession.getInstance(ststURL, appCred);

	}

	

	public Person getPerson(String personRef) {

		String appTokenId = was.getActiveApplicationTokenId();
		log.info("Attempting to look up apptoken:" + appTokenId);

		if(appTokenId!=null) {
			Response res = HttpConnectionHelper.get(ApplicationProperties.getInstance().get(MainProperties.WHYDAH_PERSONREGISTERAPI_URI).replaceFirst("/$", "") + "/person/" + personRef, appTokenId);
			if(res!=null && res.getContent()!=null) {
				log.info("Attempt to look up a person in EntraOS, response:" + res.getContent());
				return PersonMapper.fromJson(res.getContent());
			} else {
				log.error("failed to getPerson - personref {}", personRef);
			}
		} else {
			log.error("No app token found");
		}
		return null;
	}


	public boolean removeContract(String personRef, String contractId) {
		String appTokenId = was.getActiveApplicationTokenId();
		log.info("Attempting to look up apptoken:" + appTokenId);
		if(appTokenId!=null) {
			Response res = HttpConnectionHelper.delete(ApplicationProperties.getInstance().get(MainProperties.WHYDAH_PERSONREGISTERAPI_URI).replaceFirst("/$", "") + "/person/" + personRef + "/contract/" + contractId, appTokenId);
			log.info("Attempt to remove a contract in EntraOS, response:" + res.getResponseCode());
			if(res!=null && (res.getResponseCode() == 200  || res.getResponseCode() == 204 || res.getResponseCode() == 401)) {
				log.info("contract removed");
				return true;
			} else {
				log.error("failed to removeContract - personref {}, contractid {}", personRef, contractId);
			}
		} else {
			log.error("No app token found");
		}
		return false;
	}

	public Person addContract(String personRef, Contract contract) {
		String appTokenId = was.getActiveApplicationTokenId();
		log.info("Attempting to look up apptoken:" + appTokenId);

		if(appTokenId!=null) {
			byte[] payload = StringConv.getBytes(ContractMapper.toJson(contract));
			Response res = HttpConnectionHelper.put(ApplicationProperties.getInstance().get(MainProperties.WHYDAH_PERSONREGISTERAPI_URI).replaceFirst("/$", "") + "/person/" + personRef + "/add/contract/", appTokenId, payload);
			
			if(res!=null && res.getContent() !=null) {
				log.info("Attempt to add a contract to a person in EntraOS, response:" + res.getContent());
				return PersonMapper.fromJson(res.getContent());
			} else {
				log.error("failed to addContract - personref {}, contractid {}", personRef, contract.getContractId().toString());
			}
		} else {
			log.error("No app token found");
		}
		return null;
	}


	public List<Person> findAllPersons() throws JsonProcessingException {
		String appTokenId = was.getActiveApplicationTokenId();
		log.info("Attempting to look up apptoken:" + appTokenId);
		List<Person> list = new ArrayList<Person>();
		if(appTokenId!=null) {
			Response res = HttpConnectionHelper.get(ApplicationProperties.getInstance().get(MainProperties.WHYDAH_PERSONREGISTERAPI_URI).replaceFirst("/$", "") + "/person/persons", appTokenId);
			
			if(res!=null && res.getContent() !=null) {
				log.info("Attempt to look up all persons in EntraOS, response:" + res.getContent());
				list = PersonMapper.fromJsonList(res.getContent());
			} else {
				log.error("failed to findAllPersons");
			}
		} else {
			log.error("No app token found");
		}
		return list;
	}

	public List<Person> findPerson(String keyword) throws JsonProcessingException {
		String appTokenId = was.getActiveApplicationTokenId();
		log.info("Attempting to look up apptoken:" + appTokenId);
		List<Person> list = new ArrayList<Person>();
		if(appTokenId!=null) {
			Response res = HttpConnectionHelper.get(ApplicationProperties.getInstance().get(MainProperties.WHYDAH_PERSONREGISTERAPI_URI).replaceFirst("/$", "") + "/person/search/" + keyword, appTokenId);
			
			if(res!=null&&res.getContent()!=null) {
				log.info("Attempt to look up a person in EntraOS, response:" + res.getContent());
				if(res.getContent().startsWith("[")) {
					list = PersonMapper.fromJsonList(res.getContent());
				} else if(res.getContent().startsWith("{")) {
					list.add(PersonMapper.fromJson(res.getContent()));
				}
			} else {
				log.error("failed to findPerson - keyword {}", keyword);
			}
		}
		return list;
	}

	public UserToken findUserTokenFromUserTokenId(String userTokenId) {
		log.info("Attempting to lookup usertokenId:" + userTokenId);
		String userTokenXml = "";
		try {
			UserToken userToken = null;       
			URI tokenServiceUri = URI.create(was.getSTS());
			String appTokenId = was.getActiveApplicationTokenId();
			log.info("Attempting to lookup apptoken:" + appTokenId);
			String oauth2proxyAppTokenXml = was.getActiveApplicationTokenXML();
			log.info("Attempting to lookup oauth2proxyAppTokenXml:" + oauth2proxyAppTokenXml.replace("\n", ""));
			log.info("Attempting to lookup (get_usertoken_by_usertokenid) tokenServiceUri:" + tokenServiceUri);
			userTokenXml = new CommandGetUserTokenByUserTokenId(tokenServiceUri, appTokenId, oauth2proxyAppTokenXml, userTokenId).execute();   
			if(userTokenXml!=null) {
				log.info("==> Got lookup userTokenXml:" + userTokenXml.replace("\n", ""));
				userToken = UserTokenMapper.fromUserTokenXml(userTokenXml);
				log.info("Got userToken:" + userToken);
				return userToken;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.warn("Unable to parse userTokenXml returned from sts: " + userTokenXml.replace("\n", "") + "", e);
			return null;
		}
	}





}
