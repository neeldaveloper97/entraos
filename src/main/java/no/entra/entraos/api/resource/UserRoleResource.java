package no.entra.entraos.api.resource;

import java.util.logging.Logger;

import io.helidon.webserver.Routing.Rules;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.UserRole;
import no.entra.entraos.api.repository.UserRoleRepository;
import no.entra.entraos.api.security.SecurityFilter;
import no.entra.entraos.api.util.EntityUtils;

public class UserRoleResource implements Service {

	private final static Logger LOGGER = Logger.getLogger(UserRoleResource.class.getName());

	
	private final UserRoleRepository ur_repo;


	public UserRoleResource(UserRoleRepository repo) {	
		this.ur_repo = repo;
	}

	@Override
	public void update(Rules rules) {
		rules
		.get("/api/user_role", this::getUserRole);
	}
	
	
	private void getUserRole(ServerRequest serverRequest, ServerResponse serverResponse) {
		UserRole ur = SecurityFilter.getUserRole(serverRequest);
		if(ur!=null) {
			serverResponse.status(200).send(EntityUtils.object_toJsonObject(ur));
		} else {
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("No userrole found. Make sure you have a bearer accesstoken in the authorization header."));
		}
	}
	
	
}