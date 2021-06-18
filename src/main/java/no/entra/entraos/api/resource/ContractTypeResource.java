package no.entra.entraos.api.resource;

import java.net.URI;


import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.helidon.dbclient.DbClient;
import io.helidon.webserver.Routing.Rules;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.ContractType;
import no.entra.entraos.api.repository.ContractTypeRepository;
import no.entra.entraos.api.security.SecurityFilter;
import no.entra.entraos.api.util.EntityUtils;

public class ContractTypeResource implements Service {

	public static final Logger logger = LoggerFactory.getLogger(ContractTypeResource.class);


	private final ContractTypeRepository contract_type_repo;


	public ContractTypeResource(ContractTypeRepository contract_type_repo) {

		this.contract_type_repo = contract_type_repo;

	}

	@Override
	public void update(Rules rules) {
		rules
		.get("/api/contract_type", this::getContractTypeList)
		.get("/api/contract_type/{id}", this::getContractTypeById)
		.post("/api/contract_type", this::createContractType) //admin
		.delete("/api/contract_type/{id}", this::deleteContractTypeById) //admin
		.put("/api/contract_type/{id}", this::updateContractType); //admin

	}


	private void getContractTypeList(ServerRequest serverRequest, ServerResponse serverResponse) {
		logger.info("getContractTypeList called");
		this.contract_type_repo.all()
		.collectList()
		.map(EntityUtils::objectList_toJsonArray)
		.subscribe(
				serverResponse::send,
				throwable -> {
					logger.error("getContractTypeList failed", throwable);
					serverRequest.next(throwable);
				}
				);
	}

	private void getContractTypeById(ServerRequest serverRequest, ServerResponse serverResponse) {
		String id = serverRequest.path().param("id");
		logger.info("getContractTypeById called, id {}", id);
		this.contract_type_repo.getOne(id)
		.subscribe(payload -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(payload)),
				throwable -> {
					logger.error("getContractTypeById failed", throwable);
					serverRequest.next(throwable);
				});
	}

	private void createContractType(ServerRequest serverRequest, ServerResponse serverResponse) {
		logger.info("createContractType called");
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
					return EntityUtils.jsonObject_toObject(ContractType.class, jsonObj);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;

			}).subscribe(ct -> {
				if(ct==null) {
					throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
				}

				this.contract_type_repo.save(ct).subscribe(
						p -> {
							if(p!=null) {
								ct.setId(p);
								serverResponse.status(201)
								.headers()
								.location(URI.create("/api/contract_type/" + p));
								serverResponse.send(EntityUtils.object_toJsonObject(ct));
							} else {
								logger.error("createContractType failed");
								serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Failed to save to DB"));
							}
						},
						throwable -> {
							logger.error("createContractType failed", throwable);
							serverRequest.next(throwable);
						}
						);


			},
					throwable -> {
						logger.error("createContractType failed", throwable);
						serverRequest.next(throwable);
					}
					);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("createContractType failed with exception", e);
			throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("createContractType failed with exception " + e.getMessage());
		}



	}

	private void deleteContractTypeById(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			String id = serverRequest.path().param("id");
			logger.info("deleteContractTypeById called, id {}", id);
			this.contract_type_repo.deleteById(id)
			.subscribe(
					count -> {
						logger.info("deleteContractTypeById {} deleted.", count);
						serverResponse.status(204).send();
					},
					throwable -> {
						logger.error("deleteContractTypeById failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("deleteContractTypeById failed with exception", e);
			throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deleteContractTypeById failed with exception " + e.getMessage());
		}
	}

	private void updateContractType(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			String id = serverRequest.path().param("id");
			logger.info("updateContractType called, id {}", id);

			serverRequest.content().as(JsonObject.class)
			.map(jsonObj -> {

				try {
					return EntityUtils.jsonObject_toObject(ContractType.class, jsonObj);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;

			}).subscribe(data -> {
				if(data==null) {
					throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
				}


				this.contract_type_repo.update(id, data)
				.subscribe(
						p -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(data)),
						throwable -> {
							logger.error("updateContractType failed", throwable);
							serverRequest.next(throwable);
						}
						);


			},
					throwable -> {
						logger.error("updateContractType failed", throwable);
						serverRequest.next(throwable);
					}
					);}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("updateContractType failed with exception", e);
			throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("updateContractType failed with exception " + e.getMessage());
		}




	}
}
