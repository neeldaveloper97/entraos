package no.entra.entraos.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.helidon.webserver.Routing.Rules;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.Template;
import no.entra.entraos.api.repository.TemplateRepository;
import no.entra.entraos.api.security.SecurityFilter;
import no.entra.entraos.api.util.EntityUtils;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Arrays;

public class TemplateResource implements Service {

	public static final Logger logger = LoggerFactory.getLogger(TemplateResource.class);

	private final TemplateRepository template_repo;

	public TemplateResource(TemplateRepository template_repo) {
		this.template_repo = template_repo;
	}

	@Override
	public void update(Rules rules) {
		rules
		.get("/api/template", this::getTemplateList) //admin
		.get("/api/template/{id}", this::getTemplateById) //admin
		.post("/api/template", this::createTemplate) //admin
		.delete("/api/template/{id}", this::deleteTemplateById) //admin
		.put("/api/template/{id}", this::updateTemplate) //admin

		;
	}

	private void getTemplateById(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			String id = serverRequest.path().param("id");
			logger.info("getTemplateById called, id {}", id);
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}

			this.template_repo.getOne(id)
			.subscribe(template -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(template)),
					throwable -> {
						logger.error("getTemplateById failed", throwable);
						serverRequest.next(throwable);
					});
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("getTemplateById failed with exception", e);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("getTemplateById failed with exception " + e.getMessage()));
		}
	}

	private void updateTemplate(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			String id = serverRequest.path().param("id");
			logger.info("updateTemplate called, id {}", id);
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			serverRequest.content().as(JsonObject.class)
			.map(jsonObj -> {

				try {
					return EntityUtils.jsonObject_toObject(Template.class, jsonObj);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;

			}).subscribe(t -> {
				if(t==null) {
					throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
				}

				this.template_repo.update(id, t).subscribe(
						p -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(t)),
						throwable -> {
							logger.error("updateTemplate failed", throwable);
							serverRequest.next(throwable);
						}
						);

			},
					throwable -> {
						logger.error("updateTemplate failed", throwable);
						serverRequest.next(throwable);
					}
					);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("updateTemplate failed with exception", e);
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("updateTemplate failed with exception " + e.getMessage()));
		}

	}

	private void createTemplate(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			logger.info("createTemplate called");
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}

			serverRequest.content().as(JsonObject.class)
			.map(jsonObj -> {

				try {
					return EntityUtils.jsonObject_toObject(Template.class, jsonObj);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;

			}).subscribe(t -> {
				if(t==null) {
					throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
				}

				this.template_repo.save(t)
				.subscribe(
						p -> {
							if (p != null) {
								t.setId(p);
								serverResponse.status(201)
								.headers()
								.location(URI.create("/api/template/" + p));
								serverResponse.send(EntityUtils.object_toJsonObject(t));
							} else {
								logger.error("createTemplate failed");
								serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Failed to save to DB"));
							}
						},
						throwable -> {
							logger.error("createTemplate failed", throwable);
							serverRequest.next(throwable);
						}
						);


			},
					throwable -> {
						logger.error("createTemplate failed", throwable);
						serverRequest.next(throwable);
					}	
					);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("createTemplate failed with exception", e);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("createTemplate failed with exception " + e.getMessage()));
		}




	}

	private void deleteTemplateById(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			String id = serverRequest.path().param("id");
			logger.info("deleteTemplateById called, id {}", id);
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			this.template_repo.deleteById(id)
			.subscribe(
					count -> {
						logger.info("deleteTemplateById {} deleted", count);
						serverResponse.status(204).send();
					},
					throwable -> {
						logger.error("deleteTemplateById failed", throwable);
						serverRequest.next(throwable);
					}
					);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("deleteTemplateById failed with exception", e);
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deleteTemplateById failed with exception " + e.getMessage()));
		}
	}

	private void getTemplateList(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			logger.info("getTemplateList called");
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			this.template_repo.all()
			.collectList()
			.map(EntityUtils::objectList_toJsonArray)
			.subscribe(
					serverResponse::send,
					throwable -> {
						logger.error("getTemplateList failed", throwable);
						serverRequest.next(throwable);
					}
					);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("getTemplateList failed with exception", e);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("getTemplateList failed with exception " + e.getMessage()));
		}
	}


}
