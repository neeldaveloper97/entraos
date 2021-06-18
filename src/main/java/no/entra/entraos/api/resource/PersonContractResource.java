package no.entra.entraos.api.resource;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.helidon.webserver.Routing.Rules;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.Company;
import no.entra.entraos.api.model.CompanyContract;
import no.entra.entraos.api.model.ContractStatus;
import no.entra.entraos.api.model.PersonCompany;
import no.entra.entraos.api.model.PersonContract;
import no.entra.entraos.api.repository.CompanyRepository;
import no.entra.entraos.api.repository.ContractRepository;
import no.entra.entraos.api.repository.ContractTypeRepository;
import no.entra.entraos.api.repository.PersonCompanyRepository;
import no.entra.entraos.api.repository.PersonContractRepository;
import no.entra.entraos.api.security.SecurityFilter;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.api.whydah.WhydahService;
import no.entra.entraos.domain.person.Contract;
import no.entra.entraos.domain.person.Person;

public class PersonContractResource implements Service {

	public static final Logger logger = LoggerFactory.getLogger(PersonContractResource.class);

	private final PersonContractRepository person_contract_repo;
	private final CompanyRepository company_repo;
	private final PersonCompanyRepository person_company_repo;
	private final ContractRepository contract_repo;
	private final WhydahService whydahservice;
	private final ContractTypeRepository ctype_repo;

	public PersonContractResource(PersonContractRepository person_contract_repo,
			CompanyRepository company_repo,
			PersonCompanyRepository person_company_repo,
			ContractRepository contract_repo,
			WhydahService whydahservice,
			ContractTypeRepository ctype_repo
			) {
		this.person_contract_repo = person_contract_repo;
		this.company_repo = company_repo;
		this.person_company_repo = person_company_repo;
		this.contract_repo = contract_repo;
		this.whydahservice = whydahservice;
		this.ctype_repo = ctype_repo;
	}

	@Override
	public void update(Rules rules) {
		rules
		.get("/api/person_contract", this::getPersonContractList) //admin
		.get("/api/person_contract/{id}", this::getPersonContractById) //admin
		.post("/api/person_contract", this::createPersonContract) //admin + specific companyadmin
		.delete("/api/person_contract/{id}", this::deletePersonContractById) //admin
		//.put("/api/person_contract/{id}", this::updatePersonContract) //admin -> removed

		//extra rules
		.delete("/api/person_contract/contract/{contract_id}", this::deletePersonContractByContractId) //admin
		.get("/api/person_contract/company/{company_id}", this::getPersonContractListByCompany) //admin + specific company admin
		.get("/api/person_contract/company/{company_id}/{person_ref}", this::getPersonContractListByCompanyAndPersonRef) //admin + specific company admin
		.delete("/api/person_contract/company/{company_id}/{person_ref}/{contract_id}", this::deletePersonContract) //admin + specific company admin
		;

	}

	private void getPersonContractById(ServerRequest serverRequest, ServerResponse serverResponse) {

		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
		
		String id = serverRequest.path().param("id");
		logger.info("getPersonContractById called, id {}", id);
		this.person_contract_repo.getOne(id)
		.subscribe(t -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(t)),
				throwable -> {
					logger.error("getPersonContractById failed", throwable);
					serverRequest.next(throwable);
				});
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("deletePersonContract failed with exception");
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deletePersonContract failed with exception " + e.getMessage()));
		}
	}

	/*
	private void updatePersonContract(ServerRequest serverRequest, ServerResponse serverResponse) {

		String id = serverRequest.path().param("id");
		logger.info("updatePersonContract called, id {}", id);
		JsonObject jsonObj = serverRequest.content().as(JsonObject.class).await();

		try {
			PersonContract pc= EntityUtils.jsonObject_toObject(PersonContract.class, jsonObj);
			this.person_contract_repo.update(id, pc).subscribe(
					p -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(pc)),
					throwable -> {
						logger.error("updatePersonContract failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logger.error("updatePersonContract failed", e);
			throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("updatePersonContract failed with exception " + e.getMessage());
		}
	}*/

	private void deletePersonContract(ServerRequest serverRequest, ServerResponse serverResponse) {

		try {
			String companyId = serverRequest.path().param("company_id");
			String personRef = serverRequest.path().param("person_ref");
			String contractId = serverRequest.path().param("contract_id");
			logger.info("deletePersonContract called, company_id {}, person_ref {}, contract_id {}", companyId, personRef, contractId);
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				if(!SecurityFilter.getUserRole(serverRequest).hasValidAccess(companyId)) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			PersonContract person_contract_mapping_found = person_contract_repo.findByCompanyIdAndPersonRefAndContractId(companyId, personRef, contractId);
			removePersonContractMapping(serverRequest, serverResponse, person_contract_mapping_found);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("deletePersonContract failed with exception");
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deletePersonContract failed with exception " + e.getMessage()));
		}

	}


	private void createPersonContract(ServerRequest serverRequest, ServerResponse serverResponse) {
		logger.info("createPersonContract called");
		serverRequest.content().as(JsonObject.class)
		.map(jsonObj -> {

			try {
				return EntityUtils.jsonObject_toObject(PersonContract.class, jsonObj);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;

		}).subscribe(person_contract_mapping -> {
			if(person_contract_mapping==null) {
				throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
			}



			String companyId = person_contract_mapping.getCompany_id();
			String personRef = person_contract_mapping.getPerson_ref();
			String contractId = person_contract_mapping.getContract_id();
			//examine the data
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				if(!SecurityFilter.getUserRole(serverRequest).hasValidAccess(companyId)) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			Company com = company_repo.findById(companyId);
			if(com==null) {
				throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Company not found");
			}
			PersonCompany person_company_mapping = person_company_repo.findByCompanyIdAndPersonRef(companyId, personRef);
			if(person_company_mapping ==null) {
				throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("This person " + personRef + " does not belong to company " + com.getName());
			}
			CompanyContract company_contract = contract_repo.findById(contractId);
			if(company_contract ==null) {
				throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Contract not found");
			} else {
				if(company_contract.getStatus() != ContractStatus.Activated) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Contract status must be activated. The current status is " + company_contract.getStatus().toString());
				}
			}

			PersonContract person_contract_mapping_found = person_contract_repo.findByCompanyIdAndPersonRefAndContractId(companyId, personRef, contractId);
			if(person_contract_mapping_found!=null) {
				throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Contract already assigned");
			}

			if(company_contract.getQuantity() <= person_contract_repo.countByCompanyIdAndContractId(companyId, contractId)) {
				throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("The number of contracts is now reaching the limit of " + company_contract.getQuantity() + " being assigned");
			}


			//sync with whydah
			Contract contract = company_contract.convertToRegisterAPIContract(com.getName(), com.getOrg_number(), ctype_repo.findById(company_contract.getContract_type_id()).getMatchedContractType());
			Person person = whydahservice.addContract(personRef, contract);
			if(person==null) {
				logger.error("createPersonContract failed with an exception from EntraSSO person register API");
				throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("createPersonContract failed with an exception from EntraSSO person register API");
			}
			//it is safe to create a new person _ contract mapping now
			person_contract_mapping.setCreated_at(LocalDateTime.now());
			this.person_contract_repo.save(person_contract_mapping).subscribe(
					p -> {
						if (p != null) {
							person_contract_mapping.setId(p);
							//save person back
							PersonCompany pc = person_company_repo.findByCompanyIdAndPersonRef(com.getId(), personRef);
							person.getContractRoles().removeIf(i -> !com.getName().equalsIgnoreCase(i.getCompanyName()));
							pc.setPerson(person);
							person_company_repo.update(pc.getId(), pc).await();

							serverResponse.status(201)
							.headers()
							.location(URI.create("/api/personcontract/" + p));
							serverResponse.send(EntityUtils.object_toJsonObject(person_contract_mapping));
						} else {
							logger.error("createPersonContract failed");
							serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Failed to save to DB"));
						}
					},
					throwable -> {
						logger.error("createPersonContract failed", throwable);
						serverRequest.next(throwable);
					}
					);





		},
				throwable -> {
					logger.error("createPersonContract failed", throwable);
					serverRequest.next(throwable);
				}	
				);


	}


	private void deletePersonContractById(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}

			String id = serverRequest.path().param("id");
			logger.info("deletePersonContractById called, id {}", id);
			PersonContract person_contract_mapping_found = person_contract_repo.findById(id);
			removePersonContractMapping(serverRequest, serverResponse, person_contract_mapping_found);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("deletePersonContractById failed with exception");
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deletePersonContractById failed with exception " + e.getMessage()));
		}
	}

	private void deletePersonContractByContractId(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}

			String contract_id = serverRequest.path().param("contract_id");
			logger.info("deletePersonContractByContractId called, contract_id {}", contract_id);
			List<PersonContract> person_contract_mapping_found_list = person_contract_repo.findAllByContractId(contract_id).collectList().await();
			boolean ok = true;
			List<PersonContract> removed_list = new ArrayList<PersonContract>();
			for(PersonContract person_contract_mapping_found : person_contract_mapping_found_list) {
				if(whydahservice.removeContract(person_contract_mapping_found.getPerson_ref(), person_contract_mapping_found.getContract_id())) {
					Long count = this.person_contract_repo.deleteById(person_contract_mapping_found.getId()).await();
					if(count>0) {
						removed_list.add(person_contract_mapping_found);
					} else {
						ok = false;
						break;
					}
				} else {
					ok = false;
					break;
				}
			}

			if(ok) {
				for(PersonContract person_contract_mapping_found : person_contract_mapping_found_list) {
					//remove the contract from person object in person company mapping
					PersonCompany p = person_company_repo.findByCompanyIdAndPersonRef(person_contract_mapping_found.getCompany_id(), person_contract_mapping_found.getPerson_ref());
					p.getPerson().getContractRoles().removeIf(i -> person_contract_mapping_found.getContract_id().equalsIgnoreCase(i.getContractId().toString()));
					person_company_repo.update(p.getId(), p).await();
				}
				//send ok 
				serverResponse.status(204).send();

			} else {
				//rollback
				for(PersonContract contract : removed_list) {
					this.person_contract_repo.save(contract);
				}
				//send exception
				logger.error("deletePersonContractByContractId failed.");
				serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deletePersonContractByContractId failed"));

			}} catch (Exception e) {
				e.printStackTrace();
				logger.error("deletePersonContractByContractId failed with exception");
				serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deletePersonContractByContractId failed with exception " + e.getMessage()));
			}


	}



	private void removePersonContractMapping(ServerRequest serverRequest, ServerResponse serverResponse, PersonContract person_contract_mapping_found) {
		if(person_contract_mapping_found!=null) {
			if(whydahservice.removeContract(person_contract_mapping_found.getPerson_ref(), person_contract_mapping_found.getContract_id())) {
				this.person_contract_repo.deleteById(person_contract_mapping_found.getId())
				.subscribe(
						count -> {
							logger.info("deletePersonContractById {} deleted.", count);
							if(count>0) {
								//remove the contract from person object in person company mapping
								PersonCompany p = person_company_repo.findByCompanyIdAndPersonRef(person_contract_mapping_found.getCompany_id(), person_contract_mapping_found.getPerson_ref());
								p.getPerson().getContractRoles().removeIf(i -> person_contract_mapping_found.getContract_id().equalsIgnoreCase(i.getContractId().toString()));
								person_company_repo.update(p.getId(), p).await();
								serverResponse.status(204).send();
							} else {
								logger.error("deletePersonContractById failed.");
								throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deletePersonContractById failed");
							}
						},
						throwable -> {
							logger.error("deletePersonContractById failed", throwable);
							serverRequest.next(throwable);
						}
						);
			} else {
				logger.error("deletePersonContractById failed with an exception from EntraSSO person register API, company_id {}, person_ref {}, contract_id {}", person_contract_mapping_found.getCompany_id(), person_contract_mapping_found.getPerson_ref(), person_contract_mapping_found.getContract_id().toString());
				throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deletePersonContractById failed with an exception from EntraSSO person register API");
			}
		} else {
			throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deletePersonContractById failed, identity not found");
		}
	}

	private void getPersonContractList(ServerRequest serverRequest, ServerResponse serverResponse) {
		
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
		logger.info("getPersonContractList called");
		this.person_contract_repo.all()
		.collectList()
		.map(EntityUtils::objectList_toJsonArray)
		.subscribe(
				serverResponse::send,
				throwable -> {
					logger.error("getPersonContractList failed", throwable);
					serverRequest.next(throwable);
				}
				);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getPersonContractList failed with exception");
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("getPersonContractList failed with exception " + e.getMessage()));
		}
	}

	private void getPersonContractListByCompany(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			String company_id = serverRequest.path().param("company_id");
			logger.info("getPersonContractListByCompany called, company_id {}", company_id);

			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				if(!SecurityFilter.getUserRole(serverRequest).hasValidAccess(company_id)) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}

			this.person_contract_repo.findAllByCompanyId(company_id)
			.collectList()
			.map(EntityUtils::objectList_toJsonArray)
			.subscribe(
					serverResponse::send,
					throwable -> {
						logger.error("getPersonContractListByCompany failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getPersonContractListByCompany failed with exception");
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("getPersonContractListByCompany failed with exception " + e.getMessage()));
		}
	}
	private void getPersonContractListByCompanyAndPersonRef(ServerRequest serverRequest, ServerResponse serverResponse) {
		
		try {

			String company_id = serverRequest.path().param("company_id");
			String person_ref = serverRequest.path().param("person_ref");

			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				if(!SecurityFilter.getUserRole(serverRequest).hasValidAccess(company_id)) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}


			logger.info("getPersonContractListByCompanyAndPersonRef called, company_id {} person_ref {}", company_id, person_ref);
			this.person_contract_repo.findAllByCompanyIdAndPersonRef(company_id, person_ref)
			.collectList()
			.map(EntityUtils::objectList_toJsonArray)
			.subscribe(
					serverResponse::send,
					throwable -> {
						logger.error("getPersonContractListByCompanyAndPersonRef failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getPersonContractListByCompanyAndPersonRef failed with exception");
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("getPersonContractListByCompanyAndPersonRef failed with exception " + e.getMessage()));
		}
	}
}
