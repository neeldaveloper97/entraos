package no.entra.entraos.api.security;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.helidon.common.http.Http;
import io.helidon.common.http.Http.RequestMethod;
import io.helidon.config.Config;
import io.helidon.security.AuthenticationResponse;
import io.helidon.security.SecurityContext;
import io.helidon.security.Subject;
import io.helidon.webserver.Handler;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserToken;
import no.cantara.config.ApplicationProperties;
import no.entra.entraos.api.MainProperties;
import no.entra.entraos.api.model.Company;
import no.entra.entraos.api.model.UserRole;
import no.entra.entraos.api.repository.CompanyRepository;
import no.entra.entraos.api.repository.UserRoleRepository;
import no.entra.entraos.api.whydah.WhydahService;

public class SecurityFilter implements Handler {

	public static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

	public static Map<String, UserRole> userRoleMap = new HashMap<String, UserRole>();
	
	static StubConfig sc;
	UserRoleRepository urRepo;
	CompanyRepository comRepo;
	WhydahService whydahService;
	ModelMapper mapper = new ModelMapper();

	public SecurityFilter(Config config, UserRoleRepository urRepo, CompanyRepository comRepo, WhydahService whydahService) {
		sc = new StubConfig(config);
		config.onChange(newConfig -> {
			logger.debug("Config updated");
			sc = new StubConfig(newConfig);
		});

		this.urRepo = urRepo;
		this.comRepo = comRepo;
		this.whydahService = whydahService;
	}

	@Override
	public void accept(ServerRequest req, ServerResponse resp) {
		
		logger.debug("Handle path {}", req.path());

		RouteConfig route = findRoute(req.path().toRawString());
		
		if(route!=null && route.isAuthenticate() && route.getMethods().contains(req.method().name().toUpperCase())) {

			logger.info("getroute " + req.path());
			req.context().get(SecurityContext.class)
			.ifPresent(ctx -> ctx.atnClientBuilder()
					.submit().whenComplete((res, t) ->{
						if(t!=null) {
							resp.status(Http.Status.SERVICE_UNAVAILABLE_503).send(t.getMessage());
						} 

						if(!res.status().isSuccess()) {
							resp.status(Http.Status.UNAUTHORIZED_401).send();
						}

						if (res.user().isEmpty()) {
							resp.status(Http.Status.UNAUTHORIZED_401).send();
							return;
						}

						UserRole userrole = getUserRole(res);
						
						
						//check role
						if (route.isAuthorize()) {

							if (!route.getRoles().isEmpty()) {

								if (userrole.getRoles()
										.stream()
										.noneMatch(role -> route.getRoles().contains(role))) {
									resp.status(Http.Status.FORBIDDEN_403).send();
									return;
								}
							}
						}
						
						
						//store the map because we need to share among resources
						userRoleMap.put(ctx.userName(), userrole);
						req.next();
						
					})
					);
			
			

		} else {
			req.next();
		}

	}
	
	public static boolean isRouteAuthenticated(ServerRequest req) {
		RouteConfig route = findRoute(req.path().toRawString());
		return route !=null && route.isAuthenticate();
	}
	
	public static UserRole getUserRole(ServerRequest req) {
		Optional<SecurityContext> ctx = req.context().get(SecurityContext.class);
		if(ctx.isPresent()) {
			return ctx.get().isAuthenticated()? userRoleMap.get(ctx.get().userName()) : null;
		}
		return null;
	}

	private UserRole getUserRole(AuthenticationResponse res) {
		Subject subject = res.user().get();
		String username = subject.principal().getName();
		Optional<Object> userTokenId = subject.principal().abacAttribute("usertoken_id");

		
		UserRole userrole = userRoleMap.get(username);
		if(userrole==null) {
			userrole = urRepo.findByUsername(username);
			if(userrole!=null) {
				userRoleMap.put(username, userrole);
			}
		}
		

		if(userrole!=null) {
			return syncAppRoleEntry(userTokenId, userrole, true);
		} else {
			UserRole newUserRole = new UserRole();
			newUserRole.setId(UUID.randomUUID().toString());
			newUserRole.setUsername(username);
			newUserRole.setLast_synced(LocalDateTime.now());
			newUserRole.setRoles(new HashSet<String>());
			newUserRole.setCompany_ids(new HashSet<String>());
			newUserRole = syncAppRoleEntry(userTokenId, newUserRole, false);
			urRepo.save(newUserRole).await();
			return newUserRole;
		}
	}
	
	private UserRole syncAppRoleEntry(Optional<Object> userTokenId, UserRole userrole, boolean exists) {
	
		if(userTokenId.isPresent() && (!exists || exists && userrole.getLast_synced().plusMinutes(1).isBefore(LocalDateTime.now()))) {
			UserToken ut = this.whydahService.findUserTokenFromUserTokenId(String.valueOf(userTokenId.get()));
			if(ut!=null && ut.getRoleList()!=null) {
				Set<String> roleList = new HashSet<String>();
				Set<String> company_ids = new HashSet<String>();
				for(UserApplicationRoleEntry roleentry : ut.getRoleList()) {
					if(roleentry.getRoleName().equalsIgnoreCase(ApplicationProperties.getInstance().get(MainProperties.WHYDAH_APPROLEENTRYKEY_SUPERADMIN)) && 
						  (roleentry.getRoleValue().equalsIgnoreCase("true") || 	roleentry.getRoleValue().equalsIgnoreCase("1"))) {
						roleList.add("SUPERADMIN");
					}
					
					if(roleentry.getRoleName().equalsIgnoreCase(ApplicationProperties.getInstance().get(MainProperties.WHYDAH_APPROLEENTRYKEY_COMPANYNAMES))) {
						roleList.add("COMPANYADMIN");
						List<String> identities = Arrays.asList(roleentry.getRoleValue().split(","));
						List<Company> companies = comRepo.findAllByIds(identities).collectList().await();
						companies.addAll(comRepo.findAllByNames(identities).collectList().await());
						company_ids = companies.stream().map(i -> i.getId()).collect(Collectors.toSet());
						
						/*
						for(String rv : roleentry.getRoleValue().split(",")) {
							Company com = comRepo.findById(rv.trim());
							if(com==null) {
								com = comRepo.findByCompanyName(rv.trim());
							}
							if(com!=null) {
								company_ids.add(com.getId());
							}
						}*/	
					}
				}

				UserRole update = mapper.map(userrole, UserRole.class);
				update.setRoles(roleList);
				update.setCompany_ids(company_ids);
				update.setLast_synced(LocalDateTime.now());
				
				if(exists) {
					urRepo.update(update.getId(), update);
				}
				
				return update;
			}			
		}
		return userrole;
	}

	public static RouteConfig findRoute(String path) {
		Optional<Entry<String, RouteConfig>> r= sc.getRoutes().entrySet().stream().filter(route -> {
			if(route.getValue().isApply_subpaths()) {
				return path.contains(route.getKey());
			} else {
				return route.getKey().equalsIgnoreCase(path);
			}

		}).findFirst();
		if(r.isPresent()) {
			return r.get().getValue();
		} else {
			return null;
		}

	}

}
