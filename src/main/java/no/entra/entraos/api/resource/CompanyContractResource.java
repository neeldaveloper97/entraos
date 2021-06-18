package no.entra.entraos.api.resource;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import io.helidon.webserver.Routing.Rules;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.Company;
import no.entra.entraos.api.model.CompanyContract;
import no.entra.entraos.api.model.ContractProperty;
import no.entra.entraos.api.model.ContractStatus;
import no.entra.entraos.api.model.PersonContract;
import no.entra.entraos.api.model.UserRole;
import no.entra.entraos.api.repository.CompanyRepository;
import no.entra.entraos.api.repository.ContractCategoryRepository;
import no.entra.entraos.api.repository.ContractPropertyRepository;
import no.entra.entraos.api.repository.ContractRepository;
import no.entra.entraos.api.repository.ContractTypeRepository;
import no.entra.entraos.api.repository.PersonCompanyRepository;
import no.entra.entraos.api.repository.PersonContractRepository;
import no.entra.entraos.api.security.SecurityFilter;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.api.whydah.WhydahService;
import no.entra.entraos.domain.person.Contract;
import no.entra.entraos.domain.person.ContractType;
import no.entra.entraos.domain.person.Person;
import no.entra.entraos.domain.person.contracts.DefaultContract;
import no.entra.entraos.domain.person.contracts.foodandbeverage.FoodAndBeverageContract;
import no.entra.entraos.domain.person.contracts.foodandbeverage.FreeLunchContract;
import no.entra.entraos.domain.person.contracts.foodandbeverage.ProductGroupsCoveredInContract;
import no.entra.entraos.domain.person.contracts.foodandbeverage.SponsoredLunchContract;
import no.entra.entraos.domain.person.contracts.tenant.TenantContract;

public class CompanyContractResource implements Service {

	public static final Logger logger = LoggerFactory.getLogger(CompanyContractResource.class);

	private final ContractRepository contract_repo;
	private final PersonContractRepository pcontract_repo;
	private final PersonCompanyRepository pcompany_repo;
	private final WhydahService whydahService;
	private final CompanyRepository company_repo;
	private final ContractPropertyRepository cproperty_repo;
	private final ContractTypeRepository ctype_repo;

	public CompanyContractResource(ContractRepository contract_repo, PersonContractRepository pcontract_repo, PersonCompanyRepository pcompany_repo, CompanyRepository company_repo, ContractPropertyRepository cproperty_repo, ContractTypeRepository ctype_repo, WhydahService whydahService) {
		this.contract_repo = contract_repo;
		this.pcontract_repo = pcontract_repo;
		this.pcompany_repo = pcompany_repo;
		this.whydahService = whydahService;
		this.company_repo = company_repo;
		this.cproperty_repo = cproperty_repo;
		this.ctype_repo = ctype_repo;
	}

	@Override
	public void update(Rules rules) {
		rules
		.get("/api/contract", this::getContractList) //admin role
		.get("/api/contract/{id}", this::getContractById) //admin and specific companies which manage this contract
		.post("/api/contract", this::createContract) //should be validated by a specific company
		.delete("/api/contract/{id}", this::deleteContractById) //admin and specific companies which manage this contract
		.put("/api/contract/{id}", this::updateContract) //should be validated by a specific company

		//extra rules
		.get("/api/contract/company/{company_id}", this::getContractListByCompanyId) //has company admin role
		.post("/api/contract/status/{id}/{status}", this::updateContractStatus) //admin

		;
	}


	private void getContractById(ServerRequest serverRequest, ServerResponse serverResponse) {

		String id = serverRequest.path().param("id");
		logger.info("getContractById called, id {}", id);

		this.contract_repo.getOne(id)
		.subscribe(contract -> {
			if(hasContractAccess(serverRequest, contract.getId())) {
				serverResponse.status(200).send(EntityUtils.object_toJsonObject(contract));
			} else {
				throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
			}
		},
				throwable -> {
					logger.error("getContractById failed", throwable);
					serverRequest.next(throwable);
				});
	}

	private void updateContract(ServerRequest serverRequest, ServerResponse serverResponse) {

		String id = serverRequest.path().param("id");
		logger.info("updateContract called, id {}", id);


		serverRequest.content().as(JsonObject.class)
		.map(jsonObj -> {

			try {
				return EntityUtils.jsonObject_toObject(CompanyContract.class, jsonObj);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;

		}).subscribe(updated_contract -> {
			if(updated_contract==null) {
				throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
			}
			try {

				CompanyContract original_contract = contract_repo.findById(id);
				//check identity
				if(original_contract ==null) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("contract id " + id + "does not exist");
				}

				List<PersonContract> person_contracts = pcontract_repo.findAllByContractId(id).collectList().get();
				//check role
				if(SecurityFilter.isRouteAuthenticated(serverRequest) && SecurityFilter.getUserRole(serverRequest).hasValidAccess(updated_contract.getCompany_id())) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				}

				//check company identity. Do not allow to update company identity
				if(!original_contract.getCompany_id().equals(updated_contract.getCompany_id())) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Contract is invalid. Company identity can not be updated."); 
				}

				no.entra.entraos.api.model.ContractType ctt = ctype_repo.findById(updated_contract.getContract_type_id()); 
				if(ctt.getMatchedContractType() == null){
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("This contract type " + ctt.getName() + " has not been supported yet"); 
				}

				//check number of contracts purchased 
				int number_of_contracts_used = person_contracts.size(); //already assigned to some people
				int number_of_contracts_purchased = updated_contract.getQuantity();
				if(number_of_contracts_purchased<number_of_contracts_used) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Contract is invalid. The quantity property value must be greater than that of " + number_of_contracts_purchased + " contracts which has been already assigned"); 
				}
				//check expire date
				if(updated_contract.isExpired()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Contract is expired."); 
				}

				updated_contract.setLast_updated(LocalDateTime.now());

				//set to pending for confirmation if this contract is updated by a tenant
				//must be confirmed by the building owner
				if(SecurityFilter.isRouteAuthenticated(serverRequest) && !SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					updated_contract.setStatus(ContractStatus.Pending_For_Confirmation);
				}

				this.contract_repo.update(id, updated_contract).subscribe(p ->  
				{
					if(syncContractFromContractAdminToPersonAPI(original_contract, updated_contract, person_contracts)) {
						logger.info("updateContract succeeded");
						serverResponse.status(200).send(EntityUtils.object_toJsonObject(updated_contract));	
					} else {
						//roll back
						logger.error("updateContract failed");
						this.contract_repo.update(id, original_contract).toStage();
						throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Update operation succeeded but the syncing process is failed. The update operation is rolled back");
					}

				},
				throwable -> {
					logger.error("updateContract failed", throwable);
					serverRequest.next(throwable);
				});


			} catch (Exception e) {
				e.printStackTrace();
				logger.error("updateContract failed  with exception", e);
				throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("updateContract failed  with exception " + e.getMessage());
			}
		}, throwable -> {
			logger.error("updateContract failed", throwable);
			serverRequest.next(throwable);
		}
				);









	}


	private boolean syncContractFromContractAdminToPersonAPI(CompanyContract originalContract, CompanyContract updatedContract, List<PersonContract> personContracts)  {

		try {
			Company com = company_repo.findById(originalContract.getCompany_id());
			Map<String, Contract> personContractList = new HashMap<String, Contract>();
			for(PersonContract personcontract : personContracts) {

				Person p = whydahService.getPerson(personcontract.getPerson_ref());
				Contract new_contract = updatedContract.convertToRegisterAPIContract(com.getName(), com.getOrg_number(), ctype_repo.findById(updatedContract.getContract_type_id()).getMatchedContractType());
				personContractList.put(p.getId().toString(), new_contract);
			}


			for(String personRef: personContractList.keySet()) {
				if(whydahService.removeContract(personRef, personContractList.get(personRef).getContractId().toString())) {
					if(whydahService.addContract(personRef, personContractList.get(personRef)) == null) {
						logger.error("syncContractFromContractAdminToPersonAPI failed on addContract - personref {}, contractid {}", personRef, personContractList.get(personRef).getContractId().toString());
						return false;
					}
				} else {
					logger.error("syncContractFromContractAdminToPersonAPI failed on removeContract - personref {}, contractid {}", personRef, personContractList.get(personRef).getContractId().toString());
					return false;
				}
			}

			return true;

		} catch(Exception ex) {
			logger.error("syncContractFromContractAdminToPersonAPI failed  with exception", ex);
			ex.printStackTrace();
			
		}
		return false;

	}



	private void createContract(ServerRequest serverRequest, ServerResponse serverResponse) {
		logger.info("createContract called");
		serverRequest.content().as(JsonObject.class)
		.map(jsonObj -> {

			try {
				return EntityUtils.jsonObject_toObject(CompanyContract.class, jsonObj);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;

		}).subscribe(c -> {

			if(c==null) {
				throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
			}

			if(SecurityFilter.isRouteAuthenticated(serverRequest) && SecurityFilter.getUserRole(serverRequest).hasValidAccess(c.getCompany_id())) {
				throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access deined");
			}

			if(c.isExpired()) {
				throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Contract is expired."); 
			}

			//the contract created by tenants should be confirmed by the building owner
			if(SecurityFilter.isRouteAuthenticated(serverRequest) && !SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
				c.setStatus(ContractStatus.Pending_For_Confirmation);
			}


			this.contract_repo.save(c).subscribe(
					p -> {
						if (p != null) {
							logger.info("createContract succeeded");
							c.setId(p);
							serverResponse.status(201)
							.headers()
							.location(URI.create("/api/contract/" + p));
							serverResponse.send(EntityUtils.object_toJsonObject(c));
						} else {
							logger.error("createContract failed");
							serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Failed to save to DB"));
						}
					},
					throwable -> {
						logger.error("createContract failed", throwable);
						serverRequest.next(throwable);
					}
					);



		},
				throwable -> {
					logger.error("createContract failed", throwable);
					serverRequest.next(throwable);
				}

				);



	}


	private void deleteContractById(ServerRequest serverRequest, ServerResponse serverResponse) {

		String id = serverRequest.path().param("id");
		logger.info("deleteContractById called, id {}", id);
		boolean ok = hasContractAccess(serverRequest, id);

		if(ok) {

			this.contract_repo.deleteById(id)
			.subscribe(
					count -> {
						logger.info("deleteContractById {} deleted.", count);
						serverResponse.status(204).send();
					},
					throwable -> {
						logger.error("deleteContractById failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} else {
			serverRequest.next(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied"));
		}
	}

	private boolean hasContractAccess(ServerRequest serverRequest, String contractid) {
		CompanyContract contract = this.contract_repo.findById(contractid);
		boolean ok = false;
		if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
			UserRole r = SecurityFilter.getUserRole(serverRequest);
			if(r.hasValidAccess(contract.getCompany_id())) {
				ok = true;
			}
		} else {
			ok = true; //access granted for testing
		}
		return ok;
	}

	private void getContractList(ServerRequest serverRequest, ServerResponse serverResponse) {
		logger.info("getContractList called");
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(SecurityFilter.isRouteAuthenticated(serverRequest) && !SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}


			this.contract_repo.all()
			.collectList()
			.map(EntityUtils::objectList_toJsonArray)
			.subscribe(
					serverResponse::send,
					throwable -> {
						logger.error("getContractList failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getContractList failed  with exception", e);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("getContractList failed  with exception " + e.getMessage()));
		}
	}

	private void getContractListByCompanyId(ServerRequest serverRequest, ServerResponse serverResponse) {

		String companyId = serverRequest.path().param("company_id");
		logger.info("getContractListByCompanyId called, company_id {}", companyId);
		if(!SecurityFilter.isRouteAuthenticated(serverRequest) || SecurityFilter.getUserRole(serverRequest).hasValidAccess(companyId)) {
			this.contract_repo.findAllByCompanyId(companyId)
			.collectList().map(EntityUtils::objectList_toJsonArray)
			.subscribe(serverResponse::send, throwable -> {
				logger.error("getContractListByCompanyId failed", throwable);
				serverRequest.next(throwable);
			});
		} else {
			serverRequest.next(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied"));
		}
	}

	private void updateContractStatus(ServerRequest serverRequest, ServerResponse serverResponse) {

		try {
		String contract_id = serverRequest.path().param("id");
		logger.info("updateContractStatus called, id {}", contract_id);

		UserRole ctx = SecurityFilter.getUserRole(serverRequest);
		if(ctx!=null && !ctx.hasSuperAdminRole()) {
			throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied.");
		}

		ContractStatus status =  ContractStatus.valueOf(serverRequest.path().param("status"));

		this.contract_repo.updateStatus(contract_id, status).subscribe(
				count -> {
					if(count>0) {
						logger.info("updateContractStatus {0} updated.", count);
						serverResponse.status(204).send();
					} else {
						logger.error("updateContractStatus failed");
						throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("updateContractStatus failed");
					}
				},
				throwable -> {
					logger.error("updateContractStatus failed", throwable);
					serverRequest.next(throwable);
				}
				);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("updateContractStatus failed  with exception", e);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("updateContractStatus failed  with exception " + e.getMessage()));
		}

	}
}
