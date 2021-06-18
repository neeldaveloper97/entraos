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
import no.entra.entraos.api.model.ContractCategory;
import no.entra.entraos.api.repository.ContractCategoryRepository;
import no.entra.entraos.api.security.SecurityFilter;
import no.entra.entraos.api.util.EntityUtils;

public class ContractCategoryResource implements Service {

	public static final Logger logger = LoggerFactory.getLogger(ContractCategoryResource.class);


	private final ContractCategoryRepository contract_category_repo;


	public ContractCategoryResource(ContractCategoryRepository repo) {

		this.contract_category_repo = repo;

	}

	@Override
	public void update(Rules rules) {
		rules
		//contract category
		.get("/api/contract_category", this::getContractCategoryList) //open access
		.get("/api/contract_category/{id}", this::getContractCategoryById) //open access
		.post("/api/contract_category", this::createContractCategory) //admin
		.delete("/api/contract_category/{id}", this::deleteContractCategoryById) //admin
		.put("/api/contract_category/{id}", this::updateContractCategory); //admin

	}

	private void getContractCategoryList(ServerRequest serverRequest, ServerResponse serverResponse) {
		logger.info("getContractCategoryList called");
		this.contract_category_repo.all()
		.collectList()
		.map(EntityUtils::objectList_toJsonArray)
		.subscribe(
				serverResponse::send,
				throwable -> {
					logger.error("getContractCategoryList failed", throwable);
					serverRequest.next(throwable);
				}
				);
	}

	private void getContractCategoryById(ServerRequest serverRequest, ServerResponse serverResponse) {
		String id = serverRequest.path().param("id");
		logger.info("getContractCategoryById called, id {}", id);
		this.contract_category_repo.getOne(id)
		.subscribe(payload -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(payload)),
				throwable -> {
					logger.error("getContractCategoryById failed", throwable);
					serverRequest.next(throwable);
				});
	}

	private void createContractCategory(ServerRequest serverRequest, ServerResponse serverResponse) {
		logger.info("createContractCategory called");
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw  AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}

			serverRequest.content().as(JsonObject.class)
			.map(jsonObj -> {

				try {
					return EntityUtils.jsonObject_toObject(ContractCategory.class, jsonObj);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;

			}).subscribe(cc -> {
				try {
					if(cc==null) {
						throw  AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
					}

					this.contract_category_repo.save(cc).subscribe(
							p -> {
								if(p!=null) { 
									cc.setId(p);
									serverResponse.status(201)
									.headers()
									.location(URI.create("/api/contract_category/" + p));
									serverResponse.send(EntityUtils.object_toJsonObject(cc));
								} else {
									logger.error("createContractCategory failed");
									serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Failed to save to DB"));
								}
							},
							throwable -> {
								logger.error("createContractCategory failed", throwable);
								serverRequest.next(throwable);
							}
							);

				} catch (Exception e) {
					e.printStackTrace();
					logger.error("createContractCategory failed with exception", e);
					serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("createContractCategory failed with exception " + e.getMessage()));
				}
			}, throwable -> {
				logger.error("createContractCategory failed", throwable);
				serverRequest.next(throwable);
			}
					);
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("createContractCategory failed with exception", e);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("createContractCategory failed with exception " + e.getMessage()));
		}


	}

	private void deleteContractCategoryById(ServerRequest serverRequest, ServerResponse serverResponse) {
		String id = serverRequest.path().param("id");
		logger.info("deleteContractCategoryById called, id {}", id);
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw  AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			this.contract_category_repo.deleteById(id)
			.subscribe(
					count -> {
						logger.info("deleteContractCategoryById {} deleted.", count);
						serverResponse.status(204).send();
					},
					throwable -> {
						logger.error("deleteContractCategoryById failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("deleteContractCategoryById failed with exception", e);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deleteContractCategoryById failed with exception " + e.getMessage()));
		}
	}

	private void updateContractCategory(ServerRequest serverRequest, ServerResponse serverResponse) {
		String id = serverRequest.path().param("id");
		logger.info("updateContractCategory called, id {}", id);
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
					return EntityUtils.jsonObject_toObject(ContractCategory.class, jsonObj);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;

			}).subscribe(cc -> {

				if(cc==null) {
					throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
				}

				this.contract_category_repo.update(id, cc).subscribe(
						p -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(cc)),
						throwable -> {
							logger.error("updateContractCategory failed", throwable);
							serverRequest.next(throwable);
						}
						);


			},
					throwable -> {
						logger.error("updateContractCategory failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("updateContractCategory failed with exception", e);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("updateContractCategory failed with exception " + e.getMessage()));
		}
	}
}
