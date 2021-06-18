package no.entra.entraos.api.resource;

import java.net.URI;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.helidon.webserver.Routing.Rules;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.ContractProperty;
import no.entra.entraos.api.repository.ContractPropertyRepository;
import no.entra.entraos.api.security.SecurityFilter;
import no.entra.entraos.api.util.EntityUtils;

public class ContractPropertyResource implements Service {

	public static final Logger logger = LoggerFactory.getLogger(ContractPropertyResource.class);


	private final ContractPropertyRepository contract_property_repo;


	public ContractPropertyResource(ContractPropertyRepository repo) {

		this.contract_property_repo = repo;
	}

	@Override
	public void update(Rules rules) {

		//contract property
		rules
		.get("/api/contract_property", this::getContractPropertyList)
		.get("/api/contract_property/{id}", this::getContractPropertyById)
		.post("/api/contract_property", this::createContractProperty) //admin
		.delete("/api/contract_property/{id}", this::deleteContractPropertyById) //admin
		.put("/api/contract_property/{id}", this::updateContractProperty); //admin

	}


	private void getContractPropertyList(ServerRequest serverRequest, ServerResponse serverResponse) {
		logger.info("getContractPropertyList called");
		this.contract_property_repo.all()
		.collectList()
		.map(EntityUtils::objectList_toJsonArray)
		.subscribe(
				serverResponse::send,
				throwable -> {
					logger.error("getContractPropertyList failed", throwable);
					serverRequest.next(throwable);
				}
				);
	}

	private void getContractPropertyById(ServerRequest serverRequest, ServerResponse serverResponse) {
		String id = serverRequest.path().param("id");
		logger.info("getContractPropertyById called, id {}", id);
		this.contract_property_repo.getOne(id)
		.subscribe(payload -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(payload)),
				throwable -> {
					logger.error("getContractPropertyById failed", throwable);
					serverRequest.next(throwable);
				});
	}

	private void createContractProperty(ServerRequest serverRequest, ServerResponse serverResponse) {
		logger.info("createContractProperty called");
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			serverRequest.content().as(JsonObject.class)
			.map(jsonObj -> {

				try {
					return EntityUtils.jsonObject_toObject(ContractProperty.class, jsonObj);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;

			}).subscribe(cp -> {
				if(cp==null) {
					throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
				}

				this.contract_property_repo.save(cp).subscribe(
						p -> {
							if(p!=null) { 
								cp.setId(p);
								serverResponse.status(201)
								.headers()
								.location(URI.create("/api/contract_property/" + p));
								serverResponse.send(EntityUtils.object_toJsonObject(cp));
							} else {
								logger.error("createContractProperty failed");
								serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Failed to save to DB"));
							}
						},
						throwable -> {
							logger.error("createContractProperty failed", throwable);
							serverRequest.next(throwable);
						}
						);


			},
					throwable -> {
						logger.error("updateContractProperty failed", throwable);
						serverRequest.next(throwable);
					}

					);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("updateContractProperty failed with exception", e);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("updateContractProperty failed with exception " + e.getMessage()));
		}



	}

	private void deleteContractPropertyById(ServerRequest serverRequest, ServerResponse serverResponse) {
		String id = serverRequest.path().param("id");
		logger.info("deleteContractPropertyById called, id {}", id);
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}

			this.contract_property_repo.deleteById(id)
			.subscribe(
					count -> {
						logger.info("deleteContractPropertyById {} deleted.", count);
						serverResponse.status(204).send();
					},
					throwable -> {
						logger.error("deleteContractPropertyById failed", throwable);
						serverRequest.next(throwable);
					});
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("deleteContractPropertyById failed with exception", e);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deleteContractPropertyById failed with exception " + e.getMessage()));
		}
	}

	private void updateContractProperty(ServerRequest serverRequest, ServerResponse serverResponse) {
		String id = serverRequest.path().param("id");
		logger.info("updateContractProperty called, id {}", id);
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			serverRequest.content().as(JsonObject.class)
			.map(jsonObj -> {

				try {
					return EntityUtils.jsonObject_toObject(ContractProperty.class, jsonObj);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;

			}).subscribe(cp -> {
				if(cp==null) {
					throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
				}

				this.contract_property_repo.update(id, cp).subscribe(
						p -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(cp)),
						throwable -> {
							logger.error("updateContractProperty failed", throwable);
							serverRequest.next(throwable);
						});


			},
					throwable -> {
						logger.error("updateContractProperty failed", throwable);
						serverRequest.next(throwable);
					}	
					);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("updateContractProperty failed with exception", e);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("updateContractProperty failed with exception " + e.getMessage()));
		}

	}


}
